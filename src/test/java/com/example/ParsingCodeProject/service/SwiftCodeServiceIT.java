package com.example.ParsingCodeProject.service;

import com.example.ParsingCodeProject.dto.SwiftCodesCountryResponse;
import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.repository.SwiftCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SwiftCodeServiceIT {

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @Autowired
    private SwiftCodeService swiftCodeService;

    @Test
    void getSwiftCodesByCountry_Success() {
        Optional<SwiftCodesCountryResponse> response = swiftCodeService.getSwiftCodesByCountry("PL");

        assertTrue(response.isPresent());
        assertEquals("PL", response.get().getCountryISO2());
        assertEquals(459, response.get().getSwiftCodes().size());
    }

    @Test
    void getSwiftCodesByCountry_NotFound() {
        Optional<SwiftCodesCountryResponse> response = swiftCodeService.getSwiftCodesByCountry("XX");
        assertTrue(response.isEmpty());
    }

    @Test
    void getSwiftCodesByCountry_InvalidCountryCode() {
        Optional<SwiftCodesCountryResponse> response = swiftCodeService.getSwiftCodesByCountry("123");
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
        SwiftCode swiftCode = new SwiftCode("TESTTESTXXX", "Warsaw", "BRE Bank", "PL", "Poland");
        swiftCodeService.validateAndSaveSwiftCode(swiftCode);
        assertTrue(swiftCodeRepository.findById("TESTTESTXXX").isPresent());
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
}
