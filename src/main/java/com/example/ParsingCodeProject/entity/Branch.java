package com.example.ParsingCodeProject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "branches")
@Data
@NoArgsConstructor
public class Branch {

    @Id
    @JsonProperty("swiftCode")
    @Column(length = 11)
    private String code;

    @Column(length = 150)
    private String address;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "country_iso2", length = 2, nullable = false)
    private String countryISO2;

    @Column(name = "country_name", length = 50)
    private String countryName;

    @Column(name = "headquarter_flag", nullable = false, columnDefinition = "boolean default true")
    @JsonProperty("isHeadquarter")
    private boolean headquarterFlag;

    @ManyToOne
    @JoinColumn(name = "hq_swift", referencedColumnName = "code")
    @JsonIgnore
    @ToString.Exclude
    private Headquarter headquarter;

    public Branch(String code, String address, String bankName, String countryISO2, String countryName) {
        this.code = code;
        this.address = address;
        this.bankName = bankName;
        this.countryISO2 = countryISO2;
        this.countryName = countryName;
        this.headquarterFlag = code.endsWith("XXX");
    }

    public boolean getHeadquarterFlag() {
        return headquarterFlag;
    }
}