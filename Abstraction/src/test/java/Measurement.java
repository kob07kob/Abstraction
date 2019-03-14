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

	}

}
