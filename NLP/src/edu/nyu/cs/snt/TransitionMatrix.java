package edu.nyu.cs.snt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.nyu.cs.pub.Constant;
import edu.nyu.cs.pub.Corpus;
import edu.nyu.cs.pub.Matrix;
import edu.nyu.cs.pub.Sentence;
import edu.nyu.cs.pub.TextTool;
import edu.nyu.cs.pub.Token;

/**
 * Transition Probability Matrix. 
 * 
 * @author Daniel Wu
 * 
 */
public class TransitionMatrix extends Matrix {

	private List<String[]> bigram = new ArrayList<String[]>();

	private Map<String, Double> unigram = new HashMap<String, Double>();

	public TransitionMatrix(Corpus corpus) {
		super(corpus);
		// initialize transition matrix
		initialize();
		// compute probability
		normalize();
	}

	private void initialize() {
		// initialize bigram model and unigram model
		initializeModel();
		// initialize matrix
		initializeMatrix();
	}

	/**
	 * counting for transition matrix
	 * 
	 * @param bigram
	 */
	private void initializeMatrix() {
		initializeBigramMatrix();
		initializeUnigramMatrix();
	}

	private void initializeUnigramMatrix() {
		// filter out those words who appear less than 4
		super.unigramMatrix = filter(this.unigram);
	}

	/**
	 * remove those appear less than 4 times, and stop words
	 * 
	 * @param unigram2
	 * @return
	 */
	private Map<String, Double> filter(Map<String, Double> unigram2) {
		Map<String, Double> res = new HashMap<String, Double>();
		Iterator<Map.Entry<String, Double>> it = unigram2.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Double> word = it.next();
			String literal = word.getKey();
			Double count = word.getValue();
			if (count >= 4) {
				res.put(literal, count);
			}
		}
		return res;
	}

	private void initializeBigramMatrix() {
		for (String[] biStr : bigram) {
			// Tag
			String n_1 = biStr[0];
			// Following Tag
			String n = biStr[1];
			if (bigramMatrix.get(n_1) == null) {
				Map<String, Double> priorMap = new HashMap<String, Double>();
				bigramMatrix.put(n_1, priorMap);
			}
			if (bigramMatrix.get(n_1).get(n) == null) {
				Double value = 0.0;
				bigramMatrix.get(n_1).put(n, value);
			}
			Double value = bigramMatrix.get(n_1).get(n);
			bigramMatrix.get(n_1).put(n, ++value);
		}
	}

	/**
	 * Initialize bigram model for tags
	 */
	private void initializeModel() {
		List<Sentence> sentences = getCorpus().getSentences();
		for (Sentence sentence : sentences) {
			List<Token> tokens = sentence.getTokens();
			for (int i = -1; i < tokens.size(); i++) {
				String tag_n_1 = "";
				String tag_n = "";
				if (i == -1) {
					tag_n_1 = Constant.START_SYMBOL;
				} else {
					Token token_n_1 = tokens.get(i);
					tag_n_1 = token_n_1.getLiteral().toUpperCase();
				}
				if (i == tokens.size() - 1) {
					tag_n = Constant.END_SYMBOL;
				} else {
					Token token_n = tokens.get(i + 1);
					tag_n = token_n.getLiteral().toUpperCase();
				}
				String[] biStr = new String[] { tag_n_1, tag_n };
				bigram.add(biStr);
				// initialize unigram
				tag_n_1 = tag_n_1.toUpperCase();
				Double count = 0.0;
				if (unigram.containsKey(tag_n_1)) {
					count = unigram.get(tag_n_1);
				}
				unigram.put(tag_n_1, ++count);
			}
		}
	}

	// private List<String> formalizeInput(String[] input) {
	// List<String> res = TextTool.appendSymbols(input);
	// return res;
	// }

	public Double probabilityWithBigram(String[] perm) {
		List<String[]> biStrs = TextTool.bigramWithSymbols(perm);
		Double prob = new Double(1);
		for (String[] biStr : biStrs) {
			Double biStrProb = this.getItem(biStr[0], biStr[1]);
			if (biStrProb != null) {
				prob += Math.abs(Math.log10(biStrProb));
			}
		}
		return prob;
	}

	public Double probabilityWithUnigram(String[] perm) {
		Double prob = new Double(0);
		for (String word : perm) {
			Double biStrProb = this.getItem(word);
			if (biStrProb != null) {
				prob += Math.abs(Math.log10(biStrProb));
			}
		}
		return prob;
	}

}
