package com.example.ParsingCodeProject.controller;

import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.repository.SwiftCodeRepository;
import com.example.ParsingCodeProject.service.SwiftCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SwiftCodeControllerIntegrationTest {
    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @Autowired
    private SwiftCodeService swiftCodeService;

    @Autowired
    private SwiftCodeController swiftCodeController;

    @BeforeEach
    void setUp() {
        swiftCodeRepository.deleteAll();
    }

    @Test
    void getSwiftCodeDetails_Headquarter() {
        SwiftCode swiftCode = new SwiftCode("HQ123456789", "HQ Address", "HQ Bank", "PL", "Poland");
        swiftCode.setHeadquarterFlag(true);
        swiftCodeRepository.save(swiftCode);

        ResponseEntity<?> response = swiftCodeController.getSwiftCodeDetails("HQ123456789");
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getSwiftCodeDetails_Branch() {
        SwiftCode swiftCode = new SwiftCode("BR123456789", "Branch Address", "Branch Bank", "PL", "Poland");
        swiftCode.setHeadquarterFlag(false);
        swiftCodeRepository.save(swiftCode);

        ResponseEntity<?> response = swiftCodeController.getSwiftCodeDetails("BR123456789");
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void addSwiftCode_IntegrationTest() {
        SwiftCode swiftCode = new SwiftCode("TEST9999999", "Test Address", "Test Bank", "US", "United States");
        ResponseEntity<Map<String, String>> response = swiftCodeController.addSwiftCode(swiftCode);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteBySwiftCode_IntegrationTest() {
        SwiftCode swiftCode = new SwiftCode("DEL9999999", "Del Address", "Del Bank", "FR", "France");
        swiftCodeRepository.save(swiftCode);

        ResponseEntity<Map<String, String>> response = swiftCodeController.deleteBySwiftCode("DEL9999999");
        assertEquals(200, response.getStatusCode().value());
    }
}
