package com.example.salarytracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.salarytracker.entity.SalaryRecord;

public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {
	
	List<SalaryRecord> findByEmployeeEmailOrderByFinancialYearAsc(String employeeEmail);

	Optional<SalaryRecord> findFirstByEmployeeEmailOrderByCreatedAtDesc(String employeeEmail);

	List<SalaryRecord> findByEmployeeEmailAndFinancialYear(String employeeEmail, String financialYear);

	List<SalaryRecord> findByEmployeeEmailAndCompany(String employeeEmail, String company);

	List<SalaryRecord> findByCompany(String company);

}
