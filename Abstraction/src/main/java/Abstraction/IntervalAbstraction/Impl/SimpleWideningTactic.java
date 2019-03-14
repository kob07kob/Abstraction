package Abstraction.IntervalAbstraction.Impl;

import Abstraction.IntervalAbstraction.IntervalRepresentation;
import Abstraction.IntervalAbstraction.WideningTactic;
import Utils.Interval;
import hu.bme.mit.theta.cfa.CFA.Edge;
import hu.bme.mit.theta.cfa.CFA.Loc;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.stmt.Stmt;

public class SimpleWideningTactic implements WideningTactic<IntervalRepresentation> {

	NoPartitioningTactic np = new NoPartitioningTactic();

	@Override
	public IntervalRepresentation wideningConvert(final IntervalRepresentation sourceLabel, final Stmt stmt,
			final IntervalRepresentation targetLabel, final Loc sourceLoc) {
		final IntervalRepresentation newLabel = np.addStmtToLabel(sourceLabel, stmt, sourceLoc);

		IntervalRepresentation fantom = sourceLabel.copy();

		for (final VarDecl<?> var : sourceLabel.getWidenedVariables(newLabel)) {
			fantom.setVarInterval(var, Interval.getWidenedInterval(sourceLabel, newLabel, var));
		}

		for (final Edge edge : sourceLoc.getInEdges()) {
			fantom = (IntervalRepresentation) sourceLabel
					.addLabel(np.addStmtToLabel(fantom, edge.getStmt(), edge.getSource()));
		}

		return (IntervalRepresentation) targetLabel.addLabel(np.addStmtToLabel(fantom, stmt, sourceLoc));
	}

}
