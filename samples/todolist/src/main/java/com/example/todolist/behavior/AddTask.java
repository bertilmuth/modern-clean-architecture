package com.example.todolist.behavior;

import java.util.UUID;
import java.util.function.Function;

import com.example.todolist.behavior.request.AddTaskRequest;
import com.example.todolist.behavior.response.AddTaskResponse;
import com.example.todolist.domain.Task.TaskId;
import com.example.todolist.domain.TodoList;
import com.example.todolist.domain.TodoList.TodoListId;
import com.example.todolist.domain.TodoListNotFound;
import com.example.todolist.domain.TodoLists;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * A function that adds a task with a certain name to an existing todo list.
 * 
 * @author b_muth
 *
 */
@AllArgsConstructor
class AddTask implements Function<AddTaskRequest, AddTaskResponse> {
	@NonNull
	private final TodoLists repository;

	@Override
	public AddTaskResponse apply(@NonNull AddTaskRequest request) {
		final UUID todoListUuid = request.getTodoListUuid();
		final String taskName = request.getTaskName();
		
		final TodoList todoList = repository.findById(TodoListId.of(todoListUuid))
			.orElseThrow(() -> new TodoListNotFound("Repository doesn't contain a TodoList of id " + todoListUuid));

		TaskId taskId = todoList.addTask(taskName);
		repository.save(todoList);

		return new AddTaskResponse(taskId.getUuid());
	}
}
