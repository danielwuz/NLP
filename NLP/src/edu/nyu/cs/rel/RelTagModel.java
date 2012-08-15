package edu.nyu.cs.rel;

import java.io.IOException;
import java.util.List;

import edu.nyu.cs.ne.feature.RelExtractor;
import edu.nyu.cs.ne.feature.RelFeature;
import edu.nyu.cs.ne.feature.SimpleExtractor;
import edu.nyu.cs.pub.AbstractModel;
import edu.nyu.cs.pub.Decoder;
import edu.nyu.cs.pub.FileManager;

/**
 * Data model for relation tagging task
 * 
 * @author Daniel Wu
 * 
 */
public class RelTagModel extends AbstractModel {

	private String modelFile = "relFeatures.dat";

	@Override
	public void train(String filePath) throws IOException {
		// debug Load corpus into memory
		loadCorpus(filePath);
		SimpleExtractor<RelFeature> extractor = new SimpleExtractor<RelFeature>(
				new RelExtractor());
		List<RelFeature> features = extractor.extractFeature(corpus);
		FileManager.writeFeatures(modelFile, features);
	}

	@Override
	protected Decoder getDecoder() {
		try {
			return new RelTagDecoder(modelFile);
		} catch (Exception e) {
			throw new RuntimeException("Maxent Markov Model Decoder failed!\n",
					e);
		}
	}

	/**
	 * Execute command: 1. Training & Test java HiddenMarkovModel -t
	 * training.pos -d test.text
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String instruction = "Please specify args\n Typical usage: java -jar RelTag.jar -t %-training -d %-dev\n";
		if (args.length < 4) {
			System.out.println(instruction);
			return;
		}
		RelTagModel memm = new RelTagModel();
		// training
		if ("-t".equals(args[0])) {
			long t1 = System.currentTimeMillis();
			memm.train(args[1]);
			long t2 = System.currentTimeMillis();
			System.out.println("Create model time: " + (t2 - t1));
		}
		if ("-d".equals(args[2])) {
			long t2 = System.currentTimeMillis();
			memm.tag(args[3]);
			long t3 = System.currentTimeMillis();
			System.out.println("Decoding time: " + (t3 - t2));
		} else {
			System.out.println("Wrong command\n " + instruction);
			return;
		}
	}
}
