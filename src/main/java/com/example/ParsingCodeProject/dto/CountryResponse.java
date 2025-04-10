package com.example.ParsingCodeProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class CountryResponse {
    // Endpoint 2 for country multiple swift codes
    private String countryISO2;
    private String countryName;
    private List<CountryDTO> swiftCodes;
}
