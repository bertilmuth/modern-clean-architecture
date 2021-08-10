package com.example.todolist.adapter.spring;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.todolist.behavior.response.ExceptionResponse;

@ControllerAdvice
class TodoListExceptionHandling {

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<ExceptionResponse> handle(Exception e) {
		return responseOf(e, BAD_REQUEST);
	}

	private ResponseEntity<ExceptionResponse> responseOf(Throwable exception, final HttpStatus httpStatus) {
		final Date timestamp = new Date();
		final int statusValue = httpStatus.value();
		final String error = httpStatus.getReasonPhrase();
		final String message = exception.getMessage();

		final ExceptionResponse response = new ExceptionResponse(timestamp, statusValue, error, message);
		return new ResponseEntity<>(response, httpStatus);
	}
}