import org.junit.Test;

import Utils.Bound;
import Utils.Interval;

public class BasicTest {

	@Test
	public void test() {
		final Interval Ii = Interval.initialInterval();
		final Interval Ei = Interval.invalidInterval();
		final Interval minusInf1 = Interval.of(Bound.negativeInfinite(), Bound.of(1));
		final Interval plusInf1 = Interval.of(Bound.of(1), Bound.positiveInfinite());

		printSub(minusInf1, minusInf1);
		printSub(minusInf1, Ii);
		printSub(minusInf1, Ei);
		printSub(minusInf1, Interval.of(Bound.negativeInfinite(), Bound.of(0)));
		printSub(minusInf1, Interval.of(Bound.negativeInfinite(), Bound.of(2)));
		printSub(minusInf1, Interval.of(Bound.of(-1), Bound.of(1)));
		printSub(minusInf1, Interval.of(Bound.of(1), Bound.of(1)));
		printSub(Ii, Ii);
		printSub(Ei, Ei);
		printSub(Ei, Interval.of(Bound.of(1), Bound.of(1)));
		printSub(Ii, minusInf1);
		printSub(Ii, plusInf1);
		printSub(Ii, Interval.of(Bound.of(1), Bound.of(1)));
		printSub(Interval.of(Bound.of(1), Bound.of(1)), Ei);
		printSub(Interval.of(Bound.of(1), Bound.of(1)), Ii);
		printSub(Interval.of(Bound.of(0), Bound.of(0)), minusInf1);
		printSub(Interval.of(Bound.of(0), Bound.of(0)), plusInf1);
		printSub(Interval.of(Bound.of(0), Bound.of(0)), Interval.of(Bound.of(0), Bound.of(0)));
		printSub(Interval.of(Bound.of(0), Bound.of(4)), Interval.of(Bound.of(0), Bound.of(4)));

		for (int i = -2; i < 5; i++) {
			for (int j = 0; j < 3; j++) {
				printSub(Interval.of(Bound.of(i), Bound.of(i + 2)), Interval.of(Bound.of(j), Bound.of(j + 4)));
			}
		}
	}

	public void printSub(final Interval a, final Interval b) {
		System.out.println(a + " \\ " + b + "=" + Interval.basicSubstract(a, b));
	}
}
