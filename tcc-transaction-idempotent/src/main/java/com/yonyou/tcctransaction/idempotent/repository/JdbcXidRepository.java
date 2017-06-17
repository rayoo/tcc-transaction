package com.yonyou.tcctransaction.idempotent.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.mengyun.tcctransaction.api.TransactionContext;

/**
 * @author rayoo
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
		return null != tbSuffix && tbSuffix.length() > 0 ? "tcc_idempotent" + tbSuffix : "tcc_idempotent";
	}

	public int createXid(TransactionContext tctx, String methodName) {

		Connection connection = null;
		PreparedStatement stmt = null;

		try {
			connection = this.getConnection();
			// connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/TCC?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true", "root", "root");

			StringBuilder builder = new StringBuilder();
			builder.append("INSERT INTO " + getTableName() + "(gtxid, btxid, create_time, method, status");
			builder.append(null != domain && domain.length() > 0 ? ",domain ) VALUES (?,?,?,?,?,?)" : ") VALUES (?,?,?,?,?)");

			stmt = connection.prepareStatement(builder.toString());

			stmt.setBytes(1, tctx.getXid().getGlobalTransactionId());
			stmt.setBytes(2, tctx.getXid().getBranchQualifier());
			stmt.setLong(3, System.currentTimeMillis());
			stmt.setString(4, methodName);
			stmt.setInt(5, tctx.getStatus());

			if (null != domain && domain.length() > 0) {
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

}
