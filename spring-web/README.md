# spring-web
The goal of the spring-web project is to reduce the effort to develop a modern clean architecture in a Spring Boot application.

It provides the following features:
* Serialization of immutable requests/responses without extra annotations
* Single, behavior driven endpoint for all post requests
* Transactional behavior by default (customizable if necessary)

## Serialization of immutable requests and responses
spring-web autoconfigures Spring's JSON serialization mechanism (i.e. Spring's Jackson ObjectMapper).
It makes all private final fields visible for (de)serialization, and uses the all argument constructor for deserialization of requests. 

Here's a sample request class that can be deserialized using spring-web.
It's part of the [todolist sample application](https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/main/java/com/example/todolist/behavior/request/AddTaskRequest.java):

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