package com.example.ParsingCodeProject.controller;

import com.example.ParsingCodeProject.service.SwiftCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
}
