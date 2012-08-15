package edu.nyu.cs.snt;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import edu.nyu.cs.pub.Corpus;
import edu.nyu.cs.pub.Sentence;

/**
 * Naive Bayes Classifier
 * 
 * @author Daniel Wu
 * 
 */
public class NaiveBayes extends AbstractClassifier {

	/**
	 * Class constructor with folders which contain corpus
	 * 
	 * @param folders
	 */
	public NaiveBayes(List<Folder> folders) {
		super(folders);
	}

	// transition matrix for positive comments
	protected TransitionMatrix posMatrix = null;

	// transition matrix for negative comments
	protected TransitionMatrix negMatrix = null;

	/**
	 * Trains NaiveBayes classifier on training corpus. The training process
	 * will compute those two transition matrices.
	 * 
	 * @param trainingData
	 *            training corpus
	 */
	public void train(Corpus[] trainingData) {
		Set<String> vocabulary = super.mergeVocabulary(trainingData);
		trainingData[0].setVocabulary(vocabulary);
		trainingData[1].setVocabulary(vocabulary);
		posMatrix = new TransitionMatrix(trainingData[0]);
		negMatrix = new TransitionMatrix(trainingData[1]);
	}

	/**
	 * Tests NaiveBayes classifier against testing corpus. Cross validation is
	 * used.
	 * 
	 * @param testData
	 *            testing corpus
	 */
	public void test(Corpus testData) {
		// cross validation
		testBigramWithNB(testData);
		testUnigramWithNB(testData);
	}

	public int[] testBigramWithNB(Corpus testData) {
		int correct = 0;
		int incorrect = 0;
		List<Sentence> sentences = testData.getSentences();
		for (Sentence sentence : sentences) {
			String[] perm = sentence.getTokenArray();
			Double posProb = posMatrix.probabilityWithBigram(perm);
			Double negProb = negMatrix.probabilityWithBigram(perm);
			int predict = -1;
			if (posProb < negProb) {
				predict = Sentence.POSITIVE;
			} else {
				predict = Sentence.NEGATIVE;
			}
			if (predict == sentence.getPolarity()) {
				correct++;
			} else {
				incorrect++;
			}
		}
		return new int[] { correct, incorrect };
	}

	public int[] testUnigramWithNB(Corpus testData) {
		int correct = 0;
		int incorrect = 0;
		List<Sentence> sentences = testData.getSentences();
		for (Sentence sentence : sentences) {
			String[] perm = sentence.getTokenArray();
			Double posProb = posMatrix.probabilityWithUnigram(perm);
			Double negProb = negMatrix.probabilityWithUnigram(perm);
			int predict = -1;
			if (posProb < negProb) {
				predict = Sentence.POSITIVE;
			} else {
				predict = Sentence.NEGATIVE;
			}
			if (predict == sentence.getPolarity()) {
				correct++;
			} else {
				incorrect++;
			}
		}
		return new int[] { correct, incorrect };
	}

}
