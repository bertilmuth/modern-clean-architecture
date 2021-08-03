package com.example.todolist.behavior.request;

import java.util.UUID;

import lombok.NonNull;
import lombok.Value;

/**
 * Request to delete a task from an existing todo list.
 * 
 * @author b_muth
 *
 */
@Value
public class DeleteTaskRequest {
	@NonNull
	UUID todoListUuid;

	@NonNull
	UUID taskUuid;
}
