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
import hu.bme.mit.theta.core.utils.ExprUtils;

public class StmtApplier {
	private final DispatchTable<Interval> table;
	private final Map<VarDecl<?>, Interval> varsIntervals;
	private VarDecl<?> searchedVar;

	public StmtApplier(final Map<VarDecl<?>, Interval> vars) {
		varsIntervals = vars;
		table = DispatchTable.<Interval>builder()

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
		throw new IllegalStateException("Class not found: " + expr.getClass());
	}

	private Interval transformNot(final NotExpr expression) {
		// Not a!=b === a==b
		if (expression.getOp() instanceof NeqExpr<?>) {
			return transform(IntEqExpr.create(((NeqExpr<?>) expression.getOp()).getLeftOp(),
					((NeqExpr<?>) expression.getOp()).getRightOp()));
		}
		// Not a==b === a!=b

		if (expression.getOp() instanceof EqExpr<?>) {
			return transform(IntNeqExpr.create(((EqExpr<?>) expression.getOp()).getLeftOp(),
					((EqExpr<?>) expression.getOp()).getRightOp()));
		}

		// Not true === false
		if (expression.getOp() instanceof TrueExpr) {
			return Interval.invalidInterval();
		}
		// Not false === true
		if (expression.getOp() instanceof FalseExpr) {
			return varsIntervals.get(searchedVar);
		}

		final Boolean incorrect = NoIncorrectValuePossible.instance.transform(expression.getOp());
		if (!incorrect)
			return Interval.basicSubstract(varsIntervals.get(searchedVar), transform(expression.getOp()));
		else
			return varsIntervals.get(searchedVar);
	}

	private Interval transformTrue(final TrueExpr expression) {
		return Interval.initialInterval();
	}

	private Interval transformFalse(final FalseExpr expression) {
		return Interval.invalidInterval();
	}

	private Interval transformAnd(final AndExpr expression) {
		return Interval.basicSection(transformExpressions(expression.getOps()));
	}

	private Interval transformOr(final OrExpr expression) {

		// return Interval.basicSection(transformExpressions(expression.getOps()));
		return Interval.basicSection(Interval.basicUnion(transformExpressions(expression.getOps())),
				varsIntervals.get(searchedVar));
	}

	public Interval transformNotEqual(final NeqExpr<?> expr) {

		final int simple = isSimple(expr.getLeftOp(), expr.getRightOp());
		if (simple != 0) {
			final Interval left = transform(expr.getLeftOp());
			final Interval right = transform(expr.getRightOp());
			if (simple == -1) {
				return Interval.basicSubstract(left, right);
			}
			if (simple == 1)
				return Interval.basicSubstract(right, left);
		}
		return varsIntervals.get(searchedVar);
	}

	public Interval transformEqual(final EqExpr<?> expr) {

		if (isSimpleForEqual(expr.getLeftOp(), expr.getRightOp()) != 0) {
			final Interval left = transform(expr.getLeftOp());
			final Interval right = transform(expr.getRightOp());
			return Interval.basicSection(left, right);
		}

		return varsIntervals.get(searchedVar);
	}

	public Interval transformLess(final LtExpr<?> expr) {
		final int simple = isSimple(expr.getLeftOp(), expr.getRightOp());
		if (simple != 0) {
			final Interval left = transform(expr.getLeftOp());
			final Interval right = transform(expr.getRightOp());
			if (simple == -1)
				return Interval.of(left.getLowerBound(),
						Bound.min(right.getUpperBound().increase(-1), left.getUpperBound()));
			else
				return Interval.of(Bound.max(left.getLowerBound().increase(1), right.getLowerBound()),
						right.getUpperBound());
		}

		return varsIntervals.get(searchedVar);
	}

	public Interval transformLessEqual(final LeqExpr<?> expr) {

		final Interval left = transform(expr.getLeftOp());
		final Interval right = transform(expr.getRightOp());

		if (isSimple(expr.getLeftOp(), expr.getRightOp()) == -1) {
			return Interval.of(left.getLowerBound(), Bound.min(left.getUpperBound(), right.getUpperBound()));
		}

		if (isSimple(expr.getLeftOp(), expr.getRightOp()) == 1) {
			return Interval.of(Bound.max(left.getUpperBound(), right.getLowerBound()), right.getUpperBound());
		}

		return varsIntervals.get(searchedVar);
	}

	public Interval transformGreater(final GtExpr<?> expr) {
		final int simple = isSimple(expr.getLeftOp(), expr.getRightOp());
		if (simple != 0) {
			final Interval left = transform(expr.getLeftOp());
			final Interval right = transform(expr.getRightOp());
			if (simple == -1)
				return new Interval(Bound.max(right.getLowerBound().increase(1), left.getLowerBound()),
						left.getUpperBound());
			else
				return new Interval(right.getLowerBound(),
						Bound.min(left.getUpperBound().increase(-1), right.getUpperBound()));
		}

		return varsIntervals.get(searchedVar);
	}

