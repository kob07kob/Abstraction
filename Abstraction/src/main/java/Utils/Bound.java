package Utils;

public class Bound {
	private boolean infinite;
	private int value;

	public Bound(final boolean isInfinite, final int itsValue) {
		setBound(isInfinite, itsValue);
	}

	public void setBound(final boolean isInfinite, final int itsValue) {
		if (!isInfinite) {
			infinite = false;
			value = itsValue;
		} else {
			if (itsValue == 0)
				throw new IllegalArgumentException("infinite must be positive or negative");
			else {
				infinite = true;
				value = itsValue;
			}
		}
	}

	/*
	 * returns the value (Max int value if infinite)
	 */
	public int getValue() {
		if (!infinite)
			return value;
		else if (value > 0)
			return Integer.MAX_VALUE;
		else
			return Integer.MIN_VALUE;
	}

	/*
	 * returns 0 if not infinite returns 1 if its positive infinte .1 if negative
	 * infinite
	 */
	public int isInfinite() {
		if (!infinite)
			return 0;
		else if (value > 0)
			return 1;
		else
			return -1;
	}
}
