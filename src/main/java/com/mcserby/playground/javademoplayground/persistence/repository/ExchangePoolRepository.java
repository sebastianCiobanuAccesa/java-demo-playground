package com.mcserby.playground.javademoplayground.persistence.repository;

import com.mcserby.playground.javademoplayground.persistence.model.ExchangePool;
import com.mcserby.playground.javademoplayground.persistence.model.Person;
import org.springframework.data.repository.CrudRepository;

public interface ExchangePoolRepository extends CrudRepository<ExchangePool, Long> {
    
}
