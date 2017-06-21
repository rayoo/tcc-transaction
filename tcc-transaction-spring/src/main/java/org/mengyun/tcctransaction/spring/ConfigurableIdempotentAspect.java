package org.mengyun.tcctransaction.spring;

import org.aspectj.lang.annotation.Aspect;
import org.mengyun.tcctransaction.XidRepository;
import org.mengyun.tcctransaction.interceptor.IdempotentAspect;
import org.mengyun.tcctransaction.interceptor.IdempotentInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

/**
 * @author rayoo
 */
@Aspect
public class ConfigurableIdempotentAspect extends IdempotentAspect implements Ordered {
	private Logger logger = LoggerFactory.getLogger(ConfigurableIdempotentAspect.class);

	private XidRepository xidRepository;

	public void init() {
		logger.info("initialize idempotent aspect...");
		IdempotentInterceptor idempotentTransactionInterceptor = new IdempotentInterceptor();

		idempotentTransactionInterceptor.setXidRepository(xidRepository);

		this.setIdempotentInterceptor(idempotentTransactionInterceptor);
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	public void setXidRepository(XidRepository xidRepository) {
		this.xidRepository = xidRepository;
	}

}
