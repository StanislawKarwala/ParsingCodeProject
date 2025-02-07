package com.example.ParsingCodeProject.parser;

import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.repository.SwiftCodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SwiftCodeParserIntegrationTest {
    @Autowired
    private SwiftCodeParser swiftCodeParser;

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @BeforeEach
    void setUp() {
        swiftCodeRepository.deleteAll();
    }

    @Test
    void parseSwiftCodes_ValidFile_Success() {
        String filePath = "src/test/resources/valid_swift_codes.xlsx";
        List<SwiftCode> swiftCodes = swiftCodeParser.parseSwiftCodes(filePath);

        assertNotNull(swiftCodes);
        assertFalse(swiftCodes.isEmpty());
        assertEquals(1, swiftCodes.size());
        assertEquals("PL123412XXX", swiftCodes.get(0).getCode());
    }

    @Test
    void parseSwiftCodes_FileNotFound() {
        String filePath = "src/test/resources/not_found.xlsx";
        List<SwiftCode> swiftCodes = swiftCodeParser.parseSwiftCodes(filePath);

        assertNotNull(swiftCodes);
        assertTrue(swiftCodes.isEmpty());
    }

    @Test
    void storeSwiftCodes_SavesDataCorrectly() {
        SwiftCode swift1 = new SwiftCode("ABCDPLPWXXX", "Address A", "Bank A", "PL", "POLAND");
        swift1.setHeadquarterFlag(true);

        SwiftCode swift2 = new SwiftCode("ABCDPLPW001", "Address B", "Bank B", "PL", "POLAND");
        swift2.setHeadquarterFlag(false);

        List<SwiftCode> swiftCodes = List.of(swift1, swift2);
        swiftCodeParser.storeSwiftCodes(swiftCodes);

        List<SwiftCode> savedSwiftCodes = swiftCodeRepository.findAll();
        assertEquals(2, savedSwiftCodes.size());

        SwiftCode savedHeadquarter = swiftCodeRepository.findById("ABCDPLPWXXX").orElse(null);
        assertNotNull(savedHeadquarter);
        assertTrue(savedHeadquarter.getHeadquarterFlag());

        SwiftCode savedBranch = swiftCodeRepository.findById("ABCDPLPW001").orElse(null);
        assertNotNull(savedBranch);
        assertFalse(savedBranch.getHeadquarterFlag());
    }

    @Test
    void testParseAndStoreSwiftCodes() {
        String filePath = "src/test/resources/valid_swift_codes.xlsx";

        List<SwiftCode> swiftCodes = swiftCodeParser.parseSwiftCodes(filePath);
        swiftCodeParser.storeSwiftCodes(swiftCodes);

        List<SwiftCode> savedCodes = swiftCodeRepository.findAll();
        assertFalse(savedCodes.isEmpty());
        assertEquals(1, savedCodes.size());
        assertEquals("PL123412XXX", savedCodes.get(0).getCode());
    }
}
