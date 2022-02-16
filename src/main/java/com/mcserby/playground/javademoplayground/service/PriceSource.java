package com.mcserby.playground.javademoplayground.service;

public interface PriceSource {

    void subscribeForPriceChanges(PriceChangesSubscriber subscriber);
    void unSubscribeForPriceChanges(PriceChangesSubscriber subscriber);
}
