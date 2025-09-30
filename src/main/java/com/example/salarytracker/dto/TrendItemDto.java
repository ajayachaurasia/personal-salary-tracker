package com.example.salarytracker.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrendItemDto {
	
	private String financialYear;
	private Double fullCTC;
	
}
