package com.example.ParsingCodeProject.controller;

import com.example.ParsingCodeProject.dto.SwiftCodesCountryResponse;
import com.example.ParsingCodeProject.service.SwiftCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

        Optional<SwiftCodesCountryResponse> response = swiftCodeService.getSwiftCodesByCountry(countryISO2code);

        if (response.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No SWIFT codes found for country: " + countryISO2code));
        }

        return response.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
