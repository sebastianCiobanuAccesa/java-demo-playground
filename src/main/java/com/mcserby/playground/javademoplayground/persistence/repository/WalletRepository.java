package com.mcserby.playground.javademoplayground.persistence.repository;

import com.mcserby.playground.javademoplayground.persistence.model.Person;
import com.mcserby.playground.javademoplayground.persistence.model.Wallet;
import org.springframework.data.repository.CrudRepository;

public interface WalletRepository extends CrudRepository<Wallet, Long> {


}
