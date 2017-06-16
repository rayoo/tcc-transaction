package org.mengyun.tcctransaction.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.mengyun.tcctransaction.api.TransactionXid;
import org.mengyun.tcctransaction.utils.StringUtils;

/**
 * Created by changmingxie on 10/30/15.
 */
public class JdbcXidRepository {

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

	public int createXid(TransactionXid xid) {

		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			connection = this.getConnection();
			// connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/TCC?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true", "root", "root");

			StringBuilder builder = new StringBuilder();
			builder.append("INSERT INTO " + getTableName() + "(gtxid, btxid,create_time");
			builder.append(StringUtils.isNotEmpty(domain) ? ",domain ) VALUES (?,?,?,?)" : ") VALUES (?,?,?)");

			stmt = connection.prepareStatement(builder.toString());

			stmt.setBytes(1, xid.getGlobalTransactionId());
			stmt.setBytes(2, xid.getBranchQualifier());
			stmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));

			if (StringUtils.isNotEmpty(domain)) {
				stmt.setString(4, domain);
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

}
