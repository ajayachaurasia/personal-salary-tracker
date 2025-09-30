package com.example.salarytracker.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryDetailDto {
	
	private String employeeEmail;
	private String company;
	private String financialYear;
	private Double fixedCTC;
	private Double variable;
	private Double deductions;
	private String currency;
	private Double fullCTC;
	private Double convertedFullCTC;
	
}
