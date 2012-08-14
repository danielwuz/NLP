package edu.nyu.cs.pub;

/**
 * Constants
 * 
 * @author Daniel Wu
 * 
 */
public interface Constant {

	// Used when computing bigram
	static final String START_SYMBOL = "<S>";

	// Used when computing bigram
	static final String END_SYMBOL = "</S>";

	// Assumed minimum probability of a word
	static final double MIN_VALUE = 1.0e-200;

}
