# modern-clean-architecture
[![Gitter](https://badges.gitter.im/requirementsascode/community.svg)](https://gitter.im/requirementsascode/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

Libraries for creating services with a [clean architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html), but with significantly less effort necessary than usual.

Clean architecture follows these principles:
* The domain code and use cases of an application are independent of frameworks, UI, the database or any external agency, e.g. external services.
* Through this clear separation of concern, the domain code and use cases become testable independent of the framework, UI or infrastructure.
* The user interface, database technology, external services and framework specific code can change without affecting the domain code and vice versa.

Modern clean architecture provides a fresh view on clean architecture. It reduces the effort required to create a clean architecture through the following features:
* **Single, rich domain model**. For domain logic *and* persistence. No need to maintain a separate data model, and translate between data and domain model, if you don't want them to evolve separately.
* **No necessity for DTOs.** You can use the same immutable value objects for requests/responses in web layer and use cases. 
* **Serialization of immutable requests and responses** without serialization specific annotations.
* **Testing with a repository double** that acts like a normal repository. No need for mocking it.
* **Generic endpoint** that receives and forwards POST requests. New behavior and domain logic can be added and used without the need to write framework specific code.

To start creating a modern clean architecture visit the [spring-web](https://github.com/bertilmuth/modern-clean-architecture/tree/main/spring-web) page.

For a working sample from frontend to backend, see the [todo list sample](https://github.com/bertilmuth/modern-clean-architecture/tree/main/samples/todolist).

