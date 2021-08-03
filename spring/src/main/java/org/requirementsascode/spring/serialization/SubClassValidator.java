package org.requirementsascode.spring.serialization;

import static org.requirementsascode.spring.serialization.Classes.isSubClassOfAny;

import java.util.Collection;

import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator.TypeMatcher;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

class SubClassValidator {
	private PolymorphicTypeValidator polymorphicTypeValidator;

	public static PolymorphicTypeValidator forSubclassesOf(Collection<Class<?>> superClasses) {
		return new SubClassValidator(superClasses).polymorphicTypeValidator();
	}

	private SubClassValidator(Collection<Class<?>> superClasses) {
		PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
			.allowIfSubType(isSubClassOfAnySuperClass(superClasses)).build();
		setPolymorphicTypeValidator(ptv);
	}

	private TypeMatcher isSubClassOfAnySuperClass(Collection<Class<?>> superClasses) {
		return new TypeMatcher() {
			@Override
			public boolean match(MapperConfig<?> config, Class<?> potentialSubclass) {
				return isSubClassOfAny(potentialSubclass, superClasses);
			}
		};
	}

	private PolymorphicTypeValidator polymorphicTypeValidator() {
		return polymorphicTypeValidator;
	}

	private void setPolymorphicTypeValidator(PolymorphicTypeValidator polymorphicTypeValidator) {
		this.polymorphicTypeValidator = polymorphicTypeValidator;
	}
}
