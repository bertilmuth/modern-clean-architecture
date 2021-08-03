package org.requirementsascode.spring.serialization;

import static org.requirementsascode.spring.serialization.Classes.isSubClassOfAny;

import java.util.Collection;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

/**
 * Enable Jackson to resolve types that are sub classes of specified classes
 * 
 * @author b_muth
 *
 */
class JacksonSubClassResolverBuilder extends DefaultTypeResolverBuilder {
	private static final long serialVersionUID = 1L;
	private Collection<Class<?>> superClasses;

	/**
	 * Enable Jackson to resolve all types of the specified classes.
	 * 
	 * @param superClasses the classes whose types to resolve.
	 * 
	 * @param ptv          the type validator that validates all the sub classes of
	 *                     the super classes.
	 */
	public JacksonSubClassResolverBuilder(Collection<Class<?>> superClasses, PolymorphicTypeValidator ptv) {
		super(DefaultTyping.NON_FINAL, ptv);
		setSuperClasses(superClasses);
	}

	@Override
	public boolean useForType(JavaType t) {
		final Class<?> currentClass = t.getRawClass();
		final boolean isSubclassOfAnySuperclass = isSubClassOfAny(currentClass, superClasses());
		return t.isJavaLangObject() || isSubclassOfAnySuperclass;
	}

	private Collection<Class<?>> superClasses() {
		return superClasses;
	}

	private void setSuperClasses(Collection<Class<?>> superClasses) {
		this.superClasses = superClasses;
	}
}