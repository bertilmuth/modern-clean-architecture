package org.requirementsascode.serialization;

import java.util.Collection;

class Classes {
	public static boolean isSubClassOfAny(Class<?> potentialSubclass, Collection<Class<?>> superClasses) {
		return superClasses.stream().anyMatch(sC -> isSubClass(potentialSubclass, sC));
	}

	public static boolean isSubClass(Class<?> potentialSubclass, Class<?> superClass) {
		return superClass.isAssignableFrom(potentialSubclass);
	}
}