package Utils;

public class Interval {
	private Bound lowerBound;
	private Bound upperBound;

	public Interval(final Bound lower, final Bound upper) {
		setInterval(lower, upper);
	}

	public void setInterval(final Bound lower, final Bound upper) {
		lowerBound = lower;
		upperBound = upper;
	}

	// TODO implement basic methods
	public boolean inside(final int number) {
		return false;
	}

	public static Interval initialInterval() {
		return new Interval(new Bound(true, -1), new Bound(true, 1));
	}
}
