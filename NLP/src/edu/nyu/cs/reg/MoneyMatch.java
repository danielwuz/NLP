package edu.nyu.cs.reg;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Recognize dollar amounts using regular Expressions
 * 
 * @author Daniel Wu
 * 
 */
public class MoneyMatch {

	private String inputFilePath = "";

	private String outputFilePath = ".";

	// regular expression for alphabetic numbers
	private static String numbers = "(\\d|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty-?|thirty-?|forty-?|fifty-?|sixty-?|seventy-?|eighty-?|ninety-?)";

	// regular expression for numbers
	private static String numExp = "((" + numbers + ")|((" + numbers + "|,)+"
			+ numbers + "+))(\\.\\d+)?";

	// regular expression for quantifiers
	private static String quanExp = "(hundred|thousand|million|billion|trillion|quadrillion|and| )*";

	// Match strings which end with 'dollar' or 'dollars'
	private static String regexp1 = "(\\$?" + numExp + quanExp + ")+ dollars?";

	// Match strings which start with $ symbol
	private static String regexp2 = "\\$(" + numExp + quanExp + ")+";

	// Match strings containing ``half''
	private static String regexp3 = "(half )?a (half-?)?" + quanExp
			+ "dollars?";

	private static String regexp = "(" + regexp1 + ")|(" + regexp2 + ")|("
			+ regexp3 + ")";

	private static StringBuffer stat = new StringBuffer();

	// matched times
	private int count = 0;

	public MoneyMatch(String[] args) throws Exception {
		this.inputFilePath = args[0];
		if (args.length > 1) {
			this.outputFilePath = args[1];
		}
	}

	private void run() throws Exception {
		List<File> fileList = read(new File(inputFilePath));
		for (File file : fileList) {
			String content = processFile(file);
			writeFile(file, content);
		}
		stat.append("Total count: " + count);
	}

	/**
	 * Output results to file system
	 * 
	 * @param file
	 *            output file path
	 * @param content
	 *            Input with brackets around money expression
	 * @throws IOException
	 */
	private void writeFile(File file, String content) throws IOException {
		new File(outputFilePath + File.separator).mkdirs();
		String fileName = outputFilePath + File.separator + file.getName()
				+ ".out";
		FileWriter writer = new FileWriter(new File(fileName));
		writer.append(content);
		writer.close();
	}

	/**
	 * Match money expression line by line
	 * 
	 * @param content
	 *            corpus
	 * @param line
	 *            current line number
	 * @return content with brackets around money expression
	 */
	public String match(String content, int line) {
		// ignore if current line do not have key words: $,'dollar' or 'dollars'
		if (!Pattern.matches(".*(\\$|dollars?).*", content)) {
			return content;
		}
		// compile regular expression
		Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			// statistic data
			count++;
			String mat = matcher.group(0);
			String rep = "";
			// insert brackets around money expression
			if (mat.endsWith(" ")) {
				mat = mat.trim();
				rep = "[\\" + mat + "] ";
			} else {
				rep = "[\\" + mat.trim() + "]";
			}
			stat.append(" Symbol: " + mat + " Line: " + line + "\n");
			matcher.appendReplacement(sb, rep);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Read in corpus; <br/>
	 * If corpus is given as a directory, then recursively read in all files
	 * under that path
	 * 
	 * @param file
	 *            input corpus path
	 * @return List of files
	 * @throws IOException
	 * 
	 */
	private List<File> read(File file) throws IOException {
		List<File> res = new ArrayList<File>();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File dir : files) {
				res.addAll(read(dir));
			}
			return res;
		} else if (file.isFile()) {
			res.add(file);
			return res;
		} else {
			throw new IOException(
					"Cannot read files, not valid file or directory path\n");
		}
	}

	/**
	 * Search money expression against files
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private String processFile(File file) throws IOException {
		stat.append("\n\nFile: " + file.getPath() + "\n");
		StringBuffer originalFile = new StringBuffer();
		// parse Processs from input file
		FileInputStream fstream = new FileInputStream(file);
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		int line = 0;
		while ((strLine = br.readLine()) != null) {
			// match here
			strLine = match(strLine, line++);
			originalFile.append(strLine + "\r\n");
		}
		in.close();
		return originalFile.toString();
	}

	/**
	 * @param args
	 *            args[0]: input corpus, args[1]: output path<br/>
	 *            If args[1] missing, then use current directory as output path
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			throw new Exception("Please specify input corpus\n");
		}
		MoneyMatch match = new MoneyMatch(args);
		match.run();
		System.out.println(stat);
	}
}
