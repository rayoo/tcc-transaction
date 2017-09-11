package com.yonyou.bizb.api;

import org.mengyun.tcctransaction.api.TransactionContext;

import com.yonyou.cloud.middleware.rpc.RemoteCall;

@RemoteCall("tcc-transaction-eureka-bizb@c87e2267-1001-4c70-bb2a-ab41f3b81aa3")
public interface UserCreditService {

	public void delUserCredit(TransactionContext transactionContext, long userId);

	public void confirmDelUserCredit(TransactionContext transactionContext, long userId);

	public void cancelDelUserCredit(TransactionContext transactionContext, long userId);

}
