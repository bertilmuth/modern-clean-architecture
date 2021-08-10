package com.example.todolist.adapter.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.todolist.behavior.TodoListBehaviorModel;
import com.example.todolist.domain.TodoLists;

@Configuration
class TodoListConfiguration {
	@Bean
	TodoListBehaviorModel behaviorModel(TodoLists repository) {
		return new TodoListBehaviorModel(repository);
	}
}
