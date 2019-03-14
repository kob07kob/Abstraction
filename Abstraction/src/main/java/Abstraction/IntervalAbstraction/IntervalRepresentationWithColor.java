package Abstraction.IntervalAbstraction;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import Abstraction.AbstractionLabel;
import Utils.Interval;
import hu.bme.mit.theta.cfa.CFA;
import hu.bme.mit.theta.cfa.CFA.Loc;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.inttype.IntType;

public class IntervalRepresentationWithColor extends AbstractionLabel {
	private final Collection<VarDecl<?>> variables;
	private final Map<Loc, Map<VarDecl<?>, Interval>> intervals;

	public IntervalRepresentationWithColor(final Collection<VarDecl<?>> vars) {
		variables = new HashSet<>();
		intervals = new HashMap<>();
		final Loc start = CFA.builder().createLoc("initalAbstract");
		intervals.put(start, new HashMap<>());
		for (final VarDecl<?> var : vars) {

			if (var.getType() instanceof IntType) {
				variables.add(var);
				intervals.get(start).put(var, Interval.initialInterval());
			}

		}
	}

	public IntervalRepresentationWithColor createEmpty() {
		final IntervalRepresentationWithColor result = new IntervalRepresentationWithColor(variables);
		result.getMap().clear();
		return result;
	}

	/*
	 * @Override public AbstractionLabel createInitialLabel(final
	 * Collection<VarDecl<?>> vars) { return new IntervalRepresentation(vars); }
	 */

	public IntervalRepresentationWithColor merge2OneLocation(final Loc location) {
		final IntervalRepresentationWithColor merged = createEmpty();
		final Map<VarDecl<?>, Interval> map = new HashMap<>();
		for (final VarDecl<?> var : variables) {
			map.put(var, this.getVarInterval(var));
		}
		merged.getMap().put(location, map);
		return merged;
	}

	public void setVarIntervalforLoc(final VarDecl<?> var, final Interval interval, final Loc loc) {
		if (intervals.containsKey(loc)) {
			intervals.get(loc).put(var, interval);
		} else {
			intervals.put(loc, new HashMap<>());
			for (final VarDecl<?> varr : variables) {

				if (varr.getType() instanceof IntType) {
					intervals.get(loc).put(varr, Interval.initialInterval());
				}

			}
			intervals.get(loc).put(var, interval);
		}
	}

	public Map<Loc, Map<VarDecl<?>, Interval>> getMap() {
		return intervals;
	}

	public Interval getVarInterval(final VarDecl<?> var) {
		Interval result = Interval.invalidInterval();
		for (final Map<VarDecl<?>, Interval> map : intervals.values()) {
			boolean locationIsValid = true;

			for (final VarDecl<?> variable : variables) {
				if (!map.get(variable).isValid()) {
					locationIsValid = false;
				}
			}
			if (locationIsValid) {
				result = Interval.basicUnion(result, map.get(var));
			}
		}
		return result;
	}

	public Interval getVarIntervalforLoc(final VarDecl<?> var, final Loc loc) {
		if (intervals.containsKey(loc)) {
			return intervals.get(loc).get(var);
		} else {
			return Interval.invalidInterval();
		}
	}

	public int getWideningDirection4Var(final IntervalRepresentationWithColor newIntervalRepresentation,
			final VarDecl<?> var, final Loc sourceLoc) {
		if (intervals.containsKey(sourceLoc)) {
			return intervals.get(sourceLoc).get(var).isWidenedBy(newIntervalRepresentation.getVarInterval(var));
		}
		return 2;
	}

	public Collection<VarDecl<?>> getWidenedVariables(final IntervalRepresentationWithColor newIntervalRepresentation,
			final Loc sourceLoc) {
		final Collection<VarDecl<?>> widenedVariables = new HashSet<>();

		for (final VarDecl<?> var : variables) {
			if (intervals.containsKey(sourceLoc)) {
				if (intervals.get(sourceLoc).get(var)
						.isWidenedBy(newIntervalRepresentation.getVarIntervalforLoc(var, sourceLoc)) != 2) {
					widenedVariables.add(var);
				}
			}
		}

		return widenedVariables;
	}

	public Collection<VarDecl<?>> getVars() {
		return variables;
	}

	public IntervalRepresentationWithColor copy() {
		final IntervalRepresentationWithColor copy = new IntervalRepresentationWithColor(variables);
		copy.getMap().putAll(intervals);
		return copy;
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof IntervalRepresentationWithColor) {
			final boolean same[] = { true };
			((IntervalRepresentationWithColor) o).getMap().forEach((loc, map) -> {
				map.forEach((var, interval) -> {
					if (intervals.containsKey(loc)) {
						if (!intervals.get(loc).get(var).equals(interval))
							same[0] = false;
					} else {
						same[0] = false;
					}
				});
			});
			return same[0];
		}
		return false;
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		intervals.forEach((loc, map) -> {
			result.append("color: " + loc.getName());
			result.append(System.getProperty("line.separator"));
			map.forEach((var, interval) -> {
				result.append("variable Name: " + var.getName() + " interval: " + interval.toString());
				result.append(System.getProperty("line.separator"));
			});
		});
		return result.toString();
	}

	@Override
	public AbstractionLabel createInitialLabel(final Collection<VarDecl<?>> vars) {

		return new IntervalRepresentationWithColor(vars);
	}

	@Override
	public boolean isValid() {
		for (final Map<VarDecl<?>, Interval> map : intervals.values()) {
			boolean colorisValid = true;
			for (final Interval i : map.values()) {
				if (!i.isValid())
					colorisValid = false;
			}
			if (colorisValid) {
				return true;
			}
		}
		return false;
	}

	@Override
	public AbstractionLabel addLabel(final AbstractionLabel other) {
		if (other instanceof IntervalRepresentationWithColor) {
			final IntervalRepresentationWithColor newIntervals = createEmpty();
			final IntervalRepresentationWithColor o = (IntervalRepresentationWithColor) other;
			o.getMap().forEach((loc, map) -> {
				if (intervals.containsKey(loc)) {
					final Map<VarDecl<?>, Interval> intervalMap = new HashMap<>();
					for (final VarDecl<?> var : variables) {
						if (o.getVars().contains(var)) {
							intervalMap.put(var, Interval.basicUnion(intervals.get(loc).get(var), map.get(var)));
						} else {
							intervalMap.put(var, intervals.get(loc).get(var));
						}
					}
					newIntervals.getMap().put(loc, intervalMap);
				} else {
					newIntervals.getMap().put(loc, map);
				}
			});
			intervals.forEach((loc, map) -> {
				if (!newIntervals.getMap().containsKey(loc)) {
					newIntervals.getMap().put(loc, map);
				}
			});
			return newIntervals;
		}
		return this;
	}

}
