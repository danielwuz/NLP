package edu.nyu.cs.ne.feature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.nyu.cs.pub.Corpus;
import edu.nyu.cs.pub.Sentence;

/**
 * Feature Extractor Bridge
 * 
 * @author Daniel Wu
 * 
 * @param <T>
 *            feature model.
 * @see RelFeature
 * @see TagFeature
 */
public class SimpleExtractor<T> {

	private Extractor<T> tagExtractor = null;

	public SimpleExtractor(Extractor<T> extractor) {
		this.tagExtractor = extractor;
	}

	/**
	 * Extracting features from corpus.
	 * 
	 * @param corpus 
	 * @return list of features
	 */
	public List<T> extractFeature(Corpus corpus) {
		List<T> featureSet = new ArrayList<T>();
		Iterator<Sentence> sents = corpus.iterator();
		while (sents.hasNext()) {
			Sentence sent = sents.next();
			if (!sent.isEmpty()) {
				System.out.println("Extracting features from sentence "
						+ sent.getIndex());
				Set<T> features = tagExtractor.extract(sent);
				featureSet.addAll(features);
			}
		}
		return featureSet;
	}

}
