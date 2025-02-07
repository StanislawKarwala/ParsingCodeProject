package com.example.ParsingCodeProject.service;

import com.example.ParsingCodeProject.dto.CountryResponse;
import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.repository.SwiftCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SwiftCodeServiceIntegrationTest {

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @Autowired
    private SwiftCodeService swiftCodeService;

    @Test
    void getSwiftCodesByCountry_Success() {
        Optional<CountryResponse> response = swiftCodeService.getSwiftCodesByCountry("PL");

        assertTrue(response.isPresent());
        assertEquals("PL", response.get().getCountryISO2());
    }

    @Test
    void getSwiftCodesByCountry_NotFound() {
        Optional<CountryResponse> response = swiftCodeService.getSwiftCodesByCountry("XX");
        assertTrue(response.isEmpty());
    }

    @Test
    void getSwiftCodesByCountry_InvalidCountryCode() {
        Optional<CountryResponse> response = swiftCodeService.getSwiftCodesByCountry("123");
        assertTrue(response.isEmpty());
    }

    @Test
    void deleteBySwiftCode_Success() {
        boolean result = swiftCodeService.deleteBySwiftCode("BREXPLPWXXX");

        assertTrue(result);
        assertFalse(swiftCodeRepository.findByCode("BREXPLPWXXX").isPresent());
    }

    @Test
    void deleteBySwiftCode_NotFound() {
        boolean result = swiftCodeService.deleteBySwiftCode("UNKNOWN");
        assertFalse(result);
    }

    @Test
    void saveSwiftCodesData_Success() {
        SwiftCode swiftCode = new SwiftCode("TEST1ESTXXX", "Warsaw", "BRE Bank", "PL", "Poland");
        swiftCodeService.saveSwiftCodesData(swiftCode);
        assertTrue(swiftCodeRepository.findByCode("TEST1ESTXXX").isPresent());
    }

    @Test
    void validateAndSaveSwiftCode_Success() {
        SwiftCode swiftCode = new SwiftCode("TSETTESTXXX", "Warsaw", "BRE Bank", "PL", "Poland");
        swiftCodeService.validateAndSaveSwiftCode(swiftCode);
        assertTrue(swiftCodeRepository.findById("TSETTESTXXX").isPresent());
    }

    @Test
    void validateAndSaveSwiftCode_DuplicateSwiftCode() {
        SwiftCode swiftCode = new SwiftCode("TESTTESTXXX", "Warsaw", "BRE Bank", "PL", "Poland");
        swiftCodeService.validateAndSaveSwiftCode(swiftCode);
        assertThrows(IllegalArgumentException.class, () -> swiftCodeService.validateAndSaveSwiftCode(swiftCode));
    }

    @Test
    void validateAndSaveSwiftCode_InvalidCountryISO2() {
        SwiftCode swiftCode = new SwiftCode("BREXPLPWXXX", "Warsaw", "BRE Bank", "P1", "Poland");
        assertThrows(IllegalArgumentException.class, () -> swiftCodeService.validateAndSaveSwiftCode(swiftCode));
    }

    @Test
    void validateAndSaveSwiftCode_InvalidSwiftCodeLength() {
        SwiftCode swiftCode = new SwiftCode("BREXPLPWXX", "Warsaw", "BRE Bank", "PL", "Poland");
        assertThrows(IllegalArgumentException.class, () -> swiftCodeService.validateAndSaveSwiftCode(swiftCode));
    }

    @Test
    void getSwiftCodeByCode_Success() {
        SwiftCode swiftCode = new SwiftCode("BREXPLPWXXX", "Warsaw", "BRE Bank", "PL", "Poland");
        swiftCodeRepository.save(swiftCode);

        Optional<SwiftCode> result = swiftCodeService.getSwiftCodeByCode("BREXPLPWXXX");

        assertTrue(result.isPresent());
        assertEquals("BREXPLPWXXX", result.get().getCode());
    }

    @Test
    void getSwiftCodeByCode_NotFound() {
        Optional<SwiftCode> result = swiftCodeService.getSwiftCodeByCode("UNKNOWN");

        assertTrue(result.isEmpty());
    }

    @Test
    void getBranchesByHeadquarter_Success() {
        SwiftCode hq = new SwiftCode("B1234567XXX", "Warsaw", "BRE Bank", "PL", "Poland");
        SwiftCode branch1 = new SwiftCode("B1234567001", "City1", "BRE Bank", "PL", "Poland");
        SwiftCode branch2 = new SwiftCode("B1234567002", "City2", "BRE Bank", "PL", "Poland");

        swiftCodeRepository.save(hq);
        swiftCodeRepository.save(branch1);
        swiftCodeRepository.save(branch2);

        List<SwiftCode> result = swiftCodeService.getBranchesByHeadquarter("B1234567XXX");

        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(branch -> branch.getCode().endsWith("XXX")));
    }

    @Test
    void getBranchesByHeadquarter_NoBranches() {
        SwiftCode hq = new SwiftCode("B1233567XXX", "Warsaw", "BRE Bank", "PL", "Poland");
        swiftCodeRepository.save(hq);

        List<SwiftCode> result = swiftCodeService.getBranchesByHeadquarter("B1233567XXX");

        assertTrue(result.isEmpty());
    }
}
