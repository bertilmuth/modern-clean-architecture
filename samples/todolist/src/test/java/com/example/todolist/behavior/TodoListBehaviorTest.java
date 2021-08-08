package com.example.todolist.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.requirementsascode.Behavior;
import org.requirementsascode.StatelessBehavior;

import com.example.todolist.behavior.request.AddTaskRequest;
import com.example.todolist.behavior.request.ToggleTaskCompletionRequest;
import com.example.todolist.behavior.request.FindOrCreateListRequest;
import com.example.todolist.behavior.request.DeleteTaskRequest;
import com.example.todolist.behavior.request.FilterTasksRequest;
import com.example.todolist.behavior.request.ListTasksRequest;
import com.example.todolist.behavior.response.AddTaskResponse;
import com.example.todolist.behavior.response.FindOrCreateListResponse;
import com.example.todolist.behavior.response.FilterTasksResponse;
import com.example.todolist.behavior.response.ListTasksResponse;
import com.example.todolist.domain.IllegalTaskName;
import com.example.todolist.domain.Task;
import com.example.todolist.domain.Task.TaskId;
import com.example.todolist.domain.TestTodoLists;
import com.example.todolist.domain.TodoList;
import com.example.todolist.domain.TodoList.TodoListId;
import com.example.todolist.domain.TodoListNotFound;

class TodoListBehaviorTest {
	private static final String TASK_NAME = "t1";
	private static final String TASK2_NAME = "t2";
	private static final String TASK3_NAME = "t3";

	private TestTodoLists testTodoLists;
	private Behavior todoListBehavior;

	@BeforeEach
	void setup() {
		this.testTodoLists = new TestTodoLists();

		TodoListBehaviorModel behaviorModel = new TodoListBehaviorModel(testTodoLists);
		this.todoListBehavior = StatelessBehavior.of(behaviorModel);
	}

	@Test
	void createsTodoList() {
		// When system creates list
		final UUID todoListId = whenSystemFindsOrCreatesList();

		// Then identifier is non null
		thenIdentifierIsNonNull(todoListId);
	}
	
	@Test
	void findsListIfAlreadyCreated() {
		// When system creates list...
		final UUID todoListId = whenSystemFindsOrCreatesList();
		
		//...And creates it again
		final UUID todoListId2 = whenSystemFindsOrCreatesList();

		// Then the identifiers are equals
		thenIdentifiersAreEqual(todoListId, todoListId2);
	}

	@Test
	void addsTaskToList() {
		// Given empty todo list
		final UUID todoListId = givenEmptyTodoList();

		// When user adds task
		final UUID taskUuid = whenUserAddsTask(todoListId, TASK_NAME);

		// Then task exists
		thenUncompletedTaskExists(todoListId, taskUuid, TASK_NAME);
	}

	@Test
	void throwsWhenAddsToNonExistingList() {
		// Given empty todo list
		final UUID todoListId = UUID.randomUUID();

		// Then throw exception when user adds task
		thenThrows(TodoListNotFound.class, () -> whenUserAddsTask(todoListId, TASK_NAME));
	}

	@Test
	void throwsWhenAddsTaskWithWhitespaceToList() {
		// Given empty todo list
		final UUID todoListId = givenEmptyTodoList();

		// Then throw exception when add task
		thenThrows(IllegalTaskName.class, () -> whenUserAddsTask(todoListId, " "));
	}

	@Test
	void completesTask() {
		// Given todo list with task
		final UUID todoListId = givenTodoListWithTasks(TASK_NAME);

		// When user completes first task
		whenUserTogglesTaskCompletion(todoListId, taskUuid1());

		// Then first task is completed
		thenCompletedTaskExists(todoListId, taskUuid1(), TASK_NAME);
	}

	@Test
	void throwsWhenCompletesOnNonExistingList() {
		// Given empty todo list
		final UUID todoListId = UUID.randomUUID();

		// Then throw exception when user adds task
		thenThrows(TodoListNotFound.class, () -> whenUserTogglesTaskCompletion(todoListId, UUID.randomUUID()));
	}

	@Test
	void deletesTask() {
		// Given todo list with task
		final UUID todoListId = givenTodoListWithTasks(TASK_NAME);

		// When user deletes first task
		whenUserDeletesTask(todoListId, taskUuid1());

		// Then todo list is empty
		thenTodoListIsEmpty(todoListId);
	}

	@Test
	void throwsWhenDeletesTaskOnNonExistingList() {
		// Given empty todo list
		final UUID todoListId = UUID.randomUUID();

		// Then throw exception when user deletes task
		thenThrows(TodoListNotFound.class, () -> whenUserDeletesTask(todoListId, UUID.randomUUID()));
	}

	@Test
	void listsTasks() {
		// Given todo list with two tasks
		final UUID todoListId = givenTodoListWithTasks(TASK_NAME, TASK2_NAME, TASK3_NAME);

		// When user completes first task and lists tasks
		whenUserTogglesTaskCompletion(todoListId, taskUuid1());
		final ListTasksResponse listTasksResponse = whenUserListsTasks(todoListId);

		// Then the correct tasks are returned in order
		ListTasksResponse.Task expectedTask1 = new ListTasksResponse.Task(taskUuid1(), TASK_NAME, true);
		ListTasksResponse.Task expectedTask2 = new ListTasksResponse.Task(taskUuid2(), TASK2_NAME, false);
		ListTasksResponse.Task expectedTask3 = new ListTasksResponse.Task(taskUuid3(), TASK3_NAME, false);

		final List<ListTasksResponse.Task> actualTasks = listTasksResponse.getTasks();
		thenTasksAre(actualTasks, expectedTask1, expectedTask2, expectedTask3);
	}
	
