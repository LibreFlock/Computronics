package org.libreflock.computronics.integration.buildcraft.statements.parameters;

import buildcraft.api.statements.IStatementParameter;
import org.libreflock.computronics.integration.buildcraft.statements.StatementParameters;

/**
 * @author Vexatos
 */
public abstract class ComputronicsParameter implements IStatementParameter {

	protected final String name;

	public ComputronicsParameter(StatementParameters param) {
		this.name = param.name;
	}

	@Override
	public String getUniqueTag() {
		return "computronics:parameter." + this.name;
	}

}
