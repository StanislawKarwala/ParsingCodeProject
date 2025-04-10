package com.example.ParsingCodeProject;

import com.example.ParsingCodeProject.entity.SwiftCode;
import com.example.ParsingCodeProject.parser.SwiftCodeParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.nio.file.Paths;
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
	public void run(String... args) {
		String fileName = "Interns_2025_SWIFT_CODES.xlsx";
		Path filePath = Paths.get("src", "main", "resources", fileName);
		List<SwiftCode> swiftCodes = swiftCodeParser.parseSwiftCodes(filePath.toString());

		System.out.println("Przetworzone kody SWIFT:");
		swiftCodes.forEach(swift ->
				System.out.println("SwiftCode: " + swift.getCode() + " ||| Nazwa banku: " + swift.getBankName()
						+ " ||| Główny oddział?: " + swift.getHeadquarterFlag() + " ||| Kraj: " + swift.getCountryName())
		);

		swiftCodeParser.storeSwiftCodes(swiftCodes);
	}
}
