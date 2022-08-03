package com.mcserby.playground.javademoplayground.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Liquidity implements Serializable{

    @Column
    private Double value;

    @Column
    private String name;

    @Column
    private String ticker;

}
