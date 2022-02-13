package com.mcserby.playground.javademoplayground.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Map;

@Data
@Entity
@Table(name = "wallet")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Column
    private String name;

    @ElementCollection
    private Map<Currency, Double> currencies;

}
