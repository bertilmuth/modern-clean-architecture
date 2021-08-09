package org.requirementsascode.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.requirementsascode.Model;
import org.requirementsascode.spring.testbehavior.TestAddTaskRequest;
import org.requirementsascode.spring.testbehavior.TestBehaviorModel;
import org.requirementsascode.spring.testbehavior.TestCreateListRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class RequestSerializationTest {
	private final ObjectMapper objectMapper;

	public RequestSerializationTest() {
		Model behaviorModel = new TestBehaviorModel().model();
		MessageSerializationModule serializationModule = new MessageSerializationModule(behaviorModel);
		this.objectMapper = new ObjectMapper().registerModule(serializationModule);
	}

	@Test
	public void readsAndWrites_createList() throws Exception {
		String jsonCreateListRequest = jsonWithType("{%s}", TestCreateListRequest.class);
		TestCreateListRequest createList = (TestCreateListRequest) objectMapper().readValue(jsonCreateListRequest,
			Object.class);
		assertEquals(jsonCreateListRequest, writeToJson(objectMapper(), createList));
	}

	@Test
	public void readsAndWrites_addTask() throws Exception {
		String jsonAddTask = jsonWithType("{%s}", TestAddTaskRequest.class);
		TestAddTaskRequest addTask = (TestAddTaskRequest) objectMapper().readValue(jsonAddTask, Object.class);
		assertEquals(jsonAddTask, writeToJson(objectMapper(), addTask));
	}

	private String jsonWithType(String jsonWithPlaceholder, Class<?> type) {
		String typeSubstring = "\"@type\":\"" + type.getSimpleName() + "\"";
		String jsonWithType = String.format(jsonWithPlaceholder, typeSubstring);
		return jsonWithType;
	}

	private ObjectMapper objectMapper() {
		return objectMapper;
	}

	private String writeToJson(ObjectMapper objectMapper, Object object) throws JsonProcessingException {
		ObjectWriter objectWriter = objectMapper.writer();
		String writtenJsonString = objectWriter.writeValueAsString(object);
		return writtenJsonString;
	}
}
