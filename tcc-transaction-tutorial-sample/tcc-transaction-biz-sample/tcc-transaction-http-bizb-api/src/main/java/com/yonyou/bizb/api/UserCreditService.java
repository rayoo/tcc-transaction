package com.yonyou.bizb.api;

import org.mengyun.tcctransaction.api.TransactionContext;

public interface UserCreditService {

	public void delUserCredit(TransactionContext transactionContext, long userId);

	public void confirmDelUserCredit(TransactionContext transactionContext, long userId);

	public void cancelDelUserCredit(TransactionContext transactionContext, long userId);

}
