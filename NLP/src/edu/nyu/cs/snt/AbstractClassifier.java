package edu.nyu.cs.snt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.nyu.cs.pub.Corpus;

/**
 * Abstract classification class.
 * <p>
 * Provide common opperation for MaxEnt classifier and Naive Bayes classifier.
 * 
 * @author Daniel Wu
 * 
 */
public abstract class AbstractClassifier {

	// folder sets
	protected List<Folder> folders = null;

	// cutoff threshold
	protected int cutoff = 4;

	/**
	 * Class constructor with given folders
	 * 
	 * @param folders
	 */
	public AbstractClassifier(List<Folder> folders) {
		this.folders = folders;
	}

	/**
	 * Merge vocabularies from corpus into one
	 * 
	 * @param trainingData
	 *            sets of corpus
	 * @return set of words merged from given corpus
	 */
	protected Set<String> mergeVocabulary(Corpus[] trainingData) {
		Set<String> voc = new HashSet<String>();
		voc.addAll(trainingData[0].getVocabulary());
		voc.addAll(trainingData[1].getVocabulary());
		return voc;
	}

	/**
	 * Binaries current data in corpus. Each word will appear only once after
	 * binarization.
	 */
	protected void binarization() {
		List<Folder> res = new ArrayList<Folder>();
		for (Folder folder : folders) {
			int id = folder.getId();
			Corpus posCorpus = folder.getPosCorpus();
			posCorpus = posCorpus.binarization();
			Corpus negCorpus = folder.getNegCorpus();
			negCorpus = negCorpus.binarization();
			res.add(new Folder(id, posCorpus, negCorpus));
		}
		this.folders = res;
	}

}
