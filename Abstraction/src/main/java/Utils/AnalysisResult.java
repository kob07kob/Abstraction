package Utils;

public class AnalysisResult {

	boolean safe;
	int iteration;

	public AnalysisResult(final boolean safe, final int iterationNumber) {
		this.safe = safe;
		this.iteration = iterationNumber;
	}

	public boolean isSafe() {
		return safe;
	}

	public int getIterationCount() {
		return iteration;
	}

	@Override
	public String toString() {
		if (safe) {
			return "Error State is unreachable";
		}
		return "Error state reachable with current abstratcion!";
	}

}
