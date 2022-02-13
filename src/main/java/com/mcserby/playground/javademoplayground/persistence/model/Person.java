package com.mcserby.playground.javademoplayground.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@Table(name = "person")
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class Person extends MarketActor {

    @Column
    private String address;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

}