	/*
	 * @return -1 if left is the variable, 1 if right, 0 if none
	 */
	public int isSimple(final Expr<?> left, final Expr<?> right) {

		if (left instanceof RefExpr<?>) {
			if (((RefExpr<?>) left).getDecl().equals(searchedVar)) {

				final Boolean Incorrect = NoIncorrectValuePossible.instance.transform(right);
				if (!ExprUtils.getVars(right).contains(searchedVar) && !Incorrect) {
					return -1;
				}
			}
		}
		if (right instanceof RefExpr<?>) {
			if (((RefExpr<?>) right).getDecl().equals(searchedVar)) {
				final Boolean Incorrect = NoIncorrectValuePossible.instance.transform(left);
				if (!ExprUtils.getVars(left).contains(searchedVar) && !Incorrect) {
					return 1;
				}
			}
		}
		return 0;
	}

	public int isSimpleForEqual(final Expr<?> left, final Expr<?> right) {

		if (left instanceof RefExpr<?>) {
			if (((RefExpr<?>) left).getDecl().equals(searchedVar)) {
				if (!ExprUtils.getVars(right).contains(searchedVar)) {
					return -1;
				}
			}
		}
		if (right instanceof RefExpr<?>) {
			if (((RefExpr<?>) right).getDecl().equals(searchedVar)) {
				if (!ExprUtils.getVars(left).contains(searchedVar)) {
					return 1;
				}
			}
		}
		return 0;
	}

	public Interval transformGreaterEqual(final GeqExpr<?> expr) {

		final Interval left = transform(expr.getLeftOp());
		final Interval right = transform(expr.getRightOp());
		// left is the variable
		if (isSimple(expr.getLeftOp(), expr.getRightOp()) == -1) {
			return new Interval(Bound.max(right.getLowerBound(), left.getLowerBound()), left.getUpperBound());
		}

		// right is the variable
		if (isSimple(expr.getLeftOp(), expr.getRightOp()) == 1) {
			return new Interval(right.getLowerBound(), Bound.min(right.getUpperBound(), left.getUpperBound()));
		}
		return varsIntervals.get(searchedVar);
	}

	public Interval transformDivide(final DivExpr<?> expr) {
		final Interval leftOperand = transform(expr.getLeftOp());
		final Interval rightOperand = transform(expr.getRightOp());
		if (leftOperand.getLowerBound().isInfinite() == 0 && leftOperand.getUpperBound().isInfinite() == 0) {
			final int A = Bound.max(Bound.abs(leftOperand.getLowerBound()), Bound.abs(leftOperand.getUpperBound()))
					.getValue();
			// the smallest value is 1 (we do not consider 0)
			int B = 1;
			// right operand is (-x, -y) or (x, y) so just positive or just negative -> the
			// smallest absolute value is on one Bound
			if (!rightOperand.inside(0)) {
				B = Bound.min(Bound.abs(rightOperand.getLowerBound()), Bound.abs(rightOperand.getUpperBound()))
						.getValue();
			}
			return Interval.of(Bound.of((-1) * A / B), Bound.of(A / B));
		}
		return Interval.initialInterval();
	}

	public Interval transformMultiply(final MulExpr<?> expression) {
		final List<Interval> operands = transformExpressions(expression.getOps());
		int bound = 1;
		for (final Interval i : operands) {
			if (i.isInfinite()) {
				return Interval.initialInterval();
			}
			bound *= Bound.max(Bound.abs(i.getLowerBound()), Bound.abs(i.getUpperBound())).getValue();
		}
		return Interval.of(Bound.of(-bound), Bound.of(bound));
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
		} /*
			 * lowerBound can't be +INf upperBound can't be -Inf if
			 * (left.getUpperBound().isInfinite() == -1 ||
			 * right.getLowerBound().isInfinite() == 1) { upperBound =
			 * Bound.negativeInfinite(); }
			 */
		if (left.getLowerBound().isInfinite() == 0 && right.getUpperBound().isInfinite() == 0) {
			lowerBound = Bound.of(left.getLowerBound().getValue() - right.getUpperBound().getValue());
		} /*
			 * lowerBound can't be +INf upperBound can't be -Inf if
			 * (left.getLowerBound().isInfinite() == 1 || right.getUpperBound().isInfinite()
			 * == -1) { lowerBound = Bound.positiveInfinite(); }
			 */

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
			return Interval.initialInterval();
		}
		return Interval.initialInterval();
	}

	public Interval transformIntLit(final IntLitExpr expr) {
		final int val = expr.getValue();
		return Interval.of(Bound.of(val), Bound.of(val));
	}

	public void setSearchedVar(final VarDecl<?> var) {
		searchedVar = var;
	}

}
