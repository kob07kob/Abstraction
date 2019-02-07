package Abstraction.IntervalAbstraction;

import Abstraction.AbstractionLabel;
import hu.bme.mit.theta.cfa.CFA.Loc;
import hu.bme.mit.theta.core.stmt.Stmt;

public interface WideningTactic<LabelType extends AbstractionLabel> {
	public LabelType wideningConvert(LabelType sourceLabel, Stmt stmt, LabelType targetLabel, Loc sourceLoc);
}
