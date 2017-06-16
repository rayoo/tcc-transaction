package org.mengyun.tcctransaction.repository.helper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.Logger;

public class ZooKeeperHelper {
	static final Logger logger = Logger.getLogger(ZooKeeperHelper.class);

	public static CuratorFramework zkClient = null;

	public static CuratorFramework getZKClient(String zkConnectStr, String zkNameSpace, boolean readOnly) {
		if (null == zkClient) {
			synchronized (ZooKeeperHelper.class) {
				if (null == zkClient) {
					logger.info("initialize zookeeper client.");
					RetryPolicy retryPolicy = new ExponentialBackoffRetry(10, Integer.MAX_VALUE);
					zkClient = CuratorFrameworkFactory.builder().connectString(zkConnectStr).sessionTimeoutMs(30 * 1000).connectionTimeoutMs(10 * 1000).canBeReadOnly(readOnly)
							.retryPolicy(retryPolicy).namespace(zkNameSpace).defaultData(null).build();
					zkClient.start();
				}
			}
		}
		return zkClient;
	}

}
