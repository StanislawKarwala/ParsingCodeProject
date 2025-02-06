package com.example.ParsingCodeProject.service;

import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.repository.SwiftCodeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class SwiftCodeServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @Autowired
    private SwiftCodeService swiftCodeService;

    @BeforeEach
    void setUp() {
        swiftCodeRepository.deleteAll();
    }

    @Test
    void deleteBySwiftCode_Success() throws Exception {
        SwiftCode headquarter = new SwiftCode("PROLAND1XXX", "Warsaw", "BRE Bank", "PL", "Poland");
        swiftCodeRepository.save(headquarter);

        SwiftCode branch = new SwiftCode("PROLAND1WWA4", "Warsaw", "BRE Bank", "PL", "Poland");
        branch.setHeadquarter(headquarter);
        swiftCodeRepository.save(branch);

        assertTrue(swiftCodeRepository.findByCode("PROLAND1WWA4").isPresent());

        mockMvc.perform(delete("/v1/swift-codes/PROLAND1WWA4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SWIFT code data succesfully deleted"));

        assertFalse(swiftCodeRepository.findByCode("PROLAND1WWA4").isPresent());
    }

    @Test
    void deleteBySwiftCode_NotFound() throws Exception {
        mockMvc.perform(delete("/v1/swift-codes/NONEXISTENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("SWIFT code data not found"));
    }
}
