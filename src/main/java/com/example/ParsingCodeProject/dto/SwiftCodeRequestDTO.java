package com.example.ParsingCodeProject.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonPropertyOrder({"address", "bankName", "countryISO2", "countryName", "isHeadquarter", "swiftCode"})
@Getter
@Setter
public class SwiftCodeRequestDTO {
    @NotNull(message = "Address is required")
    private String address;

    @NotNull(message = "Bank name is required")
    private String bankName;

    @NotNull(message = "Country ISO2 is required")
    @Pattern(regexp = "[A-Z]{2}", message = "Country ISO2 must be 2 uppercase letters")
    private String countryISO2;

    @NotNull(message = "Country name is required")
    private String countryName;

    private boolean isHeadquarter;

    @NotNull(message = "SWIFT code is required")
    @Size(min = 11, max = 11, message = "SWIFT code must be 11 characters")
    @JsonProperty("swiftCode")
    private String swiftCode;

    @JsonGetter("isHeadquarter")
    public Boolean isHeadquarter() {
        return isHeadquarter;
    }

    @JsonSetter("isHeadquarter")
    public void setHeadquarter(boolean isHeadquarter) {
        this.isHeadquarter = isHeadquarter;
    }
}