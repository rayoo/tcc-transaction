package org.mengyun.tcctransaction.api;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by changmingxie on 10/30/15.
 */
public class TransactionContext implements Serializable {

	private static final long serialVersionUID = -8199390103169700387L;
	private TransactionXid xid;

	private int status;

	private Map<String, String> attachments = new ConcurrentHashMap<String, String>();

	public TransactionContext() {

	}

	public TransactionContext(TransactionXid xid, int status) {
		this.xid = xid;
		this.status = status;
	}

	public void setXid(TransactionXid xid) {
		this.xid = xid;
	}

	public TransactionXid getXid() {
		return xid.clone();
	}

	public void setAttachments(Map<String, String> attachments) {
		this.attachments = attachments;
	}

	public Map<String, String> getAttachments() {
		return attachments;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder("Xid:[").append(null != getXid() ? getXid().toString() : "").append("], status:").append(getStatus()).append(", attachement:[");
		if (null != getAttachments() && !getAttachments().isEmpty()) {
			for (Entry<String, String> entry : getAttachments().entrySet()) {
				ret.append(entry.getKey()).append(":").append(entry.getValue());
			}
		}
		return ret.append("]").toString();
	}

}
