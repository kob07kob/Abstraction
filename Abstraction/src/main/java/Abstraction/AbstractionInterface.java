package Abstraction;

import Utils.AnalysisResult;
import hu.bme.mit.theta.cfa.CFA;

public interface AbstractionInterface {

	public AnalysisResult analyze(CFA programCFA);

}
