package com.example.ParsingCodeProject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "swift_codes")
@Data
@NoArgsConstructor
public class SwiftCode {

    @Id
    private String code;

    private String address;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "country_iso2", length = 2, nullable = false)
    private String countryISO2;

    @Column(name = "country_name")
    private String countryName;

    @Transient
    private boolean headquarterFlag;

    @ManyToOne
    @JoinColumn(name = "headquarter_code", referencedColumnName = "code")
    private SwiftCode headquarter;

    @OneToMany(mappedBy = "headquarter", cascade = CascadeType.ALL)
    private List<SwiftCode> branches;

    public SwiftCode(String code, String address, String bankName, String countryISO2, String countryName){
        this.code = code;
        this.address = address;
        this.bankName = bankName;
        this.countryISO2 = countryISO2;
        this.countryName = countryName;
        this.headquarterFlag = code.endsWith("XXX");
    }

    public boolean getHeadquarterFlag() {
        return code.endsWith("XXX");
    }

}
