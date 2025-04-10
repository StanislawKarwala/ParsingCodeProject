package com.example.ParsingCodeProject.parser;

import com.example.ParsingCodeProject.entity.Branch;
import com.example.ParsingCodeProject.entity.Headquarter;
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

    public SwiftCodeParser(SwiftCodeService swiftCodeService) {
        this.swiftCodeService = swiftCodeService;
    }

    public void parseAndStoreSwiftCodes(String fileName) {
        List<Headquarter> headquarters = new ArrayList<>();
        List<Branch> branches = new ArrayList<>();
        Map<String, Headquarter> headquartersMap = new HashMap<>();

        Path filePath = Paths.get(fileName);

        if (!Files.exists(filePath)) {
            System.err.println("Plik '" + filePath.toAbsolutePath() + "' nie istnieje!");
            return;
        }

        try {
            long fileSize = Files.size(filePath);
            if (fileSize == 0) {
                System.err.println("Błąd: Plik '" + filePath.toAbsolutePath() + "' jest pusty!");
                return;
            }

            try (InputStream inputStream = Files.newInputStream(filePath);
                 Workbook workbook = new XSSFWorkbook(inputStream)) {
                Sheet sheet = workbook.getSheetAt(0);

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) {
                        continue;
                    }

                    String code = row.getCell(1).getStringCellValue();

                    if (code.length() != 11) {
                        continue;
                    }

                    String countryISO2 = row.getCell(0).getStringCellValue().toUpperCase();
                    String bankName = row.getCell(3).getStringCellValue();
                    String address = row.getCell(4).getStringCellValue();
                    String countryName = row.getCell(6).getStringCellValue().toUpperCase();
                    boolean isHeadquarter = code.endsWith("XXX");

                    if (isHeadquarter) {
                        Headquarter hq = new Headquarter(code, address, bankName, countryISO2, countryName);
                        if (!headquarters.contains(hq)) {
                            headquarters.add(hq);
                            headquartersMap.put(code.substring(0, 8), hq);
                        }
                    } else {
                        Branch branch = new Branch(code, address, bankName, countryISO2, countryName);
                        if (!branches.contains(branch)) {
                            branches.add(branch);
                        }
                    }
                }
                for (Branch branch : branches) {
                    String baseCode = branch.getCode().substring(0, 8);
                    Headquarter headquarter = headquartersMap.get(baseCode);
                    if (headquarter != null) {
                        branch.setHeadquarter(headquarter);
                        headquarter.getBranches().add(branch);
                    }
                }
                storeSwiftCodes(headquarters, branches);
            }
        } catch (EmptyFileException e) {
            System.err.println("Błąd: Plik '" + filePath.toAbsolutePath() + "' jest pusty!");
        } catch (NotOfficeXmlFileException e) {
            System.err.println("Błąd: Uszkodzony plik lub nieprawidłowy format - " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Błąd podczas odczytu pliku: " + e.getMessage());
        }
    }

    private void storeSwiftCodes(List<Headquarter> headquarters, List<Branch> branches) {
        for (Headquarter hq : headquarters) {
            swiftCodeService.saveHeadquarter(hq);
        }
        for (Branch branch : branches) {
            swiftCodeService.saveBranch(branch);
        }
    }
}