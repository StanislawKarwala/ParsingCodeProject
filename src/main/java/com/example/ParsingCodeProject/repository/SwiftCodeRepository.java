package com.example.ParsingCodeProject.repository;

import com.example.ParsingCodeProject.entity.SwiftCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwiftCodeRepository extends JpaRepository<SwiftCode, String> {
}
