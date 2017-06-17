package org.mengyun.tcctransaction.serializer;

import org.mengyun.tcctransaction.InvocationContext;
import org.mengyun.tcctransaction.Participant;
import org.mengyun.tcctransaction.Terminator;
import org.mengyun.tcctransaction.Transaction;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.api.TransactionXid;
import org.mengyun.tcctransaction.common.TransactionType;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

/**
 * Created by changming.xie on 7/22/16. <br>
 * Updated by rayoo - Kryo实例非线程安全,且创建实例耗费时间、资源很多, 修改为ThreadLocal方式或对象池方式避免. <br>
 * 参见：<a href="https://github.com/EsotericSoftware/kryo">Kryo官网</a>
 */
public class KryoTransactionSerializer implements ObjectSerializer<Transaction> {

	// ThreadLocal方式 和 对象池方式任意一种都可以避免Kryo实例非线程安全问题
	// // Setup ThreadLocal of Kryo instances
	// private static final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
	// protected Kryo initialValue() {
	// Kryo kryo = new Kryo();
	// // configure kryo instance, customize settings
	// return kryo;
	// };
	// };
	//
	// // 使用：Somewhere else, use Kryo
	// Kryo k = kryos.get();

	private static KryoPool kryoPool = null;

	static {
		KryoFactory factory = new KryoFactory() {
			public Kryo create() {
				// configure kryo instance, customize settings
				Kryo kryo = new Kryo();

				kryo.register(Transaction.class);
				kryo.register(TransactionXid.class);
				kryo.register(TransactionStatus.class);
				kryo.register(TransactionType.class);
				kryo.register(Participant.class);
				kryo.register(Terminator.class);
				kryo.register(InvocationContext.class);

				return kryo;
			}
		};
		kryoPool = new KryoPool.Builder(factory).softReferences().build();
	}

	@Override
	public byte[] serialize(final Transaction transaction) {
		return kryoPool.run(new KryoCallback<byte[]>() {
			@Override
			public byte[] execute(Kryo kryo) {
				Output output = new Output(256, -1);
				kryo.writeObject(output, transaction);
				return output.toBytes();
			}
		});
	}

	@Override
	public Transaction deserialize(final byte[] bytes) {
		return kryoPool.run(new KryoCallback<Transaction>() {
			@Override
			public Transaction execute(Kryo kryo) {
				Input input = new Input(bytes);
				Transaction transaction = kryo.readObject(input, Transaction.class);
				return transaction;
			}
		});
	}
}
