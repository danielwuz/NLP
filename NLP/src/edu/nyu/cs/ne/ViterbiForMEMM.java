package edu.nyu.cs.ne;

import opennlp.model.MaxentModel;
import edu.nyu.cs.pub.Decoder;
import edu.nyu.cs.pub.Sentence;
import edu.nyu.cs.pub.Token;

/**
 * Decoder for MEMM Named Entity Finder.
 * 
 * Using Viterbi{@link http://en.wikipedia.org/wiki/Viterbi_algorithm} algorithm
 * 
 * @author Daniel Wu
 * 
 */
public class ViterbiForMEMM implements Decoder {

	private String[] states = { "B-NP", "I-NP", "O" };

	private MaxentModel maxentModel = null;

	public ViterbiForMEMM(String dataFilePath) throws Exception {
		this.maxentModel = CreateModel.instance.build(dataFilePath);
	}

	@Override
	public Sentence decode(Sentence sentence) {
		// observations
		Token[] tokens = sentence.getTokens().toArray(new Token[0]);
		int N = states.length;
		int T = tokens.length;
		double[][] viterbi = new double[N + 1][T + 1];
		String[][] backtrack = new String[N + 1][T + 1];
		// initialize first step
		for (int s = 0; s < N; s++) {
			String state = states[s];
			double v = 1.0;
			// calculate posterior probability by maximum entropy
			Context.Builder builder = new Context.Builder();
			builder.setCurrentToken(tokens[0]);
			if (tokens.length > 1) {
				builder.setNextToken(tokens[1]);
			}
			builder.setFirstWord(true);
			String[] context = builder.build();
			double[] postprob = maxentModel.eval(context);
			int stateIndex = maxentModel.getIndex(state);
			// max probability
			double likelihood = v * postprob[stateIndex];
			double log = Math.abs(Math.log10(likelihood));
			viterbi[s][0] = log;
			backtrack[s][0] = "";
		}

		// recursive step
		for (int t = 1; t < T; t++) {
			for (int s = 0; s < N; s++) {
				double argmax = Double.MAX_VALUE;
				String backtrackArg = "";
				String state = states[s];
				for (int s1 = 0; s1 < N; s1++) {
					double v = viterbi[s1][t - 1];
					if (v == 0.0) {
						continue;
					}
					String previousState = states[s1];
					// build features
					Context.Builder builder = new Context.Builder();
					builder.setCurrentToken(tokens[t]);
					builder.setPreviousToken(tokens[t - 1]);
					if (t > 1) {
						builder.setPrePreToken(tokens[t - 2]);
					}
					builder.setPreType(previousState);
					if (t < tokens.length - 1) {
						builder.setNextToken(tokens[t + 1]);
					}
					String[] context = builder.build();
					double[] postprob = maxentModel.eval(context);
					int stateIndex = maxentModel.getIndex(state);
					// max probability
					double log = v + Math.abs(Math.log10(postprob[stateIndex]));
					if (log < argmax) {
						argmax = log;
						backtrackArg = previousState;
					}
				}
				viterbi[s][t] = argmax;
				backtrack[s][t] = backtrackArg;
			}
		}

		// terminate step
		viterbi[N][T] = Double.MAX_VALUE;
		for (int s = 0; s < N; s++) {
			String state = states[s];
			// max probability
			double likelihood = viterbi[s][T - 1];
			if (likelihood < viterbi[N][T]) {
				viterbi[N][T] = likelihood;
				backtrack[N][T] = state;
			}
		}
		// backtracking
		Sentence res = new Sentence();
		int tagIndex = N;
		int stepIndex = T;
		String tag = backtrack[tagIndex][stepIndex];
		while (tag != null && tag.length() != 0) {
			// set tag
			Token token = tokens[stepIndex - 1];
			token.setEntityType(tag);
			res.addToken(token);
			for (int i = 0; i < states.length; i++) {
				if (states[i].equals(tag)) {
					tagIndex = i;
					break;
				}
			}
			tag = backtrack[tagIndex][--stepIndex];
		}

		return res;
	}
}
