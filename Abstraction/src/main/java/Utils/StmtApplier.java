package Utils;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import hu.bme.mit.theta.common.DispatchTable;
import hu.bme.mit.theta.core.decl.ConstDecl;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.abstracttype.AddExpr;
import hu.bme.mit.theta.core.type.abstracttype.DivExpr;
import hu.bme.mit.theta.core.type.abstracttype.GeqExpr;
import hu.bme.mit.theta.core.type.abstracttype.GtExpr;
import hu.bme.mit.theta.core.type.abstracttype.LeqExpr;
import hu.bme.mit.theta.core.type.abstracttype.LtExpr;
import hu.bme.mit.theta.core.type.abstracttype.MulExpr;
import hu.bme.mit.theta.core.type.abstracttype.SubExpr;
import hu.bme.mit.theta.core.type.anytype.RefExpr;
import hu.bme.mit.theta.core.type.booltype.AndExpr;
import hu.bme.mit.theta.core.type.booltype.OrExpr;
import hu.bme.mit.theta.core.type.inttype.IntLitExpr;

public class StmtApplier {
	private final DispatchTable<Interval> table;
	private final Map<VarDecl<?>, Interval> varsIntervals;

	public StmtApplier(final Map<VarDecl<?>, Interval> vars) {
		varsIntervals = vars;
		table = DispatchTable.<Interval>builder()

				// BOOLEXPRS

				.addCase(AndExpr.class, this::transformAnd)

				.addCase(OrExpr.class, this::transformOr)

				// .addCase(FalseExpr.class, this::transformFalse)

				// .addCase(TrueExpr.class, this::transformTrue)

				// AbstractExprs

				// .addCase(EqualityExpressionImpl.class, this::transformEquality)

				// .addCase(InequalityExpressionImpl.class, this::transformInEquality)

				.addCase(LtExpr.class, this::transformLess)

				.addCase(LeqExpr.class, this::transformLessEqual)

				.addCase(GeqExpr.class, this::transformGreaterEqual)

				.addCase(GtExpr.class, this::transformGreater)

				.addCase(DivExpr.class, this::transformDivide)

				.addCase(MulExpr.class, this::transformMultiply)

				// .addCase(NotExpressionImpl.class, this::transformNot)

				.addCase(AddExpr.class, this::transformAdd)

				.addCase(SubExpr.class, this::transformSub)

				// EXPRS

				.addCase(RefExpr.class, this::transformRef)

				// LitExpr

				.addCase(IntLitExpr.class, this::transformIntLit)

				// .addCase(DecimalLiteralExpressionImpl.class, this::transformRatLit)

				.addDefault(this::notfound)

				.build();
	}

	public Interval transform(final Expr<?> expr) {
		return table.dispatch(expr);
	}
	/*
	 * private <T extends Type> List<Interval> transformExpressions(final List<?
	 * extends Expr<?>> expressions, final T type) { return
	 * expressions.stream().map(expression -> cast(transform(expression),
	 * type)).collect(toList()); }
	 */

	private List<Interval> transformExpressions(final List<? extends Expr<?>> expressions) {
		return expressions.stream().map(expression -> transform(expression)).collect(toList());
	}

	public Interval notfound(final Object expr) {
		return Interval.initialInterval();
	}

	private Interval transformAnd(final AndExpr expression) {
		return Interval.basicSection(transformExpressions(expression.getOps()));
	}

	private Interval transformOr(final OrExpr expression) {
		return Interval.basicUnion(transformExpressions(expression.getOps()));
	}

	public Interval transformLess(final LtExpr<?> expr) {
		final Interval left = transform(expr.getLeftOp());
		final Interval right = transform(expr.getRightOp());
		return new Interval(left.getLowerBound(), right.getUpperBound());
	}

	public Interval transformLessEqual(final LeqExpr<?> expr) {
		final Interval left = transform(expr.getLeftOp());
		final Interval right = transform(expr.getRightOp());
		return new Interval(left.getLowerBound(), right.getUpperBound());
	}

	public Interval transformGreater(final GtExpr<?> expr) {
		final Interval left = transform(expr.getLeftOp());
		final Interval right = transform(expr.getRightOp());
		return new Interval(right.getLowerBound(), left.getUpperBound());
	}

	public Interval transformGreaterEqual(final GeqExpr<?> expr) {
		final Interval left = transform(expr.getLeftOp());
		final Interval right = transform(expr.getRightOp());
		return new Interval(right.getLowerBound(), left.getUpperBound());
	}

