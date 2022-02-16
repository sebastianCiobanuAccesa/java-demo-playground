package com.mcserby.playground.javademoplayground.service;

import com.mcserby.playground.javademoplayground.model.PriceChangedEvent;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PriceOracle implements PriceChangesSubscriber {

    private final ConcurrentHashMap<String, java.lang.Double> priceRepo;
    private final String identifier;

    private final PriceSource priceSource;

    public PriceOracle(PriceSource priceSource) {
        this.priceSource = priceSource;
        this.priceSource.subscribeForPriceChanges(this);
        this.priceRepo = new ConcurrentHashMap<>();
        this.identifier = UUID.randomUUID().toString();
    }

    @PreDestroy
    public void destroy() {
        this.priceSource.unSubscribeForPriceChanges(this);
    }

    public Double getEstimatedPrice(String tickerInQuestion, String referenceTicker) {
        return priceRepo.getOrDefault(tickerInQuestion + "_" + referenceTicker,
                Optional.ofNullable(priceRepo.get(referenceTicker + "_" + tickerInQuestion)).map(p -> 1 / p)
                        .orElse(null));
    }

    @Override
    public String identifier() {
        return this.identifier;
    }

    @Override
    public void onPriceChanged(PriceChangedEvent e) {
        priceRepo.put(e.getTickerFrom() + "_" + e.getTickerTo(), e.getPrice());
    }
}
