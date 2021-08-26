package org.requirementsascode.spring.behavior.web;

import org.requirementsascode.BehaviorModel;
import org.requirementsascode.serialization.MessageSerializationModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class scans a registered behavior for its request classes, and those are
 * made serializable by Jackson (even if they are immutable value types).
 * 
 * @author b_muth
 *
 */
@Configuration
@ConditionalOnBean(BehaviorModel.class)
@ConditionalOnProperty(prefix = "behavior", name = "endpoint")
class SerializationConfiguration {
	/**
	 * Provides a Jackson module that scans the specified behavior model, and
	 * registers its request classes for serialization (even if they are immutable
	 * value types).
	 * 
	 * @param behaviorModel the behaviorModel to scan for request classes
	 * @return the Jackson module that is automatically registered by Spring for
	 *         serialization
	 */
	@Bean
	MessageSerializationModule requestSerializationModule(BehaviorModel behaviorModel) {
		MessageSerializationModule serializationModule = new MessageSerializationModule(behaviorModel.model());
		return serializationModule;
	}
}
