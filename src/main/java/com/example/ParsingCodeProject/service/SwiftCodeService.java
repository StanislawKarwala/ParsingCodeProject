package com.example.ParsingCodeProject.service;

import com.example.ParsingCodeProject.dto.CountryDTO;
import com.example.ParsingCodeProject.dto.CountryResponse;
import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.repository.SwiftCodeRepository;
import jakarta.transaction.Transactional;
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


    public Optional<SwiftCode> getSwiftCodeByCode(String swiftCode) {
        return swiftCodeRepository.findById(swiftCode);
    }


    public List<SwiftCode> getBranchesByHeadquarter(String headquarterCode) {
        return swiftCodeRepository.findByCodeStartingWith(headquarterCode.substring(0, 8)).stream()
                .filter(branch -> !branch.getCode().endsWith("XXX"))
                .toList();
    }


    public Optional<CountryResponse> getSwiftCodesByCountry(String countryISO2) {
        List<SwiftCode> swiftCodes = swiftCodeRepository.findByCountryISO2(countryISO2);

        if (swiftCodes.isEmpty()) {
            return Optional.empty();
        }

        String countryName = swiftCodes.get(0).getCountryName();

        List<CountryDTO> dto = swiftCodes.stream()
                .map(CountryDTO::new)
                .toList();

        return Optional.of(new CountryResponse(countryISO2, countryName, dto));
    }


    @Transactional
    public void validateAndSaveSwiftCode(SwiftCode swiftCode) {
        if (swiftCode.getAddress() == null || swiftCode.getBankName() == null || swiftCode.getCountryISO2() == null ||
                swiftCode.getCountryName() == null || swiftCode.getCode() == null) {
            throw new IllegalArgumentException("All fields are required");
        }

        if (!swiftCode.getCountryISO2().matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("Invalid country ISO2 code format. It must be exactly 2 uppercase letters.");
        }

        String uppercasedCode = swiftCode.getCode().toUpperCase();
        String uppercasedCountry = swiftCode.getCountryName().toUpperCase();
        if (uppercasedCode.length() != 11) {
            throw new IllegalArgumentException("SWIFT code must be exactly 11 characters long.");
        }

        boolean isHeadquarter = uppercasedCode.endsWith("XXX");

        Optional<SwiftCode> existingSwiftCode = swiftCodeRepository.findById(uppercasedCode);
        if (existingSwiftCode.isPresent()) {
            throw new IllegalArgumentException("SWIFT code like this one already exists in the database.");
        }

        swiftCode.setCountryName(uppercasedCountry);
        swiftCode.setCode(uppercasedCode);
        swiftCode.setHeadquarterFlag(isHeadquarter);
        swiftCodeRepository.save(swiftCode);
    }


//    public void validateAndSaveSwiftCode(SwiftCode swiftCode) {
//        private static final Map<String, String> COUNTRY_ISO2_MAP = new HashMap<>();
//        static {
//            COUNTRY_ISO2_MAP.put("POLAND", "PL");
//            COUNTRY_ISO2_MAP.put("UNITED STATES", "US");
//            COUNTRY_ISO2_MAP.put("GERMANY", "DE");
//            COUNTRY_ISO2_MAP.put("FRANCE", "FR");
//        }
//
//        if (swiftCode.getAddress() == null || swiftCode.getBankName() == null || swiftCode.getCountryISO2() == null ||
//                swiftCode.getCountryName() == null || swiftCode.getCode() == null) {
//            throw new IllegalArgumentException("All fields are required");
//        }
//
//        if (!swiftCode.getCountryISO2().matches("[A-Z]{2}")) {
//            throw new IllegalArgumentException("Invalid country ISO2 code format. It must be exactly 2 uppercase letters.");
//        }
//
//        String uppercasedCode = swiftCode.getCode().toUpperCase();
//        String uppercasedCountry = swiftCode.getCountryName().toUpperCase();
//
//        if (uppercasedCode.length() != 11) {
//            throw new IllegalArgumentException("SWIFT code must be exactly 11 characters long.");
//        }
//
//        String expectedISO2 = COUNTRY_ISO2_MAP.get(uppercasedCountry);
//        if (expectedISO2 != null && !swiftCode.getCountryISO2().equals(expectedISO2)) {
//            throw new IllegalArgumentException("Country ISO2 code '" + swiftCode.getCountryISO2() +
//                    "' does not match country name '" + uppercasedCountry + "'. Expected: '" + expectedISO2 + "'");
//        }
//
//        boolean isHeadquarter = uppercasedCode.endsWith("XXX");
//
//        Optional<SwiftCode> existingSwiftCode = swiftCodeRepository.findById(uppercasedCode);
//        if (existingSwiftCode.isPresent()) {
//            throw new IllegalArgumentException("SWIFT code like this one already exists in the database.");
//        }
//        swiftCode.setCountryName(uppercasedCountry);
//        swiftCode.setCode(uppercasedCode);
//        swiftCode.setHeadquarterFlag(isHeadquarter);
//        swiftCodeRepository.save(swiftCode);
//    }


    @Transactional
    public boolean deleteBySwiftCode(String code){
        SwiftCode swiftCode = swiftCodeRepository.findByCode(code).orElse(null);

        if(swiftCode == null){
            return false;
        }

        if(swiftCode.getBranches() != null){
            for(SwiftCode branch : swiftCode.getBranches()){
                branch.setHeadquarter(null);
            }
            swiftCodeRepository.saveAll(swiftCode.getBranches());
        }
        swiftCodeRepository.delete(swiftCode);
        return true;
    }
}
