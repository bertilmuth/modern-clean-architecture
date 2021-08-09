package org.requirementsascode.spring.behavior.web;

import org.requirementsascode.Behavior;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(prefix = "behavior", name = "endpoint")
class BehaviorController {
	private static final String EMPTY_STRING_RESPONSE = "";

	private final Behavior behavior;

	public BehaviorController(Behavior behavior) {
		this.behavior = behavior;
	}

	@PostMapping("${behavior.endpoint}")
	public Object handleRequest(@RequestBody Object request) {		
		Object response = behavior.reactTo(request).orElse(EMPTY_STRING_RESPONSE);
		return response;
	}
}
