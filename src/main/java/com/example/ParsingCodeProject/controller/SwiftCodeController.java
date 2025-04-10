package com.example.ParsingCodeProject.controller;


import com.example.ParsingCodeProject.dto.BranchesInfoHQResponse;
import com.example.ParsingCodeProject.dto.CountryResponse;
import com.example.ParsingCodeProject.dto.HeadquarterDTO;
import com.example.ParsingCodeProject.dto.SwiftCodeRequestDTO;
import com.example.ParsingCodeProject.service.SwiftCodeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/swift-codes")
public class SwiftCodeController {

    private final SwiftCodeService swiftCodeService;

    public SwiftCodeController(SwiftCodeService swiftCodeService) {
        this.swiftCodeService = swiftCodeService;
    }

    @GetMapping("/{swift-code}")
    public ResponseEntity<?> getSwiftCodeDetails(@PathVariable("swift-code") String swiftCode) {
        if (!swiftCode.matches("[A-Z0-9]{11}")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid SWIFT code format. It must be exactly 11 alphanumeric characters."));
        }

        Optional<Object> swiftCodeOpt = swiftCodeService.getSwiftCodeByCode(swiftCode);
        if (swiftCodeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "SWIFT code not found in database."));
        }

        return ResponseEntity.ok(swiftCodeOpt.get());
    }


    @GetMapping("/country/{countryISO2code}")
    public ResponseEntity<?> getSwiftCodesByCountry(@PathVariable String countryISO2code) {
        if (!countryISO2code.matches("[A-Z]{2}")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid country ISO2 code format. It must be exactly 2 uppercase letters."));
        }

        Optional<CountryResponse> response = swiftCodeService.getSwiftCodesByCountry(countryISO2code);

        if (response.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No SWIFT codes found for country: " + countryISO2code));
        }

        return ResponseEntity.ok(response.get());
    }


    @PostMapping
    public ResponseEntity<Map<String, String>> addSwiftCode(@Valid @RequestBody SwiftCodeRequestDTO swiftCodeDTO) {
        try {
            swiftCodeService.validateAndSaveSwiftCode(swiftCodeDTO);
            return ResponseEntity.ok(Map.of("message", "SWIFT code successfully added"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @DeleteMapping("/{swift-code}")
    public ResponseEntity<Map<String, String>> deleteBySwiftCode(@PathVariable("swift-code") String code) {
        boolean isDeleted = swiftCodeService.deleteBySwiftCode(code);

        Map<String, String> response = new HashMap<>();
        if (isDeleted) {
            response.put("message", "SWIFT code data successfully deleted");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "SWIFT code data not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
