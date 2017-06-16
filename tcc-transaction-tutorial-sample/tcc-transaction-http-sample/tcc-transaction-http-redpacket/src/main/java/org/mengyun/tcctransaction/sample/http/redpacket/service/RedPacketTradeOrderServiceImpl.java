package org.mengyun.tcctransaction.sample.http.redpacket.service;

import java.util.Calendar;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.context.MethodTransactionContextEditor;
import org.mengyun.tcctransaction.sample.http.redpacket.api.RedPacketTradeOrderService;
import org.mengyun.tcctransaction.sample.http.redpacket.api.dto.RedPacketTradeOrderDto;
import org.mengyun.tcctransaction.sample.http.redpacket.domain.entity.RedPacketAccount;
import org.mengyun.tcctransaction.sample.http.redpacket.domain.entity.TradeOrder;
import org.mengyun.tcctransaction.sample.http.redpacket.domain.repository.RedPacketAccountRepository;
import org.mengyun.tcctransaction.sample.http.redpacket.domain.repository.TradeOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by changming.xie on 4/2/16.
 */
public class RedPacketTradeOrderServiceImpl implements RedPacketTradeOrderService {
	static final Logger logger = LoggerFactory.getLogger(RedPacketTradeOrderServiceImpl.class);

	@Autowired
	RedPacketAccountRepository redPacketAccountRepository;

	@Autowired
	TradeOrderRepository tradeOrderRepository;

	@Override
	@Compensable(confirmMethod = "confirmRecord", cancelMethod = "cancelRecord", transactionContextEditor = MethodTransactionContextEditor.class)
	@Transactional
	public String record(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {
		logger.info(">> record RedPacketTradeOrderDto try");
		try {
			Thread.sleep(1000l);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		System.out.println("red packet try record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

		TradeOrder tradeOrder = new TradeOrder(tradeOrderDto.getSelfUserId(), tradeOrderDto.getOppositeUserId(), tradeOrderDto.getMerchantOrderNo(), tradeOrderDto.getAmount());

		tradeOrderRepository.insert(tradeOrder);

		RedPacketAccount transferFromAccount = redPacketAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());

		transferFromAccount.transferFrom(tradeOrderDto.getAmount());

		redPacketAccountRepository.save(transferFromAccount);

		return "success";
	}

	@Transactional
	public void confirmRecord(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {
		logger.info(">> confirmRecord RedPacketTradeOrderDto");
		try {
			Thread.sleep(1000l);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		System.out.println("red packet confirm record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

		TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());

		if (null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
			tradeOrder.confirm();
			tradeOrderRepository.update(tradeOrder);

			RedPacketAccount transferToAccount = redPacketAccountRepository.findByUserId(tradeOrderDto.getOppositeUserId());

			transferToAccount.transferTo(tradeOrderDto.getAmount());

			redPacketAccountRepository.save(transferToAccount);
		}
	}

	@Transactional
	public void cancelRecord(TransactionContext transactionContext, RedPacketTradeOrderDto tradeOrderDto) {
		logger.info(">> cancelRecord RedPacketTradeOrderDto");
		try {
			Thread.sleep(1000l);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		System.out.println("red packet cancel record called. time seq:" + DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"));

		TradeOrder tradeOrder = tradeOrderRepository.findByMerchantOrderNo(tradeOrderDto.getMerchantOrderNo());

		if (null != tradeOrder && "DRAFT".equals(tradeOrder.getStatus())) {
			tradeOrder.cancel();
			tradeOrderRepository.update(tradeOrder);

			RedPacketAccount capitalAccount = redPacketAccountRepository.findByUserId(tradeOrderDto.getSelfUserId());

			capitalAccount.cancelTransfer(tradeOrderDto.getAmount());

			redPacketAccountRepository.save(capitalAccount);
		}
	}
}
