package Abstraction.IntervalAbstraction.Impl;

import Abstraction.IntervalAbstraction.IntervalRepresentationWithColor;
import Abstraction.IntervalAbstraction.WideningTactic;
import hu.bme.mit.theta.cfa.CFA.Loc;
import hu.bme.mit.theta.core.stmt.Stmt;

public class NoWideningTactic4Color implements WideningTactic<IntervalRepresentationWithColor> {

	NoPartitioningTacticforColor np = new NoPartitioningTacticforColor();

	@Override
	public IntervalRepresentationWithColor wideningConvert(final IntervalRepresentationWithColor sourceLabel,
			final Stmt stmt, final IntervalRepresentationWithColor targetLabel, final Loc sourceLoc) {
		return (IntervalRepresentationWithColor) targetLabel.addLabel(np.addStmtToLabel(sourceLabel, stmt, sourceLoc));
	}

}
