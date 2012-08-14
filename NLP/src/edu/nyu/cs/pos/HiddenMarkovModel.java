package edu.nyu.cs.pos;

import edu.nyu.cs.pub.AbstractModel;
import edu.nyu.cs.pub.Decoder;

/**
 * POS Tagger using Hidden Markov Model
 * 
 * @author Daniel Wu
 * 
 */
public class HiddenMarkovModel extends AbstractModel {

	/**
	 * Execute command: 1. Training & Test java HiddenMarkovModel -t
	 * training.pos -d test.text
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 4) {
			System.out
					.println("Please specify args\n Typical usage: java -jar HMM.jar -t training.pos -d test.text\n");
			return;
		}
		HiddenMarkovModel bigram = new HiddenMarkovModel();
		// training
		if ("-t".equals(args[0])) {
			long t1 = System.currentTimeMillis();
			bigram.train(args[1]);
			long t2 = System.currentTimeMillis();
			System.out.println("Training time: " + (t2 - t1));
		}
		if ("-d".equals(args[2])) {
			long t2 = System.currentTimeMillis();
			bigram.tag(args[3]);
			long t3 = System.currentTimeMillis();
			System.out.println("Decoding time: " + (t3 - t2));
		} else {
			System.out.println("Wrong command, Please specify args\n Typical usage: java -jar HMM.jar -t training.pos -d test.text\n");
			return;
		}
	}

	@Override
	protected Decoder getDecoder() {
		// using viterbi decoder
		return new Viterbi(priorMatrix, likelihoodMatrix);
	}

}
