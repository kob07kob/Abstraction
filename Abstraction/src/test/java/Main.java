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
import Abstraction.IntervalAbstraction.Impl.NoPartitioningTactic;
import Abstraction.IntervalAbstraction.Impl.NoWideningTactic;
import hu.bme.mit.theta.cfa.CFA;
import hu.bme.mit.theta.cfa.dsl.CfaDslManager;

public class Main {
	@Parameter
	private List<String> parameters;

	@Parameter(names = { "-source", "-src" }, description = "path of th .cfa file")
	private String source = "";

	public Main() {
		parameters = new ArrayList<>();
	}

	public static void main(final String[] args) {
		// TODO Auto-generated method stub
		final Main main = new Main();
		main.parameters = new ArrayList<>();
		main.source = "src/svcomp/locks/locks_5_true.c_0.cfa";
		JCommander.newBuilder().addObject(main).build().parse(args);

		CFA program;
		try {
			program = CfaDslManager.createCfa(new FileInputStream(main.source));
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		final AbstractionAnalysis<IntervalRepresentation> abs = new AbstractionAnalysis<>(program,
				new NoPartitioningTactic(), new NoWideningTactic(), new IntervalRepresentation(program.getVars()),
				false);

		final Stopwatch stopwatch = Stopwatch.createStarted();

		final String result = abs.analyze().toString();

		stopwatch.stop();
		System.out.println("Works!" + result);
		final File f = new File("src/resultst.txt");
		try {
			final BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			bw.append(main.source + ":\tresult: " + result + "\ttime(milis): "
					+ stopwatch.elapsed(TimeUnit.MILLISECONDS));
			bw.newLine();
			bw.close();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
