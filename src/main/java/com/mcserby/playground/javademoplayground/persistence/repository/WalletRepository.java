package com.mcserby.playground.javademoplayground.persistence.repository;

import com.mcserby.playground.javademoplayground.persistence.model.Person;
import com.mcserby.playground.javademoplayground.persistence.model.Wallet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WalletRepository extends CrudRepository<Wallet, Long> {

    @Query("SELECT w FROM Wallet w LEFT JOIN FETCH w.liquidityList where w.person.id = :id")
    List<Wallet> findWalletsByPersonId(Long id);

}
