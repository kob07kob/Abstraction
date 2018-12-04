package Abstraction.IntervalAbstraction;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import Abstraction.AbstractionLabel;
import Utils.Interval;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.inttype.IntType;

public class IntervalRepresentation extends AbstractionLabel {
	private final Collection<VarDecl<?>> variables;
	private final Map<VarDecl<?>, Interval> intervals;

	public IntervalRepresentation(final Collection<VarDecl<?>> vars) {
		variables = new HashSet<>();
		intervals = new HashMap<>();
		for (final VarDecl<?> var : vars) {

			if (var.getType() instanceof IntType) {
				variables.add(var);
				intervals.put(var, Interval.initialInterval());
			}

		}
	}

	/*
	 * @Override public AbstractionLabel createInitialLabel(final
	 * Collection<VarDecl<?>> vars) { return new IntervalRepresentation(vars); }
	 */

	public void setVarInterval(final VarDecl<?> var, final Interval interval) {
		intervals.put(var, interval);
	}

	public Map<VarDecl<?>, Interval> getMap() {
		return intervals;
	}

	public Interval getVarInterval(final VarDecl<?> var) {
		return intervals.get(var);
	}

	public Collection<VarDecl<?>> getVars() {
		return variables;
	}

	public IntervalRepresentation copy() {
		final IntervalRepresentation copy = new IntervalRepresentation(variables);
		for (final VarDecl<?> var : variables) {
			copy.setVarInterval(var, intervals.get(var));
		}
		return copy;
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof IntervalRepresentation) {
			final boolean same[] = { true };
			((IntervalRepresentation) o).getMap().forEach((var, interval) -> {
				if (!intervals.get(var).equals(interval))
					same[0] = false;
			});
			return same[0];
		}
		return false;
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		intervals.forEach((var, interval) -> {
			result.append("variable Name: " + var.getName() + " interval: " + interval.toString());
			result.append(System.getProperty("line.separator"));
		});
		return result.toString();
	}

	@Override
	public AbstractionLabel createInitialLabel(final Collection<VarDecl<?>> vars) {

		return new IntervalRepresentation(vars);
	}

	@Override
	public boolean isValid() {
		for (final Interval i : intervals.values()) {
			if (!i.isValid())
				return false;
		}
		return true;
	}

	@Override
	public AbstractionLabel addLabel(final AbstractionLabel other) {
		if (other instanceof IntervalRepresentation) {
			final IntervalRepresentation newIntervals = new IntervalRepresentation(variables);
			final IntervalRepresentation o = (IntervalRepresentation) other;
			for (final VarDecl<?> var : variables) {
				if (o.getVars().contains(var)) {
					newIntervals.setVarInterval(var, Interval.basicUnion(intervals.get(var), o.getVarInterval(var)));
				} else {
					newIntervals.setVarInterval(var, intervals.get(var));
				}
			}
			return newIntervals;
		}
		return this;
	}

}
