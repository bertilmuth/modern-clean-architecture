package com.example.todolist.domain;

import java.util.UUID;

import org.jmolecules.ddd.types.Entity;
import org.jmolecules.ddd.types.Identifier;

import com.example.todolist.domain.Task.TaskId;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

/**
 * Represents one of several tasks on a todo list.
 * 
 * @author b_muth
 *
 */
@Getter
@EqualsAndHashCode
public class Task implements Entity<TodoList, TaskId> {
	private final TaskId id;
	private final String name;
	private final boolean completed;

	/**
	 * The identifier of the task. Will be used for storage in repository.
	 */
	@Value(staticConstructor = "of")
	public static class TaskId implements Identifier {
		@NonNull
		UUID uuid;
	}

	/**
	 * Creates a task instance.
	 * 
	 * @param id        the unique identifier of the task
	 * @param name      the name of the task
	 * @param completed whether the task has already been completed
	 */
	Task(@NonNull TaskId id, @NonNull String name, boolean completed) {
		this.id = id;
		this.name = name;
		this.completed = completed;
	}
}