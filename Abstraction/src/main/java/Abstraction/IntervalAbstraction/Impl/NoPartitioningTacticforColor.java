package Abstraction.IntervalAbstraction.Impl;

import java.util.HashMap;
import java.util.Map;

import Abstraction.IntervalAbstraction.IntervalRepresentationWithColor;
import Abstraction.IntervalAbstraction.PartitioningTactic;
import Utils.Interval;
import Utils.StmtApplier;
import hu.bme.mit.theta.cfa.CFA.Loc;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.stmt.AssignStmt;
import hu.bme.mit.theta.core.stmt.AssumeStmt;
import hu.bme.mit.theta.core.stmt.HavocStmt;
import hu.bme.mit.theta.core.stmt.SkipStmt;
import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.utils.ExprUtils;

public class NoPartitioningTacticforColor implements PartitioningTactic<IntervalRepresentationWithColor> {

	@Override
	public IntervalRepresentationWithColor addStmtToLabel(final IntervalRepresentationWithColor sourceInterval,
			final Stmt stmt, final Loc sourceLoc) {
		final IntervalRepresentationWithColor newInterval = sourceInterval.createEmpty();

		// sourceInterval.getMap().forEach((loc, map) -> {
		for (final Loc loc : sourceInterval.getMap().keySet()) {
			final StmtApplier stmtApplier = new StmtApplier(sourceInterval.getMap().get(loc));

			final Map<VarDecl<?>, Interval> map = new HashMap<>();

			for (final VarDecl<?> var : sourceInterval.getVars()) {
				map.put(var, applyStmt(stmt, sourceInterval.getVarIntervalforLoc(var, loc), var, stmtApplier));
			}
			newInterval.getMap().put(loc, map);
		} // );

		return newInterval.merge2OneLocation(sourceLoc);
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
	public IntervalRepresentationWithColor mergePartitions(final IntervalRepresentationWithColor labels) {
		return labels;
	}

}
