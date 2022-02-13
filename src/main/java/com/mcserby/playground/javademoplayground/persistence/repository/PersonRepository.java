package com.mcserby.playground.javademoplayground.persistence.repository;

import com.mcserby.playground.javademoplayground.persistence.model.Person;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person, Long> {

}
