import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import Abstraction.IntervalAbstraction.AbstractionAnalysis;
import Abstraction.IntervalAbstraction.IntervalRepresentation;
import Abstraction.IntervalAbstraction.IntervalRepresentationWithColor;
import Abstraction.IntervalAbstraction.Impl.NoPartitioningTactic;
import Abstraction.IntervalAbstraction.Impl.NoPartitioningTacticforColor;
import Abstraction.IntervalAbstraction.Impl.NoWideningTactic;
import Abstraction.IntervalAbstraction.Impl.NoWideningTactic4Color;
import Abstraction.IntervalAbstraction.Impl.SimpleWideningTactic;
import hu.bme.mit.theta.cfa.CFA;
import hu.bme.mit.theta.cfa.dsl.CfaDslManager;

public class AbstractAnalysisTester {

	@Ignore
	@Test
	public void test() throws FileNotFoundException, IOException {
		// eca/Problem01_label32_false-unreach-call.c_0
		final CFA program = CfaDslManager.createCfa(new FileInputStream("src/svcomp/myown/long_cycle_true.cfa"));

		final AbstractionAnalysis<IntervalRepresentation> abs = new AbstractionAnalysis<>(program,
				new NoPartitioningTactic(), new NoWideningTactic(), new IntervalRepresentation(program.getVars()),
				true);

		System.out.println(abs.analyze());
	}

	@Ignore
	@Test
	public void test2() throws FileNotFoundException, IOException {
		// eca/Problem01_label32_false-unreach-call.c_0
		final CFA program = CfaDslManager.createCfa(new FileInputStream("src/svcomp/myown/long_cycle_true.cfa"));

		final AbstractionAnalysis<IntervalRepresentation> abs = new AbstractionAnalysis<>(program,
				new NoPartitioningTactic(), new SimpleWideningTactic(), new IntervalRepresentation(program.getVars()),
				true);

		System.out.println(abs.analyze());
	}

	@Ignore
	@Test
	public void test4Color() throws FileNotFoundException, IOException {
		// eca/Problem01_label32_false-unreach-call.c_0
		final CFA program = CfaDslManager
				.createCfa(new FileInputStream("src/svcomp/eca/Problem01_label38_false-unreach-call.c_0.cfa"));

		final AbstractionAnalysis<IntervalRepresentationWithColor> abs = new AbstractionAnalysis<>(program,
				new NoPartitioningTacticforColor(), new NoWideningTactic4Color(),
				new IntervalRepresentationWithColor(program.getVars()), true);

		System.out.println(abs.analyze());
	}

	@Test
	public void locks() throws FileNotFoundException, IOException {
		int sum = 0;
		int shouldBeSafe = 0;
		int resultIsSafe = 0;
		int falsePositive = 0;
		int good = 0;
		final File dir = new File("src/svcomp/locks/");
		for (final File source : dir.listFiles()) {
			System.out.println("analizing " + source.getName());
			sum++;
			final CFA program = CfaDslManager.createCfa(new FileInputStream(source));
			final AbstractionAnalysis<IntervalRepresentation> abs = new AbstractionAnalysis<>(program,
					new NoPartitioningTactic(), new SimpleWideningTactic(),
					new IntervalRepresentation(program.getVars()), false);
			if (source.getName().contains("true"))
				shouldBeSafe++;
			if (abs.analyze().isSafe()) {
				resultIsSafe++;
				if (source.getName().contains("false")) {
					System.out.println("error in " + source.getName());
					falsePositive++;
				} else {
					good++;
				}
			} else {
				if (source.getName().contains("false")) {
					good++;
				} else {
					System.out.println("failure in " + source.getName());
				}
			}
		}
		System.out.println("Performance: ");
		System.out.println("Rate: " + (double) good * 100 / sum);
		System.out.println("FP: " + falsePositive);
		System.out.println("FP Rate: " + (double) falsePositive * 100 / (sum - shouldBeSafe));
		System.out.println("TP Rate: " + (double) (resultIsSafe - falsePositive) * 100 / shouldBeSafe);
	}

	@Ignore
	@Test
	public void locks4Color() throws FileNotFoundException, IOException {
		int sum = 0;
		int shouldBeSafe = 0;
		int resultIsSafe = 0;
		int falsePositive = 0;
		int good = 0;
		final File dir = new File("src/svcomp/ssh/");
		for (final File source : dir.listFiles()) {
			System.out.println("analizing " + source.getName());
			sum++;
			final CFA program = CfaDslManager.createCfa(new FileInputStream(source));
			final AbstractionAnalysis<IntervalRepresentationWithColor> abs = new AbstractionAnalysis<>(program,
					new NoPartitioningTacticforColor(), new NoWideningTactic4Color(),
					new IntervalRepresentationWithColor(program.getVars()), false);
			if (source.getName().contains("true"))
				shouldBeSafe++;
			if (abs.analyze().isSafe()) {
				resultIsSafe++;
				if (source.getName().contains("false")) {
					System.out.println("error in " + source.getName());
					falsePositive++;
				} else {
					good++;
				}
			} else {
				if (source.getName().contains("false")) {
					good++;
				} else {
					System.out.println("failure in " + source.getName());
				}
			}
		}
		System.out.println("Performance: ");
		System.out.println("Rate: " + (double) good * 100 / sum);
		System.out.println("FP: " + falsePositive);
		System.out.println("FP Rate: " + (double) falsePositive * 100 / (sum - shouldBeSafe));
		System.out.println("TP Rate: " + (double) (resultIsSafe - falsePositive) * 100 / shouldBeSafe);
	}
}
