package com.example.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the todo list application.
 * 
 * @author b_muth
 *
 */
@SpringBootApplication
public class TodoListApplication {
	
	/**
	 * Start the todo list application with the specified arguments.
	 * 
	 * @param args the application arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(TodoListApplication.class, args);
	}
	
}