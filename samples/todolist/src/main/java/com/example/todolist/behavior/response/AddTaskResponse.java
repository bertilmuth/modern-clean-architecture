package com.example.todolist.behavior.response;

import java.util.UUID;

import lombok.NonNull;
import lombok.Value;

/**
 * Response to an AddTaskRequest.
 * 
 * @author b_muth
 *
 */
@Value
public class AddTaskResponse {
	@NonNull
	UUID taskUuid;
}
