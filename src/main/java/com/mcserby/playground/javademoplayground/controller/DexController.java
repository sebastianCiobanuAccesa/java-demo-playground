package com.mcserby.playground.javademoplayground.controller;

import com.mcserby.playground.javademoplayground.dto.ExchangeRequest;
import com.mcserby.playground.javademoplayground.dto.ExchangeResult;
import com.mcserby.playground.javademoplayground.dto.SwapPrice;
import com.mcserby.playground.javademoplayground.monitoring.TrackExecutionTime;
import com.mcserby.playground.javademoplayground.service.Dex;
import com.mcserby.playground.javademoplayground.service.PriceArchive;
import com.mcserby.playground.javademoplayground.service.PriceOracle;
import com.mcserby.playground.javademoplayground.service.SwapSimulator;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DexController {

    private final Dex dex;
    private final SwapSimulator swapSimulator;
    private final PriceOracle priceOracle;
    private final PriceArchive priceArchive;

    DexController(Dex dex, SwapSimulator swapSimulator,
                  PriceOracle priceOracle, PriceArchive priceArchive) {
        this.dex = dex;
        this.swapSimulator = swapSimulator;
        this.priceOracle = priceOracle;
        this.priceArchive = priceArchive;
    }

    @PostMapping("/swap")
    @TrackExecutionTime
    ExchangeResult swap(@RequestBody ExchangeRequest request) {
        return dex.swap(request);
    }

    @PutMapping("/startSwapSimulator")
    @TrackExecutionTime
    String startSwapSimulator(@RequestParam int frequency) {
        swapSimulator.startSwap(frequency);
        return "swap simulator started";
    }

    @PutMapping("/stopSwapSimulator")
    @TrackExecutionTime
    String stopSwapSimulator() {
        swapSimulator.stopSwap();
        return "swap simulator stopped";
    }

    @GetMapping("/price")
    @TrackExecutionTime
    Double stopSwapSimulator(@RequestParam String ticker, @RequestParam String ref) {
        return priceOracle.getEstimatedPrice(ticker, ref);
    }

    @GetMapping("/history")
    @TrackExecutionTime
    List<SwapPrice> getArchiveFor(@RequestParam long agencyId, @RequestParam String from, @RequestParam String to){
        return priceArchive.getArchiveFor(agencyId, from, to);
    }

}
