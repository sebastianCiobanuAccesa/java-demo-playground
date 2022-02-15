package com.mcserby.playground.javademoplayground.service;

import com.mcserby.playground.javademoplayground.dto.Currency;
import com.mcserby.playground.javademoplayground.dto.ExchangeRequest;
import com.mcserby.playground.javademoplayground.mapper.DtoToEntityMapper;
import com.mcserby.playground.javademoplayground.persistence.model.*;
import com.mcserby.playground.javademoplayground.persistence.repository.AgencyRepository;
import com.mcserby.playground.javademoplayground.persistence.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SwapSimulator {

    private final Logger logger = LoggerFactory.getLogger(SwapSimulator.class);

    private final Dex dex;
    private final PersonRepository personRepository;
    private final AgencyRepository agencyRepository;

    private final ScheduledExecutorService executor;

    ScheduledFuture<?> scheduledFuture;

    public SwapSimulator(Dex dex,
                         PersonRepository personRepository,
                         AgencyRepository agencyRepository) {
        this.dex = dex;
        this.personRepository = personRepository;
        this.agencyRepository = agencyRepository;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    @PreDestroy
    public void destroy() {
        executor.shutdown();
    }

    public void startSwap(int swapFrequency) {
        logger.info("starting swap simulator...");
        stopSwap();
        scheduledFuture = executor.scheduleAtFixedRate(this::simulateSwapTask, 0, swapFrequency, TimeUnit.MILLISECONDS);
    }

    public void stopSwap() {
        logger.info("stopping swap simulator...");
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }
    }

    private void simulateSwapTask() {
        Random random = new Random();
        Person p = getRandomPerson();
        if(p.getWallets().isEmpty()){
            logger.info("person " + p.getName() + " does not have any wallet. Cannot simulate swap.");
            return;
        }
        Wallet w = getRandomWallet(random, p.getWallets());
        Agency a = getRandomAgency();
        Liquidity liquidityFrom = w.getLiquidityList().get(random.nextInt(w.getLiquidityList().size()));
        Optional<String> maybeTo = getRandomCurrencyInLiquidityPools(a.getExchangePools(), liquidityFrom.getTicker());
        if (maybeTo.isEmpty()) {
            logger.info("could not find a suitable liquidity pool in agency " + a.getName() +
                    " for randomly selected liquidity " + liquidityFrom.getTicker() + " from user wallet " +
                    w.getName());
            return;
        }

        com.mcserby.playground.javademoplayground.dto.Liquidity exchangedLiquidity =
                com.mcserby.playground.javademoplayground.dto.Liquidity.builder()
                        .name(liquidityFrom.getName())
                        .ticker(liquidityFrom.getTicker())
                        .value(random.nextDouble() * liquidityFrom.getValue())
                        .build();

        ExchangeRequest request = ExchangeRequest.builder()
                .personId(p.getId())
                .walletId(w.getId())
                .agencyId(a.getId())
                .from(exchangedLiquidity)
                .to(Currency.builder().ticker(maybeTo.get()).build())
                .build();
        this.dex.swap(request);
    }

    private Optional<String> getRandomCurrencyInLiquidityPools(List<ExchangePool> exchangePools, String tickerFrom) {
        List<ExchangePool> eligibleExchangePools =
                exchangePools.stream().filter(ep -> ep.getLiquidityOne().getTicker().equals(tickerFrom) ||
                        ep.getLiquidityTwo().getTicker().equals(tickerFrom)).collect(
                        Collectors.toList());
        if (eligibleExchangePools.isEmpty()) {
            return Optional.empty();
        }
        Collections.shuffle(eligibleExchangePools);
        ExchangePool exchangePool = eligibleExchangePools.get(0);
        return List.of(exchangePool.getLiquidityTwo().getTicker(), exchangePool.getLiquidityOne().getTicker()).stream()
                .filter(t -> !t.equals(tickerFrom)).findFirst();
    }

    private Agency getRandomAgency() {
        long qty = agencyRepository.count();
        int idx = (int) (Math.random() * qty);
        Page<Agency> questionPage = agencyRepository.findAll(PageRequest.of(idx, 1));
        if (!questionPage.hasContent()) {
            throw new RuntimeException("could not fetch random person from DB");
        }
        return questionPage.getContent().get(0);
    }

    private Wallet getRandomWallet(Random random, List<Wallet> wallets) {
        return wallets.get(random.nextInt(wallets.size()));
    }

    private Person getRandomPerson() {
        long qty = personRepository.count();
        int idx = (int) (Math.random() * qty);
        Page<Person> questionPage = personRepository.findAll(PageRequest.of(idx, 1));
        if (!questionPage.hasContent()) {
            throw new RuntimeException("could not fetch random person from DB");
        }
        return questionPage.getContent().get(0);
    }
}
