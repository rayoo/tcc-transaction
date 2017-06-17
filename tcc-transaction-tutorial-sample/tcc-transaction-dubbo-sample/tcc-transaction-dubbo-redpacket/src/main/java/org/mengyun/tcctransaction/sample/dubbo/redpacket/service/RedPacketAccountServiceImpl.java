package org.mengyun.tcctransaction.sample.dubbo.redpacket.service;

import java.math.BigDecimal;

import org.mengyun.tcctransaction.sample.dubbo.redpacket.api.RedPacketAccountService;
import org.mengyun.tcctransaction.sample.dubbo.redpacket.domain.repository.RedPacketAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by twinkle.zhou on 16/11/11.
 */
@Service("redPacketAccountService")
public class RedPacketAccountServiceImpl implements RedPacketAccountService {

    @Autowired
    RedPacketAccountRepository redPacketAccountRepository;

    @Override
    public BigDecimal getRedPacketAccountByUserId(long userId) {
        return redPacketAccountRepository.findByUserId(userId).getBalanceAmount();
    }
}
