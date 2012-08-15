package edu.nyu.cs.snt;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import opennlp.maxent.BasicEventStream;
import opennlp.maxent.GIS;
import opennlp.maxent.GISModel;
import opennlp.maxent.PlainTextByLineDataStream;
import opennlp.maxent.io.GISModelWriter;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.model.EventStream;
import opennlp.model.MaxentModel;
import edu.nyu.cs.pub.Corpus;
import edu.nyu.cs.pub.Sentence;
import edu.nyu.cs.pub.TextTool;

public class MaxEnt extends AbstractClassifier {

	private String modelFile = "unigramFeatures.dat";

	public MaxEnt(List<Folder> folders) {
		super(folders);
		filter();
	}

	private void filter() {
		// count words
		Map<String, Double> unigram = new HashMap<String, Double>();
		for (Folder folder : folders) {
			List<Corpus> corpuses = folder.getAllCorpus();
			for (Corpus corpus : corpuses) {
				List<Sentence> sentences = corpus.getSentences();
				for (Sentence sentence : sentences) {
					String[] tokens = sentence.getTokenArray();
					for (String str : tokens) {
						Double count = unigram.get(str);
						if (count == null) {
							count = 0.0;
						}
						unigram.put(str, ++count);
					}
				}
			}
		}
		// cut off those appear less than 4 times
		Set<String> res = new HashSet<String>();
		Iterator<Map.Entry<String, Double>> it = unigram.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Double> word = it.next();
			String literal = word.getKey();
			Double count = word.getValue();
			if (count >= cutoff) {
				res.add(literal);
			}
		}

		// filter out
		List<Folder> tmp = new ArrayList<Folder>();
		for (Folder folder : folders) {
			int id = folder.getId();
			Corpus posCorpus = folder.getPosCorpus();
			posCorpus = posCorpus.filter(res);
			Corpus negCorpus = folder.getNegCorpus();
			negCorpus = negCorpus.filter(res);
			tmp.add(new Folder(id, posCorpus, negCorpus));
		}
		this.folders = tmp;
	}

	public void train(Corpus[] trainingData) throws Exception {
		List<String[]> features = new ArrayList<String[]>();
		features.addAll(extractFeature(trainingData[0], "positive"));
		features.addAll(extractFeature(trainingData[1], "negative"));
		FileManager.writeFeatures(modelFile, features);
	}

	private Collection<String[]> extractFeature(Corpus corpus, String polarity) {
		Set<String> binarization = new HashSet<String>();
		List<String[]> res = new ArrayList<String[]>();
		List<Sentence> sents = corpus.getSentences();
		for (Sentence sentence : sents) {
			String[] perm = sentence.getTokenArray();
			for (String string : perm) {
				if (!filter(string) && !binarization.contains(string)) {
//					binarization.add(string);
					res.add(new String[] { string, polarity });
				}
			}
		}
		return res;
	}

	private boolean filter(String string) {
		if (TextTool.isEmpty(string) || string.length() < 3) {
			return true;
		}
		if (string.matches("\\d+")) {
			return true;
		}
		return false;
	}

	private MaxentModel build(String dataFilePath) throws Exception {
		// features model
		FileReader datafr = new FileReader(new File(dataFilePath));
		EventStream es = new BasicEventStream(new PlainTextByLineDataStream(
				datafr));
		GISModel model = GIS.trainModel(es, 100, 0,true,true);
		File outputFile = new File(dataFilePath + ".model");
		GISModelWriter writer = new SuffixSensitiveGISModelWriter(model,
				outputFile);
		writer.persist();
		return model;
	}

	public int[] test(Corpus testData) throws Exception {
		int correct = 0;
		int incorrect = 0;
		MaxentModel maxentModel = build(modelFile);
		List<Sentence> sentences = testData.getSentences();
		for (Sentence sentence : sentences) {
			String[] perm = sentence.getTokenArray();
			double[] postprob = maxentModel.eval(perm);
			int stateIndex = maxentModel.getIndex("positive");
			int predict = -1;
			// max probability
			double posLikelihood = postprob[stateIndex];
			if (posLikelihood >= 0.5) {
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
