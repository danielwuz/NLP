package edu.nyu.cs.ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Movie review model
 * 
 * @author Daniel Wu
 * 
 */
public class ReviewMetric {

	public static String negPrefix = "NOT_";

	private String id;

	private double label3;

	private double label4;

	private Map<String, Integer> matrix = new HashMap<String, Integer>();

	private List<Integer[]> indexAndValue = new ArrayList<Integer[]>();

	public String toLabel3() {
		StringBuffer sb = new StringBuffer();
		sb.append(label3);
		for (Integer[] pair : indexAndValue) {
			sb.append(" " + pair[0] + ":" + pair[1]);
		}
		return sb.toString();
	}

	public String toLabel4() {
		StringBuffer sb = new StringBuffer();
		sb.append(label4);
		for (Integer[] pair : indexAndValue) {
			sb.append(" " + pair[0] + ":" + pair[1]);
		}
		return sb.toString();
	}

	public ReviewMetric(String id, String label3, String label4,
			Map<String, Integer> matrix) {
		this.id = id;
		this.label3 = Double.parseDouble(label3);
		this.label4 = Double.parseDouble(label4);
		this.matrix = matrix;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Integer> getMatrix() {
		return matrix;
	}

	public void setMatrix(Map<String, Integer> matrix) {
		this.matrix = matrix;
	}

	public double getLabel3() {
		return label3;
	}

	public double getLabel4() {
		return label4;
	}

	public void buildIndexAndValuePair(List<String> vocabulary) {
		// process negation words, which have prefix "NOT_"
		matrix = preprocessNegation(this.matrix);
		indexAndValue = new ArrayList<Integer[]>();
		Iterator<Map.Entry<String, Integer>> it = matrix.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry = it.next();
			Integer count = entry.getValue();
			int index = Collections.binarySearch(vocabulary, entry.getKey());
			if (index >= 0) {
				Integer[] pair = new Integer[] { index + 1, count };
				indexAndValue.add(pair);
			}
		}
		Collections.sort(indexAndValue, new Comparator<Integer[]>() {

			@Override
			public int compare(Integer[] o1, Integer[] o2) {
				return o1[0] - o2[0];
			}

		});
	}

	private Map<String, Integer> preprocessNegation(Map<String, Integer> matrix) {
		Map<String, Integer> tmp = new HashMap<String, Integer>();
		Iterator<Map.Entry<String, Integer>> it = matrix.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> entry = it.next();
			String word = entry.getKey();
			// if current word indicates a negative meaning
			boolean neg = false;
			// if (word.startsWith(ReviewMetric.negPrefix)) {
			// neg = true;
			// // retrieve its original meaning
			// word = word.substring(4);
			// }
			Integer count = tmp.get(word);
			count = (count == null) ? 0 : count;
			if (neg) {
				// deduct weight
				count -= entry.getValue();
			} else {
				// adding weight
				count += entry.getValue();
			}
			tmp.put(word, count);
		}
		return tmp;
	}

	public List<Integer[]> getIndexAndValue() {
		return indexAndValue;
	}

}
