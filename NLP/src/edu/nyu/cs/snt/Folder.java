package edu.nyu.cs.snt;

import java.util.Arrays;
import java.util.List;

import edu.nyu.cs.pub.Corpus;

/**
 * Folder that contains two corpus under given folder number.
 * <p>
 * Each folder contains a positive corpus, which all the comments are positive
 * attitude, and a negative corpus, which all the comments are negative
 * attitude. <br/>
 * Each folder has a unique id.
 * 
 * @author Daniel Wu
 * 
 */
public class Folder {

	private int id = -1;

	private Corpus posCorpus = null;
	private Corpus negCorpus = null;

	/**
	 * Class constructor with given id and two corpuses
	 * 
	 * @param id
	 *            folder id
	 * @param positiveCorpus
	 *            positive corpus
	 * @param negativeCorpus
	 *            negative corpus
	 */
	public Folder(int id, Corpus positiveCorpus, Corpus negativeCorpus) {
		this.id = id;
		this.posCorpus = positiveCorpus;
		this.negCorpus = negativeCorpus;
	}

	/**
	 * Returns both positive corpus and negative corpus
	 * 
	 * @return list of corpus. First element in list is positive corpus, while
	 *         second element is negative one.
	 */
	public List<Corpus> getAllCorpus() {
		return Arrays.asList(new Corpus[] { posCorpus, negCorpus });
	}

	/**
	 * Returns positive corpus, where comments are positive attitude.
	 * 
	 * @return positive corpus
	 */
	public Corpus getPosCorpus() {
		return posCorpus;
	}

	/**
	 * Returns negative corpus, where comments are negative attitude.
	 * 
	 * @return negative corpus
	 */
	public Corpus getNegCorpus() {
		return negCorpus;
	}

	/**
	 * Returns folder id
	 * 
	 * @return folder id
	 */
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Folder [id=" + id + "]";
	}

}
