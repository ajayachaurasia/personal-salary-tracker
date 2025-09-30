package com.example.salarytracker.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.salarytracker.dto.SalaryComparisonDto;
import com.example.salarytracker.dto.SalaryDetailDto;
import com.example.salarytracker.dto.TrendItemDto;
import com.example.salarytracker.entity.SalaryRecord;
import com.example.salarytracker.repository.SalaryRecordRepository;

@Service
public class SalaryService {

	private final SalaryRecordRepository repo;

	private final CurrencyService currencyService;

	public SalaryService(SalaryRecordRepository repo, CurrencyService currencyService) {
		this.repo = repo;
		this.currencyService = currencyService;
	}

	@Transactional
	public List<SalaryRecord> saveAll(List<SalaryRecord> records) {
		return repo.saveAll(records);
	}

	public List<SalaryDetailDto> getByYear(String employeeEmail, String year, String baseCurrency) {
		List<SalaryRecord> recs = repo.findByEmployeeEmailAndFinancialYear(employeeEmail, year);
		return recs.stream().map(r -> mapToDto(r, baseCurrency)).collect(Collectors.toList());
	}

	public List<TrendItemDto> getTrend(String employeeEmail, String baseCurrency) {
		List<SalaryRecord> recs = repo.findByEmployeeEmailOrderByFinancialYearAsc(employeeEmail);
		Map<String, Double> byYear = new LinkedHashMap<>();
		for (SalaryRecord r : recs) {
			double converted = currencyService.convert(r.getFullCTC(), r.getCurrency(), baseCurrency);
			byYear.put(r.getFinancialYear(), converted);
		}
		return byYear.entrySet().stream().map(e -> new TrendItemDto(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}

	public SalaryDetailDto getLatest(String employeeEmail, String baseCurrency) {
		Optional<SalaryRecord> opt = repo.findFirstByEmployeeEmailOrderByCreatedAtDesc(employeeEmail);
		return opt.map(r -> mapToDto(r, baseCurrency)).orElse(null);
	}

	@Transactional
	public SalaryDetailDto applyHike(String employeeEmail, String year, double hikePercent, String baseCurrency) {
		List<SalaryRecord> recs = repo.findByEmployeeEmailAndFinancialYear(employeeEmail, year);
		if (recs.isEmpty())
			return null;
		SalaryRecord r = recs.get(0);
		r.setFixedCTC(new BigDecimal(r.getFixedCTC() * (1 + hikePercent / 100.0)).setScale(2, RoundingMode.HALF_UP)
				.doubleValue());
		r.setVariable(new BigDecimal(r.getVariable() * (1 + hikePercent / 100.0)).setScale(2, RoundingMode.HALF_UP)
				.doubleValue());
		r = repo.save(r);
		return mapToDto(r, baseCurrency);
	}

	@Transactional
	public SalaryDetailDto switchCompany(String employeeEmail, SalaryRecord newRecord, String baseCurrency) {
		newRecord.setEmployeeEmail(employeeEmail);
		SalaryRecord saved = repo.save(newRecord);
		return mapToDto(saved, baseCurrency);
	}

	public List<SalaryComparisonDto> compareByCompany(String employeeEmail, String baseCurrency) {
		List<SalaryRecord> salaryRecordlist = repo.findByEmployeeEmailOrderByFinancialYearAsc(employeeEmail);

		List<SalaryComparisonDto> comparisionList = new ArrayList<>();
		for (SalaryRecord list : salaryRecordlist) {
			SalaryComparisonDto comparision = SalaryComparisonDto.builder().financialYear(list.getFinancialYear())
					.fixedCTC(list.getFixedCTC()).variable(list.getVariable()).fullCTC(list.getFullCTC()).build();
			comparisionList.add(comparision);
		}
		return comparisionList;
	}

	private SalaryDetailDto mapToDto(SalaryRecord r, String baseCurrency) {
		double converted = r.getFullCTC() == null ? 0.0
				: currencyService.convert(r.getFullCTC(), r.getCurrency(), baseCurrency);
		return SalaryDetailDto.builder().employeeEmail(r.getEmployeeEmail()).company(r.getCompany())
				.financialYear(r.getFinancialYear()).fixedCTC(r.getFixedCTC()).variable(r.getVariable())
				.deductions(r.getDeductions()).currency(r.getCurrency()).fullCTC(r.getFullCTC())
				.convertedFullCTC(converted).build();
	}
}
