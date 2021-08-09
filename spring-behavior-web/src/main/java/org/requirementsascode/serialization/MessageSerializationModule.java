package org.requirementsascode.serialization;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.requirementsascode.Model;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A module that makes sure that message classes can be properly (de)serialized
 * from/to JSON using a Jackson ObjectMapper, even if the messages are immutable
 * objects and even if java.lang.Object is used as class for serialization.
 * 
 * @author b_muth
 *
 */
@SuppressWarnings("serial")
public class MessageSerializationModule extends SimpleModule {
	/**
	 * The name of the property that users need to use to specify the message type
	 */
	private static final String TYPE_PROPERTY_NAME = "@type";
	/**
	 * The classes of messages to serialize
	 */
	private final Collection<Class<?>> messageClasses;

	/**
	 * Crestes a new instance of the module that enables serialization of the
	 * specified message classes.
	 * 
	 * @param behaviorModel the classes of messages to serialize
	 */
	public MessageSerializationModule(Model behaviorModel) {
		super(PackageVersion.VERSION);
		Objects.requireNonNull(behaviorModel, "behaviorModel must not be null!");
		this.messageClasses = messageClassesOf(behaviorModel);
	}

	private Set<Class<?>> messageClassesOf(Model behaviorModel) {
		Set<Class<?>> messageClasses = behaviorModel.getSteps().stream()
			.map(s -> s.getMessageClass())
			.collect(Collectors.toSet());
		return messageClasses;
	}

	@Override
	public void setupModule(SetupContext context) {
		super.setupModule(context);

		ObjectMapper objectMapper = context.getOwner();
		makeAllClassFieldsVisible(objectMapper);
		dontFailOnUnknownProperties(objectMapper);
		dontFailOnEmptyBeans(objectMapper);
		registerClassesForJsonDeserialization(objectMapper, messageClasses);
		serializeObjectProperties(context);
	}

	private void makeAllClassFieldsVisible(ObjectMapper objectMapper) {
		objectMapper.setVisibility(FIELD, ANY);
	}

	private void dontFailOnUnknownProperties(ObjectMapper objectMapper) {
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private void dontFailOnEmptyBeans(ObjectMapper objectMapper) {
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	private void registerClassesForJsonDeserialization(ObjectMapper objectMapper, Collection<Class<?>> messageClasses) {
		PolymorphicTypeValidator ptv = SubClassValidator.forSubclassesOf(messageClasses);

		StdTypeResolverBuilder typeResolverBuilder = new JacksonSubClassResolverBuilder(messageClasses, ptv)
			.init(Id.CUSTOM, new ClassResolver(messageClasses)).inclusion(As.PROPERTY).typeIdVisibility(false)
			.typeProperty(TYPE_PROPERTY_NAME);

		objectMapper.setDefaultTyping(typeResolverBuilder);
	}

	private void serializeObjectProperties(SetupContext context) {
		final ParameterExtractor parameterExtractor = new ParameterExtractor();
		final AnnotationIntrospector ai = new AdaptedParameterNamesAnnotationIntrospector(parameterExtractor);
		context.insertAnnotationIntrospector(ai);
	}
}
