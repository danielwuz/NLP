package edu.nyu.cs.pub;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.nyu.cs.pub.Token.TokenBuilder;
import edu.nyu.cs.pub.Token.TokenDecor;

/**
 * Manage file resources
 * 
 * @author Daniel
 * 
 */
public class FileManager implements IFileReader {

	// singleton
	public static FileManager instance = new FileManager();

	private FileManager() {
	}

	/**
	 * Read in sentences from input corpus
	 * 
	 * @param file
	 *            Input corpus. If input file is directory, then recursively
	 *            read files within directory
	 * @return List of sentences
	 * @throws Exception
	 *             throws exception if error occurs when opening input corpus
	 */
	public List<Sentence> read(File file) throws IOException {
		if (file.isDirectory()) {
			return FileManager.instance.getFromDir(file);
		} else if (file.isFile()) {
			return FileManager.instance.getFromFile(file);
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
		Sentence sentence = null;
		int index = 0;
		int count = -1;
		do {
			count++;
			if (strLine.trim().equals("")) {
				sentence = new Sentence();
				res.add(sentence);
				index = 0;
			} else {
				String[] strs = strLine.split("[\t\n ]");
				TokenBuilder builder = new TokenBuilder(strs);
				builder.setIndex(index++);
				sentence.addToken(builder.build());
			}
		} while ((strLine = br.readLine()) != null);
		System.out.println("File " + file.getPath() + " has " + count
				+ " lines\n");
		int totalSent = 0;
		for (Sentence sentence2 : res) {
			totalSent += sentence2.length();
		}
		System.out.println("File " + file.getPath() + " has " + res.size()
				+ " sentences\n");
		System.out.println("File " + file.getPath() + " has " + totalSent
				+ " tokens\n");
		closeBuffer(br);
		return res;
	}

	private void closeBuffer(Closeable br) {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Output to test file<br/>
	 * words seperated by TAB
	 * 
	 * @param file
	 *            output file
	 * @param sentences
	 *            list of sentences
	 * @throws IOException
	 */
	public void writeFileForTest(File file, List<Sentence> sentences)
			throws IOException {
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
		closeBuffer(br);
	}

	/**
	 * Output to file<br/>
	 * words seperated by SPACE
	 * 
	 * @param file
	 *            output file
	 * @param sentences
	 *            list of sentences
	 * @throws IOException
	 */
	public void writeFile(File file, List<Sentence> sentences)
			throws IOException {
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
		closeBuffer(br);
	}

	/**
	 * Output extracted features to file<br/>
	 * Those features will be used by OpenNLP/MaxEnt package
	 * 
	 * @param modelFile
	 *            model file which contains all the features
	 * @param features
	 *            features extracted from training corpus
	 * @throws IOException
	 */
	public static <T> void writeFeatures(String modelFile,
			Collection<T> features) throws IOException {
		FileOutputStream fstream = new FileOutputStream(modelFile);
		// Get the object of DataInputStream
		DataOutputStream in = new DataOutputStream(fstream);
		BufferedWriter br = new BufferedWriter(new OutputStreamWriter(in));
		for (T feature : features) {
			br.append(feature + "\n");
		}
		br.flush();
	}
}
