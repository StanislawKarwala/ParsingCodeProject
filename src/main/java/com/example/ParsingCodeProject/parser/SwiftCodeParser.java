package com.example.ParsingCodeProject.parser;

import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.service.SwiftCodeService;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.springframework.stereotype.Component;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SwiftCodeParser {

    private final SwiftCodeService swiftCodeService;

    public SwiftCodeParser(SwiftCodeService swiftCodeService){
        this.swiftCodeService = swiftCodeService;
    }

    public List<SwiftCode> parseSwiftCodes(String fileName) {
        List<SwiftCode> swiftCodes = new ArrayList<>();
        Map<String, SwiftCode> headquartersMap = new HashMap<>();

        // Pobranie pliku z resources
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            System.err.println("Plik '" + fileName + "' nie został znaleziony w resources!");
            return swiftCodes;
        }

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
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
                boolean isHeadquarter = code.endsWith("XXX");

                System.out.println("Tworzenie obiektu SwiftCode dla kodu: " + code);
                SwiftCode swift = new SwiftCode(code, address, bankName, countryISO2, countryName);
                swift.setHeadquarterFlag(isHeadquarter);
                swiftCodes.add(swift);

                if (isHeadquarter) {
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


    public void storeSwiftCodes(List<SwiftCode> swiftCodes) {
        Map<String, SwiftCode> headquartersMap = new HashMap<>();
        List<SwiftCode> mutableSwiftCodes = new ArrayList<>(swiftCodes);

        mutableSwiftCodes.sort((a, b) -> Boolean.compare(b.getHeadquarterFlag(), a.getHeadquarterFlag()));

        for (SwiftCode swift : mutableSwiftCodes) {
            if (swift.getHeadquarterFlag()) {
                swiftCodeService.saveSwiftCodesData(swift);
                headquartersMap.put(swift.getCode().substring(0, 8), swift);
                System.out.println("Zapisano centralę: " + swift.getCode());
            }
        }

        for (SwiftCode swift : mutableSwiftCodes) {
            if (!swift.getHeadquarterFlag()) {
                SwiftCode headquarter = headquartersMap.get(swift.getCode().substring(0, 8));
                if (headquarter != null) {
                    swift.setHeadquarter(headquarter);
                }
                swiftCodeService.saveSwiftCodesData(swift);
                System.out.println("Zapisano oddział: " + swift.getCode());
            }
        }

        System.out.println("Zakończono zapisywanie do bazy danych.");
    }

}

