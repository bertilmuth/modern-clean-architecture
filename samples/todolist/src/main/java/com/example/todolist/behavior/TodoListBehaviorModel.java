package com.example.todolist.behavior;

import org.requirementsascode.BehaviorModel;
import org.requirementsascode.Model;

import com.example.todolist.behavior.request.AddTaskRequest;
import com.example.todolist.behavior.request.ToggleTaskCompletionRequest;
import com.example.todolist.behavior.request.FindOrCreateListRequest;
import com.example.todolist.behavior.request.DeleteTaskRequest;
import com.example.todolist.behavior.request.ListTasksByCompletionRequest;
import com.example.todolist.behavior.request.ListTasksRequest;
import com.example.todolist.behavior.response.EmptyResponse;
import com.example.todolist.domain.TodoLists;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Maps incoming requests for the todo list to request handlers.
 * 
 * @author b_muth
 *
 */
@AllArgsConstructor
public class TodoListBehaviorModel implements BehaviorModel {
	@NonNull
	private final TodoLists todoLists;

	@Override
	public Model model() {
		return Model.builder()
			.user(FindOrCreateListRequest.class).systemPublish(findOrCreateList())
			.user(AddTaskRequest.class).systemPublish(addTask())
			.user(ToggleTaskCompletionRequest.class).system(toggleTaskCompletion())
			.user(DeleteTaskRequest.class).system(deleteTask())
			.user(ListTasksRequest.class).systemPublish(listTasks())
			.user(ListTasksByCompletionRequest.class).systemPublish(listTasksByCompletion())
			.build();
	}

	@Override
	public Object defaultResponse() {
		return new EmptyResponse();
	}

	private FindOrCreateList findOrCreateList() {
		return new FindOrCreateList(todoLists);
	}

	private AddTask addTask() {
		return new AddTask(todoLists);
	}

	private ToggleTaskCompletion toggleTaskCompletion() {
		return new ToggleTaskCompletion(todoLists);
	}

	private DeleteTask deleteTask() {
		return new DeleteTask(todoLists);
	}

	private ListTasks listTasks() {
		return new ListTasks(todoLists);
	}
	
	private ListTasksByCompletion listTasksByCompletion() {
		return new ListTasksByCompletion(todoLists);
	}
}
