package Abstraction.IntervalAbstraction.Impl;

import Abstraction.IntervalAbstraction.IntervalRepresentationWithColor;
import Abstraction.IntervalAbstraction.WideningTactic;
import Utils.Interval;
import hu.bme.mit.theta.cfa.CFA.Edge;
import hu.bme.mit.theta.cfa.CFA.Loc;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.stmt.Stmt;

public class SimpleWideningTactic4Color implements WideningTactic<IntervalRepresentationWithColor> {

	NoPartitioningTacticforColor np = new NoPartitioningTacticforColor();

	@Override
	public IntervalRepresentationWithColor wideningConvert(final IntervalRepresentationWithColor sourceLabel,
			final Stmt stmt, final IntervalRepresentationWithColor targetLabel, final Loc sourceLoc) {
		final IntervalRepresentationWithColor newLabel = np.addStmtToLabel(sourceLabel, stmt, sourceLoc);

		IntervalRepresentationWithColor fantom = sourceLabel.copy();

		fantom = fantom.merge2OneLocation(sourceLoc);
		for (final VarDecl<?> var : sourceLabel.getWidenedVariables(newLabel, sourceLoc)) {
			fantom.setVarIntervalforLoc(var, Interval.getWidenedInterval(sourceLabel, newLabel, var, sourceLoc),
					sourceLoc);
		}

		for (final Edge edge : sourceLoc.getInEdges()) {
			fantom = (IntervalRepresentationWithColor) sourceLabel
					.addLabel(np.addStmtToLabel(fantom, edge.getStmt(), edge.getSource()));
		}

		return (IntervalRepresentationWithColor) targetLabel.addLabel(np.addStmtToLabel(fantom, stmt, sourceLoc));
	}

}
