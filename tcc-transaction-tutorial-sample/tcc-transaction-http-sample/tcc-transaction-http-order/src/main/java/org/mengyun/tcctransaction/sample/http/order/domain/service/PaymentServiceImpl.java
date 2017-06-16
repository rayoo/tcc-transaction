package org.mengyun.tcctransaction.sample.http.order.domain.service;

import java.math.BigDecimal;
import java.util.Calendar;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.sample.http.capital.api.dto.CapitalTradeOrderDto;
import org.mengyun.tcctransaction.sample.http.order.domain.entity.Order;
import org.mengyun.tcctransaction.sample.http.order.domain.repository.OrderRepository;
import org.mengyun.tcctransaction.sample.http.order.infrastructure.serviceproxy.TradeOrderServiceProxy;
import org.mengyun.tcctransaction.sample.http.redpacket.api.dto.RedPacketTradeOrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by changming.xie on 4/1/16.
 */
@Service
public class PaymentServiceImpl {
	static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

	@Autowired
	TradeOrderServiceProxy tradeOrderServiceProxy;

	@Autowired
	OrderRepository orderRepository;

	@Compensable(confirmMethod = "confirmMakePayment", cancelMethod = "cancelMakePayment")
	@Transactional
	public void makePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {
		logger.info(">> makePayment");
		System.out.println("order try make payment called.time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

		order.pay(redPacketPayAmount, capitalPayAmount);
		orderRepository.updateOrder(order);

		String result = tradeOrderServiceProxy.record(null, buildCapitalTradeOrderDto(order));
		String result2 = tradeOrderServiceProxy.record(null, buildRedPacketTradeOrderDto(order));
	}

	public void confirmMakePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {
		logger.info(">> confirmMakePayment");
		try {
			Thread.sleep(1000l);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		System.out.println("order confirm make payment called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

		order.confirm();

		orderRepository.updateOrder(order);
	}

	public void cancelMakePayment(Order order, BigDecimal redPacketPayAmount, BigDecimal capitalPayAmount) {
		logger.info(">> cancelMakePayment");
		try {
			Thread.sleep(1000l);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		System.out.println("order cancel make payment called.time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

		order.cancelPayment();

		orderRepository.updateOrder(order);
	}

	private CapitalTradeOrderDto buildCapitalTradeOrderDto(Order order) {

		CapitalTradeOrderDto tradeOrderDto = new CapitalTradeOrderDto();
		tradeOrderDto.setAmount(order.getCapitalPayAmount());
		tradeOrderDto.setMerchantOrderNo(order.getMerchantOrderNo());
		tradeOrderDto.setSelfUserId(order.getPayerUserId());
		tradeOrderDto.setOppositeUserId(order.getPayeeUserId());
		tradeOrderDto.setOrderTitle(String.format("order no:%s", order.getMerchantOrderNo()));

		return tradeOrderDto;
	}

	private RedPacketTradeOrderDto buildRedPacketTradeOrderDto(Order order) {
		RedPacketTradeOrderDto tradeOrderDto = new RedPacketTradeOrderDto();
		tradeOrderDto.setAmount(order.getRedPacketPayAmount());
		tradeOrderDto.setMerchantOrderNo(order.getMerchantOrderNo());
		tradeOrderDto.setSelfUserId(order.getPayerUserId());
		tradeOrderDto.setOppositeUserId(order.getPayeeUserId());
		tradeOrderDto.setOrderTitle(String.format("order no:%s", order.getMerchantOrderNo()));

		return tradeOrderDto;
	}
}
