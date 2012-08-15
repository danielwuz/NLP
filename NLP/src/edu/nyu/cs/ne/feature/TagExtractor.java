package edu.nyu.cs.ne.feature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.nyu.cs.ne.feature.TagFeature.FeatureBuilder;
import edu.nyu.cs.pub.Sentence;
import edu.nyu.cs.pub.TextTool;
import edu.nyu.cs.pub.Token;

public class TagExtractor implements Extractor<TagFeature> {

	public Set<TagFeature> extract(Sentence sent) {
		Set<TagFeature> res = new HashSet<TagFeature>();
		List<Token> tokens = sent.getTokens();
		for (int i = 0; i < tokens.size(); i++) {
			FeatureBuilder builder = new FeatureBuilder();
			Token token = tokens.get(i);

			// current tag
			String posTag = token.getPosTag();
			builder.setPosTag(posTag);

			// Noun Group Tag
			String goal = token.getBIOChunk();
			builder.setGoal(goal);

			if (i == 0) {
				builder.setFirstWord(true);
			}

			if (i > 0) {
				Token lastToken = tokens.get(i - 1);
				builder.setPreTag(lastToken.getPosTag());
				builder.setPreType(lastToken.getBIOChunk());
			}

			if (i > 1) {
				Token lastToken = tokens.get(i - 2);
				builder.setPrePreTag(lastToken.getPosTag());
			}

			if (i < tokens.size() - 1) {
				Token nextToken = tokens.get(i + 1);
				builder.setNextTag(nextToken.getPosTag());
			}

			if (TextTool.startsWithCapital(token.getOriginWord())) {
				builder.setCapital(true);
			}

			TagFeature feature = builder.build();
			res.add(feature);
		}
		return res;
	}
}
