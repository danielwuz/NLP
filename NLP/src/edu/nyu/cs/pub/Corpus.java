package edu.nyu.cs.pub;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Corpus Model
 * 
 * @author Daniel Wu
 * 
 */
public class Corpus implements Iterable<Sentence> {

	// sentences from input corpus
	private List<Sentence> sentences = null;

	// distinct words
	private Set<String> vocabulary = null;

	private IFileReader fileManager = null;

	/**
	 * Constructing a corpus
	 * 
	 * @param filePath
	 *            corpus path
	 * @param fileManager
	 *            file IO
	 * @throws IOException
	 */
	public Corpus(String filePath, IFileReader fileManager) throws IOException {
		this.fileManager = fileManager;
		sentences = loadFile(filePath);
		vocabulary = new HashSet<String>();
		// preprocess
		this.preprocess();
	}

	/**
	 * Constructing set of corpora
	 * 
	 * @param corpora
	 *            list of corpus
	 */
	public Corpus(List<Corpus> corpora) {
		sentences = new ArrayList<Sentence>();
		for (Corpus corpus : corpora) {
			this.sentences.addAll(corpus.getSentences());
		}
		vocabulary = new HashSet<String>();
		// preprocess
		this.preprocess();
	}

	/**
	 * Binarize corpus<br/>
	 * Counting each word only once
	 * 
	 * @return corpus after binarization
	 */
	public Corpus binarization() {
		for (Sentence sent : sentences) {
			sent.unification();
		}
		return this;
	}

	/**
	 * Load training corpus into memory
	 * 
	 * @param filePath
	 *            corpus file path
	 * @return content of that corpus in string format
	 * @throws Exception
	 *             IOException
	 */
	private List<Sentence> loadFile(String filePath) throws IOException {
		return fileManager.read(new File(filePath));
	}

	private void preprocess() {
		for (Sentence sentence : sentences) {
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (Token token : sentence.getTokens()) {
				// this is the text of the token
				String word = token.getLiteral();
				// calculate vocabulary size
				vocabulary.add(word.toLowerCase());
			}
		}
	}

	/**
	 * @return vocabulary size
	 */
	public Integer vocabularySize() {
		return vocabulary.size();
	}

	/**
	 * @return sentences from input corpus
	 */
	public List<Sentence> getSentences() {
		return sentences;
	}

	public Set<String> getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(Set<String> vocabulary) {
		this.vocabulary = vocabulary;
	}

	/**
	 * Check if current corpus contains word
	 * 
	 * @param word
	 *            any word
	 * @return true if current corpus contains word
	 */
	public boolean containsWord(String word) {
		if (word == null || word.trim().length() == 0)
			return false;
		return vocabulary.contains(word.toLowerCase());
	}

	@Override
	public Iterator<Sentence> iterator() {
		return new CorpusIterator(sentences);
	}

	/**
	 * Corpus iterator
	 * 
	 * @author Daniel Wu
	 * 
	 */
	public static class CorpusIterator implements Iterator<Sentence> {

		private int index = 0;

		private List<Sentence> sentences = null;

		private CorpusIterator(List<Sentence> sentences) {
			this.sentences = sentences;
		}

		@Override
		public boolean hasNext() {
			return index < sentences.size();
		}

		@Override
		public Sentence next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			Sentence res = sentences.get(index);
			index++;
			return res;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * Filter set of stopping words
	 * 
	 * @param res
	 *            stopping words
	 * @return corpus after filtering
	 */
	public Corpus filter(Set<String> res) {
		for (Sentence sent : sentences) {
			sent.filter(res);
		}
		return this;
	}

}
