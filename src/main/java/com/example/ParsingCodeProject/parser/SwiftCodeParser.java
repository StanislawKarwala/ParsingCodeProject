package com.example.ParsingCodeProject.parser;

import com.example.ParsingCodeProject.entity.SwiftCode;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.springframework.stereotype.Component;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SwiftCodeParser {
    public List<SwiftCode> parseSwiftCodes(String filePath) {
        List<SwiftCode> swiftCodes = new ArrayList<>();
        Map<String, SwiftCode> headquartersMap = new HashMap<>();
        File file = new File(filePath);

        if (!file.exists()) {
            System.err.println("Plik nie istnieje!");
            return swiftCodes;
        }

        if (file.length() == 0) {
            System.err.println("Plik jest pusty!");
            return swiftCodes;
        }

        try (FileInputStream inputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {
             Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String code = row.getCell(1).getStringCellValue();
                System.out.println("Przetwarzanie kodu SWIFT: " + code);

                if (code.length() != 11) {
                    System.err.println("Kod SWIFT '" + code + "' ma nieprawidłową długość. Oczekiwano 11 znaków, ale otrzymano " + code.length());
                    continue;
                }

                boolean isDuplicate = swiftCodes.stream().anyMatch(swift -> swift.getCode().equals(code));
                if (isDuplicate) {
                    System.out.println("Duplikat kodu SWIFT: " + code);
                    continue;
                }

                String countryISO2 = row.getCell(0).getStringCellValue().toUpperCase();
                String bankName = row.getCell(3).getStringCellValue();
                String address = row.getCell(4).getStringCellValue();
                String countryName = row.getCell(6).getStringCellValue().toUpperCase();

                System.out.println("Tworzenie obiektu SwiftCode dla kodu: " + code);
                SwiftCode swift = new SwiftCode(code, address, bankName, countryISO2, countryName);
                swiftCodes.add(swift);

                if (code.endsWith("XXX")) {
                    headquartersMap.put(code.substring(0, 8), swift);
                    System.out.println("Dodano centralę banku: " + code);
                }
            }

            System.out.println("Rozpoczynam przypisywanie oddziałów do central...");
            for (SwiftCode swift : swiftCodes) {
                if (!swift.getCode().endsWith("XXX")) {
                    String headquarterCode = swift.getCode().substring(0, 8) + "XXX";
                    SwiftCode headquarter = headquartersMap.get(headquarterCode);
                    if (headquarter != null) {
                        swift.setHeadquarter(headquarter);
                        headquarter.getBranches().add(swift);
                        System.out.println("Przypisano oddział " + swift.getCode() + " do centrali " + headquarterCode);
                    }
                }
            }
        } catch (NotOfficeXmlFileException e) {
            System.err.println("Błąd: Uszkodzony plik lub nieprawidłowy format - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Błąd podczas odczytu pliku: " + e.getMessage());
        }

        return swiftCodes;
    }

}

