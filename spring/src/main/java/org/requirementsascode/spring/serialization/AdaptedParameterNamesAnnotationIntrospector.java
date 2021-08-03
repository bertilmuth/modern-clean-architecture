package org.requirementsascode.spring.serialization;

import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Parameter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;

/**
 * IMPORTANT NOTE from b_muth: This has been adapted from the official ParamNames module to
 * be able to deal with all argument constructors without JsonCreator
 * annotation.
 * 
 * See https://github.com/FasterXML/jackson-modules-java8/tree/master/parameter-names for the original module.
 */

/**
 * Introspector that uses parameter name information provided by the Java
 * Reflection API additions in Java 8 to determine the parameter name for
 * methods and constructors.
 * 
 * @author Lovro Pandzic
 * @see AnnotationIntrospector
 * @see Parameter
 */
@SuppressWarnings("serial")
class AdaptedParameterNamesAnnotationIntrospector extends NopAnnotationIntrospector {
	/**
	 * The parameter extractor
	 */
	private final ParameterExtractor parameterExtractor;

	/**
	 * Create an instance of this class
	 * 
	 * @param parameterExtractor  the parameter extractor
	 * @param typeResolverBuilder
	 */
	public AdaptedParameterNamesAnnotationIntrospector(ParameterExtractor parameterExtractor) {
		this.parameterExtractor = parameterExtractor;
	}

	@Override
	public String findImplicitPropertyName(AnnotatedMember m) {
		if (m instanceof AnnotatedParameter) {
			return findParameterName((AnnotatedParameter) m);
		}
		return null;
	}

	private String findParameterName(AnnotatedParameter annotatedParameter) {

		Parameter[] params;
		try {
			params = getParameters(annotatedParameter.getOwner());
		} catch (MalformedParametersException e) {
			return null;
		}

		Parameter p = params[annotatedParameter.getIndex()];
		return p.isNamePresent() ? p.getName() : null;
	}

	private Parameter[] getParameters(AnnotatedWithParams owner) {
		if (owner instanceof AnnotatedConstructor) {
			return parameterExtractor.getParameters(((AnnotatedConstructor) owner).getAnnotated());
		}
		if (owner instanceof AnnotatedMethod) {
			return parameterExtractor.getParameters(((AnnotatedMethod) owner).getAnnotated());
		}

		return null;
	}

	/*
	 * /********************************************************** /* Creator
	 * information handling
	 * /**********************************************************
	 */

	@Override
	public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
		JsonCreator annotation = _findAnnotation(a, JsonCreator.class);
		Class<?> serializedClass = a.getRawType();

		Mode mode = null;
		if (annotation != null) {
			mode = annotation.mode();
		} else if (!serializedClass.isEnum() && !isJdkClass(serializedClass)) {
			mode = JsonCreator.Mode.PROPERTIES;
		}
		return mode;
	}

	private boolean isJdkClass(Class<?> rawType) {
		String rawTypeName = rawType.getName();
		return rawTypeName.startsWith("java.") || rawTypeName.startsWith("javax.");
	}
}