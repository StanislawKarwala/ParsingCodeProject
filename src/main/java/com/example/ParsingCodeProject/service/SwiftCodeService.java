package com.example.ParsingCodeProject.service;

import com.example.ParsingCodeProject.dto.*;
import com.example.ParsingCodeProject.entity.Branch;
import com.example.ParsingCodeProject.entity.Headquarter;
import com.example.ParsingCodeProject.repository.BranchRepository;
import com.example.ParsingCodeProject.repository.HeadquarterRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class SwiftCodeService {
    private final HeadquarterRepository headquarterRepository;
    private final BranchRepository branchRepository;

    public SwiftCodeService(HeadquarterRepository headquarterRepository, BranchRepository branchRepository) {
        this.headquarterRepository = headquarterRepository;
        this.branchRepository = branchRepository;
    }

    public void saveHeadquarter(Headquarter headquarter) {
        headquarterRepository.save(headquarter);
    }

    public void saveBranch(Branch branch) {
        branchRepository.save(branch);
    }

    public Optional<Object> getSwiftCodeByCode(String swiftCode) {
        Optional<Headquarter> headquarter = headquarterRepository.findByCode(swiftCode);
        if (headquarter.isPresent()) {
            Headquarter hq = headquarter.get();
            List<BranchesInfoHQResponse> branches = new ArrayList<>();
            for (Branch branch : hq.getBranches()) {
                branches.add(new BranchesInfoHQResponse(branch));
            }
            return Optional.of(new HeadquarterDTO(
                    hq.getAddress(), hq.getBankName(), hq.getCountryISO2(),
                    hq.getCountryName(), hq.getHeadquarterFlag(), hq.getCode(),
                    branches));
        }
        Optional<Branch> branch = branchRepository.findByCode(swiftCode);
        return branch.map(BranchDTO::new);
    }

    public Optional<CountryResponse> getSwiftCodesByCountry(String countryISO2) {
        List<Headquarter> headquarters = headquarterRepository.findByCountryISO2(countryISO2);
        List<Branch> branches = branchRepository.findByCountryISO2(countryISO2);

        if (headquarters.isEmpty() && branches.isEmpty()) {
            return Optional.empty();
        }

        List<CountryDTO> swiftCodes = new ArrayList<>();
        for (Headquarter hq : headquarters) {
            swiftCodes.add(new CountryDTO(hq));
        }
        for (Branch branch : branches) {
            swiftCodes.add(new CountryDTO(branch));
        }

        String countryName = headquarters.isEmpty() ? branches.get(0).getCountryName() : headquarters.get(0).getCountryName();
        return Optional.of(new CountryResponse(countryISO2, countryName, swiftCodes));
    }


    @Transactional
    public void validateAndSaveSwiftCode(SwiftCodeRequestDTO dto) {
        String swiftCode = dto.getSwiftCode().toUpperCase();
        String countryName = dto.getCountryName().toUpperCase();
        boolean isHeadquarter = dto.isHeadquarter();

        validateSwiftCodeConsistency(swiftCode, isHeadquarter);
        checkCodeUniqueness(swiftCode);
        saveSwiftCode(dto, swiftCode, countryName);
    }

    private void validateSwiftCodeConsistency(String swiftCode, boolean isHeadquarter) {
        boolean endsWithXXX = swiftCode.endsWith("XXX");
        if (endsWithXXX != isHeadquarter) {
            throw new IllegalArgumentException("SWIFT code ending must match isHeadquarter flag");
        }
    }

    private void checkCodeUniqueness(String swiftCode) {
        if (headquarterRepository.findByCode(swiftCode).isPresent() ||
                branchRepository.findByCode(swiftCode).isPresent()) {
            throw new IllegalArgumentException("SWIFT code already exists");
        }
    }

    private void saveSwiftCode(SwiftCodeRequestDTO dto, String swiftCode, String countryName) {
        if (dto.isHeadquarter()) {
            Headquarter headquarter = createHeadquarter(dto, swiftCode, countryName);
            headquarterRepository.save(headquarter);
            linkBranchesToHeadquarter(swiftCode, headquarter);
        } else {
            Branch branch = createBranch(dto, swiftCode, countryName);
            linkHeadquarterToBranch(swiftCode, branch);
            branchRepository.save(branch);
        }
    }

    private Headquarter createHeadquarter(SwiftCodeRequestDTO dto, String swiftCode, String countryName) {
        Headquarter hq = new Headquarter();
        hq.setCode(swiftCode);
        hq.setAddress(dto.getAddress());
        hq.setBankName(dto.getBankName());
        hq.setCountryISO2(dto.getCountryISO2());
        hq.setCountryName(countryName);
        hq.setHeadquarterFlag(true);
        return hq;
    }

    private Branch createBranch(SwiftCodeRequestDTO dto, String swiftCode, String countryName) {
        Branch branch = new Branch();
        branch.setCode(swiftCode);
        branch.setAddress(dto.getAddress());
        branch.setBankName(dto.getBankName());
        branch.setCountryISO2(dto.getCountryISO2());
        branch.setCountryName(countryName);
        branch.setHeadquarterFlag(false);
        return branch;
    }

    private void linkBranchesToHeadquarter(String swiftCode, Headquarter headquarter) {
        String baseCode = swiftCode.substring(0, 8);
        List<Branch> unlinkedBranches = branchRepository.findAllByHeadquarterIsNull();
        for (Branch branch : unlinkedBranches) {
            if (branch.getCode().startsWith(baseCode)) {
                branch.setHeadquarter(headquarter);
                branchRepository.save(branch);
            }
        }
    }

    private void linkHeadquarterToBranch(String swiftCode, Branch branch) {
        String hqCode = swiftCode.substring(0, 8) + "XXX";
        headquarterRepository.findByCode(hqCode).ifPresent(branch::setHeadquarter);
    }

    @Transactional
    public boolean deleteBySwiftCode(String code) {
        Headquarter headquarter = headquarterRepository.findByCode(code).orElse(null);
        if (headquarter != null) {
            List<Branch> branches = headquarter.getBranches();
            if (branches != null && !branches.isEmpty()) {
                for (Branch branch : branches) {
                    branch.setHeadquarter(null);
                }
                branchRepository.saveAll(branches);
            }
            headquarterRepository.delete(headquarter);
            return true;
        }

        Branch branch = branchRepository.findByCode(code).orElse(null);
        if (branch != null) {
            Headquarter hq = branch.getHeadquarter();
            if (hq != null) {
                hq.getBranches().remove(branch);
                headquarterRepository.save(hq);
            }
            branchRepository.delete(branch);
            return true;
        }
        return false;
    }
}
