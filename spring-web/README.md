# spring-web
[![Gitter](https://badges.gitter.im/requirementsascode/community.svg)](https://gitter.im/requirementsascode/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

The goal of the spring-web project is to reduce the effort to develop a modern clean architecture in a Spring Boot application.

It provides the following features:
* Serialization of immutable requests/responses without extra annotations
* Single, behavior driven endpoint for all POST requests
* Transactional behavior by default (customizable if necessary)

## Getting started
spring-web is available on Maven Central.

If you are using Maven, include the following in your `pom.xml` file:

``` xml
<dependency>
	<groupId>org.requirementsascode</groupId>
	<artifactId>spring-web</artifactId>
	<version>0.1.3</version>
</dependency>
```

If you are using Gradle, include the following in your `build.gradle` file:

```
implementation ("org.requirementsascode:spring-web:0.1.3")
```

At least Java 8 is required to use spring-web.

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

Note that it doesn't matter if the class has zero, one or several properties. 
spring-web makes sure that the serialization works. 

You don't need to put in any extra annotation to enable this kind of (de)serialization.

## Single, behavior driven endpoint for all POST requests
### Implementation
spring-web enables you to define a single endpoint for POST requests.

You need to specify the URL of that endpoint in the `application.properties` of your application (example [here](https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/main/resources/application.properties)):

`behavior.endpoint = <your endpoint URL>`

Iff you define this property, spring-web auto-configures request serialization and sets up a controller for the endpoint in the background.

That is: you don't need to write Spring specific code to add new business logic. 

That lets you focus on implementing business capabilities, instead of technical details.

&nbsp;

To augment the application's behavior, add a request handler to a so called *behavior model*.

A class implementing the [BehaviorModel](https://github.com/bertilmuth/requirementsascode/blob/master/requirementsascodecore/src/main/java/org/requirementsascode/BehaviorModel.java) interface defines the mapping between request classes and the corresponding request handlers.

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

	private Function<FindOrCreateListRequest, FindOrCreateListResponse> findOrCreateList() {
		return new FindOrCreateList(todoLists);
	}

	private Function<AddTaskRequest, AddTaskResponse> addTask() {
		return new AddTask(todoLists);
	}

	private Consumer<ToggleTaskCompletionRequest> toggleTaskCompletion() {
		return new ToggleTaskCompletion(todoLists);
	}
	...
```

The `user(...)` statements define the request classes. The `system(...)` or `systemPublish(...)` statements define the request handlers.

Look at the [FindOrCreateList](https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/main/java/com/example/todolist/behavior/FindOrCreateList.java) request handler, for example:

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

Once you've implemented that, here's what happens when a new request is received at the endpoint specified in the `application.properties`:
1. spring-web deserializes the request received at the endpoint, 
2. spring-web passes the request to a behavior configured by the behavior model,
3. the behavior passes the request to the appropriate request handler (if there is one),
4. spring-web serializes the response (if there is one) and passes it back to the endpoint.

If the behavior doesn't find a request handler for the request class in step 3, 
or there is no response in step 4, an empty string is returned as response body.
You can customize the default response by overriding the `defaultResponse()` method of the `BehaviorModel` interface.

For more examples of behavior specification, have a look at the [behavior](https://github.com/bertilmuth/modern-clean-architecture/tree/main/samples/todolist/src/main/java/com/example/todolist/behavior) package of the [todo list sample application](https://github.com/bertilmuth/modern-clean-architecture/tree/main/samples/todolist).

To learn more about how to create behavior models, have a look at the [requirements as code web page](https://github.com/bertilmuth/requirementsascode).

### Sending POST requests
Once you started your Spring Boot application, you can send POST request to the endpoint.

You need to include an additional `@type` property in the JSON content for spring-web to determine the right request class during deserialization.

For example, this is a valid `curl` command of the todo list sample application (Unix):

`curl -H "Content-Type: application/json" -X POST -d '{"@type": "FindOrCreateListRequest"}' http://localhost:8080/todolist`

And that's the corresponsing syntax to use in Windows PowerShell:

`iwr http://localhost:8080/todolist -Method 'POST' -Headers @{'Content-Type' = 'application/json'} -Body '{"@type": "FindOrCreateListRequest"}'`

## Handling of GET requests (and other HTTP methods)
For requests that are not POST requests, you need to implement the following:
1. Create a Spring Controller, and inject a `Behavior` instance into it.
2. In the controller method, create a request object based on the request parameters.
3. Call the behavior's `reactTo(...)` method with the request object, and handle the optional response.

See the [TodoListGetRequestExample](https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/main/java/com/example/todolist/web/TodoListGetRequestExample.java) for details:

``` java
@RestController
class TodoListGetRequestExample {
	private final Behavior behavior;

	// 1. Inject the behavior
	TodoListGetRequestExample(Behavior behavior) {
		this.behavior = behavior;
	}

	@GetMapping("${behavior.endpoint}")
	public Object listTasks(@RequestParam UUID todoListUuid) {
		// 2. Create the request object
		ListTasksRequest request = new ListTasksRequest(todoListUuid);
		
		// 3. Call the behavior, and handle the optional response
		Optional<Object> optionalResponse = behavior.reactTo(request);
		final Object response = optionalResponse.orElse("");
				
		return response;
	}
}
```

## Transactional behavior by default (customizable if necessary)
By default, spring-web wraps every call to a request handler in a transaction (using Spring's `@Transactional` annotation).

If you just want to call the request handlers without transaction support, create your own behavior bean:

``` java
@Configuration
class StatelessBehaviorConfiguration {
	@Bean
	Behavior statelessBehaviorOf(BehaviorModel behaviorModel) {
		StatelessBehavior statelessBehavior = StatelessBehavior.of(behaviorModel);
		return statelessBehavior;
	}
}
```