	public Interval transformDivide(final DivExpr<?> expr) {
		final Interval leftOperand = transform(expr.getLeftOp());
		final Interval rightOperand = transform(expr.getRightOp());
		if (leftOperand.isNegative() && leftOperand.isPositive() && rightOperand.isNegative()
				&& rightOperand.isPositive()) {
			return Interval.initialInterval();
		}
		if (leftOperand.isNegative() && leftOperand.isPositive() && !rightOperand.isNegative()
				&& rightOperand.isPositive()) {
			final Bound lower = Bound.negativeInfinite();
			if (leftOperand.getLowerBound().isInfinite() != -1
					&& rightOperand.getLowerBound().isBigger(new Bound(false, 0))) {
				lower.setBound(false, leftOperand.getLowerBound().getValue() / rightOperand.getLowerBound().getValue());
			}
			final Bound upper = Bound.positiveInfinite();
			if (rightOperand.getUpperBound().isInfinite() != 1
					&& rightOperand.getLowerBound().isBigger(new Bound(false, 0))) {
				upper.setBound(false,
						rightOperand.getUpperBound().getValue() / rightOperand.getLowerBound().getValue());
			}
			return new Interval(lower, upper);
		}

		// TODO finish divide appliance

		return Interval.initialInterval();
	}

	public Interval transformMultiply(final MulExpr<?> expression) {
		final List<Interval> operands = transformExpressions(expression.getOps());
		int bound = 1;
		for (final Interval i : operands) {
			if (i.isInfinite()) {
				return Interval.initialInterval();
			}
			bound *= Math.abs(Bound.max(i.getLowerBound(), i.getUpperBound()).getValue());
		}
		return new Interval(Bound.of(-bound), Bound.of(bound));
	}

	public Interval transformAdd(final AddExpr<?> expression) {
		final List<Interval> operands = transformExpressions(expression.getOps());
		int lBound = 0;
		boolean lowerInfinite = false;
		boolean upperInfinite = false;
		final Bound lowerBound = Bound.negativeInfinite();
		final Bound upperBound = Bound.positiveInfinite();
		int uBound = 0;
		for (final Interval i : operands) {
			if (i.getLowerBound().isInfinite() == -1) {
				lowerInfinite = true;
			}
			if (i.getLowerBound().isInfinite() != 1) {
				lBound += i.getLowerBound().getValue();
			}

			if (i.getUpperBound().isInfinite() == 1) {
				upperInfinite = true;
			}
			if (i.getUpperBound().isInfinite() != -1) {
				uBound += i.getUpperBound().getValue();
			}

		}
		if (lowerInfinite == false) {
			lowerBound.setBound(false, lBound);
		}
		if (upperInfinite == false) {
			upperBound.setBound(false, uBound);
		}
		return new Interval(lowerBound, upperBound);
	}

	public Interval transformSub(final SubExpr<?> expr) {
		final Interval left = transform(expr.getLeftOp());
		final Interval right = transform(expr.getRightOp());
		Bound lowerBound = Bound.negativeInfinite();
		Bound upperBound = Bound.positiveInfinite();
		if (left.getUpperBound().isInfinite() == 0 && right.getLowerBound().isInfinite() == 0) {
			upperBound = Bound.of(left.getUpperBound().getValue() - right.getLowerBound().getValue());
		}
		if (left.getUpperBound().isInfinite() == -1 || right.getLowerBound().isInfinite() == 1) {
			upperBound = Bound.negativeInfinite();
		}
		if (left.getLowerBound().isInfinite() == 0 && right.getUpperBound().isInfinite() == 0) {
			lowerBound = Bound.of(left.getLowerBound().getValue() - right.getUpperBound().getValue());
		}
		if (left.getLowerBound().isInfinite() == 1 || right.getUpperBound().isInfinite() == -1) {
			lowerBound = Bound.positiveInfinite();
		}

		return new Interval(lowerBound, upperBound);
	}

	public Interval transformRef(final RefExpr<?> expr) {
		if (expr.getDecl() instanceof VarDecl<?>) {
			final VarDecl<?> var = (VarDecl<?>) expr.getDecl();
			if (varsIntervals.containsKey(var)) {
				return varsIntervals.get(var);
			} else
				return Interval.initialInterval();
		}
		if (expr.getDecl() instanceof ConstDecl<?>) {
			// final ConstDecl<?> var = (ConstDecl<?>) expr.getDecl();
			return Interval.initialInterval();
		}
		return Interval.initialInterval();
	}

	public Interval transformIntLit(final IntLitExpr expr) {
		final int val = expr.getValue();
		return new Interval(Bound.of(val), Bound.of(val));
	}

}
