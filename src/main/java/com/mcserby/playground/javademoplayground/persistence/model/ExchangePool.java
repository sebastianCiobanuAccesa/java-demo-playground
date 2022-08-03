package com.mcserby.playground.javademoplayground.persistence.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@Table(name = "exchange_pool")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangePool {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;


    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "liquidity_one_name")),
            @AttributeOverride(name = "ticker", column = @Column(name = "liquidity_one_ticker")),
            @AttributeOverride(name = "value", column = @Column(name = "liquidity_one_value")),
    })
    @Embedded
    private Liquidity liquidityOne;

    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "liquidity_two_name")),
            @AttributeOverride(name = "ticker", column = @Column(name = "liquidity_two_ticker")),
            @AttributeOverride(name = "value", column = @Column(name = "liquidity_two_value")),
    })
    @Embedded
    private Liquidity liquidityTwo;

    // CHANGE: if not set to depend only on tickers, jgrapht no such edge in graph!!!
//    java.lang.IllegalArgumentException: no such edge in graph: ExchangePool(id=1, agency=null, liquidityOne=Liquidity(value=1005000.0, name=Dollar, ticker=USD), liquidityTwo=Liquidity(value=99502.48756218905, name=Bitcoin, ticker=BTC))

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangePool that = (ExchangePool) o;
        return Objects.equals(getLiquidityOne().getTicker(), that.getLiquidityOne().getTicker()) && Objects.equals(getLiquidityTwo().getTicker(), that.getLiquidityTwo().getTicker());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLiquidityOne().getTicker(), getLiquidityTwo().getTicker());
    }
}
