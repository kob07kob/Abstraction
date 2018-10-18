package Abstraction.IntervalAbstraction;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import Abstraction.AbstractionInterface;
import Utils.AnalysisResult;
import hu.bme.mit.theta.cfa.CFA;
import hu.bme.mit.theta.cfa.CFA.Edge;
import hu.bme.mit.theta.cfa.CFA.Loc;
import hu.bme.mit.theta.core.stmt.Stmt;

public abstract class AbstractIntervalAbstraction implements AbstractionInterface {

	@Override
	public AnalysisResult analyze(final CFA programCFA) {

		final Map<CFA.Loc, Collection<IntervalRepresentation>> abstractLocations = new HashMap<>();

		final IntervalRepresentation initRep = IntervalRepresentation.createInitialRepresentation(programCFA.getVars());
		final HashSet<IntervalRepresentation> initReps = new HashSet<>();
		initReps.add(initRep);

		abstractLocations.put(programCFA.getInitLoc(), initReps);

		return nextIteration(new HashMap<CFA.Loc, Collection<IntervalRepresentation>>(), abstractLocations,
				programCFA.getErrorLoc());
	}

	public AnalysisResult nextIteration(
			final Map<CFA.Loc, Collection<IntervalRepresentation>> previousAbstractLocations,
			final Map<CFA.Loc, Collection<IntervalRepresentation>> abstractLocations, final CFA.Loc errorLoc) {
		if (abstractLocations.containsKey(errorLoc)) {
			// TODO reached error state
			return new AnalysisResult();
		}
		if (previousAbstractLocations.equals(abstractLocations)) {
			// TODO finished abstractions (no more locations will be reached and no
			// errorstate was found)
			return new AnalysisResult();
		}

		// get the outedges from those locations which changed from the previous one
		final Collection<CFA.Edge> edges = getModifyingEdges(previousAbstractLocations, abstractLocations);

		// during one iteration we put the changes into the next one so there wont be
		// reflectal changes inside one iteration...
		final Map<CFA.Loc, Collection<IntervalRepresentation>> nextAbstractLocations = abstractLocations;

		for (final CFA.Edge edge : edges) {
			nextAbstractLocations.put(edge.getTarget(), newIntervalRepresentation(abstractLocations, edge));
		}

		return nextIteration(abstractLocations, nextAbstractLocations, errorLoc);

	}

	private Collection<IntervalRepresentation> newIntervalRepresentation(
			final Map<Loc, Collection<IntervalRepresentation>> abstractLocations, final Edge edge) {

		// if the target CFA location is in the abstract set there is possible widening
		// tactic
		if (abstractLocations.containsKey(edge.getTarget())) {
			return wideningConvert(abstractLocations.get(edge.getSource()), edge.getStmt(),
					abstractLocations.get(edge.getTarget()));
		} else {
			return simpleConvert(abstractLocations.get(edge.getSource()), edge.getStmt());
		}
	}

	// the widening tactic depends on the concrete implementation
	public abstract Collection<IntervalRepresentation> wideningConvert(
			Collection<IntervalRepresentation> sourceIntervals, Stmt stmt,
			Collection<IntervalRepresentation> targetIntervals);

	// if there is no widening possible
	private Collection<IntervalRepresentation> simpleConvert(final Collection<IntervalRepresentation> sourceIntervals,
			final Stmt stmt) {
		final Collection<IntervalRepresentation> newReps = new HashSet<>();
		for (final IntervalRepresentation interval : sourceIntervals) {
			newReps.addAll(addStmtToInterval(interval, stmt));
		}

		return mergePartitions(newReps);
	}

	// the whole idea of doing collections of interval representation is for
	// possible partitioning (for refinements)
	public abstract Collection<IntervalRepresentation> addStmtToInterval(IntervalRepresentation sourceInterval,
			Stmt stmt);

	public abstract Collection<IntervalRepresentation> mergePartitions(Collection<IntervalRepresentation> intervals);

	private Collection<Edge> getModifyingEdges(
			final Map<Loc, Collection<IntervalRepresentation>> previousAbstractLocations,
			final Map<Loc, Collection<IntervalRepresentation>> abstractLocations) {
		// TODO Auto-generated method stub
		return null;
	}

}
