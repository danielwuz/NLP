package edu.nyu.cs.snt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.nyu.cs.pub.Corpus;

public abstract class AbstractClassifier {

	protected List<Folder> folders = null;
	
	protected int cutoff = 4;

	public AbstractClassifier(List<Folder> folders) {
		this.folders = folders;
	}

	protected Set<String> mergeVocabulary(Corpus[] trainingData) {
		Set<String> voc = new HashSet<String>();
		voc.addAll(trainingData[0].getVocabulary());
		voc.addAll(trainingData[1].getVocabulary());
		return voc;
	}

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
