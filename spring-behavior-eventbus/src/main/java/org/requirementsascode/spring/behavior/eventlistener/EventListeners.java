package org.requirementsascode.spring.behavior.eventlistener;

import java.util.List;

import org.requirementsascode.Behavior;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

class EventListeners{
	private final List<Behavior> behaviors;

	EventListeners(List<Behavior> behaviors) {
		this.behaviors = behaviors;
	}
	
	@Async @EventListener
	public void on(Object event) {
		for (Behavior behavior : behaviors) {
			behavior.reactTo(event);
		}
	}
}
