package com.mcserby.playground.javademoplayground.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeRequest {

    private long personId;
    private long walletId;
    private long agencyId;
    private Liquidity from;
    private Currency to;

}
