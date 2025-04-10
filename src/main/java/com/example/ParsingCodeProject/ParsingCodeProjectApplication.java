package com.example.ParsingCodeProject;

import com.example.ParsingCodeProject.entity.Branch;
import com.example.ParsingCodeProject.entity.Headquarter;
import com.example.ParsingCodeProject.parser.SwiftCodeParser;
import com.example.ParsingCodeProject.repository.BranchRepository;
import com.example.ParsingCodeProject.repository.HeadquarterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
public class ParsingCodeProjectApplication implements CommandLineRunner {

	private final SwiftCodeParser swiftCodeParser;
	private final HeadquarterRepository headquarterRepository;
	private final BranchRepository branchRepository;

	public ParsingCodeProjectApplication(SwiftCodeParser swiftCodeParser,
										 HeadquarterRepository headquarterRepository,
										 BranchRepository branchRepository) {
		this.swiftCodeParser = swiftCodeParser;
		this.headquarterRepository = headquarterRepository;
		this.branchRepository = branchRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(ParsingCodeProjectApplication.class, args);
	}

	@Override
	public void run(String... args) {
		String fileName = "Interns_2025_SWIFT_CODES.xlsx";
		Path filePath = Paths.get("src", "main", "resources", fileName);

		swiftCodeParser.parseAndStoreSwiftCodes(filePath.toString());
	}
}