	@Test
	void listsCompletedTasks() {
		// Given todo list with two tasks
		final UUID todoListId = givenTodoListWithTasks(TASK_NAME, TASK2_NAME, TASK3_NAME);

		// When user completes first task and lists tasks
		whenUserTogglesTaskCompletion(todoListId, taskUuid1());
		final FilterTasksResponse listTasksResponse = whenUserListsTasksByCompletion(todoListId, true);

		// Then the correct tasks are returned in order
		FilterTasksResponse.Task expectedTask = new FilterTasksResponse.Task(taskUuid1(), TASK_NAME, true);

		final List<FilterTasksResponse.Task> actualTasks = listTasksResponse.getTasks();
		thenTasksAre(actualTasks, expectedTask);
	}

	@Test
	void throwsWhenListsTasksOfNonExistingList() {
		// Given empty todo list
		final UUID todoListId = UUID.randomUUID();

		// Then throw exception when add task
		thenThrows(TodoListNotFound.class, () -> whenUserListsTasks(todoListId));
	}

	private UUID givenEmptyTodoList() {
		return givenTodoListWithTasks();
	}

	private UUID givenTodoListWithTasks(String... taskNames) {
		TodoList todoList = new TodoList();
		for (String taskName : taskNames) {
			todoList.addTask(taskName);
		}
		final TodoList savedTodoList = testTodoLists.save(todoList);
		return savedTodoList.getId().getUuid();
	}

	private UUID whenSystemFindsOrCreatesList() {
		final FindOrCreateListRequest request = new FindOrCreateListRequest();
		final Optional<FindOrCreateListResponse> optionalResponse = todoListBehavior.reactTo(request);
		final FindOrCreateListResponse response = optionalResponse.get();
		return response.getTodoListUuid();
	}

	private UUID whenUserAddsTask(UUID todoListId, String taskName) {
		final AddTaskRequest request = new AddTaskRequest(todoListId, taskName);
		final Optional<AddTaskResponse> optionalResponse = todoListBehavior.reactTo(request);
		final AddTaskResponse response = optionalResponse.get();
		return response.getTaskUuid();
	}

	private void whenUserTogglesTaskCompletion(final UUID todoListId, UUID taskUuid) {
		final ToggleTaskCompletionRequest request = new ToggleTaskCompletionRequest(todoListId, taskUuid);
		todoListBehavior.reactTo(request);
	}

	private void whenUserDeletesTask(UUID todoListId, UUID taskUuid) {
		final DeleteTaskRequest request = new DeleteTaskRequest(todoListId, taskUuid);
		todoListBehavior.reactTo(request);
	}

	private ListTasksResponse whenUserListsTasks(UUID todoListId) {
		final ListTasksRequest request = new ListTasksRequest(todoListId);
		final Optional<ListTasksResponse> optionalResponse = todoListBehavior.reactTo(request);
		final ListTasksResponse tasks = optionalResponse.get();
		return tasks;
	}
	
	private FilterTasksResponse whenUserListsTasksByCompletion(UUID todoListId, boolean completed) {
		final FilterTasksRequest request = new FilterTasksRequest(todoListId, completed);
		final Optional<FilterTasksResponse> optionalResponse = todoListBehavior.reactTo(request);
		final FilterTasksResponse tasks = optionalResponse.get();
		return tasks;
	}

	private void thenIdentifierIsNonNull(UUID uuid) {
		assertNotNull(uuid);
	}
	
	private void thenIdentifiersAreEqual(UUID uuid1, UUID uuid2) {
		assertEquals(uuid1, uuid2);
	}

	private void thenUncompletedTaskExists(UUID todoListId, UUID taskUuid, String taskName) {
		thenTaskExists(todoListId, taskUuid, taskName, false);
	}

	private void thenCompletedTaskExists(UUID todoListId, UUID taskUuid, String taskName) {
		thenTaskExists(todoListId, taskUuid, taskName, true);
	}

	private void thenTaskExists(UUID todoListId, UUID taskUuid, String taskName, boolean completion) {
		final TodoList loadedTodoList = testTodoLists.findById(TodoListId.of(todoListId)).get();
		final Optional<Task> optionalTask = loadedTodoList.findTask(TaskId.of(taskUuid));
		assertTrue(optionalTask.isPresent());
		assertEquals(completion, optionalTask.get().isCompleted());
		assertEquals(taskName, optionalTask.get().getName());
	}

	private void thenTasksAre(Collection<?> actualTasks, Object... expectedTasks) {
		final Iterator<?> actualTasksIt = actualTasks.iterator();
		
		for (Object expectedTask : expectedTasks) {
			assertEquals(expectedTask, actualTasksIt.next());
		}
		assertFalse(actualTasksIt.hasNext());
	}

	private void thenTodoListIsEmpty(final UUID todoListId) {
		TodoList loadedTodoList = testTodoLists.findById(TodoListId.of(todoListId)).get();
		assertTrue(loadedTodoList.listTasks().isEmpty());
	}

	private <T extends Throwable> void thenThrows(Class<T> exceptionClass, Executable executable) {
		assertThrows(exceptionClass, executable);
	}

	private UUID taskUuid1() {
		final TodoList todoList = testTodoLists.findAll().iterator().next();
		final UUID firstTaskUuid = todoList.listTasks().get(0).getId().getUuid();
		return firstTaskUuid;
	}

	private UUID taskUuid2() {
		final TodoList todoList = testTodoLists.findAll().iterator().next();
		final UUID secondTaskUuid = todoList.listTasks().get(1).getId().getUuid();
		return secondTaskUuid;
	}

	private UUID taskUuid3() {
		final TodoList todoList = testTodoLists.findAll().iterator().next();
		final UUID thirdTaskUuid = todoList.listTasks().get(2).getId().getUuid();
		return thirdTaskUuid;
	}
}
