package edu.nyu.cs.pub;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.nyu.cs.pos.EmissionMatrix;
import edu.nyu.cs.pos.TransitionMatrix;

/**
 * Probability Model
 * 
 * @author Daniel Wu
 * 
 */
public abstract class AbstractModel {

	protected Corpus corpus = null;

	// prior probability matrix
	protected Matrix priorMatrix = null;

	// likelihood matrix
	protected Matrix likelihoodMatrix = null;

	// if debug, then output pos tag
	private boolean debug = true;

	/**
	 * Load corpus from file system
	 * 
	 * @param filePath
	 *            corpus path
	 * @throws IOException
	 * 
	 */
	public void loadCorpus(String filePath) throws IOException {
		corpus = new Corpus(filePath, FileManager.instance);
	}

	/**
	 * @return decoder
	 */
	protected abstract Decoder getDecoder();

	/**
	 * Part-of-Speech tagging, based on training corpus
	 * 
	 * @param test
	 *            words of sentence
	 * @return corresponding tags
	 * @throws Exception
	 *             throws exception when file IO exception occurs
	 */
	public List<Sentence> tag(String filePath) throws Exception {
		Decoder decoder = getDecoder();
		// viterbi algorithm decoding
		Corpus testCorpus = new Corpus(filePath, FileManager.instance);
		List<Sentence> sentences = testCorpus.getSentences();
		List<Sentence> res = new ArrayList<Sentence>();
		for (Sentence sentence : sentences) {
			if (!sentence.isEmpty()) {
				sentence = decoder.decode(sentence);
			}
			res.add(sentence);
		}
		if (debug) {
			output(filePath, res);
		}
		return res;
	}

	private void output(String outputPath, List<Sentence> res) {
		for (Sentence sentence : res) {
			sentence.sort();
		}
		File output = new File(outputPath + ".out");
		try {
			FileManager.instance.writeFile(output, res);
		} catch (Exception e) {
			e.printStackTrace();
			System.out
					.println("Exception occurs when writing output into file ");
		}
	}

	/**
	 * Model training<br/>
	 * Compute probability matrices
	 * 
	 * @param filePath
	 *            training corpus path
	 * @throws IOException
	 *             IOException
	 */
	public void train(String filePath) throws IOException {
		// debug Load corpus into memory
		loadCorpus(filePath);
		priorMatrix = new TransitionMatrix(corpus);
		likelihoodMatrix = new EmissionMatrix(corpus);
	}
}
