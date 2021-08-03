package com.example.todolist.behavior.response;

import java.util.UUID;

import lombok.NonNull;
import lombok.Value;

/**
 * Response to a CreateListRequest.
 * 
 * @author b_muth
 *
 */
@Value
public class FindOrCreateListResponse {
	@NonNull
	UUID todoListUuid;
}
