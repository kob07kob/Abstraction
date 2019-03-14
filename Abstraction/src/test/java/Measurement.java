import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Measurement {

	@Parameter
	private List<String> parameters;

	@Parameter(names = { "-source", "-src" }, description = "path of the .cfa file")
	private String source = "";

	public Measurement() {
		parameters = new ArrayList<>();
	}

	public static void main(final String[] args) {
		final Measurement main = new Measurement();
		main.parameters = new ArrayList<>();
		main.source = "src/svcomp/locks/locks_5_true.c_0.cfa";
		JCommander.newBuilder().addObject(main).build().parse(args);

		final String[] task1 = { "-src", main.source, "-wt", "NoWt", "-absType", "Interval" };
		final String[] task2 = { "-src", main.source, "-wt", "SimpleWt", "-absType", "Interval" };
		final String[] task3 = { "-src", main.source, "-wt", "NoWt", "-absType", "Color" };
		final String[] task4 = { "-src", main.source, "-wt", "SimpleWt", "-absType", "Color" };
		final String[][] tasks = { task1, task2, task3, task4 };

		for (final String[] task : tasks) {
			try {
				Main.main(task);
			} catch (final Exception e) {
				System.err.println(e);
			}
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
