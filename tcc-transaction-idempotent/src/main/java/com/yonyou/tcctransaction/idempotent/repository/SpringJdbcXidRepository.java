package com.yonyou.tcctransaction.idempotent.repository;

import java.sql.Connection;

import org.springframework.jdbc.datasource.DataSourceUtils;

import com.yonyou.tcctransaction.idempotent.repository.JdbcXidRepository;

/**
 * @author rayoo
 */
public class SpringJdbcXidRepository extends JdbcXidRepository {

	protected Connection getConnection() {
		return DataSourceUtils.getConnection(this.getDataSource());
	}

	protected void releaseConnection(Connection con) {
		DataSourceUtils.releaseConnection(con, this.getDataSource());
	}
}
