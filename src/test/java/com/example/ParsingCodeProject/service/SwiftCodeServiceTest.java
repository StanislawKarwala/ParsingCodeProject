package com.example.ParsingCodeProject.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.ParsingCodeProject.dto.CountryResponse;
import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.repository.SwiftCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SwiftCodeServiceTest {

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @InjectMocks
    private SwiftCodeService swiftCodeService;

    @Test
    void saveSwiftCodesData_Success() {
        SwiftCode swiftCode = new SwiftCode("BREXPLPWXXX", "Warsaw", "BRE Bank", "PL", "Poland");

        swiftCodeService.saveSwiftCodesData(swiftCode);

        verify(swiftCodeRepository, times(1)).save(swiftCode);
    }

    @Test
    void deleteBySwiftCode_Success() {
        String swiftCode = "BREXPLPWXXX";
        SwiftCode swiftCodeEntity = new SwiftCode(swiftCode, "Warsaw", "BRE Bank", "PL", "Poland");
        when(swiftCodeRepository.findByCode(swiftCode)).thenReturn(Optional.of(swiftCodeEntity));

        boolean result = swiftCodeService.deleteBySwiftCode(swiftCode);

        assertTrue(result);
        verify(swiftCodeRepository, times(1)).findByCode(swiftCode);
        verify(swiftCodeRepository, times(1)).delete(swiftCodeEntity);
    }

    @Test
    void deleteBySwiftCode_NotFound() {
        String swiftCode = "UNKNOWN";
        when(swiftCodeRepository.findByCode(swiftCode)).thenReturn(Optional.empty());

        boolean result = swiftCodeService.deleteBySwiftCode(swiftCode);

        assertFalse(result);
        verify(swiftCodeRepository, times(1)).findByCode(swiftCode);
        verify(swiftCodeRepository, never()).delete(any());
    }

    @Test
    void getSwiftCodesByCountry_Success() {
        when(swiftCodeRepository.findByCountryISO2("PL"))
                .thenReturn(List.of(
                        new SwiftCode("BREXPLPWXXX", "Warsaw", "BRE Bank", "PL", "Poland"),
                        new SwiftCode("BREXPLPW002", "Krakow", "BRE Bank", "PL", "Poland")
                ));

        Optional<CountryResponse> response = swiftCodeService.getSwiftCodesByCountry("PL");

        assertTrue(response.isPresent());
        assertEquals("PL", response.get().getCountryISO2());
        assertEquals(2, response.get().getSwiftCodes().size());

        verify(swiftCodeRepository, times(1)).findByCountryISO2("PL");
    }

    @Test
    void getSwiftCodesByCountry_NotFound() {
        when(swiftCodeRepository.findByCountryISO2("XX")).thenReturn(List.of());

        Optional<CountryResponse> response = swiftCodeService.getSwiftCodesByCountry("XX");

        assertTrue(response.isEmpty());
        verify(swiftCodeRepository, times(1)).findByCountryISO2("XX");
    }

    @Test
    void getSwiftCodesByCountry_InvalidCountryCode() {
        String invalidCode = "123";
        boolean isValid = invalidCode.length() == 2 && invalidCode.matches("[A-Z]{2}");
        assertFalse(isValid);
    }

    @Test
    void getSwiftCodesByCountry_EmptyResult() {
        when(swiftCodeRepository.findByCountryISO2("DE")).thenReturn(List.of());

        Optional<CountryResponse> response = swiftCodeService.getSwiftCodesByCountry("DE");

        assertFalse(response.isPresent());
        verify(swiftCodeRepository, times(1)).findByCountryISO2("DE");
    }

    @Test
    void validateAndSaveSwiftCode_Success() {
        SwiftCode swiftCode = new SwiftCode("BREXPLPWXXX", "Warsaw", "BRE Bank", "PL", "Poland");
        when(swiftCodeRepository.findById(swiftCode.getCode())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> swiftCodeService.validateAndSaveSwiftCode(swiftCode));
        verify(swiftCodeRepository, times(1)).save(swiftCode);
    }

    @Test
    void validateAndSaveSwiftCode_MissingFields() {
        SwiftCode swiftCode = new SwiftCode();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> swiftCodeService.validateAndSaveSwiftCode(swiftCode));
        assertEquals("All fields are required", exception.getMessage());
    }

    @Test
    void validateAndSaveSwiftCode_InvalidCountryISO2() {
        SwiftCode swiftCode = new SwiftCode("BREXPLPWXXX", "Warsaw", "BRE Bank", "P1", "Poland");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> swiftCodeService.validateAndSaveSwiftCode(swiftCode));
        assertEquals("Invalid country ISO2 code format. It must be exactly 2 uppercase letters.", exception.getMessage());
    }

    @Test
    void validateAndSaveSwiftCode_InvalidSwiftCodeLength() {
        SwiftCode swiftCode = new SwiftCode("BREXPLPWXX", "Warsaw", "BRE Bank", "PL", "Poland");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> swiftCodeService.validateAndSaveSwiftCode(swiftCode));
        assertEquals("SWIFT code must be exactly 11 characters long.", exception.getMessage());
    }

    @Test
    void validateAndSaveSwiftCode_ExistingSwiftCode() {
        SwiftCode swiftCode = new SwiftCode("BREXPLPWXXX", "Warsaw", "BRE Bank", "PL", "Poland");
        when(swiftCodeRepository.findById(swiftCode.getCode())).thenReturn(Optional.of(swiftCode));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> swiftCodeService.validateAndSaveSwiftCode(swiftCode));
        assertEquals("SWIFT code with this code already exists in the database.", exception.getMessage());
    }

    @Test
    void getSwiftCodeByCode_Success() {
        String swiftCode = "BREXPLPWXXX";
        SwiftCode swiftCodeEntity = new SwiftCode(swiftCode, "Warsaw", "BRE Bank", "PL", "Poland");
        when(swiftCodeRepository.findById(swiftCode)).thenReturn(Optional.of(swiftCodeEntity));

        Optional<SwiftCode> result = swiftCodeService.getSwiftCodeByCode(swiftCode);

        assertTrue(result.isPresent());
        assertEquals(swiftCode, result.get().getCode());
        verify(swiftCodeRepository, times(1)).findById(swiftCode);
    }

    @Test
    void getSwiftCodeByCode_NotFound() {
        String swiftCode = "UNKNOWN";
        when(swiftCodeRepository.findById(swiftCode)).thenReturn(Optional.empty());

        Optional<SwiftCode> result = swiftCodeService.getSwiftCodeByCode(swiftCode);

        assertTrue(result.isEmpty());
        verify(swiftCodeRepository, times(1)).findById(swiftCode);
    }

    @Test
    void getBranchesByHeadquarter_Success() {
        String headquarterCode = "BREXPLPW";
        List<SwiftCode> branches = List.of(
                new SwiftCode("BREXPLPW001", "City1", "Bank1", "PL", "Poland"),
                new SwiftCode("BREXPLPW002", "City2", "Bank2", "PL", "Poland")
        );
        when(swiftCodeRepository.findByCodeStartingWith(headquarterCode)).thenReturn(branches);

        List<SwiftCode> result = swiftCodeService.getBranchesByHeadquarter(headquarterCode);

        assertEquals(2, result.size());
        verify(swiftCodeRepository, times(1)).findByCodeStartingWith(headquarterCode);
    }
}

