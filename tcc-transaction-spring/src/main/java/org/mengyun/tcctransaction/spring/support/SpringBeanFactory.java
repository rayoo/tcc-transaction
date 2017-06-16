package org.mengyun.tcctransaction.spring.support;

import java.util.Map;

import org.mengyun.tcctransaction.support.BeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by changmingxie on 11/22/15.
 */
public class SpringBeanFactory implements BeanFactory, ApplicationContextAware {

	private ApplicationContext applicationContext;
	private static ApplicationContext staticAplicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		staticAplicationContext = applicationContext;
	}

	@Override
	public boolean isFactoryOf(Class clazz) {
		Map map = this.applicationContext.getBeansOfType(clazz);
		return map.size() > 0;
	}

	@Override
	public <T> T getBean(Class<T> var1) {
		return this.applicationContext.getBean(var1);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getsBean(String name) {
		return (T) staticAplicationContext.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getsBean(Class<T> requiredType) {
		return staticAplicationContext.getBean(requiredType);
	}

}
