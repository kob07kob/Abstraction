import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import Abstraction.IntervalAbstraction.AbstractionAnalysis;
import Abstraction.IntervalAbstraction.IntervalRepresentation;
import Abstraction.IntervalAbstraction.Impl.NoPartitioningTactic;
import Abstraction.IntervalAbstraction.Impl.NoWideningTactic;
import hu.bme.mit.theta.cfa.CFA;
import hu.bme.mit.theta.cfa.dsl.CfaDslManager;

public class AbstractAnalysisTester {

	@Test
	public void test() throws FileNotFoundException, IOException {
		final CFA program = CfaDslManager.createCfa(new FileInputStream("src/svcomp/locks/locks_5_true.c_0.cfa"));

		final AbstractionAnalysis<IntervalRepresentation> abs = new AbstractionAnalysis<>(program,
				new NoPartitioningTactic(), new NoWideningTactic(), new IntervalRepresentation(program.getVars()));

		System.out.println(abs.analyze());

	}

}
