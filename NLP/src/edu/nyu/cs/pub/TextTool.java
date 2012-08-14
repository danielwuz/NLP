package edu.nyu.cs.pub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility for processing text
 * 
 * @author Daniel Wu
 * 
 */
public class TextTool {

	/**
	 * Convert unigram text to bigram mode
	 * 
	 * @param text
	 *            unigram text
	 * @return bigram text
	 */
	public static List<String[]> bigram(String[] text) {
		List<String[]> bigram = new ArrayList<String[]>();
		for (int i = 0; i < text.length - 1; i++) {
			String word_n_1 = text[i];
			String word_n = text[i + 1];
			String[] biStr = new String[] { word_n_1, word_n };
			bigram.add(biStr);
		}
		return bigram;
	}

	/**
	 * Convert unigram text to bigram mode, with start symbol and end symbol
	 * added
	 * 
	 * @param text
	 *            unigram text
	 * @return bigram text
	 * @see edu.nyu.cs.pub.Constant
	 */
	public static List<String[]> bigramWithSymbols(String[] text) {
		List<String> withSymbol = TextTool.appendSymbols(text);
		return TextTool.bigram(withSymbol.toArray(new String[0]));
	}

	/**
	 * Append dummy symbols
	 * 
	 * @param input
	 *            unigram text
	 * @return unigram text with start symbol and end symbol
	 * @see edu.nyu.cs.pub.Constant
	 */
	public static List<String> appendSymbols(String[] input) {
		List<String> res = new ArrayList<String>();
		res.add(Constant.START_SYMBOL);
		res.addAll(Arrays.asList(input));
		res.add(Constant.END_SYMBOL);
		return res;
	}

	/**
	 * Convert word to First-letter-upper-case
	 * 
	 * @param word
	 * @return word with first letter upper case
	 */
	public static String firstLetterUpperCase(String word) {
		final StringBuilder result = new StringBuilder(word.length());
		result.append(Character.toUpperCase(word.charAt(0))).append(
				word.substring(1));
		return result.toString();
	}

	/**
	 * @param s
	 *            any word
	 * @return true if given word starts with capital letter
	 */
	public static boolean startsWithCapital(String s) {
		return Pattern.compile("^[A-Z]").matcher(s).find();
	}

	/**
	 * @param goal
	 *            any string
	 * @return true if string is empty
	 */
	public static boolean isEmpty(String goal) {
		return goal == null || goal.trim().equals("");
	}
}
