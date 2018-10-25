package Abstraction.IntervalAbstraction.Impl;

import Abstraction.IntervalAbstraction.IntervalRepresentation;
import Abstraction.IntervalAbstraction.WideningTactic;
import hu.bme.mit.theta.core.stmt.Stmt;

public class NoWideningTactic implements WideningTactic<IntervalRepresentation> {

	@Override
	public IntervalRepresentation wideningConvert(final IntervalRepresentation sourceLabel, final Stmt stmt,
			final IntervalRepresentation targetLabel) {
		return new NoPartitioningTactic().addStmtToLabel(sourceLabel, stmt);
	}

}
