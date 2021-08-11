# modern-clean-architecture
[![Gitter](https://badges.gitter.im/requirementsascode/community.svg)](https://gitter.im/requirementsascode/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

Create services with a [clean architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) with significantly less effort necessary than usual.

The main idea of Clean Architecture: Domain code and use cases are independent of frameworks, UI, the database and external services.

So building an application in a Clean Architecture style has a positive effect on maintainability:
* You can test domain code and use cases without the framework, UI and infrastructure.
* Technology decisions can change without affecting the domain code. And vice versa. It is even possible to switch to a new framework with limited effort.

Modern clean architecture reduces the effort to create a clean architecture through the following features:
* **Serialization of immutable requests and responses** without serialization specific annotations.
* **No necessity for DTOs.** You can use the same immutable value objects for requests/responses in web layer and use cases. 
* **Generic endpoint** that receives and forwards POST requests. New behavior and domain logic can be added and used without the need to write framework specific code.
* **Testing with a repository double** that acts like a normal repository. No need for mocking it.

To start creating a modern clean architecture, visit the [spring-behavior-web](https://github.com/bertilmuth/modern-clean-architecture/tree/main/spring-behavior-web) page.

For testing support, see the [spring-behavior-test](https://github.com/bertilmuth/modern-clean-architecture/tree/main/spring-behavior-test) page.

For a working sample from frontend to backend, see the [To Do List sample](https://github.com/bertilmuth/modern-clean-architecture/tree/main/samples/todolist).

If you have any questions, see the Q[uestions & Answers](https://github.com/bertilmuth/modern-clean-architecture/wiki/Questions-&-Answers) on the wiki. Or chat with me on Gitter.

