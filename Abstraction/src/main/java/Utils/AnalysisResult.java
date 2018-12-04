package Utils;

public class AnalysisResult {

	boolean safe;

	public AnalysisResult(final boolean safe) {
		this.safe = safe;
	}

	public boolean isSafe() {
		return safe;
	}

	@Override
	public String toString() {
		if (safe) {
			return "Error State is unreachable";
		}
		return "Error state reachable with current abstratcion!";
	}

}
