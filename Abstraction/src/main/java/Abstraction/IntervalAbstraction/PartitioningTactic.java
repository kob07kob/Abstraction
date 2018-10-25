package Abstraction.IntervalAbstraction;

import Abstraction.AbstractionLabel;
import hu.bme.mit.theta.core.stmt.Stmt;

public interface PartitioningTactic<LabelType extends AbstractionLabel> {
	public LabelType addStmtToLabel(LabelType sourceLabel, Stmt stmt);

	public LabelType mergePartitions(LabelType label);

}
