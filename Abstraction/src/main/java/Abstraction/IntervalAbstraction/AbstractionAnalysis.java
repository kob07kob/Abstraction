package Abstraction.IntervalAbstraction;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import Abstraction.AbstractionInterface;
import Abstraction.AbstractionLabel;
import Utils.AnalysisResult;
import hu.bme.mit.theta.cfa.CFA;
import hu.bme.mit.theta.cfa.CFA.Edge;
import hu.bme.mit.theta.cfa.CFA.Loc;
import hu.bme.mit.theta.core.stmt.Stmt;

public class AbstractionAnalysis<LabelType extends AbstractionLabel> implements AbstractionInterface {
	// <T extends AbstractionLabel>

	PartitioningTactic<LabelType> pTactic;
	WideningTactic<LabelType> wTactic;
	LabelType staticLabel;
	CFA programCFA;

	public AbstractionAnalysis(final CFA program, final PartitioningTactic<LabelType> pt,
			final WideningTactic<LabelType> wt, final LabelType initialLabel) {
		pTactic = pt;
		wTactic = wt;
		staticLabel = initialLabel;
		programCFA = program;
	}

	@Override
	public AnalysisResult analyze() {
		final Map<CFA.Loc, LabelType> abstractLocations = new HashMap<>();

		@SuppressWarnings("unchecked")
		final LabelType initReps = (LabelType) staticLabel.createInitialLabel(programCFA.getVars());

		abstractLocations.put(programCFA.getInitLoc(), initReps);

		return nextIteration(new HashMap<CFA.Loc, LabelType>(), abstractLocations, programCFA.getErrorLoc());
	}

	public AnalysisResult nextIteration(final Map<CFA.Loc, LabelType> previousAbstractLocations,
			final Map<CFA.Loc, LabelType> abstractLocations, final CFA.Loc errorLoc) {
		if (abstractLocations.containsKey(errorLoc)) {
			// TODO reached error state
			return new AnalysisResult(true);
		}
		if (previousAbstractLocations.equals(abstractLocations)) {
			// TODO finished abstractions (no more locations will be reached and no
			// errorstate was found)
			return new AnalysisResult(false);
		}

		// get the outedges from those locations which changed from the previous one
		final Collection<CFA.Edge> edges = getModifyingEdges(previousAbstractLocations, abstractLocations);

		// during one iteration we put the changes into the next one so there wont be
		// reflectal changes inside one iteration...
		final Map<CFA.Loc, LabelType> nextAbstractLocations = abstractLocations;

		for (final CFA.Edge edge : edges) {
			nextAbstractLocations.put(edge.getTarget(), newIntervalRepresentation(abstractLocations, edge));
		}

		return nextIteration(abstractLocations, nextAbstractLocations, errorLoc);

	}

	private LabelType newIntervalRepresentation(final Map<Loc, LabelType> abstractLocations, final Edge edge) {

		// if the target CFA location is in the abstract set there is possible widening
		// tactic
		if (abstractLocations.containsKey(edge.getTarget())) {

			// the widening tactic depends on the concrete implementation
			return wTactic.wideningConvert(abstractLocations.get(edge.getSource()), edge.getStmt(),
					abstractLocations.get(edge.getTarget()));
		} else {
			// még nem volt felcimkézve (labelling)
			return simpleConvert(abstractLocations.get(edge.getSource()), edge.getStmt());
		}
	}

	public Collection<CFA.Edge> getModifyingEdges(final Map<CFA.Loc, ? extends AbstractionLabel> prev,
			final Map<CFA.Loc, ? extends AbstractionLabel> curr) {
		final HashSet<CFA.Edge> modifyingEdges = new HashSet<>();
		curr.forEach((loc, label) -> {
			if (prev.containsKey(loc)) {
				if (!prev.get(loc).equals(label)) {
					modifyingEdges.addAll(loc.getOutEdges());
				}
			} else {
				modifyingEdges.addAll(loc.getOutEdges());
			}
		});
		return modifyingEdges;
	}

	// if there is no widening possible
	private LabelType simpleConvert(final LabelType sourceLabel, final Stmt stmt) {
		final LabelType newReps = pTactic.addStmtToLabel(sourceLabel, stmt);

		return pTactic.mergePartitions(newReps);
	}

}
