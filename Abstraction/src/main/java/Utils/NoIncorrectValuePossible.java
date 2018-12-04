package Utils;

import static java.util.stream.Collectors.toList;

import java.util.List;

import hu.bme.mit.theta.common.DispatchTable;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.abstracttype.AddExpr;
import hu.bme.mit.theta.core.type.abstracttype.DivExpr;
import hu.bme.mit.theta.core.type.abstracttype.EqExpr;
import hu.bme.mit.theta.core.type.abstracttype.GeqExpr;
import hu.bme.mit.theta.core.type.abstracttype.GtExpr;
import hu.bme.mit.theta.core.type.abstracttype.LeqExpr;
import hu.bme.mit.theta.core.type.abstracttype.LtExpr;
import hu.bme.mit.theta.core.type.abstracttype.MulExpr;
import hu.bme.mit.theta.core.type.abstracttype.NeqExpr;
import hu.bme.mit.theta.core.type.abstracttype.SubExpr;
import hu.bme.mit.theta.core.type.anytype.RefExpr;
import hu.bme.mit.theta.core.type.booltype.AndExpr;
import hu.bme.mit.theta.core.type.booltype.FalseExpr;
import hu.bme.mit.theta.core.type.booltype.NotExpr;
import hu.bme.mit.theta.core.type.booltype.OrExpr;
import hu.bme.mit.theta.core.type.booltype.TrueExpr;
import hu.bme.mit.theta.core.type.inttype.IntAddExpr;
import hu.bme.mit.theta.core.type.inttype.IntDivExpr;
import hu.bme.mit.theta.core.type.inttype.IntEqExpr;
import hu.bme.mit.theta.core.type.inttype.IntGeqExpr;
import hu.bme.mit.theta.core.type.inttype.IntGtExpr;
import hu.bme.mit.theta.core.type.inttype.IntLeqExpr;
import hu.bme.mit.theta.core.type.inttype.IntLitExpr;
import hu.bme.mit.theta.core.type.inttype.IntLtExpr;
import hu.bme.mit.theta.core.type.inttype.IntMulExpr;
import hu.bme.mit.theta.core.type.inttype.IntNeqExpr;
import hu.bme.mit.theta.core.type.inttype.IntSubExpr;

public class NoIncorrectValuePossible {

	private final DispatchTable<Boolean> table;

	public static NoIncorrectValuePossible instance = new NoIncorrectValuePossible();

	private NoIncorrectValuePossible() {

		table = DispatchTable.<Boolean>builder()

				// BOOLEXPRS

				.addCase(AndExpr.class, this::transformAnd)

				.addCase(OrExpr.class, this::transformOr)

				.addCase(TrueExpr.class, this::transformTrue)

				.addCase(FalseExpr.class, this::transformFalse)

				// .addCase(FalseExpr.class, this::transformFalse)

				// .addCase(TrueExpr.class, this::transformTrue)

				// AbstractExprs

				.addCase(EqExpr.class, this::transformEqual)

				.addCase(IntEqExpr.class, this::transformEqual)

				.addCase(NotExpr.class, this::transformNot)

				.addCase(IntNeqExpr.class, this::transformNotEqual)

				.addCase(NeqExpr.class, this::transformNotEqual)

				// .addCase(InequalityExpressionImpl.class, this::transformInEquality)

				.addCase(IntLtExpr.class, this::transformLess)

				.addCase(IntLeqExpr.class, this::transformLessEqual)

				.addCase(IntGeqExpr.class, this::transformGreaterEqual)

				.addCase(IntGtExpr.class, this::transformGreater)

				.addCase(LtExpr.class, this::transformLess)

				.addCase(LeqExpr.class, this::transformLessEqual)

				.addCase(GeqExpr.class, this::transformGreaterEqual)

				.addCase(GtExpr.class, this::transformGreater)

				.addCase(DivExpr.class, this::transformDivide).addCase(IntDivExpr.class, this::transformDivide)

				.addCase(MulExpr.class, this::transformMultiply).addCase(IntMulExpr.class, this::transformMultiply)

				// .addCase(NotExpressionImpl.class, this::transformNot)

				.addCase(AddExpr.class, this::transformAdd).addCase(IntAddExpr.class, this::transformAdd)

				.addCase(SubExpr.class, this::transformSub).addCase(IntSubExpr.class, this::transformSub)

				// EXPRS

				.addCase(RefExpr.class, this::transformRef)

				// LitExpr

				.addCase(IntLitExpr.class, this::transformIntLit)

				// .addCase(DecimalLiteralExpressionImpl.class, this::transformRatLit)

				.addDefault(this::notfound)

				.build();
	}

