package com.yonyou.biza.api;

import org.mengyun.tcctransaction.api.TransactionContext;

public interface UserHonourService {

	public void delUserHonour(TransactionContext transactionContext, long userId);

	public void confirmDelUserHonour(TransactionContext transactionContext, long userId);

	public void cancelDelUserHonour(TransactionContext transactionContext, long userId);

}
