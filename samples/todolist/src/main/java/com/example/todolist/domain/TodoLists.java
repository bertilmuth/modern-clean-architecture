package com.example.todolist.domain;

import java.util.Optional;

import org.jmolecules.ddd.types.Repository;

import com.example.todolist.domain.TodoList.TodoListId;

/**
 * The repository used for storing todo lists. 
 * 
 * @author b_muth
 *
 */
public interface TodoLists extends Repository<TodoList, TodoListId> {
	TodoList save(TodoList entity);
	Optional<TodoList> findById(TodoListId id);
	Iterable<TodoList> findAll();
}