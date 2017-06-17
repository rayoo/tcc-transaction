package com.yonyou.tcctransaction.idempotent.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author rayoo
 */
@Aspect
public abstract class IdempotentAspect {

	private IdempotentInterceptor idempotentInterceptor;

	public void setIdempotentInterceptor(IdempotentInterceptor idempotentInterceptor) {
		this.idempotentInterceptor = idempotentInterceptor;
	}

	@Pointcut("@annotation(org.mengyun.tcctransaction.api.Idempotent)")
	public void idempotentService() {

	}

	@Around("idempotentService()")
	public Object interceptIdempotentMethod(ProceedingJoinPoint pjp) throws Throwable {
		return idempotentInterceptor.interceptIdempotentMethod(pjp);
	}

	public abstract int getOrder();
}
