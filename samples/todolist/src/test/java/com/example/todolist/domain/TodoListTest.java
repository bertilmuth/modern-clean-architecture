package com.example.todolist.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.example.todolist.domain.Task.TaskId;

class TodoListTest {
	@Test
	void createsEmptyList() {
		final TodoList todoList = new TodoList();
		assertEquals(0, todoList.size());
	}

	@Test
	void addsTask() {
		final String task1Name = "TaskOne";

		final TodoList todoList = new TodoList();
		todoList.addTask(task1Name);

		assertEquals(1, todoList.size());
		assertEquals(task1Name, task1NameOf(todoList));
	}

	@Test
	void addsTwoTasks() {
		final String task1Name = "TaskOne";
		final String task2Name = "TaskTwo";

		final TodoList todoList = new TodoList();
		todoList.addTask(task1Name);
		todoList.addTask(task2Name);

		assertEquals(2, todoList.size());
		assertEquals(task1Name, task1NameOf(todoList));
		assertEquals(task2Name, task2NameOf(todoList));
	}

	@Test
	void throwsWhenAddingTaskWithNullName() {
		final TodoList todoList = new TodoList();
		assertThrows(IllegalTaskName.class, () -> todoList.addTask(null));
	}

	@Test
	void throwsWhenAddingTaskWithEmptyName() {
		final TodoList todoList = new TodoList();
		assertThrows(IllegalTaskName.class, () -> todoList.addTask(""));
	}

	@Test
	void throwsWhenAddingTaskWithWhitespaceName() {
		final TodoList todoList = new TodoList();
		assertThrows(IllegalTaskName.class, () -> todoList.addTask(" "));
	}

	@Test
	void deletesTask() {
		final String task1Name = "TaskOne";
		final String task2Name = "TaskTwo";

		final TodoList todoList = new TodoList();
		final TaskId task1 = todoList.addTask(task1Name);
		todoList.addTask(task2Name);

		todoList.deleteTask(task1);

		assertEquals(1, todoList.size());
		assertEquals(task2Name, task1NameOf(todoList));
	}

	@Test
	void deletesNonExistingTaskButDoesntThrow() {
		final TodoList todoList = new TodoList();
		final TaskId randomId = TaskId.of(UUID.randomUUID());
		todoList.deleteTask(randomId);

		assertEquals(0, todoList.size());
	}

	@Test
	void newTaskIsNotCompleted() {
		final String task1Name = "task";

		final TodoList todoList = new TodoList();
		todoList.addTask(task1Name);

		assertEquals(1, todoList.size());
		assertFalse(todoList.listTasks().get(0).isCompleted());
	}

	@Test
	void completesTask() {
		final String task1Name = "task";

		final TodoList todoList = new TodoList();
		final TaskId taskId = todoList.addTask(task1Name);

		todoList.toggleTaskCompletion(taskId);

		assertEquals(1, todoList.size());
		assertTrue(todoList.listTasks().get(0).isCompleted());
	}
	
	@Test
	void uncompletesTask() {
		final String task1Name = "task";

		final TodoList todoList = new TodoList();
		final TaskId taskId = todoList.addTask(task1Name);

		todoList.toggleTaskCompletion(taskId);
		todoList.toggleTaskCompletion(taskId);

		assertEquals(1, todoList.size());
		assertFalse(todoList.listTasks().get(0).isCompleted());
	}
	
	@Test
	void ignoresCompletingNonExistingTask() {
		final TodoList todoList = new TodoList();
		todoList.toggleTaskCompletion(TaskId.of(UUID.randomUUID()));
		assertEquals(0, todoList.size());
	}
	
	@Test
	void filterCompletedTasksOfEmptyList() {
		final TodoList todoList = new TodoList();
		final List<Task> tasks = todoList.filterTasks(true);
		assertEquals(0, tasks.size());
	}
	
	@Test
	void filterCompletedTasksOfOneUncompletedTaskList() {
		final String task1Name = "TaskOne";
		
		final TodoList todoList = new TodoList();
		todoList.addTask(task1Name);
		
		final List<Task> tasks = todoList.filterTasks(true);
		assertEquals(0, tasks.size());
	}
	
	@Test
	void filterCompletedTasksOfOneCompletedTaskList() {
		final String task1Name = "TaskOne";
		
		final TodoList todoList = new TodoList();
		final TaskId taskId = todoList.addTask(task1Name);
		todoList.toggleTaskCompletion(taskId);
		
		final List<Task> tasks = todoList.filterTasks(true);
		assertEquals(1, tasks.size());
		assertEquals(task1Name, tasks.get(0).getName());
	}
	
	@Test
	void filterCompletedTasksOfList() {
		final String task1Name = "TaskOne";
		final String task2Name = "TaskTwo";
		final String task3Name = "TaskThree";
		
		final TodoList todoList = new TodoList();
		final TaskId taskId1 = todoList.addTask(task1Name);
		todoList.addTask(task2Name);
		final TaskId taskId3 = todoList.addTask(task3Name);
		
		todoList.toggleTaskCompletion(taskId1);
		todoList.toggleTaskCompletion(taskId3);
		
		final List<Task> tasks = todoList.filterTasks(true);
		assertEquals(2, tasks.size());
		assertEquals(task1Name, tasks.get(0).getName());
		assertEquals(task3Name, tasks.get(1).getName());
	}
	
	@Test
	void filterUncompletedTasksOfEmptyList() {
		final TodoList todoList = new TodoList();
		final List<Task> tasks = todoList.filterTasks(false);
		assertEquals(0, tasks.size());
	}
	
	@Test
	void filterUncompletedTasksOfOneUncompletedTaskList() {
		final String task1Name = "TaskOne";
		
		final TodoList todoList = new TodoList();
		todoList.addTask(task1Name);
		
		List<Task> tasks = todoList.filterTasks(false);
		assertEquals(1, tasks.size());
		assertEquals(task1Name, tasks.get(0).getName());
	}
	
	@Test
	void filterUncompletedTasksOfList() {
		final String task1Name = "TaskOne";
		final String task2Name = "TaskTwo";
		final String task3Name = "TaskThree";
		
		final TodoList todoList = new TodoList();
		final TaskId taskId1 = todoList.addTask(task1Name);
		todoList.addTask(task2Name);
		final TaskId taskId3 = todoList.addTask(task3Name);
		
		todoList.toggleTaskCompletion(taskId1);
		todoList.toggleTaskCompletion(taskId3);
		
		final List<Task> tasks = todoList.filterTasks(false);
		assertEquals(1, tasks.size());
		assertEquals(task2Name, tasks.get(0).getName());
	}

	private String task1NameOf(TodoList todoList) {
		final String firstTask = todoList.listTasks().get(0).getName();
		return firstTask;
	}

	private String task2NameOf(TodoList todoList) {
		final String secondTask = todoList.listTasks().get(1).getName();
		return secondTask;
	}
}
