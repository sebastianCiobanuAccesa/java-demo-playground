package com.mcserby.playground.javademoplayground.persistence.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "agency")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Agency extends MarketActor {

    @Column
    private String cui;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ExchangePool> exchangePools;

}
