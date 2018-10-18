package Abstraction.IntervalAbstraction;

import java.util.Collection;

import hu.bme.mit.theta.core.stmt.Stmt;

public interface WideningTactic {
	public Collection<IntervalRepresentation> wideningConvert(Collection<IntervalRepresentation> sourceIntervals,
			Stmt stmt, Collection<IntervalRepresentation> targetIntervals);
}
