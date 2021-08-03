package com.example.todolist.domain;

/**
 * Super class for all domain exceptions of the todo list.
 * 
 * @author b_muth
 *
 */
@SuppressWarnings("serial")
public abstract class TodoListException extends RuntimeException {
	/**
	 * Creates an exception with the specified message.
	 * 
	 * @param message the message to display to the user.
	 */
	public TodoListException(String message) {
		super(message);
	}
}
