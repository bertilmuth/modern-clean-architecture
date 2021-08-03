package com.example.todolist.behavior.request;

import java.util.UUID;

import lombok.NonNull;
import lombok.Value;

/**
 * Request to add a task to an existing todo list.
 * 
 * @author b_muth
 *
 */
@Value
public class AddTaskRequest {
	@NonNull
	UUID todoListUuid;

	@NonNull
	String taskName;
}
