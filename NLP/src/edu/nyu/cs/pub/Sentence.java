package edu.nyu.cs.pub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sentence Model
 * 
 * @author Daniel Wu
 * 
 */
public class Sentence implements Cloneable {

	// constant indicating positive attitude
	public static final int POSITIVE = 111;

	// constant indicating negative attitude
	public static final int NEGATIVE = 211;

	private int polarity = -1;

	// tokens which form the sentence
	private List<Token> tokens = null;

	public Sentence() {
		this.tokens = new ArrayList<Token>();
	}

	/**
	 * Append a token to sentence
	 * 
	 * @param token
	 * @return current sentence
	 */
	public Sentence addToken(Token token) {
		tokens.add(token);
		return this;
	}

	/**
	 * @return tokens as an array
	 */
	public String[] getTokenArray() {
		List<String> res = new ArrayList<String>();
		for (Token token : tokens) {
			res.add(token.getLiteral().toUpperCase());
		}
		return res.toArray(new String[0]);
	}

	/**
	 * @return tokens as a list
	 */
	public List<Token> getTokens() {
		return tokens;
	}

	/**
	 * @return true if current sentence contains nothing
	 */
	public boolean isEmpty() {
		return this.tokens.isEmpty();
	}

	@Override
	public String toString() {
		return "Sentence [tokens=" + tokens + "]";
	}

	/**
	 * return and remove first token in sentence
	 * 
	 * @return first token in sentence
	 */
	public Token popNextToken() {
		return tokens.remove(0);
	}

	public Sentence clone() {
		Sentence s = new Sentence();
		for (Token token : tokens) {
			s.addToken(token.clone());
		}
		return s;
	}

	/**
	 * remove last token from token list
	 */
	public void removeLast() {
		if (!this.isEmpty()) {
			int length = tokens.size();
			this.tokens.remove(length - 1);
		}
	}

	/**
	 * @return sentence length
	 */
	public int length() {
		return this.tokens.size();
	}

	/**
	 * sort words by input order
	 */
	public void sort() {
		Collections.sort(tokens);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sentence other = (Sentence) obj;
		if (tokens == null) {
			if (other.tokens != null)
				return false;
		} else if (!tokens.equals(other.tokens))
			return false;
		return true;
	}

	public int getIndex() {
		if (!this.isEmpty()) {
			String sentIndex = this.getTokens().get(0).getSentIndex();
			return TextTool.isEmpty(sentIndex) ? -1 : Integer
					.parseInt(sentIndex);
		}
		return -1;
	}

	public int getPolarity() {
		return polarity;
	}

	public void setPolarity(int polarity) {
		this.polarity = polarity;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	/**
	 * Unify current sentence<br/>
	 * Each word only appears once
	 */
	public void unification() {
		Set<Token> res = new HashSet<Token>();
		for (Token token : tokens) {
			res.add(token);
		}
		List<Token> tmp = new ArrayList<Token>();
		tmp.addAll(res);
		this.tokens = tmp;
	}

	/**
	 * Filter out stopping words
	 * 
	 * @param filter
	 *            stopping words
	 */
	public void filter(Set<String> filter) {
		List<Token> res = new ArrayList<Token>();
		for (Token token : tokens) {
			if (filter.contains(token.getLiteral().toUpperCase())) {
				res.add(token);
			}
		}
		this.tokens = res;
	}

}
