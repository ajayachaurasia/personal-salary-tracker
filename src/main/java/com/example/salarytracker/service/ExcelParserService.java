package com.example.salarytracker.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.example.salarytracker.entity.SalaryRecord;

@Service
public class ExcelParserService {

	public List<SalaryRecord> parseMultiYearExcel(InputStream inputStream, String employeeEmail) throws Exception {
		List<SalaryRecord> records = new ArrayList<>();
		try (Workbook workbook = new XSSFWorkbook(inputStream)) {
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet sheet = workbook.getSheetAt(i);
				String sheetName = sheet.getSheetName();
				Iterator<Row> rows = sheet.iterator();
				if (!rows.hasNext())
					continue;
				Row header = rows.next();
				int colCompany = -1, colCurrency = -1, colFixed = -1, colVariable = -1, colDeductions = -1;
				for (Cell c : header) {
					String v = c.getStringCellValue().trim().toLowerCase();
					if (v.contains("company"))
						colCompany = c.getColumnIndex();
					if (v.contains("currency"))
						colCurrency = c.getColumnIndex();
					if (v.contains("fixed"))
						colFixed = c.getColumnIndex();
					if (v.contains("variable"))
						colVariable = c.getColumnIndex();
					if (v.contains("deduction"))
						colDeductions = c.getColumnIndex();
				}
				while (rows.hasNext()) {
					Row r = rows.next();
					if (isRowEmpty(r))
						continue;
					String company = getStringCellValue(r, colCompany);
					String currency = getStringCellValue(r, colCurrency);
					Double fixed = getNumericCellValue(r, colFixed);
					Double variable = getNumericCellValue(r, colVariable);
					Double deductions = getNumericCellValue(r, colDeductions);
					SalaryRecord rec = SalaryRecord.builder().employeeEmail(employeeEmail).company(company)
							.currency(currency).financialYear(sheetName).fixedCTC(fixed == null ? 0.0 : fixed)
							.variable(variable == null ? 0.0 : variable)
							.deductions(deductions == null ? 0.0 : deductions).build();
					records.add(rec);
				}
			}
		}
		return records;
	}

	private boolean isRowEmpty(Row r) {
		for (Cell c : r) {
			if (c != null && c.getCellType() != CellType.BLANK)
				return false;
		}
		return true;
	}

	private String getStringCellValue(Row r, int idx) {
		if (idx < 0)
			return null;
		Cell c = r.getCell(idx);
		if (c == null)
			return null;
		if (c.getCellType() == CellType.STRING)
			return c.getStringCellValue();
		if (c.getCellType() == CellType.NUMERIC)
			return String.valueOf(c.getNumericCellValue());
		return c.toString();
	}

	private Double getNumericCellValue(Row r, int idx) {
		if (idx < 0)
			return 0.0;
		Cell c = r.getCell(idx);
		if (c == null)
			return 0.0;
		if (c.getCellType() == CellType.NUMERIC)
			return c.getNumericCellValue();
		try {
			String s = c.getStringCellValue();
			return Double.parseDouble(s);
		} catch (Exception e) {
			return 0.0;
		}
	}
}
