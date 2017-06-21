package org.mengyun.tcctransaction;

import org.mengyun.tcctransaction.api.TransactionContext;

/**
 * @author rayoo
 */
public interface XidRepository {

	public int createXid(TransactionContext tctx, String methodName);

	public int deleteXid(Transaction transaction);

}
