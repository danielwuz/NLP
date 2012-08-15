package edu.nyu.cs.snt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.nyu.cs.pub.Corpus;
import edu.nyu.cs.pub.Sentence;

/**
 * 
 * Sentimental analysis model.
 * <p>
 * This model tries out three different classifiers
 * <ul>
 * <li>Naive Bayes Classifier</li>
 * <li>Maximum Entropy Classifier</li>
 * <li>SVM Classifier</li>
 * </ul>
 * against either bigram or unigram model. <br/>
 * see {@link www.cs.cornell.edu/home/llee/papers/sentiment.pdf} for more
 * detail.
 * 
 * @author Daniel Wu
 * 
 */
public class SentimentModel {

	private FileManager fileManager = new FileManager();

	protected List<Folder> folders = new ArrayList<Folder>();

	private Logger logger = new Logger();

	/**
	 * Loads corpus from file system
	 * 
	 * @param filePaths
	 *            corpus path
	 * @throws IOException
	 *             if error occurs when read in corpus file
	 */
	public void loadCorpus(String[] filePaths) throws IOException {
		// positive comments
		List<Corpus> posList = loadPositiveCorpus(filePaths[0]);
		List<Corpus> negList = loadNegativeCorpus(filePaths[1]);
		// seperate as folders
		for (int i = 0; i < posList.size(); i++) {
			Folder folder = new Folder(i, posList.get(i), negList.get(i));
			folders.add(folder);
		}
	}

	private List<Corpus> loadCorpusFromDir(String path) throws IOException {
		List<Corpus> corpusList = new ArrayList<Corpus>();
		File[] files = new File(path).listFiles();
		for (File file : files) {
			corpusList.add(new Corpus(file.getPath(), fileManager));
		}
		return corpusList;
	}

	private List<Corpus> loadNegativeCorpus(String negFilePath)
			throws IOException {
		List<Corpus> negList = loadCorpusFromDir(negFilePath);
		setPolarityTags(negList, Sentence.NEGATIVE);
		return negList;
	}

	private List<Corpus> loadPositiveCorpus(String posFilePath)
			throws IOException {
		List<Corpus> posList = loadCorpusFromDir(posFilePath);
		// set polarity tags, used for accuracy scoring later
		setPolarityTags(posList, Sentence.POSITIVE);
		return posList;
	}

