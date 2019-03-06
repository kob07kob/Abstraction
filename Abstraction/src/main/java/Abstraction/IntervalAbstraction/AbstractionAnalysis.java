package Abstraction.IntervalAbstraction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

	int iteration;
	PartitioningTactic<LabelType> pTactic;
	WideningTactic<LabelType> wTactic;
	LabelType staticLabel;
	CFA programCFA;
	boolean write = false;

	/*
	 * @program: the analyzed CFA
	 *
	 * @pt: partition tactic
	 *
	 * @wt: widening tactic (same generic type as partition tactic)
	 *
	 * @initialLabel: the initial label for the locations
	 *
	 * @analyze: true: deep step by step analysis to file
	 */
	public AbstractionAnalysis(final CFA program, final PartitioningTactic<LabelType> pt,
			final WideningTactic<LabelType> wt, final LabelType initialLabel, final boolean analize) {
		pTactic = pt;
		wTactic = wt;
		staticLabel = initialLabel;
		programCFA = program;
		write = analize;
		iteration = 0;
	}

	@Override
	public AnalysisResult analyze() {

		final Map<CFA.Loc, LabelType> abstractLocations = new HashMap<>();

		@SuppressWarnings("unchecked")
		final LabelType initReps = (LabelType) staticLabel.createInitialLabel(programCFA.getVars());

		abstractLocations.put(programCFA.getInitLoc(), initReps);

		iteration = 1;

		return nextIteration(new HashMap<CFA.Loc, LabelType>(), abstractLocations, programCFA.getErrorLoc());
	}

	@SuppressWarnings("unchecked")
	public AnalysisResult nextIteration(final Map<CFA.Loc, LabelType> previousAbstractLocations,
			final Map<CFA.Loc, LabelType> abstractLocations, final CFA.Loc errorLoc) {

		if (write) {
			final File f = new File("src/analize.txt");
			try {
				final BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
				abstractLocations.forEach((loc, label) -> {
					try {
						bw.append(loc.toString() + " : " + label.toString());
						bw.newLine();
					} catch (final IOException e) {
						e.printStackTrace();
					}
				});
				bw.newLine();
				bw.append("next iteration");
				bw.newLine();
				bw.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		if (abstractLocations.containsKey(errorLoc)) {
			// reached error state
			// System.out.println(errorLoc.toString() + " : " +
			// abstractLocations.get(errorLoc).toString());
			return new AnalysisResult(false, iteration);
		}
		if (isFixpoint(previousAbstractLocations, abstractLocations)) {
			// finished abstractions (no more locations will be reached and no
			// errorstate was found)
			return new AnalysisResult(true, iteration);
		}

		// get the outedges from those locations which changed from the previous one
		final Collection<CFA.Edge> edges = getModifyingEdges(previousAbstractLocations, abstractLocations);

		// during one iteration we put the changes into the next one so there wont be
		// reflectal changes inside one iteration...
		final Map<CFA.Loc, LabelType> nextAbstractLocations = new HashMap<>();
		nextAbstractLocations.putAll(abstractLocations);

		for (final CFA.Edge edge : edges) {
			final LabelType label = newIntervalRepresentation(abstractLocations, edge);
			if (label.isValid()) {
				// if we have multiple edges that changes the same location we put the union of
				// the edges
				if (nextAbstractLocations.containsKey(edge.getTarget())) {
					nextAbstractLocations.put(edge.getTarget(),
							(LabelType) label.addLabel(nextAbstractLocations.get(edge.getTarget())));
				} else {
					nextAbstractLocations.put(edge.getTarget(), label);
				}
			} else {
			}
		}
		iteration++;
		return nextIteration(abstractLocations, nextAbstractLocations, errorLoc);

	}

	// if we have a new location than we are not at a fixpoint or if we have a
	// different label to any of the locations
	private boolean isFixpoint(final Map<Loc, LabelType> previousAbstractLocations,
			final Map<Loc, LabelType> abstractLocations) {
		for (final Map.Entry<CFA.Loc, LabelType> entry : abstractLocations.entrySet()) {
			if (previousAbstractLocations.containsKey(entry.getKey())) {
				if (!previousAbstractLocations.get(entry.getKey()).equals(entry.getValue())) {
					return false;
				}
			} else
				return false;
		}
		return true;
	}

	private LabelType newIntervalRepresentation(final Map<Loc, LabelType> abstractLocations, final Edge edge) {

		// if the target CFA location is in the abstract set there is possible widening
		// tactic
		if (abstractLocations.containsKey(edge.getTarget())) {

			// the widening tactic depends on the concrete implementation
			return wTactic.wideningConvert(abstractLocations.get(edge.getSource()), edge.getStmt(),
					abstractLocations.get(edge.getTarget()), edge.getSource());
		} else {
			return simpleConvert(abstractLocations.get(edge.getSource()), edge.getStmt(), edge.getSource());
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
	private LabelType simpleConvert(final LabelType sourceLabel, final Stmt stmt, final Loc sourceLoc) {
		final LabelType newReps = pTactic.addStmtToLabel(sourceLabel, stmt, sourceLoc);

		return pTactic.mergePartitions(newReps);
	}

}
