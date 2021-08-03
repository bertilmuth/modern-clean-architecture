package com.example.todolist.behavior;

import java.util.UUID;
import java.util.function.Consumer;

import com.example.todolist.behavior.request.DeleteTaskRequest;
import com.example.todolist.domain.Task.TaskId;
import com.example.todolist.domain.TodoList;
import com.example.todolist.domain.TodoList.TodoListId;
import com.example.todolist.domain.TodoListNotFound;
import com.example.todolist.domain.TodoLists;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * A function that deletes a task from a todo list.
 * 
 * @author b_muth
 *
 */
@AllArgsConstructor
class DeleteTask implements Consumer<DeleteTaskRequest> {
	@NonNull
	private final TodoLists repository;

	@Override
	public void accept(@NonNull DeleteTaskRequest request) {
		final UUID todoListUuid = request.getTodoListUuid();
		final UUID taskUuid = request.getTaskUuid();
		
		final TodoList todoList = repository.findById(TodoListId.of(todoListUuid))
			.orElseThrow(() -> new TodoListNotFound("Repository doesn't contain a TodoList of id " + todoListUuid));

		todoList.deleteTask(TaskId.of(taskUuid));
		repository.save(todoList);
	}
}
