package com.example.ParsingCodeProject.parser;

import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.service.SwiftCodeService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SwiftCodeParserUnitTest {

    @Mock
    private SwiftCodeService swiftCodeService;

    @InjectMocks
    private SwiftCodeParser swiftCodeParser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFileNotFound() {
        String fileName = "not_found.xlsx";
        Path filePath = Paths.get("src", "test", "resources", fileName);
        List<SwiftCode> result = swiftCodeParser.parseSwiftCodes(filePath.toString());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testEmptyFile() throws IOException {
        String fileName = "empty_swift_codes.xlsx";
        Path filePath = Paths.get("src", "test", "resources", fileName);
        List<SwiftCode> swiftCodes = swiftCodeParser.parseSwiftCodes(filePath.toString());

        assertNotNull(swiftCodes);
        assertTrue(swiftCodes.isEmpty());
    }

    @Test
    void testInvalidDataFile() throws IOException {
        String fileName = "invalid_swift_codes.xlsx";
        Path filePath = Paths.get("src", "test", "resources", fileName);
        System.out.println("Test parsowania uszkodzonego pliku: ");

        try (FileWriter writer = new FileWriter(filePath.toString())) {
            writer.write("INVALID DATA\n");
        }

        List<SwiftCode> swiftCodes = swiftCodeParser.parseSwiftCodes(filePath.toString());

        assertNotNull(swiftCodes);
        assertTrue(swiftCodes.isEmpty());
    }

    @Test
    void testDuplicateSwiftCodes() throws IOException {
        String fileName = "duplicate_swift_codes.xlsx";
        Path filePath = Paths.get("src", "test", "resources", fileName);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Swift Codes");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Country");
            headerRow.createCell(1).setCellValue("Code");
            headerRow.createCell(3).setCellValue("Bank Name");
            headerRow.createCell(4).setCellValue("Address");
            headerRow.createCell(6).setCellValue("Country Name");

            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("PL");
            row1.createCell(1).setCellValue("PL123411XXX");
            row1.createCell(3).setCellValue("Bank A");
            row1.createCell(4).setCellValue("Address A");
            row1.createCell(6).setCellValue("POLAND");

            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("PL");
            row2.createCell(1).setCellValue("PL123411XXX");
            row2.createCell(3).setCellValue("Bank B");
            row2.createCell(4).setCellValue("Address B");
            row2.createCell(6).setCellValue("POLAND");

            try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
                workbook.write(fileOut);
            }
        }
        List<SwiftCode> swiftCodes = swiftCodeParser.parseSwiftCodes(filePath.toString());

        System.out.println("Liczba unikalnych kodów SWIFT po parsowaniu: " + swiftCodes.size());

        assertNotNull(swiftCodes, "Lista kodów SWIFT nie powinna być null!");
        assertEquals(1, swiftCodes.size(), "Lista kodów SWIFT powinna zawierać dokładnie 1 unikalny kod!");
    }




    @Test
    void testParseFile_ValidData() throws IOException {
        String fileName = "valid_swift_codes.xlsx";
        Path filePath = Paths.get("src", "test", "resources", fileName);
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Swift Codes");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Country");
            headerRow.createCell(1).setCellValue("Code");
            headerRow.createCell(3).setCellValue("Bank Name");
            headerRow.createCell(4).setCellValue("Address");
            headerRow.createCell(6).setCellValue("Country Name");

            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("PL");
            row.createCell(1).setCellValue("PL123412XXX");
            row.createCell(3).setCellValue("Bank A");
            row.createCell(4).setCellValue("Address A");
            row.createCell(6).setCellValue("POLAND");

            try (FileOutputStream fileOut = new FileOutputStream(filePath.toString())) {
                workbook.write(fileOut);
            }
        }

        List<SwiftCode> result = swiftCodeParser.parseSwiftCodes(filePath.toString());
        assertNotNull(result);
        assertFalse(result.isEmpty());
        SwiftCode swiftCode = result.get(0);
        assertEquals("PL123412XXX", swiftCode.getCode());
        assertTrue(swiftCode.getHeadquarterFlag());
    }

    @Test
    void testStoreSwiftCodes() {
        SwiftCode swift1 = new SwiftCode("ABCDPLPWXXX", "Address A", "Bank A", "PL", "POLAND");
        swift1.setHeadquarterFlag(true);

        SwiftCode swift2 = new SwiftCode("ABCDPLPW001", "Address B", "Bank B", "PL", "POLAND");
        swift2.setHeadquarterFlag(false);

        List<SwiftCode> swiftCodes = Arrays.asList(swift1, swift2);
        swiftCodeParser.storeSwiftCodes(swiftCodes);

        ArgumentCaptor<SwiftCode> captor = ArgumentCaptor.forClass(SwiftCode.class);
        verify(swiftCodeService, times(2)).saveSwiftCodesData(captor.capture());

        List<SwiftCode> savedSwiftCodes = captor.getAllValues();
        assertEquals(2, savedSwiftCodes.size());
        assertEquals("ABCDPLPWXXX", savedSwiftCodes.get(0).getCode());
        assertEquals("ABCDPLPW001", savedSwiftCodes.get(1).getCode());
    }

    @Test
    void testCodeFormatAndRules() {
        System.out.println("Test formatu i reguł kodów SWIFT: ");

        List<SwiftCode> swiftCodes = List.of(
                new SwiftCode("ABCDPLPWXXX", "Address1", "Bank1", "PL", "Poland"),
                new SwiftCode("ABCDPLPW001", "Address2", "Bank1", "PL", "Poland"),
                new SwiftCode("INVALID123", "Address3", "Bank2", "US", "USA"),
                new SwiftCode("TOOLONGCODEXYZ", "Address4", "Bank3", "DE", "Germany")
        );

        System.out.println("Wszystkie kody SWIFT przed filtrowaniem:");
        swiftCodes.forEach(swift -> System.out.println("Kod: " + swift.getCode() + ", Flaga HQ: " + swift.getHeadquarterFlag()));

        List<SwiftCode> validSwiftCodes = swiftCodes.stream()
                .filter(swift -> swift.getCode() != null && swift.getCode().length() == 11)
                .toList();

        System.out.println("Poprawne kody SWIFT po filtrowaniu:");
        validSwiftCodes.forEach(swift -> System.out.println("Kod: " + swift.getCode() + ", Flaga HQ: " + swift.getHeadquarterFlag()));

        assertEquals(2, validSwiftCodes.size(), "Nieprawidłowe kody SWIFT nie zostały poprawnie odfiltrowane.");

        for (SwiftCode swift : validSwiftCodes) {
            String code = swift.getCode();
            assertNotNull(code, "Kod SWIFT nie może być null.");
            System.out.println("Przetwarzanie kodu SWIFT: " + code);
            if (swift.getHeadquarterFlag()) {
                System.out.println("Kod SWIFT '" + code + "' jest siedzibą główną.");
                assertTrue(swift.getHeadquarterFlag(), "Kod SWIFT '" + code + "' powinien być siedzibą główną, ale flaga headquarterFlag zwraca false.");
            } else {
                System.out.println("Kod SWIFT '" + code + "' jest oddziałem.");
                assertFalse(swift.getHeadquarterFlag(), "Kod SWIFT '" + code + "' nie powinien być siedzibą główną, ale flaga headquarterFlag zwraca true.");
            }

            if (!swift.getHeadquarterFlag()) {
                String branchPrefix = code.substring(0, 8);
                System.out.println("Szukanie siedziby głównej dla oddziału '" + code + "' (prefix: " + branchPrefix + ")");

                boolean hasMatchingHeadquarter = validSwiftCodes.stream()
                        .filter(SwiftCode::getHeadquarterFlag)
                        .anyMatch(headquarter -> headquarter.getCode().startsWith(branchPrefix));

                assertTrue(hasMatchingHeadquarter, "Kod oddziału '" + code + "' nie ma powiązanej siedziby głównej.");
                System.out.println("Znaleziono powiązaną siedzibę główną dla oddziału '" + code + "'.");
            }
        }
    }


}

