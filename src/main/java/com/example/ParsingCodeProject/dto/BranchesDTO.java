package com.example.ParsingCodeProject.dto;

import com.example.ParsingCodeProject.entity.SwiftCode;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@JsonPropertyOrder({ "address", "bankName", "countryISO2", "countryName", "isHeadquarter", "swiftCode" })
@Getter
public class BranchesDTO {
    // Endpoint 1 for branch without connect to HQ
    private String address;
    private String bankName;
    private String countryISO2;
    private String countryName;
    private boolean isHeadquarter;
    private String swiftCode;

    public BranchesDTO(SwiftCode swiftCode){
        this.address = swiftCode.getAddress();
        this.bankName = swiftCode.getBankName();
        this.countryISO2 = swiftCode.getCountryISO2();
        this.countryName = swiftCode.getCountryName();
        this.isHeadquarter = swiftCode.getHeadquarterFlag();
        this.swiftCode = swiftCode.getCode();
    }

    @JsonGetter("isHeadquarter")
    public Boolean isHeadquarter() {
        return isHeadquarter;
    }
}
