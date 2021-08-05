package org.requirementsascode.spring.behavior;

import org.requirementsascode.Behavior;
import org.requirementsascode.BehaviorModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class registers a behavior (if there isn't any yet). The behavior is
 * based on a behavior model bean.
 * 
 * @author b_muth
 *
 */
@Configuration
@ConditionalOnMissingBean(Behavior.class)
@ConditionalOnBean(BehaviorModel.class)
class BehaviorConfiguration {
	/**
	 * Registers a transactional behavior based on the specified behavior model.
	 * 
	 * @param behaviorModel the behavior model specifying the behavior to be
	 *                      registered
	 * @return the registered behavior
	 */
	@Bean
	Behavior transactionalBehaviorOf(BehaviorModel behaviorModel) {
		TransactionalBehavior transactionalBehavior = new TransactionalBehavior(behaviorModel);
		return transactionalBehavior;
	}
}
