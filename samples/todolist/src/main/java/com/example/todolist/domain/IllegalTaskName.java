package com.example.todolist.domain;

/**
 * An exception that is thrown when a task name doesn't match the constraints,
 * e.g. contains only whitespace.
 * 
 * @author b_muth
 *
 */
@SuppressWarnings("serial")
public class IllegalTaskName extends TodoListException {
	IllegalTaskName(String message) {
		super(message);
	}
}
