package com.example.todolist.behavior.request;

import java.util.UUID;

import lombok.NonNull;
import lombok.Value;

/**
 * Request to either list all tasks that have been completed, or all tasks that
 * haven't been completed.
 */
@Value
public class FilterTasksRequest {
	@NonNull
	UUID todoListUuid;

	@NonNull
	Boolean completed;
}
