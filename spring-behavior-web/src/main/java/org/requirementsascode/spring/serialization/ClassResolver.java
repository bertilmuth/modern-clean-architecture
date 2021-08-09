package org.requirementsascode.spring.serialization;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

class ClassResolver extends TypeIdResolverBase {
	private Map<String, String> classSimpleNameToFullNameMap;

	public ClassResolver(Collection<Class<?>> classes) {
		super(TypeFactory.defaultInstance().constructType(java.lang.Object.class), TypeFactory.defaultInstance());
		this.classSimpleNameToFullNameMap = classSimpleToFullNameMapOf(classes);
	}

	@Override
	public String idFromValue(java.lang.Object currentObject) {
		return idFromValueAndType(currentObject, currentObject.getClass());
	}

	@Override
	public String idFromValueAndType(java.lang.Object currentObject, Class<?> suggestedType) {
		String id = suggestedType.getSimpleName();
		return id;
	}

	@Override
	public JavaType typeFromId(DatabindContext context, String simpleClassName) throws IOException {
		JavaType subType = resolveMessageType(classSimpleNameToFullNameMap, simpleClassName, context);
		return subType;
	}

	private JavaType resolveMessageType(Map<String, String> messageTypeSimpleNameToFullNameMap, String simpleClassName,
		DatabindContext context) throws IOException {
		String fullyQualifiedClassName = messageTypeSimpleNameToFullNameMap.get(simpleClassName);

		JavaType messageType = null;
		if (fullyQualifiedClassName != null) {
			messageType = context.resolveSubType(_baseType, fullyQualifiedClassName);
		}

		return messageType;
	}

	@Override
	public Id getMechanism() {
		return Id.CUSTOM;
	}

	private Map<String, String> classSimpleToFullNameMapOf(Collection<Class<?>> classes) {
		Map<String, String> classSimpleNameToFullNameMap = classes.stream()
			.collect(Collectors.toMap(Class::getSimpleName, Class::getName));
		return classSimpleNameToFullNameMap;
	}
}