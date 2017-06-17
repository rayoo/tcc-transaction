package org.mengyun.tcctransaction;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionContextEditor;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.mengyun.tcctransaction.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * Created by changmingxie on 10/30/15.
 */
public class Terminator implements Serializable {
	static final Logger logger = LoggerFactory.getLogger(Terminator.class);
	private static final long serialVersionUID = -164958655471605778L;

	public Terminator() {

	}

	public Object invoke(TransactionContext transactionContext, InvocationContext invocationContext, Class<? extends TransactionContextEditor> transactionContextEditorClass) {
		logger.info("invoke context:{}, class:{}", invocationContext.getMethodName() + " " + transactionContext + " " + JSON.toJSON(invocationContext),
				null == transactionContextEditorClass ? null : transactionContextEditorClass.getName());
		if (StringUtils.isNotEmpty(invocationContext.getMethodName())) {

			try {

				Object target = FactoryBuilder.factoryOf(invocationContext.getTargetClass()).getInstance();

				Method method = null;

				method = target.getClass().getMethod(invocationContext.getMethodName(), invocationContext.getParameterTypes());

				FactoryBuilder.factoryOf(transactionContextEditorClass).getInstance().set(transactionContext, target, method, invocationContext.getArgs());

				return method.invoke(target, invocationContext.getArgs());

			} catch (Exception e) {
				throw new SystemException(e);
			}
		}
		return null;
	}
}
