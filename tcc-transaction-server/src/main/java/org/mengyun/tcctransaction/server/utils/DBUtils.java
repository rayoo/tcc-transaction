package org.mengyun.tcctransaction.server.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

public class DBUtils {

	public static void releaseConnection(Connection connection) {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static void closeStatement(PreparedStatement preparedStatement) {
		try {
			if (preparedStatement != null && !preparedStatement.isClosed()) {
				preparedStatement.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static Connection getConnection(DataSource dataSource) {
		Connection connection;
		try {
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("get jdbc connection error", e);
		}
		return connection;
	}

}
