package org.mengyun.tcctransaction.spring.support;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * 未完整实现，TODO
 * 
 * @author rayoo
 */
public class SpringMethodInvoker {

	private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(false).setNameFormat("TCCSpringInvoker-%d").build());

	public <V> Future<V> invoke(final String beanName, final String methodName) {
		Future<V> ret = EXECUTOR.submit(new Callable<V>() {
			@Override
			public V call() throws Exception {
				// Object bean = SpringBeanFactory.getsBean(beanType);
				MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
				// jobDetail.setTargetObject(bean);
				jobDetail.setTargetMethod(methodName);
				jobDetail.setName(methodName);
				// jobDetail.setConcurrent(false);
				jobDetail.setTargetBeanName(beanName);
				jobDetail.afterPropertiesSet();
				V ret = (V) jobDetail.invoke();
				return ret;
			}
		});
		return ret;
	}

}
