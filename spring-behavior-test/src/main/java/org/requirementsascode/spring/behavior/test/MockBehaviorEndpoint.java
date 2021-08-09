package org.requirementsascode.spring.behavior.test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An endpoint that can receive post requests and forwards them to a behavior.
 * Requires @AutoConfigureWebMvc annotation on test class (unless used in a test class
 * annotated with @WebMcTest)
 * @author b_muth
 *
 */
@Component
public class MockBehaviorEndpoint {
	private final MockMvc mockMvc;
	private final String behaviorEndpointUrl;
	private final ObjectMapper objectMapper;
	private final HttpStatus httpStatus;

	/**
	 * Creates an endpoint, and injects the dependencies necessary for testing.
	 * 
	 * @param mockMvc used for post requests
	 * @param behaviorEndpointUrl the url used to received post requests
	 * @param objectMapper used for (de)serializing requests
	 */
	@Autowired
	private MockBehaviorEndpoint(MockMvc mockMvc, @Value("${behavior.endpoint}") String behaviorEndpointUrl,
		ObjectMapper objectMapper) {
		this(mockMvc, behaviorEndpointUrl, objectMapper, HttpStatus.OK);
	}

	private MockBehaviorEndpoint(MockMvc mockMvc, String behaviorEndpointUrl,
		ObjectMapper objectMapper, HttpStatus httpStatus) {
		this.mockMvc = mockMvc;
		this.behaviorEndpointUrl = behaviorEndpointUrl;
		this.objectMapper = objectMapper;
		this.httpStatus = httpStatus;
	}

	/**
	 * Call this method to configure which status to expect in the response. Default is HttpStatus.OK.
	 * @param expectedHttpStatus the status to expect
	 * @return this endpoint, for method chaining
	 */
	public MockBehaviorEndpoint expectStatus(HttpStatus expectedHttpStatus) {
		return new MockBehaviorEndpoint(mockMvc, behaviorEndpointUrl, objectMapper, expectedHttpStatus);
	}

	/**
	 * Send a POST request to the url defined with the <code>behavior.endpoint</code> property.
	 * 
	 * @param request the request to post
	 * @return the content of the response as string
	 * @throws Exception if anything goes wrong, e.g. during serialization
	 */
	public String postRequest(Object request) throws Exception {
		String responseAsString = mockMvc
			.perform(MockMvcRequestBuilders.post(behaviorEndpointUrl).content(toJson(request)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is(httpStatus.value())).andReturn().getResponse().getContentAsString();

		return responseAsString;
	}

	/**
	 * Send a POST request to the url defined with the <code>behavior.endpoint</code> property.
	 * Responses are deserialized to an object of the specified class
	 * 
	 * @param request the request to post
	 * @param expectedResponseClass the class of response that you expect
	 * @param <T> the response type
	 * @return the content of the response as as instance of the specified class
	 * @throws Exception if anything goes wrong, e.g. during serialization
	 */
	public <T> T post(Object request, Class<T> expectedResponseClass) throws Exception {
		String resultString = postRequest(request);
		T result = jsonToObject(resultString, expectedResponseClass);
		return result;
	}

	private String toJson(final Object obj) throws JsonProcessingException {
		return objectMapper.writeValueAsString(obj);
	}

	private <T> T jsonToObject(final String json, Class<T> resultClass)
		throws JsonMappingException, JsonProcessingException {
		return objectMapper.readValue(json, resultClass);
	}
}
