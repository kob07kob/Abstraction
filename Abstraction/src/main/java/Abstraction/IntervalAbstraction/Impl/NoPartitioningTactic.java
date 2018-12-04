package Abstraction.IntervalAbstraction.Impl;

import Abstraction.IntervalAbstraction.IntervalRepresentation;
import Abstraction.IntervalAbstraction.PartitioningTactic;
import Utils.Interval;
import Utils.StmtApplier;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.stmt.AssignStmt;
import hu.bme.mit.theta.core.stmt.AssumeStmt;
import hu.bme.mit.theta.core.stmt.HavocStmt;
import hu.bme.mit.theta.core.stmt.SkipStmt;
import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.utils.ExprUtils;

public class NoPartitioningTactic implements PartitioningTactic<IntervalRepresentation> {

	@Override
	public IntervalRepresentation addStmtToLabel(final IntervalRepresentation sourceInterval, final Stmt stmt) {
		final IntervalRepresentation newInterval = sourceInterval.copy();
		final StmtApplier stmtApplier = new StmtApplier(sourceInterval.getMap());
		for (final VarDecl<?> var : sourceInterval.getVars()) {
			newInterval.setVarInterval(var, applyStmt(stmt, sourceInterval.getVarInterval(var), var, stmtApplier));
		}
		return newInterval;
	}

	public Interval applyStmt(final Stmt stmt, final Interval interval, final VarDecl<?> var,
			final StmtApplier applier) {

		applier.setSearchedVar(var);

		if (stmt instanceof HavocStmt) {
			if (((HavocStmt<?>) stmt).getVarDecl().equals(var)) {
				return Interval.initialInterval();
			}
		}

		if (stmt instanceof AssumeStmt) {
			Expr<BoolType> expr = ((AssumeStmt) stmt).getCond();

			if (ExprUtils.getVars(expr).size() == 0) {
				// this means the assumption can be decided (usually it is a true or a false
				// expression)
				if (!applier.transform(expr).isValid()) {
					return Interval.invalidInterval();
				} else {
					return interval;
				}
			}
			if (ExprUtils.getVars(expr).contains(var)) {
				expr = ExprUtils.simplify(expr);

				// System.out.println("for var: " + var.getName() + " from interval: " +
				// interval.toString()
				// + " to interval: " + applier.transform(expr));

				// ExprUtils. must simplify so it is in var <=>... () form to return true result
				return Interval.basicSection(interval, applier.transform(expr));
			} else {
				return interval;
			}
		}

		if (stmt instanceof AssignStmt<?>) {

			if (((AssignStmt<?>) stmt).getVarDecl().equals(var)) {

				// System.out.println("for var: " + var.getName() + " from interval: " +
				// interval.toString()
				// + " to interval: " + applier.transform(((AssignStmt<?>) stmt).getExpr()));

				return applier.transform(((AssignStmt<?>) stmt).getExpr());
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
