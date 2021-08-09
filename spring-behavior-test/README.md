# spring-behavior-test
[![Gitter](https://badges.gitter.im/requirementsascode/community.svg)](https://gitter.im/requirementsascode/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

The goal of the spring-behavior-test project is to support testing of applications with a modern clean architecture.

It provides the following features:
* a test repository that acts as a test double for Spring Data `CrudRepository` or jMolecules `Repository`
* a mock behavior endpoint to test behavior endpoints created by the spring-behavior-web project

## Getting started
spring-behavior-test is available on Maven Central.

If you are using Maven, include the following in your `pom.xml` file:

``` xml
<dependency>
	<groupId>org.requirementsascode</groupId>
	<artifactId>spring-behavior-test</artifactId>
	<version>0.2</version>
	<scope>test</scope>
</dependency>
```

If you are using Gradle, include the following in your `build.gradle` file:

```
testImplementation "org.requirementsascode:spring-behavior-test:0.2"
```

At least Java 8 is required to use spring-behavior-test.

## Test repository
To create a test repository that acts as a test double for a real repository, you need to create a repository class.

That repository class must extend the `TestRepository` class, and pass an implementation of the [EntityAccess](https://github.com/bertilmuth/modern-clean-architecture/blob/main/spring-behavior-test/src/main/java/org/requirementsascode/spring/behavior/test/EntityAccess.java) interface to the super class constructor.

[Example](https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/test/java/com/example/todolist/domain/TestTodoLists.java):

``` java
public class TestTodoLists extends TestRepository<TodoList, TodoListId> implements TodoLists {

	public TestTodoLists() {
		super(new TodoListAccess());
	}

	private static class TodoListAccess implements EntityAccess<TodoList, TodoListId> {
		@Override
		public TodoListId idOf(TodoList entity) {
			return entity.getId();
		}

		@Override
		public TodoListId nextId() {
			return TodoListId.of(UUID.randomUUID());
		}

		@Override
		public TodoList copyWithId(TodoList todoList, TodoListId id) {
			List<Task> tasks = Objects.requireNonNull(todoList.listTasks(), "tasks must not be null!");
			final TodoList newTodoList = new TodoList(id, tasks);
			return newTodoList;
		}
	}
}
```

## Mock behavior endpoint
If you are using spring-behavior-test together with [spring-behavior-web](https://github.com/bertilmuth/modern-clean-architecture/tree/main/spring-behavior-web), you can mock the behavior endpoint and send POST requests to it.

The syntax is shown in the following [example](https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/test/java/com/example/todolist/web/TodoListBehaviorEndpointTest.java):

``` java 
@WebMvcTest
public class TodoListBehaviorEndpointTest {
	@Autowired
	private MockBehaviorEndpoint endpoint;
	
	...

	FindOrCreateListResponse response = endpoint.post(new FindOrCreateListRequest(), FindOrCreateListResponse.class);
	...
}
```

As you can see, the first argument of the `post(...)` method is the request object.

The second argument is the expected respose class.

The return value is the response received.

Internally, the `post(...)` method will call Spring's `MockMvcRequestBuilders.post(...)` and perform the necessary (de)serialization.

