package edu.nyu.cs.pos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import edu.nyu.cs.pub.Corpus;
import edu.nyu.cs.pub.Matrix;
import edu.nyu.cs.pub.Sentence;
import edu.nyu.cs.pub.Token;

/**
 * Likelihood matrix
 * 
 * @author Daniel Wu
 * 
 */
public class EmissionMatrix extends Matrix {

	// probability that a NNS followed by VBZ
	private double NNS_AND_VBZ_PROB = 1.0;

	// probability that a NNS followed by VB
	private double NN_AND_VB_PROB = 1.0;

	// highest probability of a NP
	private double NP_PROB = 0.0;

	// highest probability of a CD
	private double CD_PROB = 0.0;

	// highest probability of a NNPS
	private double NNPS_PROB = 0.0;

	// highest probability of a VBN
	private double VBN_PROB = 1.0;

	// highest probability of a RB
	private double RB_PROB = 1.0;

	// highest probability of a JJ
	private double JJ_PROB = 1.0;

	// highest probability of a NN
	private double NN_PROB = 1.0;

	// highest probability of a VBG
	private double VBG_PROB = 1.0;

	public EmissionMatrix(Corpus corpus) {
		super(corpus);
		// initialize transition matrix
		initialize();
		// compute probability
		normalize();
	}

	private void initialize() {
		List<Sentence> sentences = getCorpus().getSentences();
		for (Sentence sentence : sentences) {
			for (Token token : sentence.getTokens()) {
				String word = token.getLiteral();
				String tag = token.getPosTag();
				if (bigramMatrix.get(tag) == null) {
					Map<String, Double> priorMap = new HashMap<String, Double>();
					bigramMatrix.put(tag, priorMap);
				}
				if (bigramMatrix.get(tag).get(word) == null) {
					Double value = 0.0;
					bigramMatrix.get(tag).put(word, value);
				}
				Double value = bigramMatrix.get(tag).get(word);
				bigramMatrix.get(tag).put(word, ++value);
			}
		}
	}

	/*
	 * Given an OOV word w, if it ends in "s", use the lowest probabilities for
	 * NNS and VBZ, otherwise, use the lowest probabilities for NN and JJ.
	 */
	@Override
	public double getItem(String state, String word) {
		// if out of vocabulary
		boolean oov = !(getCorpus().containsWord(word));
		if (oov) {
			if (startsWithCapital(word) && word.toLowerCase().endsWith("s")
					&& state.equals("NNPS")) {
				// NP
				return getHighestProbForNNPS();
			}
			if ((startsWithCapital(word)) && state.equals("NNP")) {
				// NP
				return getHighestProbForNNP();
			}
			if (startsWithNum(word) && state.equals("CD")) {
				// CD
				return getHighestProbForCD();
			}
			if (word.toLowerCase().endsWith("s")
					&& (state.equals("NNS") || state.equals("VBZ"))) {
				// NNS and VBZ
				return getLowestProbForNNSAndVBZ();
			}
			if (word.toLowerCase().endsWith("ed") && state.equals("VBN")) {
				// VBN
				return getLowestProbForVBN();
			}
			if (word.toLowerCase().endsWith("ly") && state.equals("RB")) {
				// RB
				return getLowestProbForRB();
			}
			if ((word.toLowerCase().endsWith("able")
					|| word.toLowerCase().endsWith("al") || word.contains("-"))
					&& state.equals("JJ")) {
				// JJ
				return getLowestProbForJJ();
			}
			if (word.toLowerCase().endsWith("ion") && state.equals("NN")) {
				// NN
				return getLowestProbForNN();
			}
			if (word.toLowerCase().endsWith("ing") && state.equals("VBG")) {
				// NN
				return getLowestProbForVBG();
			} else {
				// use the lowest probabilities for NN and JJ.
				return getLowestProbForNNAndVB();
			}
		} else {
			// search with case sensitive
			double caseSenstive = super.getItem(state, word);
			return caseSenstive;
		}
	}

	private double getHighestProbForNNPS() {
		if (NNPS_PROB == 0.0) {
			Map<String, Double> NNSMap = super.getRow("NNPS");
			Iterator<Entry<String, Double>> nnsIt = NNSMap.entrySet()
					.iterator();
			while (nnsIt.hasNext()) {
				double prob = nnsIt.next().getValue();
				if (prob > NNPS_PROB) {
					NNPS_PROB = prob;
				}
			}
		}
		return NNPS_PROB;
	}

	private double getLowestProbForVBG() {
		if (VBG_PROB == 1.0) {
			Map<String, Double> NNSMap = super.getRow("VBG");
			Iterator<Entry<String, Double>> nnsIt = NNSMap.entrySet()
					.iterator();
			while (nnsIt.hasNext()) {
				double prob = nnsIt.next().getValue();
				if (prob < VBG_PROB) {
					VBG_PROB = prob;
				}
			}
		}
		return VBG_PROB;
	}

