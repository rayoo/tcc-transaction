package org.mengyun.tcctransaction.sample.http.capital.service;

import java.math.BigDecimal;

import org.mengyun.tcctransaction.sample.http.capital.api.CapitalAccountService;
import org.mengyun.tcctransaction.sample.http.capital.domain.repository.CapitalAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by twinkle.zhou on 16/11/11.
 */
public class CapitalAccountServiceImpl implements CapitalAccountService{


    @Autowired
    CapitalAccountRepository capitalAccountRepository;

    @Override
    public BigDecimal getCapitalAccountByUserId(long userId) {
        return capitalAccountRepository.findByUserId(userId).getBalanceAmount();
    }
}
