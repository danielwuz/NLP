package edu.nyu.cs.snt;

import java.util.Arrays;
import java.util.List;

import edu.nyu.cs.pub.Corpus;

public class Folder {

	private int id = -1;

	private Corpus posCorpus = null;
	private Corpus negCorpus = null;

	public Folder(int id, Corpus positiveCorpus, Corpus negativeCorpus) {
		this.id = id;
		this.posCorpus = positiveCorpus;
		this.negCorpus = negativeCorpus;
	}

	public List<Corpus> getAllCorpus() {
		return Arrays.asList(new Corpus[] { posCorpus, negCorpus });
	}

	public Corpus getPosCorpus() {
		return posCorpus;
	}

	public Corpus getNegCorpus() {
		return negCorpus;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Folder [id=" + id + "]";
	}

}
