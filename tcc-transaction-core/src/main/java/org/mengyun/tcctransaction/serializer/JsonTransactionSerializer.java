package org.mengyun.tcctransaction.serializer;

import org.mengyun.tcctransaction.Transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonTransactionSerializer implements ObjectSerializer<Transaction> {

	private static ObjectMapper OBJECT_MAPPER;

	static {
		OBJECT_MAPPER = new ObjectMapper();

		OBJECT_MAPPER.disable(SerializationFeature.INDENT_OUTPUT);
		OBJECT_MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public byte[] serialize(Transaction t) {
		try {
			return OBJECT_MAPPER.writeValueAsBytes(t);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Transaction deserialize(byte[] bytes) {
		try {
			return OBJECT_MAPPER.readValue(bytes, Transaction.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
