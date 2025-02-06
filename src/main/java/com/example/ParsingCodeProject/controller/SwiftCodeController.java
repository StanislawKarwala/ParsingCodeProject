package com.example.ParsingCodeProject.controller;

import com.example.ParsingCodeProject.dto.BranchesDTO;
import com.example.ParsingCodeProject.dto.BranchesInfoHQResponse;
import com.example.ParsingCodeProject.dto.CountryResponse;
import com.example.ParsingCodeProject.dto.HeadquarterDTO;
import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.service.SwiftCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/swift-codes")
public class SwiftCodeController {

    private final SwiftCodeService swiftCodeService;

    public SwiftCodeController(SwiftCodeService swiftCodeService){
        this.swiftCodeService = swiftCodeService;
    }

    @DeleteMapping("/{swift-code}")
    public ResponseEntity<Map<String, String>> deleteBySwiftCode(@PathVariable("swift-code") String code){
        boolean isDeleted = swiftCodeService.deleteBySwiftCode(code);

        Map<String, String> response = new HashMap<>();
        if(isDeleted){
            response.put("message", "SWIFT code data succesfully deleted");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "SWIFT code data not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/country/{countryISO2code}")
    public ResponseEntity<?> getSwiftCodesByCountry(@PathVariable String countryISO2code) {
        if (countryISO2code.length() != 2 || !countryISO2code.matches("[A-Z]{2}")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid country ISO2 code format. It must be exactly 2 uppercase letters."));
        }

        Optional<CountryResponse> response = swiftCodeService.getSwiftCodesByCountry(countryISO2code);

        if (response.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No SWIFT codes found for country: " + countryISO2code));
        }

        return response.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addSwiftCode(@RequestBody SwiftCode swiftCode) {
        try {
            swiftCodeService.validateAndSaveSwiftCode(swiftCode);
            Map<String, String> response = new HashMap<>();
            response.put("message", "SWIFT code successfully added");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{swiftCode}")
    public ResponseEntity<?> getSwiftCodeDetails(@PathVariable String swiftCode) {
        if (!swiftCode.matches("[A-Z0-9]{11}")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid SWIFT code format. It must be exactly 11 alphanumeric characters."));
        }

        Optional<SwiftCode> swiftCodeOpt = swiftCodeService.getSwiftCodeByCode(swiftCode);
        if (swiftCodeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "SWIFT code not found in database."));
        }

        SwiftCode code = swiftCodeOpt.get();
        if (code.getHeadquarterFlag()) {
            try {
                List<BranchesInfoHQResponse> branches = swiftCodeService.getBranchesByHeadquarter(swiftCode)
                        .stream()
                        .map(BranchesInfoHQResponse::new)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(new HeadquarterDTO(
                        code.getAddress(), code.getBankName(), code.getCountryISO2(), code.getCountryName(), true, code.getCode(), branches
                ));
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
        }

        return ResponseEntity.ok(new BranchesDTO(code));
    }

}
