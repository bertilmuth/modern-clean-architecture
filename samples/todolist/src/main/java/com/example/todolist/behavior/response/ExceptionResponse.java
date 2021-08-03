package com.example.todolist.behavior.response;

import java.util.Date;

import lombok.NonNull;
import lombok.Value;

/**
 * A response in case an exception occured during request processing.
 * 
 * @author b_muth
 *
 */
@Value
public class ExceptionResponse {
	/**
	 * The time the exception response was created.
	 * @return the reponse time
	 */
	@NonNull
	Date timestamp;
	
	/**
	 * The http status code of the response.
	 * @return http response status 
	 */
	int status;
	
	/**
	 * The reason phrase of the http status.
	 * @return the http status reason
	 */
	@NonNull
	String error;
	
	/**
	 * The message of the eception that occured.
	 * @return the exception message
	 */
	@NonNull
	String message;
}
