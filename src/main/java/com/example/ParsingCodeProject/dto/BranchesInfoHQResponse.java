package com.example.ParsingCodeProject.dto;

import com.example.ParsingCodeProject.entity.Branch;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@JsonPropertyOrder({"address", "bankName", "countryISO2", "isHeadquarter", "swiftCode"})
@Getter
public class BranchesInfoHQResponse {
    private String address;
    private String bankName;
    private String countryISO2;
    private boolean isHeadquarter;
    private String swiftCode;

    public BranchesInfoHQResponse(Branch branch) {
        this.address = branch.getAddress();
        this.bankName = branch.getBankName();
        this.countryISO2 = branch.getCountryISO2();
        this.isHeadquarter = branch.getHeadquarterFlag();
        this.swiftCode = branch.getCode();
    }

    @JsonGetter("isHeadquarter")
    public Boolean isHeadquarter() {
        return isHeadquarter;
    }
}