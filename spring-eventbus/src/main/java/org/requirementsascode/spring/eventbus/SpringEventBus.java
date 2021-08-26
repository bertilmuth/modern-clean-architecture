package org.requirementsascode.spring.eventbus;

import java.util.Objects;
import java.util.Optional;

import org.requirementsascode.Behavior;
import org.requirementsascode.BehaviorModel;
import org.requirementsascode.StatelessBehavior;
import org.requirementsascode.eventbus.EventBus;

class SpringEventBus implements EventBus {
	private final Behavior behavior;
	
	SpringEventBus(BehaviorModel eventBusBehaviorModel) {
		Objects.requireNonNull(eventBusBehaviorModel, "behaviorModel must not be null!");
		this.behavior = StatelessBehavior.of(eventBusBehaviorModel);
	}

	@Override
	public <T> Optional<T> reactTo(Object event) {
		return behavior.reactTo(event);
	}

	@Override
	public BehaviorModel behaviorModel() {
		return behavior.behaviorModel();
	}
}

