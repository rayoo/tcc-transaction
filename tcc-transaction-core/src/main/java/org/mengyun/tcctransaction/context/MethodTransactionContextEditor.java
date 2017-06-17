package org.mengyun.tcctransaction.context;

import java.lang.reflect.Method;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionContextEditor;
import org.mengyun.tcctransaction.utils.CompensableMethodUtils;

/**
 * Created by changming.xie on 1/18/17.
 */
public class MethodTransactionContextEditor implements TransactionContextEditor {

    @Override
    public TransactionContext get(Object target, Method method, Object[] args) {
        int position = CompensableMethodUtils.getTransactionContextParamPosition(method.getParameterTypes());

        if (position >= 0) {
            return (TransactionContext) args[position];
        }
        
        return null;
    }

    @Override
    public void set(TransactionContext transactionContext, Object target, Method method, Object[] args) {

        int position = CompensableMethodUtils.getTransactionContextParamPosition(method.getParameterTypes());
        if (position >= 0) {
            args[position] = transactionContext;
        }
    }
}
