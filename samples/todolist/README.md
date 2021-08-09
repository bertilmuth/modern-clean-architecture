# To Do List sample
To run the sample with Gradle, change into your local todolist directory, and enter the following in a shell:

`./gradlew bootRun`

Alternatively, you can run it with Maven:

`mvn spring-boot:run`

Then, visit `http://localhost:8080/` in your browser.

# acknowledgements
Thank you to Oliver Drotbohm for pointing me to the awesome [jMolecules](https://github.com/xmolecules/jmolecules) library.  The todolist sample uses a single data model as domain model and for persistence. This is enabled by jMolecules together with a byte buddy plugin. The plugin automatically translates the (framework independent) jMolecules DDD annotations to Spring Data code.


Thank you to Surya Shakti for providing the original frontend only [todo list code](https://suryashakti1999.medium.com/to-do-list-app-using-javascript-for-absolute-beginners-13ea9e38a033).

