package com.yonyou.biza.api;

import org.mengyun.tcctransaction.api.TransactionContext;

import com.yonyou.cloud.middleware.rpc.RemoteCall;

@RemoteCall("tcc-transaction-eureka-biza@c87e2267-1001-4c70-bb2a-ab41f3b81aa3")
public interface UserHonourService {

	public void delUserHonour(TransactionContext transactionContext, long userId);

	// public void confirmDelUserHonour(TransactionContext transactionContext, long userId);
	//
	// public void cancelDelUserHonour(TransactionContext transactionContext, long userId);

}
