package edu.nyu.cs.ranking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.nyu.cs.pub.TextTool;

/**
 * Manage file resources, including input and output
 * 
 * @author Zhe Wu N16445442 zw339@nyu.edu
 * 
 */
public class FileManager {

	private static List<String> stopList = edu.nyu.cs.snt.FileManager
			.readStopList("resources/english.stop");
	
	private List<String> negwords = Arrays.asList(new String[] { "don't",
			"doesn't", "didn't", "not", "hasn't", "haven't", "hadn't", "won't",
			"wouldn't", "can't", "cannot", "couldn't", "shalln't", "shouldn't",
			"isn't", "wasn't", "aren't", "weren't", "never", "no" });
	
	private List<String> punctuations = Arrays.asList(new String[] { ".", ",",
			"!", "?", ";", ":", "(", ")", "\"", "\'","--" });

	public FileManager() {
	}

	public List<ReviewMetric> read(File file) throws Exception {
		List<ReviewMetric> res = new ArrayList<ReviewMetric>();
		File[] subdirs = file.listFiles();
		for (File subdir : subdirs) {
			res.addAll(getFromFile(subdir));
		}
		return res;
	}

	private List<ReviewMetric> getFromFile(File file) throws Exception {
		List<ReviewMetric> res = new ArrayList<ReviewMetric>();
		File id = getId(file);
		File label3 = getLabel3(file);
		File label4 = getLabel4(file);
		File subj = getSubj(file);
		// parse Processs from input file
		BufferedReader idReader = getReader(id);
		BufferedReader label3Reader = getReader(label3);
		BufferedReader label4Reader = getReader(label4);
		BufferedReader subjReader = getReader(subj);
		String strId, strLabel3, strLabel4, strSubj;
		while (((strId = idReader.readLine()) != null)
				&& ((strLabel3 = label3Reader.readLine()) != null)
				&& ((strLabel4 = label4Reader.readLine()) != null)
				&& ((strSubj = subjReader.readLine()) != null)) {
			Map<String, Integer> matrix = createWordMatrix(strSubj);
			ReviewMetric rm = new ReviewMetric(strId, strLabel3, strLabel4,
					matrix);
			res.add(rm);
		}
		idReader.close();
		label3Reader.close();
		label4Reader.close();
		subjReader.close();
		return res;
	}

	private Map<String, Integer> createWordMatrix(String subj) {
		Map<String, Integer> matrix = new HashMap<String, Integer>();
		String[] strs = negativeWords(subj);
		for (String str : strs) {
			str = str.toUpperCase();
			if (!filterStopWord(str)) {
				Integer count = matrix.get(str);
				if (count == null) {
					count = 0;
				}
				matrix.put(str, ++count);
			}
		}
		return matrix;
	}
	
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
				strs[i] = ReviewMetric.negPrefix + strs[i];
			}
		}
		return strs;
	}

	private boolean filterStopWord(String str) {
		return TextTool.isEmpty(str) || stopList.contains(str.toUpperCase())
				|| !str.matches("[A-Za-z]+.*");
	}

	private BufferedReader getReader(File file) throws FileNotFoundException {
		FileInputStream fstream = new FileInputStream(file);
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		return br;
	}

	private File getSubj(File file) {
		return getWithPrefix(file, "subj");
	}

	private File getLabel4(File file) {
		return getWithPrefix(file, "label.4");
	}

	private File getLabel3(File file) {
		return getWithPrefix(file, "label.3");
	}

	private File getId(File file) {
		return getWithPrefix(file, "id");
	}

	private File getWithPrefix(File file, final String prefix) {
		File[] files = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (!TextTool.isEmpty(name) && name.startsWith(prefix)) {
					return true;
				}
				return false;
			}
		});
		return files[0];

	}

	public void writeMatrix3(String modelFile, List<ReviewMetric> rmList)
			throws IOException {
		FileOutputStream fstream = new FileOutputStream(modelFile);
		// Get the object of DataInputStream
		DataOutputStream in = new DataOutputStream(fstream);
		BufferedWriter br = new BufferedWriter(new OutputStreamWriter(in));
		for (ReviewMetric feature : rmList) {
			br.append(feature.toLabel3() + "\n");
		}
		br.flush();
		br.close();
	}

	public void writeMatrix4(String modelFile, List<ReviewMetric> rmList)
			throws IOException {
		FileOutputStream fstream = new FileOutputStream(modelFile);
		// Get the object of DataInputStream
		DataOutputStream in = new DataOutputStream(fstream);
		BufferedWriter br = new BufferedWriter(new OutputStreamWriter(in));
		for (ReviewMetric feature : rmList) {
			br.append(feature.toLabel4() + "\n");
		}
		br.flush();
		br.close();
	}
}
