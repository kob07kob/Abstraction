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

	/*
	 * returns the bigger bound
	 */
	public static Bound max(final Bound b1, final Bound b2) {
		if (b1.isInfinite() == 1 || b2.isInfinite() == 1) {
			return new Bound(true, 1);
		} else if ((b1.infinite && b1.value < 0) || (b1.value < b2.value && !b2.infinite)) {
			return b2;
		} else
			return b1;
	}

	/*
	 * returns the smaller bound
	 */
	public static Bound min(final Bound b1, final Bound b2) {
		if (b1.isInfinite() == -1 || b2.isInfinite() == -1) {
			return new Bound(true, -1);
		} else if ((b1.isInfinite() == 1) || (b1.value > b2.value && !b2.infinite)) {
			return b2;
		} else
			return b1;
	}

	public boolean isSmaller(final Bound than) {
		if (!infinite && !than.infinite && value < than.value)
			return true;
		if (isInfinite() == -1 && than.isInfinite() != -1)
			return true;
		if (!infinite && than.isInfinite() == 1)
			return true;
		return false;
	}

	public boolean isBigger(final Bound than) {
		if (!infinite && !than.infinite && value > than.value)
			return true;
		if (isInfinite() == 1 && than.isInfinite() != 1)
			return true;
		if (!infinite && than.isInfinite() == -1)
			return true;
		return false;
	}

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Bound) {
			if (isBigger((Bound) other) || isSmaller((Bound) other))
				return false;
			return true;
		}
		return false;
	}

	public static Bound of(final int a) {
		return new Bound(false, a);
	}

	public static Bound positiveInfinite() {
		return new Bound(true, 1);
	}

	public static Bound negativeInfinite() {
		return new Bound(true, -1);
	}

}
