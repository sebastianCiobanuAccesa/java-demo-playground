package com.mcserby.playground.javademoplayground.persistence.repository;

import com.mcserby.playground.javademoplayground.persistence.model.Currency;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyRepository extends CrudRepository<Currency, Long> {
    
}
