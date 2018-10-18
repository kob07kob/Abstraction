package Abstraction.IntervalAbstraction;

import java.util.Collection;

import hu.bme.mit.theta.core.stmt.Stmt;

public interface PartitioningTactic {
	public Collection<IntervalRepresentation> addStmtToInterval(IntervalRepresentation sourceInterval, Stmt stmt);

	public Collection<IntervalRepresentation> mergePartitions(Collection<IntervalRepresentation> intervals);

}
