package com.example.todolist.behavior.request;

import java.util.UUID;

import lombok.NonNull;
import lombok.Value;

/**
 * Request to find all tasks of a todo list.
 * 
 * @author b_muth
 *
 */
@Value
public class ListTasksRequest {
	@NonNull
	UUID todoListUuid;
}
