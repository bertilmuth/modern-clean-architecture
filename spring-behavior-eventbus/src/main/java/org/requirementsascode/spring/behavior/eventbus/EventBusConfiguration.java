package org.requirementsascode.spring.behavior.eventbus;

import org.requirementsascode.BehaviorModel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class EventBusConfiguration {
	@Bean
	SpringEventBus eventBusBehavior(ApplicationEventPublisher eventPublisher) {
		BehaviorModel behaviorModel = new EventBusBehaviorModel(eventPublisher);
		return new SpringEventBus(behaviorModel);
	}
}
