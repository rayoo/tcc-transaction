package org.mengyun.tcctransaction.sample.dubbo.capital.service;

import java.math.BigDecimal;

import org.mengyun.tcctransaction.sample.dubbo.capital.api.CapitalAccountService;
import org.mengyun.tcctransaction.sample.dubbo.capital.domain.repository.CapitalAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by twinkle.zhou on 16/11/11.
 */
@Service("capitalAccountService")
public class CapitalAccountServiceImpl implements CapitalAccountService{


    @Autowired
    CapitalAccountRepository capitalAccountRepository;

    @Override
    public BigDecimal getCapitalAccountByUserId(long userId) {
        return capitalAccountRepository.findByUserId(userId).getBalanceAmount();
    }
}
