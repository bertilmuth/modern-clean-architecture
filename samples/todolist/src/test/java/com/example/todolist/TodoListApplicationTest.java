package com.example.todolist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.requirementsascode.spring.test.MockBehaviorEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.example.todolist.behavior.request.AddTaskRequest;
import com.example.todolist.behavior.request.FindOrCreateListRequest;
import com.example.todolist.behavior.request.ListTasksRequest;
import com.example.todolist.behavior.response.AddTaskResponse;
import com.example.todolist.behavior.response.FindOrCreateListResponse;
import com.example.todolist.behavior.response.ListTasksResponse;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TodoListApplicationTest {
	@Autowired
	private MockBehaviorEndpoint endpoint;
	
	@Test
	void addsTask() throws Exception {
		UUID todoListUuid = createList();
		UUID taskUuid = addTask(todoListUuid, "task1");
		List<ListTasksResponse.Task> tasks = listTasks(todoListUuid);
		
		assertEquals(1, tasks.size());
		assertEquals(taskUuid, tasks.get(0).getUuid());
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
	
	private List<ListTasksResponse.Task> listTasks(UUID todoListUuid) throws Exception {
		ListTasksRequest request = new ListTasksRequest(todoListUuid);
		ListTasksResponse response = endpoint.post(request, ListTasksResponse.class);
		return response.getTasks();
	}
}
