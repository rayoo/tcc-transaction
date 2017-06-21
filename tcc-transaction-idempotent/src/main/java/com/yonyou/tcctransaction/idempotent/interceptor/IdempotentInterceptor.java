package com.yonyou.tcctransaction.idempotent.interceptor;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yonyou.tcctransaction.idempotent.XidRepository;

/**
 * @author rayoo
 */
public class IdempotentInterceptor {

	static final Logger logger = LoggerFactory.getLogger(IdempotentInterceptor.class.getSimpleName());

	private XidRepository xidRepository;

	public void setXidRepository(XidRepository xidRepository) {
		this.xidRepository = xidRepository;
	}

	public Object interceptIdempotentMethod(ProceedingJoinPoint pjp) throws Throwable {
		Method method = ((MethodSignature) (pjp.getSignature())).getMethod();

		int position = getTransactionContextParamPosition(method.getParameterTypes());

		if (position == -1) {
			throw new RuntimeException("no parameter TransactionContext found.");
		}

		TransactionContext transactionContext = (TransactionContext) pjp.getArgs()[position];

		TransactionStatus transactionStatus = TransactionStatus.valueOf(transactionContext.getStatus());

		logger.info("interceptor idempotent method, method:{}, transactionContext:{}", method.getName(), transactionContext);

		if (transactionStatus == TransactionStatus.TRYING) { // try 阶段不做拦截
			return pjp.proceed();
		}

		int xidRet = xidRepository.createXid(transactionContext, method.getName());
		if (xidRet == -1) {
			logger.error("repetitive execution, method:{}, transactionStatus:{}", method.getName(), transactionStatus.toString());
			return null;
		}
		return pjp.proceed();
	}

	public static int getTransactionContextParamPosition(Class<?>[] parameterTypes) {
		int i = -1;
		for (i = 0; i < parameterTypes.length; i++) {
			if (parameterTypes[i].equals(org.mengyun.tcctransaction.api.TransactionContext.class)) {
				break;
			}
		}
		return i;
	}

}
