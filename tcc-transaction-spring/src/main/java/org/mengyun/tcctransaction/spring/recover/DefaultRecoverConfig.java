package org.mengyun.tcctransaction.spring.recover;

import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

import org.mengyun.tcctransaction.OptimisticLockException;
import org.mengyun.tcctransaction.recover.RecoverConfig;

/**
 * Created by changming.xie on 6/1/16.
 */
public class DefaultRecoverConfig implements RecoverConfig {

	public static final RecoverConfig INSTANCE = new DefaultRecoverConfig();

	private int maxRetryCount = 30;

	private int recoverDuration = 120; // 120 seconds

	private String cronExpression = "0 */1 * * * ?";

	private Set<Class<? extends Exception>> delayCancelExceptions = new HashSet<Class<? extends Exception>>();

	private String zookeeperNamespace;

	public String getZookeeperNamespace() {
		return zookeeperNamespace;
	}

	public void setZookeeperNamespace(String zookeeperNamespace) {
		this.zookeeperNamespace = zookeeperNamespace;
	}

	public DefaultRecoverConfig() {
		delayCancelExceptions.add(OptimisticLockException.class);
		delayCancelExceptions.add(SocketTimeoutException.class);
	}

	@Override
	public int getMaxRetryCount() {
		return maxRetryCount;
	}

	@Override
	public int getRecoverDuration() {
		return recoverDuration;
	}

	@Override
	public String getCronExpression() {
		return cronExpression;
	}

	public void setMaxRetryCount(int maxRetryCount) {
		this.maxRetryCount = maxRetryCount;
	}

	public void setRecoverDuration(int recoverDuration) {
		this.recoverDuration = recoverDuration;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	@Override
	public void setDelayCancelExceptions(Set<Class<? extends Exception>> delayCancelExceptions) {
		this.delayCancelExceptions.addAll(delayCancelExceptions);
	}

	@Override
	public Set<Class<? extends Exception>> getDelayCancelExceptions() {
		return this.delayCancelExceptions;
	}
}
