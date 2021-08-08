package com.example.todolist.behavior;

import java.util.function.Consumer;
import java.util.function.Function;

import org.requirementsascode.BehaviorModel;
import org.requirementsascode.Model;

import com.example.todolist.behavior.request.AddTaskRequest;
import com.example.todolist.behavior.request.DeleteTaskRequest;
import com.example.todolist.behavior.request.FindOrCreateListRequest;
import com.example.todolist.behavior.request.FilterTasksRequest;
import com.example.todolist.behavior.request.ListTasksRequest;
import com.example.todolist.behavior.request.ToggleTaskCompletionRequest;
import com.example.todolist.behavior.response.AddTaskResponse;
import com.example.todolist.behavior.response.FindOrCreateListResponse;
import com.example.todolist.behavior.response.FilterTasksResponse;
import com.example.todolist.behavior.response.ListTasksResponse;
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
			.user(FilterTasksRequest.class).systemPublish(filterTasks())
			.build();
	}

	private Function<FindOrCreateListRequest, FindOrCreateListResponse> findOrCreateList() {
		return new FindOrCreateList(todoLists);
	}

	private Function<AddTaskRequest, AddTaskResponse> addTask() {
		return new AddTask(todoLists);
	}

	private Consumer<ToggleTaskCompletionRequest> toggleTaskCompletion() {
		return new ToggleTaskCompletion(todoLists);
	}

	private Consumer<DeleteTaskRequest> deleteTask() {
		return new DeleteTask(todoLists);
	}

	private Function<ListTasksRequest, ListTasksResponse>  listTasks() {
		return new ListTasks(todoLists);
	}
	
	private Function<FilterTasksRequest, FilterTasksResponse> filterTasks() {
		return new FilterTasks(todoLists);
	}
}
