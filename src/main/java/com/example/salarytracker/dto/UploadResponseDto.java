package com.example.salarytracker.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponseDto {
	
	private int recordsSaved;
	private String message;
	
}
