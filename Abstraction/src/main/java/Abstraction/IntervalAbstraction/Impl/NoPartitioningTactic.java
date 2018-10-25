package Abstraction.IntervalAbstraction.Impl;

import Abstraction.IntervalAbstraction.IntervalRepresentation;
import Abstraction.IntervalAbstraction.PartitioningTactic;
import Utils.Interval;
import Utils.StmtApplier;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.stmt.AssignStmt;
import hu.bme.mit.theta.core.stmt.HavocStmt;
import hu.bme.mit.theta.core.stmt.SkipStmt;
import hu.bme.mit.theta.core.stmt.Stmt;

public class NoPartitioningTactic implements PartitioningTactic<IntervalRepresentation> {

	@Override
	public IntervalRepresentation addStmtToLabel(final IntervalRepresentation sourceInterval, final Stmt stmt) {
		final IntervalRepresentation newInterval = sourceInterval;
		final StmtApplier stmtApplier = new StmtApplier(sourceInterval.getMap());
		for (final VarDecl<?> var : sourceInterval.getVars()) {
			newInterval.setVarInterval(var, applyStmt(stmt, sourceInterval.getVarInterval(var), var, stmtApplier));
		}
		return newInterval;
	}

	public Interval applyStmt(final Stmt stmt, final Interval interval, final VarDecl<?> var,
			final StmtApplier applier) {

		if (stmt instanceof HavocStmt) {
			if (((HavocStmt<?>) stmt).getVarDecl().equals(var)) {
				return Interval.initialInterval();
			}
		}
		/*
		 * if(stmt instanceof AssumeStmt) { ((AssumeStmt) stmt).getCond() }
		 */
		// TODO apply assumeStmt
		if (stmt instanceof AssignStmt<?>) {
			if (((AssignStmt<?>) stmt).getVarDecl().equals(var)) {
				return Interval.basicSection(interval, applier.transform(((AssignStmt<?>) stmt).getExpr()));
			}
		}

		if (stmt instanceof SkipStmt) {
			return interval;
		}

		return interval;

	}

	@Override
	public IntervalRepresentation mergePartitions(final IntervalRepresentation labels) {
		return labels;
	}

}
