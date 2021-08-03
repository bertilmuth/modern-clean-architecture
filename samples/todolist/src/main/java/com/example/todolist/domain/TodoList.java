package com.example.todolist.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jmolecules.ddd.types.AggregateRoot;
import org.jmolecules.ddd.types.Identifier;

import com.example.todolist.domain.Task.TaskId;
import com.example.todolist.domain.TodoList.TodoListId;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * A simple todo list containing tasks.
 * 
 * @author b_muth
 *
 */
@EqualsAndHashCode
public class TodoList implements AggregateRoot<TodoList, TodoListId> {
	private final TodoListId id;
	private final List<Task> tasks;

	/**
	 * The identifier of the list. Will be used for storage in repository.
	 */
	@Value(staticConstructor = "of")
	public static class TodoListId implements Identifier {
		@NonNull
		UUID uuid;
	}
	
	/**
	 * Creates an empty todo list.
	 */
	public TodoList() {
		this(TodoListId.of(UUID.randomUUID()), new ArrayList<>());
	}

	/**
	 * Creates a new TodoList instance, and copies the specified tasks into it.
	 * 
	 * @param id    the id of the instance
	 * @param tasks the tasks of the instance
	 */
	TodoList(@NonNull TodoListId id, @NonNull List<Task> tasks) {
		this.id = id;
		this.tasks = new ArrayList<>();
		tasks.forEach(t -> add(t.getId(), t.getName(), t.isCompleted()));
	}
	
	/**
	 * Returns the unique id of this todo list.
	 * 
	 * @return todo list id
	 */
	@Override
	public TodoListId getId() {
		return id;
	}
	
	/**
	 * List all the tasks contained by this list.
	 * 
	 * @return the tasks on this list
	 */
	public List<Task> listTasks(){
		return Collections.unmodifiableList(tasks);
	}

	/**
	 * Adds a task with the specified name to the todo list. The task name must not
	 * be all whitespace.
	 * 
	 * @param taskName the name of the tasks
	 * @return the id of the added task, and the todo list with the task added
	 */
	public TaskId addTask(String taskName) {
		if (taskName == null || isWhitespaceName(taskName)) {
			throw new IllegalTaskName("Please specify a non-null, non-whitespace task name!");
		}

		TaskId taskId = add(TaskId.of(UUID.randomUUID()), taskName, false);
		return taskId;
	}

	/**
	 * If the task is not completed, completes it. Otherwise, uncompletes it.
	 * 
	 * @param taskId the id of the task to (un)complete
	 */
	public void toggleTaskCompletion(TaskId taskId) {
		Optional<Task> optionalFoundTask = findTask(taskId);

		optionalFoundTask.ifPresent(foundTask -> {
			int foundTaskIndex = tasks.indexOf(foundTask);
			Task newTask = new Task(taskId, foundTask.getName(), !foundTask.isCompleted());
			tasks.set(foundTaskIndex, newTask);
		});
	}

	/**
	 * Deletes the specified task. If no task with the specified id exists in the
	 * todo list, silently ignores it.
	 * 
	 * @param task the id of the task to delete
	 */
	public void deleteTask(TaskId task) {
		Optional<Task> foundTask = findTask(task);
		foundTask.ifPresent(tasks::remove);
	}

	/**
	 * Returns the task with the specified id
	 * 
	 * @param taskId the id to look for
	 * @return the optional task if found, or an empty optional
	 */
	public Optional<Task> findTask(TaskId taskId) {
		Optional<Task> foundTask = tasks.stream()
			.filter(t -> taskId.equals(t.getId()))
			.findFirst();
		return foundTask;
	}

	/**
	 * List either all tasks that have been completed, or tasks that haven't been
	 * completed.
	 * 
	 * @param completed true to list completed tasks, false to list uncompleted
	 *                  tasks
	 * @return the (un)completed tasks
	 */
	public List<Task> listTasksByCompletion(boolean completed) {
		final List<Task> resultTasks = tasks.stream()
			.filter(t -> completed == t.isCompleted())
			.collect(Collectors.toList());

		return Collections.unmodifiableList(resultTasks);
	}

	/**
	 * Returns the number of tasks on this list.
	 * 
	 * @return the todo list size
	 */
	public Object size() {
		return tasks.size();
	}

	private boolean isWhitespaceName(String taskName) {
		return "".equals(taskName.trim());
	}

	private TaskId add(TaskId taskId, String taskName, boolean isCompleted) {
		Task task = new Task(taskId, taskName, isCompleted);
		tasks.add(task);
		return task.getId();
	}
}
