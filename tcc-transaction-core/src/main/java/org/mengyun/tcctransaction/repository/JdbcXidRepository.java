package org.mengyun.tcctransaction.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.curator.shaded.com.google.common.collect.Sets;
import org.mengyun.tcctransaction.Participant;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.XidRepository;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.common.TransactionType;
import org.mengyun.tcctransaction.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * @author rayoo
 */
public class JdbcXidRepository implements XidRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(JdbcXidRepository.class);

	private String domain;

	private String tbSuffix;

	private DataSource dataSource;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getTbSuffix() {
		return tbSuffix;
	}

	public void setTbSuffix(String tbSuffix) {
		this.tbSuffix = tbSuffix;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	protected Connection getConnection() {
		try {
			return this.dataSource.getConnection();
		} catch (SQLException e) {
			throw new TransactionIOException(e);
		}
	}

	protected void releaseConnection(Connection con) {
		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (SQLException e) {
			throw new TransactionIOException(e);
		}
	}

	private void closeStatement(Statement stmt) {
		try {
			if (stmt != null && !stmt.isClosed()) {
				stmt.close();
			}
		} catch (Exception ex) {
			throw new TransactionIOException(ex);
		}
	}

	private String getTableName() {
		return StringUtils.isNotEmpty(tbSuffix) ? "tcc_idempotent" + tbSuffix : "tcc_idempotent";
	}

	public int createXid(TransactionContext tctx, String methodName) {
		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			connection = this.getConnection();
			// connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/TCC?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true", "root", "root");

			StringBuilder builder = new StringBuilder();
			builder.append("INSERT INTO " + getTableName() + "(gtxid, btxid, create_time, method, status");
			builder.append(StringUtils.isNotEmpty(domain) ? ",domain ) VALUES (?,?,?,?,?,?)" : ") VALUES (?,?,?,?,?)");

			stmt = connection.prepareStatement(builder.toString());

			stmt.setBytes(1, tctx.getXid().getGlobalTransactionId());
			stmt.setBytes(2, tctx.getXid().getBranchQualifier());
			stmt.setLong(3, System.currentTimeMillis());
			stmt.setString(4, methodName);
			stmt.setInt(5, tctx.getStatus());

			if (StringUtils.isNotEmpty(domain)) {
				stmt.setString(6, domain);
			}

			return stmt.executeUpdate();

		} catch (SQLException e) {
			if ("23000".equals(e.getSQLState())) { // Duplicate Unique key
				return -1;
			}
			throw new TransactionIOException(e);
		} finally {
			closeStatement(stmt);
			this.releaseConnection(connection);
		}
	}

	@Override
	public int deleteXid(Transaction transaction) {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = this.getConnection();
			// connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/TCC?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true", "root", "root");

			StringBuilder builder = new StringBuilder("delete from ");
			builder.append(getTableName()).append(" where gtxid=? ");

			Set<byte[]> btxids = Sets.newHashSet();

			if (TransactionType.BRANCH == transaction.getTransactionType()) {
				builder.append(" and (btxid=? OR ");
				for (Participant participant : transaction.getParticipants()) {
					builder.append("btxid=? OR ");
					btxids.add(participant.getXid().getBranchQualifier());
				}
				builder.setLength(builder.length() - 4);
				builder.append(")");
			}

			String sql = builder.toString();
			stmt = connection.prepareStatement(sql);

			stmt.setBytes(1, transaction.getXid().getGlobalTransactionId());

			if (TransactionType.BRANCH == transaction.getTransactionType()) {
				int idx = 2;
				stmt.setBytes(idx++, transaction.getXid().getBranchQualifier());
				for (byte[] btxid : btxids) {
					stmt.setBytes(idx++, btxid);
				}
			}

			int ret = stmt.executeUpdate();
			LOGGER.info("delete idempotent records:{}, sql:{} transaction:{}", ret, sql, JSON.toJSON(transaction));
			return ret;
		} catch (SQLException e) {
			throw new TransactionIOException(e);
		} finally {
			closeStatement(stmt);
			this.releaseConnection(connection);
		}
	}

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder("and (btxid=? OR ");
		sb.setLength(sb.length() - 4);
		System.out.println(sb);
	}

}
