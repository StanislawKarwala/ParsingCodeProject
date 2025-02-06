package com.example.ParsingCodeProject.dto;

import com.example.ParsingCodeProject.entity.SwiftCode;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@JsonPropertyOrder({ "address", "bankName", "countryISO2", "isHeadquarter", "swiftCode" })
@Getter
public class BranchesInfoHQResponse {
    private String address;
    private String bankName;
    private String countryISO2;
    private boolean isHeadquarter;
    private String swiftCode;

    public BranchesInfoHQResponse(SwiftCode swiftCode){
        this.address = swiftCode.getAddress();
        this.bankName = swiftCode.getBankName();
        this.countryISO2 = swiftCode.getCountryISO2();
        this.isHeadquarter = swiftCode.getHeadquarterFlag();
        this.swiftCode = swiftCode.getCode();
    }

    @JsonGetter("isHeadquarter")
    public Boolean isHeadquarter() {
        return isHeadquarter;
    }
}
