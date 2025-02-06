package com.example.ParsingCodeProject.repository;

import com.example.ParsingCodeProject.entity.SwiftCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SwiftCodeRepository extends JpaRepository<SwiftCode, String> {
    Optional<SwiftCode> findByCode(String code);
}
