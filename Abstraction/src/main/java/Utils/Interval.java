package Utils;

import java.util.Collection;
import java.util.HashSet;

public class Interval {
	private Bound lowerBound;
	private Bound upperBound;

	public Interval(final Bound lower, final Bound upper) {
		setInterval(lower, upper);
	}

	public static Interval of(final Bound lower, final Bound upper) {
		return new Interval(lower, upper);
	}

	public void setInterval(final Bound lower, final Bound upper) {
		lowerBound = lower;
		upperBound = upper;
	}

	public boolean inside(final int number) {
		if ((lowerBound.isInfinite() == -1 || number >= lowerBound.getValue())
				&& (upperBound.isInfinite() == 1 || number <= upperBound.getValue())) {
			return true;
		}
		return false;
	}

	public static Interval invalidInterval() {
		return new Interval(Bound.of(1), Bound.of(-1));
	}

	/*
	 * Section of two intervals returns only one interval: example: (2;4)U(3;17) ->
	 * (3;4)
	 */
	public static Interval basicSection(final Interval i1, final Interval i2) {
		if (!i1.isValid() || !i2.isValid())
			return Interval.invalidInterval();
		return new Interval(Bound.max(i1.lowerBound, i2.lowerBound), Bound.min(i1.upperBound, i2.upperBound));
	}

	/*
	 * Union of more intervals returns only one interval: example:
	 * (2;4)U(3;17)U(1;7) -> (3;4)
	 */
	public static Interval basicSection(final Collection<Interval> intervals) {
		if (intervals.size() == 0)
			return Interval.invalidInterval();
		Interval section = Interval.initialInterval();
		for (final Interval i : intervals) {
			section = basicSection(section, i);

		}
		return section;

	}

	/*
	 * Union of two intervals returns only one interval: example: (2;4)U(3;17) ->
	 * (2;17)
	 */
	public static Interval basicUnion(final Interval i1, final Interval i2) {
		if (!i1.isValid() && !i2.isValid())
			return Interval.invalidInterval();
		if (!i1.isValid())
			return Interval.of(i2.getLowerBound(), i2.getUpperBound());
		if (!i2.isValid())
			return Interval.of(i1.getLowerBound(), i1.getUpperBound());
		return Interval.of(Bound.min(i1.lowerBound, i2.lowerBound), Bound.max(i1.upperBound, i2.upperBound));

	}

	/*
	 * Union of more intervals returns only one interval: example:
	 * (2;4)U(3;17)U(1;7) -> (1;17)
	 */
	public static Interval basicUnion(final Collection<Interval> intervals) {
		if (intervals.size() == 0)
			return Interval.invalidInterval();
		Interval union = Interval.invalidInterval();
		for (final Interval i : intervals) {
			union = basicUnion(union, i);

		}
		return union;

	}

	public static Collection<Interval> Complementer(final Interval i) {
		final Collection<Interval> intervals = new HashSet<>();
		if (i.lowerBound.isInfinite() != -1) {
			intervals.add(new Interval(new Bound(true, -1), i.lowerBound));
		}
		if (i.upperBound.isInfinite() != 1) {
			intervals.add(new Interval(i.upperBound, new Bound(true, 1)));
		}
		return intervals;
	}

	public Bound getLowerBound() {
		return lowerBound;
	}

	public Bound getUpperBound() {
		return upperBound;
	}

	public static Interval initialInterval() {
		return new Interval(new Bound(true, -1), new Bound(true, 1));
	}

	public boolean isNegative() {
		return lowerBound.isSmaller(new Bound(false, 0));
	}

	public boolean isPositive() {
		return upperBound.isBigger(new Bound(false, 0));
	}

	public Interval[] cut(final int here) {
		final Interval[] intervals = new Interval[2];
		final Bound middle = new Bound(false, here);
		if (lowerBound.isSmaller(middle)) {
			intervals[0] = new Interval(lowerBound, middle);
		} else
			intervals[0] = new Interval(middle, middle);
		if (upperBound.isBigger(middle)) {
			intervals[1] = new Interval(middle, upperBound);
		} else
			intervals[1] = new Interval(middle, middle);
		return intervals;
	}

