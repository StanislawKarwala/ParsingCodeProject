package com.example.ParsingCodeProject.service;

import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.repository.SwiftCodeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SwiftCodeService {
    private final SwiftCodeRepository swiftCodeRepository;

    public SwiftCodeService(SwiftCodeRepository swiftCodeRepository){
        this.swiftCodeRepository = swiftCodeRepository;
    }

    public void saveSwiftCodesData(SwiftCode code){
        swiftCodeRepository.save(code);
    }

    public boolean deleteBySwiftCode(String code){
        Optional<SwiftCode> swiftCodeOptional = swiftCodeRepository.findByCode(code);

        if(swiftCodeOptional.isPresent()){
            swiftCodeRepository.delete(swiftCodeOptional.get());
            return true;
        }
        return false;
    }
}
