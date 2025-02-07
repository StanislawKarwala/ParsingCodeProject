package com.example.ParsingCodeProject.controller;

import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.parser.SwiftCodeParser;
import com.example.ParsingCodeProject.repository.SwiftCodeRepository;
import com.example.ParsingCodeProject.service.SwiftCodeService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwiftCodeControllerUnitTest {

    @Mock
    private SwiftCodeService swiftCodeService;

    @InjectMocks
    private SwiftCodeController swiftCodeController;

    @Test
    void deleteBySwiftCode_Success() {
        when(swiftCodeService.deleteBySwiftCode("TEST1234567")).thenReturn(true);
        ResponseEntity<Map<String, String>> response = swiftCodeController.deleteBySwiftCode("TEST1234567");
        assertEquals(200, response.getStatusCode().value());
        assertEquals("SWIFT code data succesfully deleted", response.getBody().get("message"));
    }

    @Test
    void deleteBySwiftCode_NotFound() {
        when(swiftCodeService.deleteBySwiftCode("TEST1234567")).thenReturn(false);
        ResponseEntity<Map<String, String>> response = swiftCodeController.deleteBySwiftCode("TEST1234567");
        assertEquals(404, response.getStatusCode().value());
        assertEquals("SWIFT code data not found", response.getBody().get("message"));
    }

    @Test
    void getSwiftCodesByCountry_InvalidISOCode() {
        ResponseEntity<?> response = swiftCodeController.getSwiftCodesByCountry("PL123");
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void getSwiftCodesByCountry_NoDataFound() {
        when(swiftCodeService.getSwiftCodesByCountry("PL")).thenReturn(Optional.empty());
        ResponseEntity<?> response = swiftCodeController.getSwiftCodesByCountry("PL");
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void addSwiftCode_Success() {
        SwiftCode swiftCode = new SwiftCode("TEST1234567", "Test Address", "Test Bank", "PL", "Poland");
        ResponseEntity<Map<String, String>> response = swiftCodeController.addSwiftCode(swiftCode);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void addSwiftCode_InvalidData() {
        doThrow(new IllegalArgumentException("Invalid SWIFT Code"))
                .when(swiftCodeService).validateAndSaveSwiftCode(any());
        ResponseEntity<Map<String, String>> response = swiftCodeController.addSwiftCode(new SwiftCode());
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid SWIFT Code", response.getBody().get("error"));
    }

    @Test
    void getSwiftCodeDetails_InvalidFormat() {
        ResponseEntity<?> response = swiftCodeController.getSwiftCodeDetails("INVALID");
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void getSwiftCodeDetails_NotFound() {
        when(swiftCodeService.getSwiftCodeByCode("TEST1234567")).thenReturn(Optional.empty());
        ResponseEntity<?> response = swiftCodeController.getSwiftCodeDetails("TEST1234567");
        assertEquals(404, response.getStatusCode().value());
    }
}
