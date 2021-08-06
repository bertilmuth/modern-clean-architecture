package com.example.todolist.web;

import java.util.Optional;

import org.requirementsascode.Behavior;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.todolist.behavior.request.FindOrCreateListRequest;
import com.example.todolist.behavior.request.ListTasksRequest;
import com.example.todolist.behavior.response.EmptyResponse;
import com.example.todolist.behavior.response.FindOrCreateListResponse;

/**
 * This class is just an example of how GET requests can be handled. In contrast
 * to POST requests, you need to inject the behavior and call its reactTo(...)
 * method.
 * 
 * Note that as an alternative, you can decide to send all requests, even
 * queries, as POST requests. Use what makes sense depending on your
 * application's requirements.
 * 
 * @author b_muth
 */
@RestController
public class TodoListGetRequestExample {
	private final Behavior behavior;

	TodoListGetRequestExample(Behavior behavior) {
		this.behavior = behavior;
	}

	@GetMapping("${behavior.endpoint}")
	public Object listTasks() {
		// First, find or create the todo list
		final Optional<FindOrCreateListResponse> findOrCreateListResponse = behavior.reactTo(new FindOrCreateListRequest());
		
		// Now, list its tasks
		Optional<Object> optionalResponse = findOrCreateListResponse
			.map(r -> r.getTodoListUuid())
			.flatMap(uuid -> behavior.reactTo(new ListTasksRequest(uuid)));

		final Object response = optionalResponse.orElse(new EmptyResponse());
				
		return response;
	}
}
