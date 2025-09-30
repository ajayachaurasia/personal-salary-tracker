package com.example.salarytracker.exception;

import java.io.IOException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IOException.class)
	public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
		ErrorResponse message = new ErrorResponse("I/O error while processing file.", ex.getMessage());
		return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		ErrorResponse message = new ErrorResponse("Invalid file format.", ex.getMessage());
		return new ResponseEntity<>(message, HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		ErrorResponse message = new ErrorResponse("Duplicate entry or constraint violation.",
				ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage());
		return new ResponseEntity<>(message, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		ErrorResponse message = new ErrorResponse("Unexpected error.", ex.getMessage());
		return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