	private double getLowestProbForNN() {
		if (NN_PROB == 1.0) {
			Map<String, Double> NNSMap = super.getRow("NN");
			Iterator<Entry<String, Double>> nnsIt = NNSMap.entrySet()
					.iterator();
			while (nnsIt.hasNext()) {
				double prob = nnsIt.next().getValue();
				if (prob < NN_PROB) {
					NN_PROB = prob;
				}
			}
		}
		return NN_PROB;
	}

	private double getLowestProbForJJ() {
		if (JJ_PROB == 1.0) {
			Map<String, Double> NNSMap = super.getRow("JJ");
			Iterator<Entry<String, Double>> nnsIt = NNSMap.entrySet()
					.iterator();
			while (nnsIt.hasNext()) {
				double prob = nnsIt.next().getValue();
				if (prob < JJ_PROB) {
					JJ_PROB = prob;
				}
			}
		}
		return JJ_PROB;
	}

	private double getLowestProbForRB() {
		if (RB_PROB == 1.0) {
			Map<String, Double> NNSMap = super.getRow("RB");
			Iterator<Entry<String, Double>> nnsIt = NNSMap.entrySet()
					.iterator();
			while (nnsIt.hasNext()) {
				double prob = nnsIt.next().getValue();
				if (prob < RB_PROB) {
					RB_PROB = prob;
				}
			}
		}
		return RB_PROB;
	}

	private double getLowestProbForVBN() {
		if (VBN_PROB == 1.0) {
			Map<String, Double> NNSMap = super.getRow("VBN");
			Iterator<Entry<String, Double>> nnsIt = NNSMap.entrySet()
					.iterator();
			while (nnsIt.hasNext()) {
				double prob = nnsIt.next().getValue();
				if (prob < VBN_PROB) {
					VBN_PROB = prob;
				}
			}
		}
		return VBN_PROB;
	}

	private double getHighestProbForCD() {
		if (CD_PROB == 0.0) {
			Map<String, Double> NNSMap = super.getRow("CD");
			Iterator<Entry<String, Double>> nnsIt = NNSMap.entrySet()
					.iterator();
			while (nnsIt.hasNext()) {
				double prob = nnsIt.next().getValue();
				if (prob > CD_PROB) {
					CD_PROB = prob;
				}
			}
		}
		return CD_PROB;
	}

	private double getHighestProbForNNP() {
		if (NP_PROB == 0.0) {
			Map<String, Double> NNSMap = super.getRow("NNP");
			Iterator<Entry<String, Double>> nnsIt = NNSMap.entrySet()
					.iterator();
			while (nnsIt.hasNext()) {
				double prob = nnsIt.next().getValue();
				if (prob > NP_PROB) {
					NP_PROB = prob;
				}
			}
		}
		return NP_PROB;
	}

	boolean startsWithCapital(String s) {
		return Pattern.compile("^[A-Z]").matcher(s).find();
	}

	boolean startsWithNum(String s) {
		return Pattern.compile("^.*[0-9].*").matcher(s).find();
	}

	private double getLowestProbForNNSAndVBZ() {
		if (NNS_AND_VBZ_PROB == 1.0) {
			Map<String, Double> NNSMap = super.getRow("NNS");
			Iterator<Entry<String, Double>> nnsIt = NNSMap.entrySet()
					.iterator();
			while (nnsIt.hasNext()) {
				double prob = nnsIt.next().getValue();
				if (prob < NNS_AND_VBZ_PROB) {
					NNS_AND_VBZ_PROB = prob;
				}
			}
			Map<String, Double> VBZMap = super.getRow("VBZ");
			Iterator<Entry<String, Double>> vbzIt = VBZMap.entrySet()
					.iterator();
			while (vbzIt.hasNext()) {
				double prob = vbzIt.next().getValue();
				if (prob < NNS_AND_VBZ_PROB) {
					NNS_AND_VBZ_PROB = prob;
				}
			}
		}
		return NNS_AND_VBZ_PROB;
	}

	private double getLowestProbForNNAndVB() {
		if (NN_AND_VB_PROB == 1.0) {
			Map<String, Double> NNSMap = super.getRow("NN");
			Iterator<Entry<String, Double>> nnsIt = NNSMap.entrySet()
					.iterator();
			while (nnsIt.hasNext()) {
				double prob = nnsIt.next().getValue();
				if (prob < NN_AND_VB_PROB) {
					NN_AND_VB_PROB = prob;
				}
			}
			Map<String, Double> VBZMap = super.getRow("VB");
			Iterator<Entry<String, Double>> vbzIt = VBZMap.entrySet()
					.iterator();
			while (vbzIt.hasNext()) {
				double prob = vbzIt.next().getValue();
				if (prob < NN_AND_VB_PROB) {
					NN_AND_VB_PROB = prob;
				}
			}
		}
		return NN_AND_VB_PROB;

	}
}
