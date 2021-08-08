package com.example.todolist.behavior;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.example.todolist.behavior.request.FilterTasksRequest;
import com.example.todolist.behavior.response.FilterTasksResponse;
import com.example.todolist.domain.Task;
import com.example.todolist.domain.TodoList;
import com.example.todolist.domain.TodoList.TodoListId;
import com.example.todolist.domain.TodoListNotFound;
import com.example.todolist.domain.TodoLists;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * A function that lists either all tasks that have been completed, or tasks
 * that haven't been completed.
 * 
 * @author b_muth
 *
 */
@AllArgsConstructor
class FilterTasks implements Function<FilterTasksRequest, FilterTasksResponse> {
	@NonNull
	private final TodoLists repository;

	@Override
	public FilterTasksResponse apply(@NonNull FilterTasksRequest request) {
		final UUID todoListUuid = request.getTodoListUuid();

		final TodoList todoList = repository.findById(TodoListId.of(todoListUuid))
			.orElseThrow(() -> new TodoListNotFound("Repository doesn't contain a TodoList of id " + todoListUuid));

		final List<Task> tasks = todoList.filterTasks(request.getCompleted());

		List<FilterTasksResponse.Task> taskList = tasks.stream()
			.map(t -> new FilterTasksResponse.Task(t.getId().getUuid(), t.getName(), t.isCompleted()))
			.collect(Collectors.toList());

		return new FilterTasksResponse(taskList);
	}
}
