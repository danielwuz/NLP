package edu.nyu.cs.ranking;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

/**
 * @author Zhe Wu N16445442 zw339@nyu.edu
 * 
 */
public class SVMModel {

	private String modelPath3 = "movie_scale_label3";

	private String modelPath4 = "movie_scale_label4";

	private int label = 3;

	private int cutoff = 0;

	private svm_model model = null;

	private int nr_fold = 3;

	private static String msg() {
		return "Please specify args\n Typical usage: java -jar SVM_RANKING.jar -t training_corpus_path -l 3_or_4(using label3 or label4, default 3) -v num_of_folds(default 3) -c cutoff(default 7)\n";
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println(msg());
			return;
		}
		if ("-t".equals(args[0])) {
			SVMModel model = createModel(args);
			long t1 = System.currentTimeMillis();
			List<ReviewMetric> data = null;
			try {
				data = model.loadCorpus(args[1]);
			} catch (Exception e) {
				System.out.println("Loading corpus exception!");
				throw e;
			}
			long t2 = System.currentTimeMillis();
			System.out.println("Loading corpus time: " + (t2 - t1));
			int[] predict;
			try {
				// training
				predict = model.train(data);
			} catch (Exception e) {
				System.out.println("Training exception!");
				throw e;
			}
			long t3 = System.currentTimeMillis();
			System.out.println("Training time: " + (t3 - t2));
			// int[] predict = model.test(data);
			double accuracy = calcAccuracy(predict[0], predict[1]);
			System.out.println("Correct predict: " + predict[0]
					+ " Incorrect predict: " + predict[1] + " Accuracy: "
					+ accuracy);
			long t4 = System.currentTimeMillis();
			System.out.println("Testing time: " + (t4 - t3));
		} else {
			System.out.println("Wrong command!");
			System.out.println(msg());
			return;
		}
	}

	private static SVMModel createModel(String[] args) throws Exception {
		SVMModel svm = new SVMModel();
		int index_l = getIndex(args, "-l");
		if (index_l != -1) {
			int label = Integer.parseInt(args[index_l + 1]);
			if (label != 3 && label != 4) {
				throw new Exception(
						"Unsupported label number, label should be 3 or 4");
			}
			svm.label = label;
		}
		int n_folds = getIndex(args, "-v");
		if (n_folds != -1) {
			int folds = Integer.parseInt(args[n_folds + 1]);
			svm.nr_fold = folds;
		}
		int c_index = getIndex(args, "-c");
		if (c_index != -1) {
			int cutoff = Integer.parseInt(args[c_index + 1]);
			svm.cutoff = cutoff;
		}
		return svm;
	}

	private static int getIndex(String[] args, String option) {
		for (int i = 0; i < args.length; i++) {
			if (option.equals(args[i])) {
				return i;
			}
		}
		return -1;
	}

	private static double calcAccuracy(int correct, int incorrect) {
		double accuracy = 1.0 * correct / (correct + incorrect);
		return accuracy;
	}

	private int[] test(List<ReviewMetric> data) {
		// classes: 1 positive -1 negative
		double[] clazz = buildClassesList(data);
		svm_node[][] x = buildSVMNodes(data);

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

	private int[] train(List<ReviewMetric> data) throws Exception {
		System.out.println("Training with label" + label
				+ " , Cross validation with " + nr_fold + " folds...");
		// create svm problem
		svm_problem prob = createProblem(data);
		svm_parameter param = createDefaultParam();
		String msg = svm.svm_check_parameter(prob, param);
		if (msg != null) {
			throw new Exception(msg);
		}
		// model = svm.svm_train(prob, param);
		double[] target = new double[data.size()];
		svm.svm_cross_validation(prob, param, nr_fold, target);
		// accuracy
		int correct = 0;
		int incorrect = 0;
		for (int i = 0; i < target.length; i++) {
			double trueLabel = (label == 3) ? data.get(i).getLabel3() : data
					.get(i).getLabel4();
			if (trueLabel == target[i]) {
				correct++;
			} else {
				incorrect++;
			}
		}
		return new int[] { correct, incorrect };
	}

	private svm_problem createProblem(List<ReviewMetric> data) {
		// classes: 1 positive -1 negative
		double[] clazz = buildClassesList(data);
		// training data
		svm_node[][] x = buildSVMNodes(data);
		svm_problem problem = new svm_problem();
		// total training data
		problem.l = data.size();
		problem.y = clazz;
		problem.x = x;
		return problem;
	}

	private svm_node[][] buildSVMNodes(List<ReviewMetric> data) {
		// build svm nodes
		int m = data.size();
		svm_node[][] nodes = new svm_node[m][];
		for (int row = 0; row < data.size(); row++) {
			List<Integer[]> list = data.get(row).getIndexAndValue();
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

	private double[] buildClassesList(List<ReviewMetric> data) {
		// convert to double value
		double[] returnValue = new double[data.size()];
		for (int i = 0; i < data.size(); i++) {
			ReviewMetric rm = data.get(i);
			if (label == 3) {
				returnValue[i] = rm.getLabel3();
			} else {
				returnValue[i] = rm.getLabel4();
			}
		}
		return returnValue;
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

	private List<String> buildVocabulary(List<ReviewMetric> rm) {
		List<String> voc = new ArrayList<String>();
		Map<String, Integer> tmp = new HashMap<String, Integer>();
		for (ReviewMetric matrix : rm) {
			Iterator<Map.Entry<String, Integer>> it = matrix.getMatrix()
					.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> entry = it.next();
				String word = cutoffNegation(entry.getKey());
				Integer count = tmp.get(word);
				if (count == null) {
					count = 0;
				}
				count += entry.getValue();
				tmp.put(word, count);
			}
		}
		// cut-off those appear less than 4 times
		Iterator<Map.Entry<String, Integer>> it = tmp.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> entry = it.next();
			int count = entry.getValue();
			if (count >= cutoff) {
				voc.add(cutoffNegation(entry.getKey()));
			}
		}
		System.out.println("Cut off words appear less than " + cutoff
				+ " times...");
		System.out.println("After cutting off, vocabulary size " + voc.size());
		Collections.sort(voc);
		return voc;
	}

	private String cutoffNegation(String word) {
		// if (word.startsWith(ReviewMetric.negPrefix)) {
		// word = word.substring(4);
		// }
		return word;
	}

	private List<ReviewMetric> loadCorpus(String filePath) throws Exception {
		FileManager fileManager = new FileManager();
		List<ReviewMetric> rmList = fileManager.read(new File(filePath));
		List<String> vocabulary = buildVocabulary(rmList);
		for (ReviewMetric metric : rmList) {
			metric.buildIndexAndValuePair(vocabulary);
		}
		// fileManager.writeMatrix3(modelPath3, rmList);
		// fileManager.writeMatrix4(modelPath4, rmList);
		return rmList;
	}

}
