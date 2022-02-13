package com.mcserby.playground.javademoplayground.persistence.repository;

import com.mcserby.playground.javademoplayground.persistence.model.Agency;
import com.mcserby.playground.javademoplayground.persistence.model.Person;
import org.springframework.data.repository.CrudRepository;

public interface AgencyRepository extends CrudRepository<Agency, Long> {
    
}
