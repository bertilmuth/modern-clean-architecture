package com.example.todolist.behavior.response;

import java.util.List;
import java.util.UUID;

import lombok.NonNull;
import lombok.Value;

/**
 * A response to a ListTasksRequest.
 * 
 * @author b_muth
 *
 */
@Value
public class ListTasksResponse {
	@Value
	public static class Task {
		@NonNull
		UUID uuid;

		@NonNull
		String name;

		@NonNull
		Boolean completed;
	}
	
	/**
	 * Information about each task contained in the todo list.
	 * 
	 * @return the task infos
	 */
	@NonNull
	List<Task> tasks;
}
