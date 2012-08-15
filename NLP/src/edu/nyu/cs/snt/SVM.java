package edu.nyu.cs.snt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import edu.nyu.cs.pub.Corpus;
import edu.nyu.cs.pub.Sentence;
import edu.nyu.cs.pub.TextTool;

/**
 * SVM classifier.
 * <p>
 * This classifier uses open source svm library which can be found by
 * {@link http://www.csie.ntu.edu.tw/~cjlin/libsvm/}
 * 
 * @author Daniel Wu
 * 
 */
public class SVM {

	private static final Double POSITIVE = 1.0;

	private static final Double NEGATIVE = -1.0;

	private int cutoff = 4;

	private List<String> vocabulary;

	private svm_model model = null;

	/**
	 * Trains SVM model on training corpus
	 * 
	 * @param trainingData
	 *            training corpus
	 * @throws Exception
	 *             if error occurs when writing features into model file
	 */
	public void train(Corpus[] trainingData) throws Exception {
		// vocabulary, being used to find word index
		this.vocabulary = buildVocabulary(trainingData);
		// create svm problem
		svm_problem prob = createProblem(trainingData);
		svm_parameter param = createDefaultParam();
		String msg = svm.svm_check_parameter(prob, param);
		if (msg != null) {
			throw new Exception(msg);
		}
		model = svm.svm_train(prob, param);
		// svm.svm_cross_validation(prob, param, nr_fold, target);
		// svm.svm_save_model(modelFile, model);
	}

	private svm_problem createProblem(Corpus[] trainingData) {
		// total training data
		int size = trainingData[0].getSentences().size()
				+ trainingData[1].getSentences().size();
		// classes: 1 positive -1 negative
		double[] clazz = buildClassesList(trainingData);
		// training data
		svm_node[][] x = buildSVMNodes(trainingData);
		svm_problem problem = new svm_problem();
		problem.l = size;
		problem.y = clazz;
		problem.x = x;
		return problem;
	}

	private svm_node[][] buildSVMNodes(Corpus[] trainingData) {
		// positive sentences
		List<List<Integer[]>> indexAndValue = new ArrayList<List<Integer[]>>();
		indexAndValue.addAll(buildIndexAndValuePair(trainingData[0]));
		indexAndValue.addAll(buildIndexAndValuePair(trainingData[1]));
		// build svm nodes
		int m = indexAndValue.size();
		svm_node[][] nodes = new svm_node[m][];
		for (int row = 0; row < indexAndValue.size(); row++) {
			List<Integer[]> list = indexAndValue.get(row);
			int n = list.size();
			nodes[row] = new svm_node[n];
			for (int col = 0; col < n; col++) {
				Integer[] pair = list.get(col);
				svm_node node = new svm_node();
				node.index = pair[0];
				node.value = pair[1];
				nodes[row][col] = node;
			}
		}
		return nodes;
	}

	private List<List<Integer[]>> buildIndexAndValuePair(Corpus posSent) {
		List<List<Integer[]>> indexAndValue = new ArrayList<List<Integer[]>>();
		for (Sentence sentence : posSent) {
			String[] tokens = sentence.getTokenArray();
			Set<String> binarization = new HashSet<String>();
			List<Integer[]> tmpList = new ArrayList<Integer[]>();
			for (String token : tokens) {
				if (!filter(token) && !binarization.contains(token)) {
					binarization.add(token);
					int index = Collections.binarySearch(vocabulary, token);
					if (index >= 0) {
						Integer[] pair = new Integer[] { index + 1, 1 };
						tmpList.add(pair);
					}
				}
			}
			// ascend order
			Collections.sort(tmpList, new Comparator<Integer[]>() {
				@Override
				public int compare(Integer[] o1, Integer[] o2) {
					return o1[0] - o2[0];
				}

			});
			indexAndValue.add(tmpList);
		}
		return indexAndValue;
	}

	@SuppressWarnings("unused")
	private double[] buildClassesList(Corpus[] trainingData) {
		List<Double> res = new ArrayList<Double>();
		List<Sentence> posSent = trainingData[0].getSentences();
		for (Sentence sentence : posSent) {
			res.add(POSITIVE);
		}
		List<Sentence> negSent = trainingData[1].getSentences();
		for (Sentence sentence : posSent) {
			res.add(NEGATIVE);
		}
		// convert to double value
		double[] returnValue = new double[res.size()];
		for (int i = 0; i < res.size(); i++) {
			returnValue[i] = res.get(i).doubleValue();
		}
		return returnValue;
	}

	private List<String> buildVocabulary(Corpus[] trainingData) {
		// assemble sentences
		List<Sentence> totalSentence = new ArrayList<Sentence>();
		totalSentence.addAll(trainingData[0].getSentences());
		totalSentence.addAll(trainingData[1].getSentences());

		// build vocabulary
		List<String> voc = new ArrayList<String>();
		Map<String, Integer> tmp = new HashMap<String, Integer>();
		for (Sentence sent : totalSentence) {
			String[] words = sent.getTokenArray();
			for (String word : words) {
				Integer count = tmp.get(word);
				if (count == null) {
					count = 0;
				}
				count += 1;
				tmp.put(word, count);
			}
		}
		// cut-off those appear less than 4 times
		Iterator<Map.Entry<String, Integer>> it = tmp.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> entry = it.next();
			int count = entry.getValue();
			if (count >= cutoff) {
				voc.add(entry.getKey());
			}
		}
		System.out.println("After cutting off, vocabulary size " + voc.size());
		Collections.sort(voc);
		return voc;
	}

	private svm_parameter createDefaultParam() {
		svm_parameter param = new svm_parameter();

		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		param.degree = 3;
		param.gamma = 0;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 40;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

		return param;
	}

	private boolean filter(String string) {
		if (TextTool.isEmpty(string) || string.length() < 3) {
			return true;
		}
		if (string.matches("\\d+") || !string.matches("[A-Za-z]+.*")) {
			return true;
		}
		return false;
	}

	/**
	 * Predicts polarity of movie comments using svm classifier
	 * 
	 * @param testData
	 *            testing corpus
	 * @return predicting stats. Result is an integer array with length of 2.<br/>
	 *         First element is the number of correct predict. Second element is
	 *         the number of incorrect predict.
	 */
	public int[] test(Corpus[] trainingData) {
		// classes: 1 positive -1 negative
		double[] clazz = buildClassesList(trainingData);
		svm_node[][] x = buildSVMNodes(trainingData);

		int correct = 0;
		int incorrect = 0;
		for (int i = 0; i < x.length; i++) {
			double predicted = svm.svm_predict(model, x[i]);
			if (clazz[i] == predicted) {
				correct++;
			} else {
				incorrect++;
			}
		}
		return new int[] { correct, incorrect };
	}
}
