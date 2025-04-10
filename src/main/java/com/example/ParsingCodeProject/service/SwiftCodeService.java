package com.example.ParsingCodeProject.service;

import com.example.ParsingCodeProject.dto.CountryDTO;
import com.example.ParsingCodeProject.dto.CountryResponse;
import com.example.ParsingCodeProject.dto.SwiftCodeRequestDTO;
import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.repository.SwiftCodeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        List<CountryDTO> dtoList = new ArrayList<>();
        for (SwiftCode code : swiftCodes) {
            dtoList.add(new CountryDTO(code));
        }

        return Optional.of(new CountryResponse(countryISO2, countryName, dtoList));
    }


    @Transactional
    public void validateAndSaveSwiftCode(SwiftCodeRequestDTO dto) {
        String swiftCode = dto.getSwiftCode().toUpperCase();
        String countryName = dto.getCountryName().toUpperCase();
        boolean isHeadquarter = dto.isHeadquarter();

        validateSwiftCodeConsistency(swiftCode, isHeadquarter);
        checkCodeUniqueness(swiftCode);

        SwiftCode swiftCodeEntity = createSwiftCode(dto, swiftCode, countryName);
        swiftCodeRepository.save(swiftCodeEntity);

        if (isHeadquarter) {
            linkBranchesToHeadquarter(swiftCode, swiftCodeEntity);
        } else {
            linkHeadquarterToBranch(swiftCode, swiftCodeEntity);
        }
    }

    private void validateSwiftCodeConsistency(String swiftCode, boolean isHeadquarter) {
        boolean endsWithXXX = swiftCode.endsWith("XXX");
        if (endsWithXXX != isHeadquarter) {
            throw new IllegalArgumentException("SWIFT code ending must match isHeadquarter flag");
        }
    }

    private void checkCodeUniqueness(String swiftCode) {
        if (swiftCodeRepository.findById(swiftCode).isPresent()) {
            throw new IllegalArgumentException("SWIFT code already exists");
        }
    }

    private SwiftCode createSwiftCode(SwiftCodeRequestDTO dto, String swiftCode, String countryName) {
        SwiftCode entity = new SwiftCode();
        entity.setCode(swiftCode);
        entity.setAddress(dto.getAddress());
        entity.setBankName(dto.getBankName());
        entity.setCountryISO2(dto.getCountryISO2());
        entity.setCountryName(countryName);
        entity.setHeadquarterFlag(dto.isHeadquarter());
        return entity;
    }

    private void linkBranchesToHeadquarter(String swiftCode, SwiftCode headquarter) {
        String baseCode = swiftCode.substring(0, 8);
        List<SwiftCode> unlinkedBranches = swiftCodeRepository.findAllByHeadquarterIsNullAndHeadquarterFlagFalse();
        for (SwiftCode branch : unlinkedBranches) {
            if (branch.getCode().startsWith(baseCode)) {
                branch.setHeadquarter(headquarter);
                swiftCodeRepository.save(branch);
            }
        }
    }

    private void linkHeadquarterToBranch(String swiftCode, SwiftCode branch) {
        String hqCode = swiftCode.substring(0, 8) + "XXX";
        swiftCodeRepository.findById(hqCode).ifPresent(headquarter -> {
            branch.setHeadquarter(headquarter);
            swiftCodeRepository.save(branch);
        });
    }


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