	private void setPolarityTags(List<Corpus> posList, int polarity) {
		for (Corpus corpus : posList) {
			List<Sentence> sentences = corpus.getSentences();
			for (Sentence sentence : sentences) {
				sentence.setPolarity(polarity);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// if (args.length < 3) {
		// System.out
		// .println("Please specify args\n Typical usage: java -jar SENT_CLASSIFIER.jar -t pos_corpus_dir neg_corpus_dir\n");
		// return;
		// }
		SentimentModel model = new SentimentModel();
		// training
		if ("-t".equals(args[0])) {
			long t1 = System.currentTimeMillis();
			model.loadCorpus(new String[] { args[1], args[2] });
			long t2 = System.currentTimeMillis();
			System.out.println("Loading corpus time: " + (t2 - t1));
			model.test();
		} else {
			System.out.println("Wrong command");
			return;
		}
	}

	private Corpus[] createTrainingData(int i) {
		List<Corpus> posList = new ArrayList<Corpus>();
		List<Corpus> negList = new ArrayList<Corpus>();
		for (int j = 0; j < folders.size(); j++) {
			Folder jthFolder = folders.get(j);
			if (j != i) {
				posList.add(jthFolder.getPosCorpus());
				negList.add(jthFolder.getNegCorpus());
			}
		}
		Corpus posCorpus = new Corpus(posList);
		Corpus negCorpus = new Corpus(negList);
		return new Corpus[] { posCorpus, negCorpus };
	}

	private void test() throws Exception {
		// test with frequency
		testUnigramWithFreq();
		testBigramWithFreq();
		// test with binarization
		testUnigramWithBinary();
		testBigramWithBinary();
		// test with Maxent
		testUnigramWithMaxent();
		// test with SVM
		testUnigramWithSVM();
	}

	private void testUnigramWithSVM() {
		System.out
				.println("\n\nExperience classification with SVM on unigram features...");
		int correct = 0;
		int incorrect = 0;
		SVM svm = new SVM();
		try {
			for (int i = 0; i < folders.size(); i++) {
				Folder ithFolder = folders.get(i);
				log("\n\n" + ithFolder
						+ " for testing, training with others...");
				long t1 = System.currentTimeMillis();
				Corpus[] trainingData = createTrainingData(i);
				svm.train(trainingData);
				long t2 = System.currentTimeMillis();
				log("Training time: " + (t2 - t1));
				// test unigram
				Corpus[] testData = new Corpus[] { ithFolder.getPosCorpus(),
						ithFolder.getNegCorpus() };
				int[] predict = svm.test(testData);
				correct += predict[0];
				incorrect += predict[1];
				double accuracy = calcAccuracy(predict[0], predict[1]);
				log("Correct predict: " + predict[0] + " Incorrect predict: "
						+ predict[1] + " Accuracy: " + accuracy);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		double overAllAccuracy = calcAccuracy(correct, incorrect);
		log("Overall correct predict: " + correct
				+ " Overall incorrect predict: " + incorrect + " Accuracy: "
				+ overAllAccuracy);
	}

	private void testUnigramWithMaxent() throws Exception {
		System.out
				.println("\n\nExperience classification with MaxEnt on unigram features...");
		int correct = 0;
		int incorrect = 0;
		MaxEnt me = new MaxEnt(folders);
		for (int i = 0; i < folders.size(); i++) {
			Folder ithFolder = folders.get(i);
			log("\n\n" + ithFolder + " for testing, training with others...");
			Corpus testData = createTestData(i);
			long t1 = System.currentTimeMillis();
			Corpus[] trainingData = createTrainingData(i);
			me.train(trainingData);
			long t2 = System.currentTimeMillis();
			log("Training time: " + (t2 - t1));
			{
				// test unigram
				int[] predict = me.test(testData);
				correct += predict[0];
				incorrect += predict[1];
				double accuracy = calcAccuracy(predict[0], predict[1]);
				log("Correct predict: " + predict[0] + " Incorrect predict: "
						+ predict[1] + " Accuracy: " + accuracy);
			}
		}
		double overAllAccuracy = calcAccuracy(correct, incorrect);
		log("Overall correct predict: " + correct
				+ " Overall incorrect predict: " + incorrect + " Accuracy: "
				+ overAllAccuracy);

	}

	private void testBigramWithBinary() {
		System.out
				.println("\n\nExperience classification with Naive Bayes on bigram binarization features...");
		int correct = 0;
		int incorrect = 0;
		NaiveBayesWithPres nb = new NaiveBayesWithPres(folders);
		for (int i = 0; i < folders.size(); i++) {
			Folder ithFolder = folders.get(i);
			log("\n\n" + ithFolder + " for testing, training with others...");
			Corpus testData = createTestData(i);
			long t1 = System.currentTimeMillis();
			Corpus[] trainingData = createTrainingData(i);
			nb.train(trainingData);
			long t2 = System.currentTimeMillis();
			log("Training time: " + (t2 - t1));
			{
				// test bigram
				int[] predict = nb.testBigramWithNB(testData);
				correct += predict[0];
				incorrect += predict[1];
				double accuracy = calcAccuracy(predict[0], predict[1]);
				log("Correct predict: " + predict[0] + " Incorrect predict: "
						+ predict[1] + " Accuracy: " + accuracy);
			}
		}
		double overAllAccuracy = calcAccuracy(correct, incorrect);
		log("Overall correct predict: " + correct
				+ " Overall incorrect predict: " + incorrect + " Accuracy: "
				+ overAllAccuracy);

	}

	private void testUnigramWithBinary() {
		System.out
				.println("\n\nExperience classification with Naive Bayes on unigram binarization features...");
		int correct = 0;
		int incorrect = 0;
		NaiveBayesWithPres nb = new NaiveBayesWithPres(folders);
		for (int i = 0; i < folders.size(); i++) {
			Folder ithFolder = folders.get(i);
			log("\n\n" + ithFolder + " for testing, training with others...");
			Corpus testData = createTestData(i);
			long t1 = System.currentTimeMillis();
			Corpus[] trainingData = createTrainingData(i);
			nb.train(trainingData);
			long t2 = System.currentTimeMillis();
			log("Training time: " + (t2 - t1));
			{
				// test unigram
				int[] predict = nb.testUnigramWithNB(testData);
				correct += predict[0];
				incorrect += predict[1];
				double accuracy = calcAccuracy(predict[0], predict[1]);
				log("Correct predict: " + predict[0] + " Incorrect predict: "
						+ predict[1] + " Accuracy: " + accuracy);
			}
		}
		double overAllAccuracy = calcAccuracy(correct, incorrect);
		log("Overall correct predict: " + correct
				+ " Overall incorrect predict: " + incorrect + " Accuracy: "
				+ overAllAccuracy);

	}

	private void testBigramWithFreq() throws Exception {
		System.out
				.println("\n\nExperience classification with Naive Bayes on bigram frequency features...");
		int correct = 0;
		int incorrect = 0;
		NaiveBayes nb = new NaiveBayes(folders);
		for (int i = 0; i < folders.size(); i++) {
			Folder ithFolder = folders.get(i);
			log("\n\n" + ithFolder + " for testing, training with others...");
			Corpus testData = createTestData(i);
			long t1 = System.currentTimeMillis();
			Corpus[] trainingData = createTrainingData(i);
			nb.train(trainingData);
			long t2 = System.currentTimeMillis();
			log("Training time: " + (t2 - t1));
			{
				// test bigram
				int[] predict = nb.testBigramWithNB(testData);
				correct += predict[0];
				incorrect += predict[1];
				double accuracy = calcAccuracy(predict[0], predict[1]);
				log("Correct predict: " + predict[0] + " Incorrect predict: "
						+ predict[1] + " Accuracy: " + accuracy);
			}
		}
		double overAllAccuracy = calcAccuracy(correct, incorrect);
		log("Overall correct predict: " + correct
				+ " Overall incorrect predict: " + incorrect + " Accuracy: "
				+ overAllAccuracy);

	}

	private void testUnigramWithFreq() throws Exception {
		System.out
				.println("\n\nExperience classification with Naive Bayes on unigram frequency features...");
		int correct = 0;
		int incorrect = 0;
		NaiveBayes nb = new NaiveBayes(folders);
		for (int i = 0; i < folders.size(); i++) {
			Folder ithFolder = folders.get(i);
			log("\n\n" + ithFolder + " for testing, training with others...");
			Corpus testData = createTestData(i);
			long t1 = System.currentTimeMillis();
			Corpus[] trainingData = createTrainingData(i);
			nb.train(trainingData);
			long t2 = System.currentTimeMillis();
			log("Training time: " + (t2 - t1));
			{
				// test unigram
				int[] predict = nb.testUnigramWithNB(testData);
				correct += predict[0];
				incorrect += predict[1];
				double accuracy = calcAccuracy(predict[0], predict[1]);
				log("Correct predict: " + predict[0] + " Incorrect predict: "
						+ predict[1] + " Accuracy: " + accuracy);
			}
		}
		double overAllAccuracy = calcAccuracy(correct, incorrect);
		log("Overall correct predict: " + correct
				+ " Overall incorrect predict: " + incorrect + " Accuracy: "
				+ overAllAccuracy);
	}

	private double calcAccuracy(int correct, int incorrect) {
		double accuracy = 1.0 * correct / (correct + incorrect);
		return accuracy;
	}

	private Corpus createTestData(int i) {
		Folder ithFolder = folders.get(i);
		return new Corpus(ithFolder.getAllCorpus());
	}

	protected void log(String msg) {
		logger.log(msg);
	}

	/**
	 * Log class
	 * 
	 * @author Daniel Wu
	 * 
	 */
	public static class Logger {

		private StringBuffer log = new StringBuffer();

		public void output() {
			System.out.println(log);
		}

		/**
		 * write message in log
		 * 
		 * @param string
		 *            message
		 */
		public void log(String string) {
			// log.append(string + "\n");
			System.out.println(string);
		}
	}

}
