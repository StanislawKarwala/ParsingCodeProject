package com.example.ParsingCodeProject.dto;

import com.example.ParsingCodeProject.entity.Branch;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@JsonPropertyOrder({"address", "bankName", "countryISO2", "countryName", "isHeadquarter", "swiftCode"})
@Getter
public class BranchDTO {
    private String address;
    private String bankName;
    private String countryISO2;
    private String countryName;
    private boolean isHeadquarter;
    private String swiftCode;

    public BranchDTO(Branch branch) {
        this.address = branch.getAddress();
        this.bankName = branch.getBankName();
        this.countryISO2 = branch.getCountryISO2();
        this.countryName = branch.getCountryName();
        this.isHeadquarter = branch.getHeadquarterFlag();
        this.swiftCode = branch.getCode();
    }

    @JsonGetter("isHeadquarter")
    public Boolean isHeadquarter() {
        return isHeadquarter;
    }
}