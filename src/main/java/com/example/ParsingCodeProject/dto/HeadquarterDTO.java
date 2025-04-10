package com.example.ParsingCodeProject.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.List;

@JsonPropertyOrder({"address", "bankName", "countryISO2", "countryName", "isHeadquarter", "swiftCode", "branches"})
@Getter
public class HeadquarterDTO {
    private String address;
    private String bankName;
    private String countryISO2;
    private String countryName;
    private boolean isHeadquarter;
    private String swiftCode;
    private List<BranchesInfoHQResponse> branches;

    public HeadquarterDTO(String address, String bankName, String countryISO2, String countryName,
                          boolean isHeadquarter, String swiftCode, List<BranchesInfoHQResponse> branches) {
        this.address = address;
        this.bankName = bankName;
        this.countryISO2 = countryISO2;
        this.countryName = countryName;
        this.isHeadquarter = isHeadquarter;
        this.swiftCode = swiftCode;
        this.branches = branches;
    }

    @JsonGetter("isHeadquarter")
    public Boolean isHeadquarter() {
        return isHeadquarter;
    }
}