package org.mengyun.tcctransaction.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.bind.DatatypeConverter;

import org.mengyun.tcctransaction.server.utils.DBUtils;
import org.mengyun.tcctransaction.server.vo.PageVo;
import org.mengyun.tcctransaction.server.vo.TransactionVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Created by cheng.zeng on 2016/9/2.
 */
@Repository("jdbcTransactionDao")
public class JdbcTransactionDao implements TransactionDao {
	// private ObjectSerializer<Transaction> serializer = new JsonTransactionSerializer();

	@Resource(name = "tccDataSource")
	private DataSource dataSource;

	private static final String TABLE_NAME_PREFIX = "TCC_TRANSACTION";

	@Value("#{jdbcDomainSuffix}")
	private Properties domainSuffix;

	@Override
	public List<TransactionVo> findTransactions(String domain, Integer pageNum, int pageSize) {
		if (domainSuffix.getProperty(domain) == null) {
			return Collections.emptyList();
		}
		Connection connection = DBUtils.getConnection(dataSource);
		List<TransactionVo> transactionVos = new ArrayList<TransactionVo>();
		PreparedStatement preparedStatement = null;
		try {
			String tableName = TABLE_NAME_PREFIX + domainSuffix.getProperty(domain);
			String sql = "select DOMAIN," + "GLOBAL_TX_ID," + "BRANCH_QUALIFIER," + "STATUS," + "TRANSACTION_TYPE," + "RETRIED_COUNT," + "CREATE_TIME," + "LAST_UPDATE_TIME,"
					+ "CONTENT from " + tableName + " order by create_time desc limit ?,?";

			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, (pageNum - 1) * pageSize);
			preparedStatement.setInt(2, pageSize);

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				TransactionVo transactionVo = new TransactionVo();
				transactionVo.setDomain(resultSet.getString(1));
				transactionVo.setGlobalTxId(DatatypeConverter.printHexBinary(resultSet.getBytes(2)));
				transactionVo.setBranchQualifier(DatatypeConverter.printHexBinary(resultSet.getBytes(3)));
				transactionVo.setStatus(resultSet.getInt(4));
				transactionVo.setTransactionType(resultSet.getInt(5));
				transactionVo.setRetriedCount(resultSet.getInt(6));
				transactionVo.setCreateTime(resultSet.getTimestamp(7));
				transactionVo.setLastUpdateTime(resultSet.getTimestamp(8));
				transactionVo.setContent(new String(resultSet.getBytes(9)));
				transactionVos.add(transactionVo);
			}
		} catch (Exception e) {
			throw new RuntimeException("findTransactions error", e);
		} finally {
			DBUtils.closeStatement(preparedStatement);
			DBUtils.releaseConnection(connection);
		}
		return transactionVos;
	}

	@Override
	public Integer countOfFindTransactions(String domain) {
		if (domainSuffix.getProperty(domain) == null) {
			return 0;
		}
		Connection connection = DBUtils.getConnection(dataSource);
		PageVo<TransactionVo> pageVo = new PageVo<TransactionVo>();
		List<TransactionVo> transactionVos = new ArrayList<TransactionVo>();
		pageVo.setItems(transactionVos);
		PreparedStatement preparedStatement = null;

		try {
			String tableName = TABLE_NAME_PREFIX + domainSuffix.getProperty(domain);

			preparedStatement = connection.prepareStatement("select COUNT(*) as count from " + tableName);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (Exception e) {
			throw new RuntimeException("countOfFindTransactions error", e);
		} finally {
			DBUtils.closeStatement(preparedStatement);
			DBUtils.releaseConnection(connection);
		}
		return 0;
	}

	@Override
	public boolean resetRetryCount(String domain, byte[] globalTxId, byte[] branchQualifier) {
		if (domainSuffix.getProperty(domain) == null) {
			return false;
		}
		Connection connection = DBUtils.getConnection(dataSource);
		PreparedStatement preparedStatement = null;
		try {
			String tableName = TABLE_NAME_PREFIX + domainSuffix.getProperty(domain);
			;
			String sql = "UPDATE " + tableName + " SET RETRIED_COUNT=0" + " WHERE GLOBAL_TX_ID = ? AND BRANCH_QUALIFIER = ?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setBytes(1, globalTxId);
			preparedStatement.setBytes(2, branchQualifier);
			int result = preparedStatement.executeUpdate();
			return result > 0;
		} catch (Exception e) {
			throw new RuntimeException("resetRetryCount error", e);
		} finally {
			DBUtils.closeStatement(preparedStatement);
			DBUtils.releaseConnection(connection);
		}
	}

}
