package Abstraction.IntervalAbstraction.Impl;

import java.util.Collection;
import java.util.HashSet;

import Abstraction.IntervalAbstraction.IntervalRepresentation;
import Abstraction.IntervalAbstraction.PartitioningTactic;
import Utils.Interval;
import Utils.StmtApplier;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.stmt.AssignStmt;
import hu.bme.mit.theta.core.stmt.HavocStmt;
import hu.bme.mit.theta.core.stmt.SkipStmt;
import hu.bme.mit.theta.core.stmt.Stmt;

public class NoPartitioningTactic implements PartitioningTactic {

	@Override
	public Collection<IntervalRepresentation> addStmtToInterval(final IntervalRepresentation sourceInterval,
			final Stmt stmt) {
		final IntervalRepresentation newInterval = sourceInterval;
		final StmtApplier stmtApplier = new StmtApplier(sourceInterval.getMap());
		for (final VarDecl<?> var : sourceInterval.getVars()) {
			newInterval.setVarInterval(var, applyStmt(stmt, sourceInterval.getVarInterval(var), var, stmtApplier));
		}
		final Collection<IntervalRepresentation> result = new HashSet<>();
		result.add(newInterval);
		return result;
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
	public Collection<IntervalRepresentation> mergePartitions(final Collection<IntervalRepresentation> intervals) {
		// TODO Auto-generated method stub
		return null;
	}

}
