package com.example.todolist.domain;

/**
 * An exception that is thrown when there is an attempt to access a todo list
 * that hasn't been created before.
 * 
 * @author b_muth
 *
 */
@SuppressWarnings("serial")
public class TodoListNotFound extends TodoListException {
	/**
	 * Creates an exception with the specified message.
	 * 
	 * @param message the message to display to the user.
	 */
	public TodoListNotFound(String message) {
		super(message);
	}
}
