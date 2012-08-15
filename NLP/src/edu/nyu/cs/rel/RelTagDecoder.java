package edu.nyu.cs.rel;

import java.util.List;

import opennlp.model.MaxentModel;
import edu.nyu.cs.ne.CreateModel;
import edu.nyu.cs.ne.feature.RelFeature;
import edu.nyu.cs.ne.feature.RelFeatureBuilder;
import edu.nyu.cs.pub.Decoder;
import edu.nyu.cs.pub.Sentence;
import edu.nyu.cs.pub.Token;

/**
 * Decoder for Relationship Tag
 * 
 * @author Daniel Wu
 * 
 */
public class RelTagDecoder implements Decoder {

	private String[] states = { "ARG1", "PRED", "SUPPORT", "NONE" };

	private MaxentModel maxentModel = null;

	public RelTagDecoder(String dataFilePath) throws Exception {
		this.maxentModel = CreateModel.instance.build(dataFilePath);
	}

	@Override
	public Sentence decode(Sentence sentence) {
		// observations
		List<Token> tokens = sentence.getTokens();
		int T = tokens.size();
		double arg1_max = 0.0;
		int arg1_ind = -1;
		RelFeatureBuilder builder = new RelFeatureBuilder(tokens);
		for (int i = 0; i < T; i++) {
			Token token = tokens.get(i);
			if (token.isPRED() || token.isSUPPORT()) {
				continue;
			}
			// calculate posterior probability by maximum entropy
			RelFeature feature = builder.buildWithIndex(i);
			String[] context = feature.genContext();
			double[] postprob = maxentModel.eval(context);
			// max probability
			int index = maxentModel.getIndex(states[0]);
			if (postprob[index] >= arg1_max) {
				arg1_max = postprob[index];
				arg1_ind = i;
			}
		}
		tokens.get(arg1_ind).setPredictRelType("PRE_ARG1");

		return sentence;
	}
}
