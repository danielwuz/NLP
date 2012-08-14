package edu.nyu.cs.pub;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Base matrix, for transmission matrix and emission matrix
 * 
 * @author Daniel Wu
 * 
 */
public abstract class Matrix {

	// transition probability matrix <K,V> = <Tag,<Following Tag,Probability>>
	protected Map<String, Map<String, Double>> bigramMatrix = new HashMap<String, Map<String, Double>>();

	protected Map<String, Double> unigramMatrix = new HashMap<String, Double>();

	private final Corpus corpus;

	private Double oovForUnigram = 0.0;

	private Double oovForBigram = 0.0;

	public Matrix(Corpus corpus) {
		this.corpus = corpus;
	}

	protected Corpus getCorpus() {
		return this.corpus;
	}

	/**
	 * normalize matrix cells, counting probability
	 */
	protected void normalize() {
		normalizeUnigram();
		normalizeBigram();
	}

	private void normalizeUnigram() {
		// total number of words
		Iterator<Map.Entry<String, Double>> it = unigramMatrix.entrySet()
				.iterator();
		double count = 0;
		while (it.hasNext()) {
			Map.Entry<String, Double> entry = it.next();
			count += entry.getValue();
		}
		oovForUnigram = 1.0 / (count + corpus.vocabularySize());

		// normalize
		it = unigramMatrix.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Double> entry = it.next();
			Double numberOfword = entry.getValue();
			// add one smoothing
			Double prob = (numberOfword + 1)
					/ (count + corpus.vocabularySize());
			entry.setValue(prob);
		}
	}

	public Double getItem(String word) {
		Double prob = this.unigramMatrix.get(word);
		if (prob == null) {
			return oovForUnigram;
		}
		return prob;
	}

	private void normalizeBigram() {
		Iterator<Map.Entry<String, Map<String, Double>>> it = bigramMatrix
				.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Map<String, Double>> entry = it.next();
			Map<String, Double> colList = entry.getValue();
			double count = 0;
			for (Entry<String, Double> ent : colList.entrySet()) {
				count += ent.getValue();
			}
			oovForBigram = 1.0 / (count + corpus.vocabularySize());
			// normalize
			for (Entry<String, Double> ent : colList.entrySet()) {
				// add 1 smoothing
				Double prob = (ent.getValue() + 1)
						/ (count + corpus.vocabularySize());
				// Double prob = (ent.getValue() / count);
				ent.setValue(prob);
			}
		}
	}

	/**
	 * bigram probablity, with add-one smoothing
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public double getItem(String row, String col) {
		Map<String, Double> rowMap = this.getRow(row);
		if (rowMap == null) {
			return oovForBigram;
		}
		Double value = rowMap.get(col);
		return (value == null) ? oovForBigram : value;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getName());
		sb.append("\n------------------\n");
		Iterator<Map.Entry<String, Map<String, Double>>> it = bigramMatrix
				.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Map<String, Double>> entry = it.next();
			sb.append("[" + entry.getKey() + "=");
			sb.append(entry.getValue());
			sb.append("]\n");
		}

		return sb.toString();
	}

	public Map<String, Double> getRow(String row) {
		return bigramMatrix.get(row);
	}

	public String[] getKeys() {
		return bigramMatrix.keySet().toArray(new String[0]);
	}
}
