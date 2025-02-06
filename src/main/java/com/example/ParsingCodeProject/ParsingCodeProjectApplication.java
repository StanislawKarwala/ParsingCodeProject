package com.example.ParsingCodeProject;

import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.parser.SwiftCodeParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class ParsingCodeProjectApplication implements CommandLineRunner {

	private final SwiftCodeParser swiftCodeParser;

	public ParsingCodeProjectApplication(SwiftCodeParser swiftCodeParser){
		this.swiftCodeParser = swiftCodeParser;
	}

	public static void main(String[] args) {
		SpringApplication.run(ParsingCodeProjectApplication.class, args);
	}

	@Override
	public void run(String... args){
		String filePath = "src\\main\\resources\\Interns_2025_SWIFT_CODES.xlsx";
		List<SwiftCode> swiftCodes = swiftCodeParser.parseSwiftCodes(filePath);

		System.out.println("Przetworzone kody SWIFT:");
		swiftCodes.forEach(swift ->
				System.out.println("SwiftCode: " + swift.getCode() + " ||| Nazwa banku: " + swift.getBankName()
						+ " ||| Główny oddział?: " + swift.getHeadquarterFlag() + " ||| Kraj: " + swift.getCountryName())
		);
	}
}
