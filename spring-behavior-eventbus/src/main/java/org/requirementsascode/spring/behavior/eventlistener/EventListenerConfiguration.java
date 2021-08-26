package org.requirementsascode.spring.behavior.eventlistener;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.requirementsascode.Behavior;
import org.requirementsascode.BehaviorModel;
import org.requirementsascode.spring.behavior.web.TransactionalBehavior;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class EventListenerConfiguration {
	private final List<BehaviorModel> behaviorModels;

	EventListenerConfiguration(List<BehaviorModel> behaviorModels) {
		Objects.requireNonNull(behaviorModels, "behaviorModels must be non-null!");
		this.behaviorModels = behaviorModels;
	}
	
	@Bean
	EventListeners eventListeners() {
		List<Behavior> behaviors = createTransactionalBehaviorsFor(behaviorModels);
		return new EventListeners(behaviors);
	}

	private List<Behavior> createTransactionalBehaviorsFor(List<BehaviorModel> behaviorModels) {
		return behaviorModels.stream()
			.map(TransactionalBehavior::new)
			.collect(Collectors.toList());
	}
}
