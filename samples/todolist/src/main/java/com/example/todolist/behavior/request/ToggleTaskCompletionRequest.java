package com.example.todolist.behavior.request;

import java.util.UUID;

import lombok.NonNull;
import lombok.Value;

/**
 * Request to complete a task on a todo list.
 * 
 * @author b_muth
 *
 */
@Value
public class ToggleTaskCompletionRequest {
	@NonNull
	UUID todoListUuid;

	@NonNull
	UUID taskUuid;
}
