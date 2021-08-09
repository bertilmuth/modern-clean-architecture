package org.requirementsascode.spring.behavior.web;

import java.util.Objects;
import java.util.Optional;

import org.requirementsascode.Behavior;
import org.requirementsascode.BehaviorModel;
import org.requirementsascode.StatelessBehavior;
import org.springframework.transaction.annotation.Transactional;

/**
 * Wraps any behavior so that a new transaction is started whenever
 * {@link #reactTo(Object)} is called.
 * 
 * @author b_muth
 *
 */
public class TransactionalBehavior implements Behavior {
	private final Behavior behavior;

	/**
	 * Convenience constructor that first creates a stateless behavior
	 * from the specified model, and then creates a transactional behavior
	 * based on the stateless behavior.
	 * 
	 * @param behaviorModel the behavior model to create a trasactional behavior for
	 */
	public TransactionalBehavior(BehaviorModel behaviorModel) {
		this(StatelessBehavior.of(behaviorModel));
	}
	
	/**
	 * Wraps the specified behavior so that a new transaction is started whenever
	 * {@link #reactTo(Object)} is called.
	 * 
	 * @param behavior the not yet transactional behavior
	 */
	public TransactionalBehavior(Behavior behavior) {
		this.behavior = Objects.requireNonNull(behavior, "behavior must not be null!");
	}

	@Transactional
	@Override
	public <T> Optional<T> reactTo(Object message) {
		return behavior.reactTo(message);
	}

	@Override
	public BehaviorModel behaviorModel() {
		return behavior.behaviorModel();
	}
}
