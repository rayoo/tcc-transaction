package com.yonyou.tcctransaction.idempotent;

import javax.transaction.xa.Xid;

import org.mengyun.tcctransaction.api.TransactionContext;

/**
 * @author rayoo
 */
public interface XidRepository {

	public int createXid(TransactionContext tctx, String methodName);

	public int deleteXid(Xid xid, Integer transactionType);

}
