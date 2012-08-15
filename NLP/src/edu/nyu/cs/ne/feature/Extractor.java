package edu.nyu.cs.ne.feature;

import java.util.Set;

import edu.nyu.cs.pub.Sentence;

/**
 * Extract feature from training corpus
 * 
 * @author Daniel Wu
 * 
 */
public interface Extractor<T> {

	/**
	 * Extract features from sentence. <br/>
	 * A feature contains context information of a word within given sentence.
	 * 
	 * @param sentence
	 *            sentence in corpus
	 * @return set of features, to be used to create a maximum entropy model
	 * @see RelFeature
	 * @see TagFeature
	 */
	Set<T> extract(Sentence sentence);
}
