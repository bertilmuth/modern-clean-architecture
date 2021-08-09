package org.requirementsascode.spring.testbehavior;

import org.requirementsascode.Model;
import org.requirementsascode.BehaviorModel;

public class EmptyBehavior implements BehaviorModel {
	@Override
	public Model model() {
		return Model.builder().build();
	}

	@Override
	public Object defaultResponse() {
		return null;
	}
}