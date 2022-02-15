package com.mcserby.playground.javademoplayground.service;

import com.mcserby.playground.javademoplayground.dto.ExchangeRequest;
import com.mcserby.playground.javademoplayground.dto.ExchangeResult;
import com.mcserby.playground.javademoplayground.persistence.model.*;
import com.mcserby.playground.javademoplayground.persistence.repository.AgencyRepository;
import com.mcserby.playground.javademoplayground.persistence.repository.ExchangePoolRepository;
import com.mcserby.playground.javademoplayground.persistence.repository.PersonRepository;
import com.mcserby.playground.javademoplayground.persistence.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Dex {

    private final Logger logger = LoggerFactory.getLogger(Dex.class);

    private final AgencyRepository agencyRepository;
    private final PersonRepository personRepository;
    private final WalletRepository walletRepository;
    private final ExchangePoolRepository exchangePoolRepository;

    public Dex(AgencyRepository agencyRepository,
               PersonRepository personRepository,
               WalletRepository walletRepository,
               ExchangePoolRepository exchangePoolRepository) {
        this.agencyRepository = agencyRepository;
        this.personRepository = personRepository;
        this.walletRepository = walletRepository;
        this.exchangePoolRepository = exchangePoolRepository;
    }

    @Transactional
    public ExchangeResult swap(ExchangeRequest request) {
        try {
            logger.info("swap request: " + request);
            Agency agency = this.agencyRepository.findById(request.getAgencyId())
                    .orElseThrow(() -> new RuntimeException("Agency not found"));

            personRepository.findById(request.getPersonId())
                    .orElseThrow(() -> new RuntimeException("Person not found"));

            Wallet wallet = walletRepository.findById(request.getWalletId())
                    .orElseThrow(() -> new RuntimeException("Wallet not found"));

            Double exchangedValue = request.getFrom().getValue();
            // validate liquidity
            List<Liquidity> userLiquidities = new ArrayList<>(wallet.getLiquidityList());
            if (userLiquidities.stream().noneMatch(
                    l -> l.getTicker().equals(request.getFrom().getTicker())
                            && l.getValue() > exchangedValue)) {
                throw new RuntimeException("Insufficient funds");
            }


            ExchangePool exchangePool = agency.getExchangePools().stream()
                    .filter(ep -> (ep.getLiquidityOne().getTicker().equals(request.getFrom().getTicker())
                            && ep.getLiquidityTwo().getTicker().equals(request.getTo().getTicker()))
                            || (ep.getLiquidityTwo().getTicker().equals(request.getFrom().getTicker())
                            && ep.getLiquidityOne().getTicker().equals(request.getTo().getTicker()))).findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Liquidity pool not found for this token pair: " + request.getFrom().getTicker() + "/" +
                                    request.getTo().getTicker()));

            Liquidity liquidityFrom =
                    userLiquidities.stream().filter(l -> l.getTicker().equals(request.getFrom().getTicker())).findFirst()
                            .get();
            Liquidity liquidityTo =
                    userLiquidities.stream().filter(l -> l.getTicker().equals(request.getTo().getTicker())).findFirst().orElse(
                            Liquidity.builder().ticker(request.getTo().getTicker()).value(0.0).build());
            logger.info("initial balances for wallet " + wallet.getId() +  ":\nfrom: " + liquidityFrom + "\nto:" + liquidityTo);
            if (userLiquidities.stream().noneMatch(l -> l.getTicker().equals(request.getTo().getTicker()))){
                userLiquidities.add(liquidityTo);
            }

            double x = exchangePool.getLiquidityOne().getValue();
            double y = exchangePool.getLiquidityTwo().getValue();
            double k = x * y;

            double delta = exchangedValue;
            double returnedValue = 0;

            if (request.getFrom().getTicker().equals(exchangePool.getLiquidityOne().getTicker())) {
                returnedValue = y - k / (x + delta);
                double newX = x + delta;
                double newY = y - returnedValue;
                exchangePool.getLiquidityOne().setValue(newX);
                exchangePool.getLiquidityTwo().setValue(newY);
            } else {
                returnedValue = x - k / (y + delta);
                double newX = x - returnedValue;
                double newY = y + delta;
                exchangePool.getLiquidityOne().setValue(newX);
                exchangePool.getLiquidityTwo().setValue(newY);
            }
            liquidityFrom.setValue(liquidityFrom.getValue() - delta);
            liquidityTo.setValue(liquidityTo.getValue() + returnedValue);
            wallet.setLiquidityList(userLiquidities);

            this.exchangePoolRepository.save(exchangePool);
            this.walletRepository.save(wallet);

            logger.info("after swap balances for wallet " + wallet.getId() +  ":\nfrom: " + liquidityFrom + "\nto:" + liquidityTo);

            return ExchangeResult.builder().message(
                    "new wallet balances for exchanged liquidity:\nfrom: " + liquidityFrom + "\nto: " + liquidityTo)
                    .successful(true)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ExchangeResult.builder().message("Could not perform swap. Reason: " + e.getMessage())
                    .successful(false)
                    .build();
        }

    }

}
