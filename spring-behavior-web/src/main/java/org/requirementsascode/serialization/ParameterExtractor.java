package org.requirementsascode.serialization;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

class ParameterExtractor implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public Parameter[] getParameters(Executable executable) {
		return executable.getParameters();
	}
}