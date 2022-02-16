package com.mcserby.playground.javademoplayground.service;


import com.mcserby.playground.javademoplayground.model.PriceChangedEvent;

public interface PriceChangesSubscriber {

    String identifier();
    void onPriceChanged(PriceChangedEvent e);

}
