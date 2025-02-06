package com.example.ParsingCodeProject.controller;

import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.repository.SwiftCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SwiftCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @BeforeEach
    void setUp() {
        swiftCodeRepository.deleteAll();
    }

    @Test
    void deleteBySwiftCode_Success() throws Exception {
        SwiftCode swiftCode = new SwiftCode();
        swiftCode.setCode("PROLAND1WXXX");
        swiftCode.setAddress("Test Address");
        swiftCode.setBankName("Test Bank");
        swiftCode.setCountryISO2("PL");
        swiftCode.setCountryName("Poland");
        swiftCodeRepository.save(swiftCode);

        mockMvc.perform(delete("/v1/swift-codes/PROLAND1WXXX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SWIFT code data succesfully deleted"));

        assertFalse(swiftCodeRepository.findByCode("PROLAND1WXXX").isPresent());
    }

    @Test
    void deleteBySwiftCode_NotFound() throws Exception {
        mockMvc.perform(delete("/v1/swift-codes/NONEXISTENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("SWIFT code data not found"));
    }
}
