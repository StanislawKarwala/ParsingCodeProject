package com.example.ParsingCodeProject.repository;

import com.example.ParsingCodeProject.entity.Headquarter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HeadquarterRepository extends JpaRepository<Headquarter, String> {
    Optional<Headquarter> findByCode(String code);
    List<Headquarter> findByCountryISO2(String countryISO2);
}
