package com.example.ParsingCodeProject.parser;

import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.service.SwiftCodeService;
import org.apache.poi.EmptyFileException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        Path filePath = Paths.get(fileName);

        if (!Files.exists(filePath)) {
            System.err.println("Plik '" + filePath.toAbsolutePath() + "' nie istnieje!");
            return swiftCodes;
        }

        try {
            long fileSize = Files.size(filePath);
            if (fileSize == 0) {
                System.err.println("Błąd: Plik '" + filePath.toAbsolutePath() + "' jest pusty!");
                return swiftCodes;
            }

            try (InputStream inputStream = Files.newInputStream(filePath);
                 Workbook workbook = new XSSFWorkbook(inputStream)) {
                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;

                    String code = row.getCell(1).getStringCellValue();

                    if (code.length() != 11) {
                        System.err.println("Kod SWIFT '" + code + "' ma nieprawidłową długość. Oczekiwano 11 znaków, ale otrzymano " + code.length());
                        continue;
                    }

                    boolean isDuplicate = swiftCodes.stream().anyMatch(swift -> swift.getCode().equals(code));
                    if (isDuplicate) {
                        continue;
                    }

                    String countryISO2 = row.getCell(0).getStringCellValue().toUpperCase();
                    String bankName = row.getCell(3).getStringCellValue();
                    String address = row.getCell(4).getStringCellValue();
                    String countryName = row.getCell(6).getStringCellValue().toUpperCase();
                    boolean isHeadquarter = code.endsWith("XXX");

                    SwiftCode swift = new SwiftCode(code, address, bankName, countryISO2, countryName);
                    swiftCodes.add(swift);

                    if (isHeadquarter) {
                        headquartersMap.put(code.substring(0, 8), swift);
                    }
                }
                for (SwiftCode swift : swiftCodes) {
                    if (!swift.getCode().endsWith("XXX")) {
                        String baseCode = swift.getCode().substring(0, 8);
                        SwiftCode headquarter = headquartersMap.get(baseCode);
                        if (headquarter != null) {
                            swift.setHeadquarter(headquarter);
                            headquarter.getBranches().add(swift);
                        }
                    }
                }
            }
        } catch (EmptyFileException e) {
            System.err.println("Błąd: Plik '" + filePath.toAbsolutePath() + "' jest pusty!");
        } catch (NotOfficeXmlFileException e) {
            System.err.println("Błąd: Uszkodzony plik lub nieprawidłowy format - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Błąd podczas odczytu pliku: " + e.getMessage());
        }
        return swiftCodes;
    }

    public void storeSwiftCodes(List<SwiftCode> swiftCodes) {
        List<SwiftCode> mutableSwiftCodes = new ArrayList<>(swiftCodes);

        mutableSwiftCodes.sort((a, b) -> Boolean.compare(b.getHeadquarterFlag(), a.getHeadquarterFlag()));

        for(SwiftCode swift : mutableSwiftCodes){
            swiftCodeService.saveSwiftCodesData(swift);
        }
    }
}

