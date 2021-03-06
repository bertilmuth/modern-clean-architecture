# spring-behavior-web
[![Gitter](https://badges.gitter.im/requirementsascode/community.svg)](https://gitter.im/requirementsascode/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

The goal of the spring-behavior-web project is to reduce the effort to develop a modern clean architecture in a Spring Boot application.

It provides the following features:
* Serialization of immutable requests/responses without extra annotations
* Single, behavior driven endpoint for all POST requests
* Transactional behavior by default (customizable if necessary)

If you have any questions, see the [Questions & Answers](https://github.com/bertilmuth/modern-clean-architecture/wiki/Questions-&-Answers) on the wiki. Or chat with me on Gitter.

## Getting started
spring-behavior-web is available on Maven Central.

If you are using Maven, include the following dependencies in your `pom.xml` file:

``` xml
<dependency>
	<groupId>org.requirementsascode</groupId>
	<artifactId>requirementsascodecore</artifactId>
	<version>2.0</version>
</dependency>
<dependency>
	<groupId>org.requirementsascode</groupId>
	<artifactId>spring-behavior-web</artifactId>
	<version>0.2.1</version>
</dependency>
```

If you are using Gradle, include the following in your `build.gradle` file:

```
implementation "org.requirementsascode:requirementsascodecore:2.0"
implementation "org.requirementsascode:spring-behavior-web:0.2.1"
```

At least Java 8 is required to use spring-behavior-web.

## Serialization of immutable requests and responses
spring-behavior-web autoconfigures Spring's JSON serialization mechanism, i.e. Spring's Jackson `ObjectMapper`.

It makes all private final fields visible for serialization, and uses the all argument constructor for deserialization of requests. 

Here's a [sample request class](https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/main/java/com/example/todolist/behavior/request/AddTaskRequest.java) that can be deserialized using spring-behavior-web. It's part of the [todolist sample application](https://github.com/bertilmuth/modern-clean-architecture/tree/main/samples/todolist):

``` java
import lombok.Value;

@Value
public class AddTaskRequest {
	UUID todoListUuid;
	String taskName;
}
```

As you can see, the class uses [Lombok](https://projectlombok.org/) to enable a concise notation. 

That's not a requirement of spring-behavior-web. You can use a POJO with getters as well.

Note that it doesn't matter if the class has zero, one or several properties. 
spring-behavior-web makes sure that the serialization works. 

You don't need to put in any extra annotation to enable this kind of (de)serialization.

## Single, behavior driven endpoint for all POST requests
### Implementation
spring-behavior-web enables you to define a single endpoint for POST requests.

You need to specify the URL of that endpoint in the `application.properties` of your application (example [here](https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/main/resources/application.properties)):

`behavior.endpoint = <your endpoint URL>`

Iff you define this property, spring-behavior-web auto-configures request serialization and sets up a controller for the endpoint in the background.

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
}
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

In order for spring-behavior-web to know about the behavior model, you need to register it as a bean. 

[Example](https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/main/java/com/example/todolist/adapter/spring/TodoListConfiguration.java):

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
1. spring-behavior-web deserializes the request received at the endpoint, 
2. spring-behavior-web passes the request to a behavior configured by the behavior model,
3. the behavior passes the request to the appropriate request handler (if there is one),
4. spring-behavior-web serializes the response (if there is one) and passes it back to the endpoint.

If the behavior doesn't find a request handler for the request class in step 3, 
or there is no response in step 4, an empty string is returned as response body.
You can customize the default response by overriding the `defaultResponse()` method of the `BehaviorModel` interface.

For more examples of behavior specification, have a look at the [behavior](https://github.com/bertilmuth/modern-clean-architecture/tree/main/samples/todolist/src/main/java/com/example/todolist/behavior) package of the [todo list sample application](https://github.com/bertilmuth/modern-clean-architecture/tree/main/samples/todolist).

To learn more about how to create behavior models, have a look at the [requirements as code web page](https://github.com/bertilmuth/requirementsascode).

### Sending POST requests
Once you started your Spring Boot application, you can send POST request to the endpoint.

You need to include an additional `@type` property in the JSON content for spring-behavior-web to determine the right request class during deserialization.

For example, this is a valid `curl` command of the todo list sample application (Unix):

`curl -H "Content-Type: application/json" -X POST -d '{"@type": "FindOrCreateListRequest"}' http://localhost:8080/todolist`

And that's the corresponsing syntax to use in Windows PowerShell:

`iwr http://localhost:8080/todolist -Method 'POST' -Headers @{'Content-Type' = 'application/json'} -Body '{"@type": "FindOrCreateListRequest"}'`

## Transactional behavior by default (customizable if necessary)
By default, spring-behavior-web wraps every call to a request handler in a transaction (using Spring's `@Transactional` annotation).

If you just want to call the request handlers without transaction support, create your own behavior bean:

``` java
@Configuration
class StatelessBehaviorConfiguration {
	@Bean
	Behavior statelessBehavior(BehaviorModel behaviorModel) {
		StatelessBehavior statelessBehavior = StatelessBehavior.of(behaviorModel);
		return statelessBehavior;
	}
}
```


