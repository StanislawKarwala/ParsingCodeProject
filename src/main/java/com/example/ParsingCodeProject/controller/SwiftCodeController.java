package com.example.ParsingCodeProject.controller;

import com.example.ParsingCodeProject.dto.*;
import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.service.SwiftCodeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/swift-codes")
public class SwiftCodeController {

    private final SwiftCodeService swiftCodeService;

    public SwiftCodeController(SwiftCodeService swiftCodeService){
        this.swiftCodeService = swiftCodeService;
    }


    @GetMapping("/{swift-code}")
    public ResponseEntity<?> getSwiftCodeDetails(@PathVariable("swift-code") String swiftCode) {
        if (!swiftCode.matches("[A-Z0-9]{11}")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid SWIFT code format"));
        }

        Optional<SwiftCode> swiftCodeOpt = swiftCodeService.getSwiftCodeByCode(swiftCode);
        if (swiftCodeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "SWIFT code not found"));
        }

        SwiftCode code = swiftCodeOpt.get();
        if (!code.getHeadquarterFlag()) {
            return ResponseEntity.ok(new BranchesDTO(code));
        }

        List<SwiftCode> branchEntities = swiftCodeService.getBranchesByHeadquarter(swiftCode);
        List<BranchesInfoHQResponse> branches = new ArrayList<>();
        for (SwiftCode branch : branchEntities) {
            branches.add(new BranchesInfoHQResponse(branch));
        }

        return ResponseEntity.ok(new HeadquarterDTO(code.getAddress(), code.getBankName(),
                code.getCountryISO2(), code.getCountryName(), true, code.getCode(), branches));
    }


    @GetMapping("/country/{countryISO2code}")
    public ResponseEntity<?> getSwiftCodesByCountry(@PathVariable String countryISO2code) {
        if (!countryISO2code.matches("[A-Z]{2}")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid country ISO2 code format"));
        }

        Optional<CountryResponse> response = swiftCodeService.getSwiftCodesByCountry(countryISO2code);
        if (response.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No SWIFT codes found for country: " + countryISO2code));
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
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("message", "SWIFT code data successfully deleted"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "SWIFT code data not found"));
    }
}
