package com.mcserby.playground.javademoplayground.persistence.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public class MarketActor {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Column
    private String name;

    @OneToMany(cascade=CascadeType.ALL)
    private List<Wallet> wallets;
}
