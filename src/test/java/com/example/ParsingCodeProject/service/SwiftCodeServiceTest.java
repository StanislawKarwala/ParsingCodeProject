package com.example.ParsingCodeProject.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.ParsingCodeProject.dto.SwiftCodesCountryResponse;
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

        Optional<SwiftCodesCountryResponse> response = swiftCodeService.getSwiftCodesByCountry("PL");

        assertTrue(response.isPresent());
        assertEquals("PL", response.get().getCountryISO2());
        assertEquals(2, response.get().getSwiftCodes().size());

        verify(swiftCodeRepository, times(1)).findByCountryISO2("PL");
    }

    @Test
    void getSwiftCodesByCountry_NotFound() {
        when(swiftCodeRepository.findByCountryISO2("XX")).thenReturn(List.of());

        Optional<SwiftCodesCountryResponse> response = swiftCodeService.getSwiftCodesByCountry("XX");

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

        Optional<SwiftCodesCountryResponse> response = swiftCodeService.getSwiftCodesByCountry("DE");

        assertFalse(response.isPresent());
        verify(swiftCodeRepository, times(1)).findByCountryISO2("DE");
    }
}

