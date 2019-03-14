import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.base.Stopwatch;

import Abstraction.IntervalAbstraction.AbstractionAnalysis;
import Abstraction.IntervalAbstraction.IntervalRepresentation;
import Abstraction.IntervalAbstraction.IntervalRepresentationWithColor;
import Abstraction.IntervalAbstraction.PartitioningTactic;
import Abstraction.IntervalAbstraction.WideningTactic;
import Abstraction.IntervalAbstraction.Impl.NoPartitioningTactic;
import Abstraction.IntervalAbstraction.Impl.NoPartitioningTacticforColor;
import Abstraction.IntervalAbstraction.Impl.NoWideningTactic;
import Abstraction.IntervalAbstraction.Impl.NoWideningTactic4Color;
import Abstraction.IntervalAbstraction.Impl.SimpleWideningTactic;
import Abstraction.IntervalAbstraction.Impl.SimpleWideningTactic4Color;
import Utils.AnalysisResult;
import hu.bme.mit.theta.cfa.CFA;
import hu.bme.mit.theta.cfa.dsl.CfaDslManager;

public class Main {
	@Parameter
	private List<String> parameters;

	@Parameter(names = { "-source", "-src" }, description = "path of the .cfa file")
	private String source = "";

	@Parameter(names = { "-partitioning",
			"-pt" }, description = "partitioning tactic: Use NoPt for no partitioning tactic (no other implemented yet)")
	private String pTactic = "";

	@Parameter(names = { "-widening",
			"-wt" }, description = "widening tactic: Use NoWt for no partitioning tactic use SimpleWt for simple widening tactic (no other implemented yet)")
	private String wTactic = "";

	@Parameter(names = { "-absType",
			"-abs" }, description = "abstraction type: Use Interval for regular interval abstraction and Color for interval abstraction with color classes.")
	private String absType = "";

	public Main() {
		parameters = new ArrayList<>();
	}

	public static void main(final String[] args) {
		final Main main = new Main();
		main.parameters = new ArrayList<>();
		main.source = "src/svcomp/locks/locks_5_true.c_0.cfa";
		main.pTactic = "NoPt";
		main.wTactic = "NoWt";
		main.absType = "Interval";

		JCommander.newBuilder().addObject(main).build().parse(args);

		final String analysisType = main.absType + " " + main.pTactic + " " + main.wTactic;

		CFA program;
		try {
			program = CfaDslManager.createCfa(new FileInputStream(main.source));
		} catch (final FileNotFoundException e) {
			System.out.println("FileNotFoundException!");
			e.printStackTrace();
			return;
		} catch (final IOException e) {
			System.out.println("IOException!");
			e.printStackTrace();
			return;
		}

		final Stopwatch stopwatch = Stopwatch.createUnstarted();

		AnalysisResult result = null;

		switch (main.absType) {
		case "Interval":
			PartitioningTactic<IntervalRepresentation> pt = null;
			WideningTactic<IntervalRepresentation> wt = null;

			switch (main.pTactic) {
			case "NoPt":
				pt = new NoPartitioningTactic();
				break;
			default:
				System.out.println("This partitioning tactic is not implemented!");
			}
			if (pt == null)
				break;

			switch (main.wTactic) {
			case "NoWt":
				wt = new NoWideningTactic();
				break;
			case "SimpleWt":
				wt = new SimpleWideningTactic();
				break;
			default:
				System.out.println("This widening tactic is not implemented!");
			}
			if (wt == null)
				break;

			final AbstractionAnalysis<IntervalRepresentation> abs = new AbstractionAnalysis<>(program, pt, wt,
					new IntervalRepresentation(program.getVars()), false);

			stopwatch.start();
			result = abs.analyze();
			stopwatch.stop();

			break;

		case "Color":
			PartitioningTactic<IntervalRepresentationWithColor> pt2 = null;
			WideningTactic<IntervalRepresentationWithColor> wt2 = null;

			switch (main.pTactic) {
			case "NoPt":
				pt2 = new NoPartitioningTacticforColor();
				break;
			default:
				System.out.println("This partitioning tactic is not implemented!");
			}
			if (pt2 == null)
				break;

			switch (main.wTactic) {
			case "NoWt":
				wt2 = new NoWideningTactic4Color();
				break;
			case "SimpleWt":
				wt2 = new SimpleWideningTactic4Color();
				break;
			default:
				System.out.println("This widening tactic is not implemented!");
			}
			if (wt2 == null)
				break;

			final AbstractionAnalysis<IntervalRepresentationWithColor> abs2 = new AbstractionAnalysis<>(program, pt2,
					wt2, new IntervalRepresentationWithColor(program.getVars()), false);

			stopwatch.start();
			result = abs2.analyze();
			stopwatch.stop();

			break;

		default:
			System.out.println("This abstraction type is not implemented yet!");
		}

		if (result == null) {
			System.out.println("Something went wrong, analysis could not be complete!");
		} else {
			System.out.println("Analysis finished succesfully!");

			final File f = new File("src/results.txt");
			try {
				final BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
				bw.append(main.source + "\t" + analysisType + "\tresult:\t" + result.toString()
						+ "\tnumber of iteration:\t" + result.getIterationCount() + "\ttime(milis):\t"
						+ stopwatch.elapsed(TimeUnit.MILLISECONDS));
				bw.newLine();
				bw.close();
			} catch (final IOException e) {
				System.out.println("IO error during writing result!");
				e.printStackTrace();
			}
		}

	}

}
