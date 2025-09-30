package com.example.salarytracker.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "salary", uniqueConstraints = { @UniqueConstraint(columnNames = { "employeeEmail", "financialYear" }) })
public class SalaryRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String employeeEmail;
	private String company;
	private String currency;
	private String financialYear;
	private Double fixedCTC;
	private Double variable;
	private Double deductions;
	private Double fullCTC;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@PrePersist
	void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = createdAt;
		recalc();
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = LocalDateTime.now();
		recalc();
	}

	private void recalc() {
		double f = fixedCTC == null ? 0.0 : fixedCTC;
		double v = variable == null ? 0.0 : variable;
		double d = deductions == null ? 0.0 : deductions;
		fullCTC = f + v - d;
	}
}
