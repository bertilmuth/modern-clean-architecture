package org.requirementsascode.spring.behavior;

import java.util.Objects;
import java.util.Optional;

import org.requirementsascode.Behavior;
import org.requirementsascode.BehaviorModel;
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
