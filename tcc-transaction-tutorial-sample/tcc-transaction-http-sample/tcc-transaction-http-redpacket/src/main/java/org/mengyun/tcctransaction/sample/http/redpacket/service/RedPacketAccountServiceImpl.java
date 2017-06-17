package org.mengyun.tcctransaction.sample.http.redpacket.service;

import java.math.BigDecimal;

import org.mengyun.tcctransaction.sample.http.redpacket.api.RedPacketAccountService;
import org.mengyun.tcctransaction.sample.http.redpacket.domain.repository.RedPacketAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by twinkle.zhou on 16/11/11.
 */
public class RedPacketAccountServiceImpl implements RedPacketAccountService {

    @Autowired
    RedPacketAccountRepository redPacketAccountRepository;

    @Override
    public BigDecimal getRedPacketAccountByUserId(long userId) {
        return redPacketAccountRepository.findByUserId(userId).getBalanceAmount();
    }
}
