package com.example.ParsingCodeProject.repository;

import com.example.ParsingCodeProject.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, String> {
    Optional<Branch> findByCode(String code);
    List<Branch> findAllByHeadquarterIsNull();
    List<Branch> findByCountryISO2(String countryISO2);

}
