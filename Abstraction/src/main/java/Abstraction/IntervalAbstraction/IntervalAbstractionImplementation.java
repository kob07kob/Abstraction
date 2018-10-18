package Abstraction.IntervalAbstraction;

import java.util.Collection;

import hu.bme.mit.theta.core.stmt.Stmt;

public class IntervalAbstractionImplementation extends AbstractIntervalAbstraction {

	WideningTactic wt;
	PartitioningTactic pt;

	private IntervalAbstractionImplementation(final WideningTactic wideningTactic,
			final PartitioningTactic partitioningTactic) {
		wt = wideningTactic;
		pt = partitioningTactic;
	}

	@Override
	public Collection<IntervalRepresentation> wideningConvert(final Collection<IntervalRepresentation> sourceIntervals,
			final Stmt stmt, final Collection<IntervalRepresentation> targetIntervals) {
		return wt.wideningConvert(sourceIntervals, stmt, targetIntervals);
	}

	@Override
	public Collection<IntervalRepresentation> addStmtToInterval(final IntervalRepresentation sourceInterval,
			final Stmt stmt) {
		return pt.addStmtToInterval(sourceInterval, stmt);
	}

	@Override
	public Collection<IntervalRepresentation> mergePartitions(final Collection<IntervalRepresentation> intervals) {
		return pt.mergePartitions(intervals);
	}

}