	public Boolean transform(final Expr<?> expr) {
		// return false;
		return table.dispatch(expr);
	}
	/*
	 * private <T extends Type> List<Interval> transformExpressions(final List<?
	 * extends Expr<?>> expressions, final T type) { return
	 * expressions.stream().map(expression -> cast(transform(expression),
	 * type)).collect(toList()); }
	 */

	private List<Boolean> transformExpressions(final List<? extends Expr<?>> expressions) {
		return expressions.stream().map(expression -> transform(expression)).collect(toList());
	}

	public Boolean notfound(final Object expr) {
		throw new IllegalStateException("Class not found: " + expr.getClass());
	}

	private Boolean transformNot(final NotExpr expression) {
		return true;
	}

	private Boolean transformTrue(final TrueExpr expression) {
		return false;
	}

	private Boolean transformFalse(final FalseExpr expression) {
		return false;
	}

	private Boolean transformAnd(final AndExpr expression) {

		for (final Boolean b : transformExpressions(expression.getOps())) {
			if (b) {
				return true;
			}
		}
		return false;
	}

	private Boolean transformOr(final OrExpr expression) {

		for (final Boolean b : transformExpressions(expression.getOps())) {
			if (b) {
				return true;
			}
		}
		return false;
	}

	public Boolean transformNotEqual(final NeqExpr<?> expr) {

		return true;
	}

	public Boolean transformEqual(final EqExpr<?> expr) {
		final Boolean left = transform(expr.getLeftOp());
		final Boolean right = transform(expr.getRightOp());

		return left || right;
	}

	public Boolean transformLess(final LtExpr<?> expr) {
		final Boolean left = transform(expr.getLeftOp());
		final Boolean right = transform(expr.getRightOp());

		return left || right;
	}

	public Boolean transformLessEqual(final LeqExpr<?> expr) {
		final Boolean left = transform(expr.getLeftOp());
		final Boolean right = transform(expr.getRightOp());

		return left || right;
	}

	public Boolean transformGreater(final GtExpr<?> expr) {
		final Boolean left = transform(expr.getLeftOp());
		final Boolean right = transform(expr.getRightOp());

		return left || right;
	}

	public Boolean transformGreaterEqual(final GeqExpr<?> expr) {
		final Boolean left = transform(expr.getLeftOp());
		final Boolean right = transform(expr.getRightOp());

		return left || right;
	}

	public Boolean transformDivide(final DivExpr<?> expr) {
		return true;
	}

	public Boolean transformMultiply(final MulExpr<?> expression) {
		return true;
	}

	public Boolean transformAdd(final AddExpr<?> expression) {

		for (final Boolean b : transformExpressions(expression.getOps())) {
			if (b) {
				return true;
			}
		}
		return false;
	}

	public Boolean transformSub(final SubExpr<?> expr) {
		final Boolean left = transform(expr.getLeftOp());
		final Boolean right = transform(expr.getRightOp());
		return left || right;
	}

	public Boolean transformRef(final RefExpr<?> expr) {
		return true;
	}

	public Boolean transformIntLit(final IntLitExpr expr) {
		return false;
	}
}
