package com.example.todolist.behavior.response;

import java.util.List;
import java.util.UUID;

import lombok.NonNull;
import lombok.Value;

/**
 * A response to a ListTasksByCompletionRequest.
 * 
 * @author b_muth
 *
 */
@Value
public class ListTasksByCompletionResponse {
	@Value
	public static class Task {
		@NonNull
		UUID uuid;

		@NonNull
		String name;

		@NonNull
		Boolean completed;
	}
	
	@NonNull
	List<Task> tasks;
}
