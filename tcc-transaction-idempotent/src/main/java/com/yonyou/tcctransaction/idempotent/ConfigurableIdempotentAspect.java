package com.yonyou.tcctransaction.idempotent;

import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import com.yonyou.tcctransaction.idempotent.interceptor.IdempotentAspect;
import com.yonyou.tcctransaction.idempotent.interceptor.IdempotentInterceptor;
import com.yonyou.tcctransaction.idempotent.repository.JdbcXidRepository;

/**
 * @author rayoo
 */
@Aspect
public class ConfigurableIdempotentAspect extends IdempotentAspect implements Ordered {
	private Logger logger = LoggerFactory.getLogger(ConfigurableIdempotentAspect.class);

	private JdbcXidRepository jdbcXidRepository;

	public void init() {
		logger.info("initialize idempotent aspect...");
		IdempotentInterceptor idempotentTransactionInterceptor = new IdempotentInterceptor();

		idempotentTransactionInterceptor.setJdbcXidRepository(jdbcXidRepository);

		this.setIdempotentInterceptor(idempotentTransactionInterceptor);
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	public void setJdbcXidRepository(JdbcXidRepository jdbcXidRepository) {
		this.jdbcXidRepository = jdbcXidRepository;
	}

}
