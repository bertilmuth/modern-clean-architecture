package com.example.todolist.behavior;

import java.util.Iterator;
import java.util.UUID;
import java.util.function.Function;

import com.example.todolist.behavior.request.FindOrCreateListRequest;
import com.example.todolist.behavior.response.FindOrCreateListResponse;
import com.example.todolist.domain.TodoList;
import com.example.todolist.domain.TodoLists;

import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Returns an existing todo list, if there is one. Otherwise, creates a new one
 * and returns it.
 * 
 * @author b_muth
 *
 */
@AllArgsConstructor
class FindOrCreateList implements Function<FindOrCreateListRequest, FindOrCreateListResponse> {
	@NonNull
	private final TodoLists repository;

	@Override
	public FindOrCreateListResponse apply(@NonNull FindOrCreateListRequest request) {
		final TodoList list = findOrCreateList();
		final UUID listUuid = list.getId().getUuid();
				
		return new FindOrCreateListResponse(listUuid);
	}

	private TodoList findOrCreateList() {
		final Iterator<TodoList> foundListsIt = repository.findAll().iterator();
		final TodoList list = foundListsIt.hasNext()? foundListsIt.next() : createAndSaveList();
		return list;
	}

	private TodoList createAndSaveList() {
		final TodoList list = new TodoList();
		repository.save(list);
		return list;
	}
}
