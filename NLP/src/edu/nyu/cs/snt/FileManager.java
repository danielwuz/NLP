package edu.nyu.cs.snt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.nyu.cs.pub.IFileReader;
import edu.nyu.cs.pub.Sentence;
import edu.nyu.cs.pub.TextTool;
import edu.nyu.cs.pub.Token;
import edu.nyu.cs.pub.Token.TokenBuilder;
import edu.nyu.cs.pub.Token.TokenDecor;

/**
 * Manage file resources, including input and output
 * 
 * @author Zhe Wu N16445442 zw339@nyu.edu
 * 
 */
public class FileManager implements IFileReader {

	private String negPrefix = "NOT_";

	private List<String> negwords = Arrays.asList(new String[] { "don't",
			"doesn't", "didn't", "not", "hasn't", "haven't", "hadn't", "won't",
			"wouldn't", "can't", "cannot", "couldn't", "shalln't", "shouldn't",
			"isn't", "wasn't", "aren't", "weren't", "never", "no" });

	private List<String> punctuations = Arrays.asList(new String[] { ".", ",",
			"!", "?", ";", ":", "(", ")", "\"", "\'" });

	private static List<String> stopList = FileManager
			.readStopList("resources/english.stop");

	public FileManager() {
	}

	/**
	 * Read in sentences from input corpus
	 * 
	 * @param file
	 *            Input corpus. If input file is directory, then recursively
	 *            read files within directory
	 * @return List of sentences
	 * @throws IOException
	 *             throws exception if error occurs when opening input corpus
	 */
	public List<Sentence> read(File file) throws IOException {
		if (file.isDirectory()) {
			return this.getFromDir(file);
		} else if (file.isFile()) {
			return this.getFromFile(file);
		} else {
			throw new IOException(
					"Cannot read files, not valid file or directory path\n");
		}
	}

	private List<Sentence> getFromDir(File file) throws IOException {
		List<Sentence> res = new ArrayList<Sentence>();
		File[] files = file.listFiles();
		for (File sub : files) {
			res.addAll(read(sub));
		}
		return res;
	}

	private List<Sentence> getFromFile(File file) throws IOException {
		List<Sentence> res = new ArrayList<Sentence>();
		// parse Processs from input file
		FileInputStream fstream = new FileInputStream(file);
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine = "";
		Sentence sentence = new Sentence();
		int count = 0;
		while ((strLine = br.readLine()) != null) {
			String[] strs = negativeWords(strLine);
			for (String str : strs) {
				if (!filterStopWord(str)) {
					count++;
					TokenBuilder builder = new TokenBuilder(
							new String[] { str });
					sentence.addToken(builder.build());
				}
			}
		}
		System.out.println("File " + file.getPath() + " has " + count
				+ " words\n");
		res.add(sentence);
		return res;
	}

	private boolean filterStopWord(String str) {
		return TextTool.isEmpty(str) || stopList.contains(str.toUpperCase())
				|| punctuations.contains(str);
	}

	/**
	 * Add negative prefix between words in negative word and next punctuation
	 * 
	 * @param strLine
	 * @return
	 */
	private String[] negativeWords(String strLine) {
		String[] strs = strLine.split("\\s");
		boolean addPrefix = false;
		for (int i = 0; i < strs.length; i++) {
			if (negwords.contains(strs[i])) {
				addPrefix = true;
			}
			if (punctuations.contains(strs[i])) {
				addPrefix = false;
			}
			if (addPrefix && !filterStopWord(strs[i])) {
				strs[i] = negPrefix + strs[i];
			}
		}
		return strs;
	}

	public static void writeFileForTest(File file, List<Sentence> sentences)
			throws Exception {
		FileOutputStream fstream = new FileOutputStream(file);
		// Get the object of DataInputStream
		DataOutputStream in = new DataOutputStream(fstream);
		BufferedWriter br = new BufferedWriter(new OutputStreamWriter(in));
		for (Sentence sentence : sentences) {
			List<Token> tokens = sentence.getTokens();
			for (Token token : tokens) {
				br.append(new TokenDecor(token, TokenDecor.TAB).toString());
				br.append("\n");
			}
			br.append("\n");
		}
		br.flush();
	}

	public static void writeFile(File file, List<Sentence> sentences)
			throws Exception {
		FileOutputStream fstream = new FileOutputStream(file);
		// Get the object of DataInputStream
		DataOutputStream in = new DataOutputStream(fstream);
		BufferedWriter br = new BufferedWriter(new OutputStreamWriter(in));
		for (Sentence sentence : sentences) {
			List<Token> tokens = sentence.getTokens();
			for (Token token : tokens) {
				br.append(new TokenDecor(token, TokenDecor.BLANK).toString());
				br.append("\n");
			}
			br.append("\n");
		}
		br.flush();
	}

	public static void writeFeatures(String modelFile, List<String[]> features)
			throws Exception {
		FileOutputStream fstream = new FileOutputStream(modelFile);
		// Get the object of DataInputStream
		DataOutputStream in = new DataOutputStream(fstream);
		BufferedWriter br = new BufferedWriter(new OutputStreamWriter(in));
		for (String[] feature : features) {
			br.append(feature[0] + " " + feature[1] + "\n");
		}
		br.flush();
	}

	public static List<String> readStopList(String path) {
		try {
			StringBuilder contents = new StringBuilder();
			BufferedReader input = new BufferedReader(new FileReader(new File(
					path)));
			for (String line = input.readLine(); line != null; line = input
					.readLine()) {
				contents.append(line);
				contents.append("\n");
			}
			input.close();
			return segmentWords(contents.toString());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	private static List<String> segmentWords(String s) {
		List<String> ret = new ArrayList<String>();

		for (String word : s.split("\\s")) {
			if (word.length() > 0) {
				ret.add(word.toUpperCase());
			}
		}
		return ret;
	}
}
