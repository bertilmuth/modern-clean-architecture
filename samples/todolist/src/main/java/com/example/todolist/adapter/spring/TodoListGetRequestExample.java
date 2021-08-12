package com.example.todolist.adapter.spring;

import java.util.Optional;
import java.util.UUID;

import org.requirementsascode.Behavior;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.todolist.behavior.request.ListTasksRequest;

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
class TodoListGetRequestExample {
	private final Behavior behavior;

	TodoListGetRequestExample(Behavior behavior) {
		this.behavior = behavior;
	}

	@GetMapping("/todolist/tasks")
	public Object listTasks(@RequestParam UUID todoListUuid) {
		final ListTasksRequest request = new ListTasksRequest(todoListUuid);
		
		final Optional<Object> optionalResponse = behavior.reactTo(request);
		final Object response = optionalResponse.orElse("");
				
		return response;
	}
}
