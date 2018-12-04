package Abstraction.IntervalAbstraction.Impl;

import Abstraction.IntervalAbstraction.IntervalRepresentation;
import Abstraction.IntervalAbstraction.WideningTactic;
import hu.bme.mit.theta.core.stmt.Stmt;

public class NoWideningTactic implements WideningTactic<IntervalRepresentation> {

	NoPartitioningTactic np = new NoPartitioningTactic();

	@Override
	public IntervalRepresentation wideningConvert(final IntervalRepresentation sourceLabel, final Stmt stmt,
			final IntervalRepresentation targetLabel) {
		return (IntervalRepresentation) targetLabel.addLabel(np.addStmtToLabel(sourceLabel, stmt));
	}

}
