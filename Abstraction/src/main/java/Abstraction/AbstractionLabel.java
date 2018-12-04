package Abstraction;

import java.util.Collection;

import hu.bme.mit.theta.core.decl.VarDecl;

public abstract class AbstractionLabel {

	public AbstractionLabel() {
	}

	// to start an initial label which can be added to the first location
	public abstract AbstractionLabel createInitialLabel(final Collection<VarDecl<?>> vars);

	// the label is valid is the edge that allows us to reach a certain location,
	// but that is not possible (for example the assumption denies it) than an
	// invalid label should show that.
	public abstract boolean isValid();

	// to ensure that if multiple edges modify the same location the labels could be
	// added together by this method
	public abstract AbstractionLabel addLabel(AbstractionLabel other);

	// to make sure when two labels are the same (important during fixpoint check)
	@Override
	public abstract boolean equals(Object o);

}
