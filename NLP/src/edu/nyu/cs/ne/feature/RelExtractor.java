package edu.nyu.cs.ne.feature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.nyu.cs.pub.Sentence;
import edu.nyu.cs.pub.TextTool;
import edu.nyu.cs.pub.Token;

/**
 * Feature extractor for relation tagging task
 * 
 * @author Daniel Wu
 * 
 */
public class RelExtractor implements Extractor<RelFeature> {

	@Override
	public Set<RelFeature> extract(Sentence sent) {
		Set<RelFeature> res = new HashSet<RelFeature>();
		List<Token> tokens = sent.getTokens();
		RelFeatureBuilder builder = new RelFeatureBuilder(tokens);
		for (int i = 0; i < tokens.size(); i++) {
			Token token = tokens.get(i);
			// Noun Group Tag
			String goal = token.getOriginalRelType();
			if (TextTool.isEmpty(goal)) {
				builder.setGoal("NONE");
			} else {
				builder.setGoal(goal);
			}
			RelFeature relFeature = builder.buildWithIndex(i);
			res.add(relFeature);
		}
		return res;
	}

}
