package com.example.ParsingCodeProject.service;

import com.example.ParsingCodeProject.dto.CountryDTO;
import com.example.ParsingCodeProject.dto.SwiftCodesCountryResponse;
import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.repository.SwiftCodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SwiftCodeService {
    private final SwiftCodeRepository swiftCodeRepository;

    public SwiftCodeService(SwiftCodeRepository swiftCodeRepository){
        this.swiftCodeRepository = swiftCodeRepository;
    }

    public void saveSwiftCodesData(SwiftCode code){
        swiftCodeRepository.save(code);
    }

    public boolean deleteBySwiftCode(String code){
        Optional<SwiftCode> swiftCodeOptional = swiftCodeRepository.findByCode(code);

        if(swiftCodeOptional.isPresent()){
            swiftCodeRepository.delete(swiftCodeOptional.get());
            return true;
        }
        return false;
    }

    public Optional<SwiftCodesCountryResponse> getSwiftCodesByCountry(String countryISO2) {
        System.out.println("CountryISO2 = " + countryISO2);
        List<SwiftCode> swiftCodes = swiftCodeRepository.findByCountryISO2(countryISO2);
        System.out.println("Znalezione kody: " + swiftCodes);

        if (swiftCodes.isEmpty()) {
            return Optional.empty();
        }

        SwiftCodesCountryResponse response = new SwiftCodesCountryResponse(
                countryISO2,
                swiftCodes.getFirst().getCountryName(),
                swiftCodes.stream().map(CountryDTO::new).toList()
        );

        System.out.println("Response: " + response);
        return Optional.of(response);
    }

    public void validateAndSaveSwiftCode(SwiftCode swiftCode) {
        if (swiftCode.getAddress() == null || swiftCode.getBankName() == null || swiftCode.getCountryISO2() == null ||
                swiftCode.getCountryName() == null || swiftCode.getCode() == null) {
            throw new IllegalArgumentException("All fields are required");
        }

        if (!swiftCode.getCountryISO2().matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("Invalid country ISO2 code format. It must be exactly 2 uppercase letters.");
        }

        if (swiftCode.getCode().length() != 11) {
            throw new IllegalArgumentException("SWIFT code must be exactly 11 characters long.");
        }
        boolean isHeadquarter = swiftCode.getCode().endsWith("XXX");

        Optional<SwiftCode> existingSwiftCode = swiftCodeRepository.findById(swiftCode.getCode());
        if (existingSwiftCode.isPresent()) {
            throw new IllegalArgumentException("SWIFT code with this code already exists in the database.");
        }

        swiftCode.setHeadquarterFlag(isHeadquarter);
        swiftCodeRepository.save(swiftCode);
    }
}
