package com.example.salarytracker.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryComparisonDto {
	
	private String financialYear;
	private Double fixedCTC;
	private Double variable;
	private Double fullCTC;
	
}
