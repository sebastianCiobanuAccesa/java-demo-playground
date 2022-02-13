package com.mcserby.playground.javademoplayground.persistence.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "exchange_pool")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangePool {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="agency_id", nullable=false)
    private Agency agency;

    @OneToOne
    private Currency currencyOne;

    @Column
    private Double tokenOneValue;

    @OneToOne
    private Currency currencyTwo;

    @Column
    private Double tokenTwoValue;

}
