package org.mengyun.tcctransaction.spring.repository;

import java.sql.Connection;

import org.mengyun.tcctransaction.repository.JdbcXidRepository;
import org.springframework.jdbc.datasource.DataSourceUtils;

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
