package org.requirementsascode.spring.behavior.eventbus;

import org.requirementsascode.BehaviorModel;
import org.requirementsascode.Model;
import org.springframework.context.ApplicationEventPublisher;

class EventBusBehaviorModel implements BehaviorModel{
	private final ApplicationEventPublisher eventPublisher;

	EventBusBehaviorModel(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	@Override
	public Model model() {
		return Model.builder()
			.on(Object.class).system(this::publishEvent)
			.build();
	}
	
	private void publishEvent(Object event) {
		eventPublisher.publishEvent(event);
	}
}

