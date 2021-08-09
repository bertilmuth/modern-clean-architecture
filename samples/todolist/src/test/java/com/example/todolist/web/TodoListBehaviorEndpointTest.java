package com.example.todolist.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.requirementsascode.spring.behavior.test.MockBehaviorEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.example.todolist.behavior.TodoListBehaviorModel;
import com.example.todolist.behavior.request.AddTaskRequest;
import com.example.todolist.behavior.request.DeleteTaskRequest;
import com.example.todolist.behavior.request.FilterTasksRequest;
import com.example.todolist.behavior.request.FindOrCreateListRequest;
import com.example.todolist.behavior.request.ToggleTaskCompletionRequest;
import com.example.todolist.behavior.response.AddTaskResponse;
import com.example.todolist.behavior.response.FilterTasksResponse;
import com.example.todolist.behavior.response.FindOrCreateListResponse;
import com.example.todolist.domain.TestTodoLists;

@WebMvcTest
public class TodoListBehaviorEndpointTest {
	@Autowired
	private MockBehaviorEndpoint endpoint;

	@Test
	void createsAndModifiesTodoList() throws Exception {
		UUID todoListUuid = createList();

		UUID taskUuid1 = addTask(todoListUuid, "task1");
		UUID taskUuid2 = addTask(todoListUuid, "task2");

		toggleTaskCompletion(todoListUuid, taskUuid1);

		deleteTask(todoListUuid, taskUuid1);

		List<FilterTasksResponse.Task> tasks = listUncompletedTasks(todoListUuid);

		assertEquals(1, tasks.size());
		assertEquals(taskUuid2, tasks.get(0).getUuid());
	}

	@TestConfiguration
	static class TestTodoListConfiguration {
		@Bean
		TodoListBehaviorModel todoListBehaviorModel() {
			return new TodoListBehaviorModel(new TestTodoLists());
		}
	}

	private UUID createList() throws Exception {
		FindOrCreateListResponse response = endpoint.post(new FindOrCreateListRequest(), FindOrCreateListResponse.class);
		return response.getTodoListUuid();
	}

	private UUID addTask(UUID todoListUuid, String taskName) throws Exception {
		AddTaskRequest request = new AddTaskRequest(todoListUuid, taskName);
		AddTaskResponse response = endpoint.post(request, AddTaskResponse.class);
		return response.getTaskUuid();
	}

	private void deleteTask(UUID todoListUuid, UUID taskUuid) throws Exception {
		DeleteTaskRequest request = new DeleteTaskRequest(todoListUuid, taskUuid);
		endpoint.postRequest(request);
	}

	private void toggleTaskCompletion(UUID todoListUuid, UUID taskUuid) throws Exception {
		ToggleTaskCompletionRequest request = new ToggleTaskCompletionRequest(todoListUuid, taskUuid);
		endpoint.postRequest(request);
	}

	private List<FilterTasksResponse.Task> listUncompletedTasks(UUID todoListUuid) throws Exception {
		FilterTasksRequest request = new FilterTasksRequest(todoListUuid, false);
		FilterTasksResponse response = endpoint.post(request, FilterTasksResponse.class);
		return response.getTasks();
	}
}
