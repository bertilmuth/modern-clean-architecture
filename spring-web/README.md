# spring-web
The goal of the spring-web project is to reduce the effort to develop a modern clean architecture in a Spring Boot application.

It provides the following features:
* Serialization of immutable requests/responses without extra annotations
* Single, behavior driven endpoint for all POST requests
* Transactional behavior by default (customizable if necessary)

## Serialization of immutable requests and responses
spring-web autoconfigures Spring's JSON serialization mechanism, i.e. Spring's Jackson `ObjectMapper`.

It makes all private final fields visible for serialization, and uses the all argument constructor for deserialization of requests. 

Here's a [sample request class](https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/main/java/com/example/todolist/behavior/request/AddTaskRequest.java) that can be deserialized using spring-web. It's part of the [todolist sample application](https://github.com/bertilmuth/modern-clean-architecture/tree/main/samples/todolist):

``` java
import lombok.Value;

@Value
public class AddTaskRequest {
	UUID todoListUuid;
	String taskName;
}
```

As you can see, the class uses [Lombok](https://projectlombok.org/) to enable a concise notation. 
That's not a requirement of spring-web. You can use a POJO with getters as well.
Here's the long version of the above class:

``` java
public final class AddTaskRequest {
	private final UUID todoListUuid;
	private final String taskName;

	public AddTaskRequest(final UUID todoListUuid, final String taskName) {
		this.todoListUuid = todoListUuid;
		this.taskName = taskName;
	}

	public UUID getTodoListUuid() {
		return this.todoListUuid;
	}

	public String getTaskName() {
		return this.taskName;
	}
	
	// equals(), hashCode() and toString() omitted for brevity
}
```

Note that it doesn't matter if the class has zero, one or several properties. 
spring-web makes sure that the serialization works. You don't need to put in any extra annotation to enable this kind of (de)serialization.

## Single, behavior driven endpoint for all POST requests
spring-web enables you to define a single endpoint for POST requests.

You need to specify the URL of that endpoint in the `application.properties` of your application (example [here](https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/main/resources/application.properties)):

&nbsp;

`behavior.endpoint = <your endpoint URL>`

&nbsp;

spring-web autoconfigures request serialization and sets up a controller for the endpoint in the background.

That is: you don't need to write Spring specific code to add new business logic. 

That lets you focus on implementing business capabilities, instead of technical details.

&nbsp;

To augment the application's behavior, add a request handler to a so called *behavior model*.

A behavior model defines the mapping between request classes and the corresponding request handlers.

Example (source code [here](https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/main/java/com/example/todolist/behavior/TodoListBehaviorModel.java)):

``` java
public class TodoListBehaviorModel implements BehaviorModel {
	...
	@Override
	public Model model() {
		return Model.builder()
			.user(FindOrCreateListRequest.class).systemPublish(findOrCreateList())
			.user(AddTaskRequest.class).systemPublish(addTask())
			.user(ToggleTaskCompletionRequest.class).system(toggleTaskCompletion())
			...
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
	...
```

The `user(...)` statements define the request classes. The `system(...)` or `systemPublish(...)` statements define the request handlers.

Let's have a look at the [FindOrCreateList](https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/main/java/com/example/todolist/behavior/FindOrCreateList.java) request handler, for example:

``` java
class FindOrCreateList implements Function<FindOrCreateListRequest, FindOrCreateListResponse> {
	...
	
	@Override
	public FindOrCreateListResponse apply(FindOrCreateListRequest request) {
		final TodoList list = findOrCreateList();
		final UUID listUuid = list.getId().getUuid();
				
		return new FindOrCreateListResponse(listUuid);
	}
	...
}
```

It's a regular `java.util.Function` that takes a request as input and produces a response. 

In order for spring-web to know about the behavior model, you need to register it as a bean. 

[Example](https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/main/java/com/example/todolist/web/TodoListConfiguration.java):

``` java
@Configuration
class TodoListConfiguration {
	@Bean
	TodoListBehaviorModel behaviorModel(TodoLists repository) {
		return new TodoListBehaviorModel(repository);
	}
}
```

That's all.

To summarize, this is what happens when a new request is received at the endpoint specified in the `application.properties`:
1. spring-web deserializes the request received at the endpoint, 
2. spring-web passes the request to a behavior configured by the behavior model,
3. the behavior passes the request to the appropriate request handler (if there is one),
4. spring-web serializes the response (if there is one) and passes it back to the endpoint.

If the behavior doesn't find a request handler for the request class in step 3, 
or there is no response in step 4, the `defaultResponse(...)` method is called for the response.

For more examples of behavior specification, have a look at the [behavior](https://github.com/bertilmuth/modern-clean-architecture/tree/main/samples/todolist/src/main/java/com/example/todolist/behavior) package of the [todo list sample application](https://github.com/bertilmuth/modern-clean-architecture/tree/main/samples/todolist).

To learn more about how to create behavior models, have a look at the [requirements as code web page](https://github.com/bertilmuth/requirementsascode).
