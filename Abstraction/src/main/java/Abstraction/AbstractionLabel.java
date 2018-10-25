package Abstraction;

import java.util.Collection;

import hu.bme.mit.theta.core.decl.VarDecl;

public abstract class AbstractionLabel {

	public AbstractionLabel() {
	}

	public abstract AbstractionLabel createInitialLabel(final Collection<VarDecl<?>> vars);

	@Override
	public abstract boolean equals(Object o);

}
