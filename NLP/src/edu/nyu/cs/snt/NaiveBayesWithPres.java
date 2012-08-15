package edu.nyu.cs.snt;

import java.util.List;

public class NaiveBayesWithPres extends NaiveBayes {

	public NaiveBayesWithPres(List<Folder> folders) {
		super(folders);
		binarization();
	}

}
