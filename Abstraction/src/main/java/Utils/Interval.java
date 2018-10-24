package Utils;

import java.util.Collection;
import java.util.HashSet;

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

	public boolean inside(final int number) {
		if ((lowerBound.isInfinite() == -1 || number > lowerBound.getValue())
				&& (upperBound.isInfinite() == 1 || number < upperBound.getValue())) {
			return true;
		}
		return false;
	}

	/*
	 * Section of two intervals returns only one interval: example: (2;4)U(3;17) ->
	 * (3;4)
	 */
	public static Interval basicSection(final Interval i1, final Interval i2) {
		final Interval section = new Interval(Bound.max(i1.lowerBound, i2.lowerBound),
				Bound.min(i1.upperBound, i2.upperBound));
		if (section.lowerBound.isBigger(section.upperBound))
			return null;
		else
			return section;

	}

	/*
	 * Union of more intervals returns only one interval: example:
	 * (2;4)U(3;17)U(1;7) -> (3;4)
	 */
	public static Interval basicSection(final Collection<Interval> intervals) {
		if (intervals.size() == 0)
			return null;
		Interval section = intervals.iterator().next();
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
		return new Interval(Bound.min(i1.lowerBound, i2.lowerBound), Bound.max(i1.upperBound, i2.upperBound));

	}

	/*
	 * Union of more intervals returns only one interval: example:
	 * (2;4)U(3;17)U(1;7) -> (1;17)
	 */
	public static Interval basicUnion(final Collection<Interval> intervals) {
		if (intervals.size() == 0)
			return null;
		Interval union = intervals.iterator().next();
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

	public boolean isInfinite() {
		if (lowerBound.isInfinite() != 0 || upperBound.isInfinite() != 0)
			return true;
		return false;
	}
}
