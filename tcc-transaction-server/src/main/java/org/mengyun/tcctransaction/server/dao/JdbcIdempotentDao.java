package org.mengyun.tcctransaction.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.bind.DatatypeConverter;

import org.mengyun.tcctransaction.server.utils.DBUtils;
import org.mengyun.tcctransaction.server.vo.IdempotentVo;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcIdempotentDao {

	private static final String TABLE_NAME_PREFIX = "tcc_idempotent";

	@Resource(name = "idempotentDataSource")
	private DataSource dataSource;

	public List<IdempotentVo> findIdempotent(byte[] gtxid) {
		Connection connection = DBUtils.getConnection(dataSource);
		List<IdempotentVo> idempotentVos = new ArrayList<IdempotentVo>();
		PreparedStatement preparedStatement = null;
		try {
			String tableName = TABLE_NAME_PREFIX;
			String sql = "select domain, gtxid, btxid, status, method, create_time from " + tableName + " where gtxid=?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setBytes(1, gtxid);

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				IdempotentVo idempotentVo = new IdempotentVo();

				idempotentVo.setDomain(resultSet.getString(1));
				idempotentVo.setGtxid(DatatypeConverter.printHexBinary(resultSet.getBytes(2)));
				idempotentVo.setBtxid(DatatypeConverter.printHexBinary(resultSet.getBytes(3)));
				idempotentVo.setStatus(resultSet.getInt(4));
				idempotentVo.setMethod(resultSet.getString(5));
				idempotentVo.setCreateTime(resultSet.getLong(6));

				idempotentVos.add(idempotentVo);
			}
		} catch (Exception e) {
			throw new RuntimeException("findTransactions error", e);
		} finally {
			DBUtils.closeStatement(preparedStatement);
			DBUtils.releaseConnection(connection);
		}
		return idempotentVos;
	}

}
