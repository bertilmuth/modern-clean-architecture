package org.requirementsascode.spring.eventbus;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.requirementsascode.Behavior;
import org.requirementsascode.BehaviorModel;
import org.requirementsascode.spring.behavior.web.TransactionalBehavior;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

public class EventListeners{
	private final List<Behavior> behaviors;

	private EventListeners(Collection<BehaviorModel> behaviorModels) {
		this.behaviors = createTransactionalBehaviorsFor(behaviorModels);
	}
	
	@Async @EventListener
	public void on(Object event) {
		for (Behavior behavior : behaviors) {
			behavior.reactTo(event);
		}
	}
	
	public static EventListeners of(Collection<BehaviorModel> behaviorModels) {
		Objects.requireNonNull(behaviorModels, "behaviorModels must be non-null!");
		return new EventListeners(behaviorModels);
	}
	
	private List<Behavior> createTransactionalBehaviorsFor(Collection<BehaviorModel> behaviorModels) {
		return behaviorModels.stream()
			.map(TransactionalBehavior::new)
			.collect(Collectors.toList());
	}
}
