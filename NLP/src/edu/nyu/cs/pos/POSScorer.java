package edu.nyu.cs.pos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * A scorer for POS tagging. It reports overall tagging accuracy.
 * 
 * @author Ang Sun (Feb. 2012)
 * @version 1.0
 * 
 */
public class POSScorer {
	static int numCorrect = 0;
	static int numWrong = 0;

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err
					.println("RegexScorer requires 2 arguments: [SystemOutputFile] [KeyFile]");
			return;
		}
		File sysFile = new File(args[0]);
		File keyFile = new File(args[1]);
		score(sysFile, keyFile);
	}

	private static void score(File systemFile, File keyFile) throws IOException {
		BufferedReader readSys = new BufferedReader(new FileReader(systemFile));
		BufferedReader readKey = new BufferedReader(new FileReader(keyFile));
		String sline;
		String kline;

		while ((sline = readSys.readLine()) != null
				&& (kline = readKey.readLine()) != null) {
			if ((!sline.isEmpty()) && (!kline.isEmpty())) {
				String sysPOS = sline.substring(sline.indexOf("\t") + 1);
				String keyPOS = kline.substring(kline.indexOf("\t") + 1);
				if (sysPOS.equals(keyPOS)) {
					numCorrect++;
				} else {
					numWrong++;
				}
			}
		}
		System.out.println("Correct tagged tokens:   " + numCorrect);
		System.out.println("Incorrect tagged tokens: " + numWrong);
		int accuracy = (int) 100.0 * numCorrect / (numCorrect + numWrong);
		System.out.println("Accuracy is:             " + accuracy + "%");
	}
}
