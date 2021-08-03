package com.example.todolist.domain;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.requirementsascode.spring.test.EntityAccess;
import org.requirementsascode.spring.test.TestRepository;

import com.example.todolist.domain.TodoList.TodoListId;

public class TestTodoLists extends TestRepository<TodoList, TodoListId> implements TodoLists {

	public TestTodoLists() {
		super(new TodoListAccess());
	}

	private static class TodoListAccess implements EntityAccess<TodoList, TodoListId> {
		@Override
		public TodoListId idOf(TodoList entity) {
			return entity.getId();
		}

		@Override
		public TodoListId nextId() {
			return TodoListId.of(UUID.randomUUID());
		}

		@Override
		public TodoList copyWithId(TodoList todoList, TodoListId id) {
			List<Task> tasks = Objects.requireNonNull(todoList.listTasks(), "tasks must not be null!");
			final TodoList newTodoList = new TodoList(id, tasks);
			return newTodoList;
		}
	}
}
