package com.example.salarytracker.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.salarytracker.dto.SalaryComparisonDto;
import com.example.salarytracker.dto.SalaryDetailDto;
import com.example.salarytracker.dto.TrendItemDto;
import com.example.salarytracker.dto.UploadResponseDto;
import com.example.salarytracker.entity.SalaryRecord;
import com.example.salarytracker.service.ExcelParserService;
import com.example.salarytracker.service.SalaryService;

@RestController
@RequestMapping("/api/salaries")
public class SalaryController {

	private final ExcelParserService parser;

	private final SalaryService service;

	public SalaryController(ExcelParserService parser, SalaryService service) {
		this.parser = parser;
		this.service = service;
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UploadResponseDto> uploadExcel(@RequestParam("file") MultipartFile file,
			@RequestParam("employeeEmail") String employeeEmail) throws Exception {
		List<SalaryRecord> parsedRecord = parser.parseMultiYearExcel(file.getInputStream(), employeeEmail);
		service.saveAll(parsedRecord);
		return ResponseEntity.ok(new UploadResponseDto(parsedRecord.size(), "Uploaded"));
	}

	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@GetMapping("/{year}")
	public ResponseEntity<List<SalaryDetailDto>> getByYear(@PathVariable String year,
			@RequestParam String employeeEmail, @RequestParam(defaultValue = "USD") String baseCurrency) {
		return ResponseEntity.ok(service.getByYear(employeeEmail, year, baseCurrency));
	}

	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@GetMapping("/trend")
	public ResponseEntity<List<TrendItemDto>> getTrend(@RequestParam String employeeEmail,
			@RequestParam(defaultValue = "USD") String baseCurrency) {
		return ResponseEntity.ok(service.getTrend(employeeEmail, baseCurrency));
	}

	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@GetMapping("/latest")
	public ResponseEntity<SalaryDetailDto> getLatest(@RequestParam String employeeEmail,
			@RequestParam(defaultValue = "USD") String baseCurrency) {
		SalaryDetailDto dto = service.getLatest(employeeEmail, baseCurrency);
		if (dto == null)
			return ResponseEntity.notFound().build();
		return ResponseEntity.ok(dto);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{year}/hike")
	public ResponseEntity<SalaryDetailDto> applyHike(@PathVariable String year, @RequestParam String employeeEmail,
			@RequestParam double hikePercent, @RequestParam(defaultValue = "USD") String baseCurrency) {
		SalaryDetailDto dto = service.applyHike(employeeEmail, year, hikePercent, baseCurrency);
		if (dto == null)
			return ResponseEntity.notFound().build();
		return ResponseEntity.ok(dto);
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/switch")
	public ResponseEntity<SalaryDetailDto> switchCompany(@RequestParam String employeeEmail,
			@RequestBody SalaryRecord newRecord, @RequestParam(defaultValue = "USD") String baseCurrency) {
		SalaryDetailDto dto = service.switchCompany(employeeEmail, newRecord, baseCurrency);
		return ResponseEntity.ok(dto);
	}

	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@GetMapping("/compare")
	public ResponseEntity<List<SalaryComparisonDto>> compare(@RequestParam String employeeEmail,
			@RequestParam(defaultValue = "USD") String baseCurrency) {
		return ResponseEntity.ok(service.compareByCompany(employeeEmail, baseCurrency));
	}
}
