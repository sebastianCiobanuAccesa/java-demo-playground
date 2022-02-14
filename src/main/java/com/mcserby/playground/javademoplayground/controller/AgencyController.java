package com.mcserby.playground.javademoplayground.controller;

import com.mcserby.playground.javademoplayground.dto.Agency;
import com.mcserby.playground.javademoplayground.mapper.DtoToEntityMapper;
import com.mcserby.playground.javademoplayground.persistence.repository.AgencyRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class AgencyController {

    private final AgencyRepository repository;

    AgencyController(AgencyRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/agencies")
    List<Agency> all() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(DtoToEntityMapper::map)
                .collect(Collectors.toList());
    }

    @PostMapping("/agency")
    Agency newAgency(@RequestBody Agency agency) {
        return DtoToEntityMapper.map(repository.save(DtoToEntityMapper.map(agency)));
    }

    @GetMapping("/agency/{id}")
    Agency getOne(@PathVariable Long id) {
        return repository.findById(id).map(DtoToEntityMapper::map)
                .orElseThrow(() -> new RuntimeException("not found"));
    }

    @PutMapping("/agency")
    Agency replace(@RequestBody Agency agency) {
        return DtoToEntityMapper.map(repository.save(DtoToEntityMapper.map(agency)));

    }

    @DeleteMapping("/agencies/{id}")
    void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