	public boolean isValid() {
		if (lowerBound.isInfinite() == 1 || upperBound.isInfinite() == -1)
			return false;
		return !lowerBound.isBigger(upperBound);
	}

	@Override
	public String toString() {
		return "(" + lowerBound.toString() + ";" + upperBound.toString() + ")";
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof Interval) {
			return lowerBound.equals(((Interval) o).getLowerBound())
					&& upperBound.equals(((Interval) o).getUpperBound());
		}
		return false;
	}

	public boolean isInfinite() {
		if (lowerBound.isInfinite() != 0 || upperBound.isInfinite() != 0)
			return true;
		return false;
	}

	public static Interval basicComplementer(final Interval interval) {
		return Interval.basicSubstract(Interval.initialInterval(), interval);
	}

	public static Interval basicSubstract(final Interval from, final Interval that) {
		if (from.isValid() && that.isValid()) {

			// thatL ... fromL ... fromU ... thatU
			if (Bound.min(from.lowerBound, that.lowerBound).equals(that.lowerBound)
					&& Bound.max(from.upperBound, that.upperBound).equals(that.upperBound)) {
				return Interval.invalidInterval();
			}

			// we eliminate the bounds as well
			final Bound thatLowerBound = that.lowerBound.increase(-1);
			final Bound thatUpperBound = that.upperBound.increase(1);

			// if fromL ... -INF<thatL ... thatU<INF ... fromU
			if (Bound.min(from.lowerBound, thatLowerBound).equals(from.lowerBound)
					&& Bound.max(from.upperBound, thatUpperBound).equals(from.upperBound)
					&& thatUpperBound.isInfinite() == 0 && thatLowerBound.isInfinite() == 0) {
				// if (from.lowerBound.isSmaller(thatLowerBound) &&
				// from.getUpperBound().isBigger(thatUpperBound)) {
				return new Interval(from.lowerBound, from.upperBound);
			}
			// if fromL ... -INF<thatL ... thatU==INF== fromU
			else if (Bound.min(from.lowerBound, thatLowerBound).equals(from.lowerBound)
					&& Bound.max(from.upperBound, thatUpperBound).equals(from.upperBound)
					&& thatUpperBound.isInfinite() == 1 && thatLowerBound.isInfinite() == 0) {
				return new Interval(from.lowerBound, thatLowerBound);
			}
			// if fromL==-INF==thatL ... thatU<INF... fromU
			else if (Bound.min(from.lowerBound, thatLowerBound).equals(from.lowerBound)
					&& Bound.max(from.upperBound, thatUpperBound).equals(from.upperBound)
					&& thatUpperBound.isInfinite() == 0 && thatLowerBound.isInfinite() == -1) {
				return new Interval(thatUpperBound, from.upperBound);
			}
			// if fromL... fromU ... thatL ... thatU OR if thatL ... thatU ... fromL ...
			// fromU
			else if (Bound.min(from.lowerBound, thatUpperBound).equals(thatUpperBound)
					|| Bound.max(from.upperBound, thatLowerBound).equals(thatLowerBound)) {
				return new Interval(from.lowerBound, from.upperBound);
			}
			// if fromL... thatL ... fromU ... thatU
			else if (Bound.min(from.lowerBound, thatLowerBound).equals(from.lowerBound)
					&& Bound.max(from.upperBound, thatUpperBound).equals(thatUpperBound)) {
				return new Interval(from.lowerBound, thatLowerBound);
			}
			// if thatL ...fromL ... thatU ... fromU
			else if (Bound.min(from.lowerBound, thatLowerBound).equals(thatLowerBound)
					&& Bound.max(from.upperBound, thatUpperBound).equals(from.upperBound)) {
				return new Interval(thatUpperBound, from.upperBound);
			}
		}
		if (!that.isValid())
			return new Interval(from.lowerBound, from.upperBound);

		return Interval.invalidInterval();
	}
}
