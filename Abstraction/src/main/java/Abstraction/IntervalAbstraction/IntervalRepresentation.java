package Abstraction.IntervalAbstraction;

import java.util.Collection;
import java.util.Map;

import Utils.Interval;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.inttype.IntType;

public class IntervalRepresentation {
	private Collection<VarDecl<?>> variables;
	private Map<VarDecl<?>, Interval> intervals;

	private IntervalRepresentation(final Collection<VarDecl<?>> vars) {
		for (final VarDecl<?> var : vars) {

			if (var.getType() instanceof IntType) {
				variables.add(var);
				intervals.put(var, Interval.initialInterval());
			}

		}
	}

	public static IntervalRepresentation createInitialRepresentation(final Collection<VarDecl<?>> vars) {
		return new IntervalRepresentation(vars);
	}

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

}
