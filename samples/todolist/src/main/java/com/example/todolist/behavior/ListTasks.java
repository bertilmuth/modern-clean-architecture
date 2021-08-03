package com.example.todolist.behavior;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.example.todolist.behavior.request.ListTasksRequest;
import com.example.todolist.behavior.response.ListTasksResponse;
import com.example.todolist.domain.Task;
import com.example.todolist.domain.TodoList;
import com.example.todolist.domain.TodoList.TodoListId;
import com.example.todolist.domain.TodoListNotFound;
import com.example.todolist.domain.TodoLists;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * A function that lists the tasks of a todo list.
 * 
 * @author b_muth
 *
 */
@AllArgsConstructor
class ListTasks implements Function<ListTasksRequest, ListTasksResponse> {
	@NonNull
	private final TodoLists repository;

	@Override
	public ListTasksResponse apply(@NonNull ListTasksRequest request) {
		final UUID todoListUuid = request.getTodoListUuid();

		final TodoList todoList = repository.findById(TodoListId.of(todoListUuid))
			.orElseThrow(() -> new TodoListNotFound("Repository doesn't contain a TodoList of id " + todoListUuid));

		final List<Task> tasks = todoList.listTasks();

		List<ListTasksResponse.Task> taskList = tasks.stream()
			.map(t -> new ListTasksResponse.Task(t.getId().getUuid(), t.getName(), t.isCompleted()))
			.collect(Collectors.toList());

		return new ListTasksResponse(taskList);
	}
}
