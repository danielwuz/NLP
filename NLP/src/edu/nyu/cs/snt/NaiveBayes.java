package edu.nyu.cs.snt;

import java.util.List;
import java.util.Set;

import edu.nyu.cs.pub.Corpus;
import edu.nyu.cs.pub.Sentence;

public class NaiveBayes extends AbstractClassifier {

	public NaiveBayes(List<Folder> folders) {
		super(folders);
	}

	protected TransitionMatrix posMatrix = null;

	protected TransitionMatrix negMatrix = null;

	public void train(Corpus[] trainingData) {
		Set<String> vocabulary = super.mergeVocabulary(trainingData);
		trainingData[0].setVocabulary(vocabulary);
		trainingData[1].setVocabulary(vocabulary);
		posMatrix = new TransitionMatrix(trainingData[0]);
		negMatrix = new TransitionMatrix(trainingData[1]);
	}

	public void test(Corpus testData) throws Exception {
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
