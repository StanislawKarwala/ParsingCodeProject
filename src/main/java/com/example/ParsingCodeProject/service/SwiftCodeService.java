package com.example.ParsingCodeProject.service;

import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.repository.SwiftCodeRepository;
import org.springframework.stereotype.Service;

@Service
public class SwiftCodeService {
    private final SwiftCodeRepository swiftCodeRepository;

    public SwiftCodeService(SwiftCodeRepository swiftCodeRepository){
        this.swiftCodeRepository = swiftCodeRepository;
    }

    public void saveSwiftCodesData(SwiftCode code){
        swiftCodeRepository.save(code);
    }
}
