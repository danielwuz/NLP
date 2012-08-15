package edu.nyu.cs.snt;

import java.util.List;

/**
 * Naive Bayes classifier with binarization. Each word will only be counted once
 * in this case.
 * 
 * @author Daniel Wu
 * 
 */
public class NaiveBayesWithPres extends NaiveBayes {

	/**
	 * Class constructor with input corpus. Each word will only be counted once.
	 * 
	 * @param folders
	 *            corpus
	 */
	public NaiveBayesWithPres(List<Folder> folders) {
		super(folders);
		binarization();
	}

}
