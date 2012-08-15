package edu.nyu.cs.ne.feature;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.util.Span;
import edu.nyu.cs.ne.CreateModel;
import edu.nyu.cs.pub.TextTool;
import edu.nyu.cs.pub.Token;

/**
 * Feature model builder for relation tagger
 * 
 * @author Daniel Wu
 * @see RelFeature
 */
public class RelFeatureBuilder {

	private String candToken = "";

	private String tokenBeforeCand = "";

	private String tokenAfterCand = "";

	private String tokensBetweenCandPRED = "";

	private int numberOfTokensBetween;

	private boolean exisitVerbBetweenCandPred;

	private boolean exisitSUPPORTBetweenCandPred;

	private String candTokenPOS = "";

	private String posBeforeCand = "";

	private String posAfterCand = "";

	private String possBetweenCandPRED = "";

	private String BIOChunkChain = "";

	private String chunkChain = "";

	private boolean lastWordOfNounGroup;

	private boolean followingSupport;

	private boolean withinINGroup;

	private String goal = "";

	private boolean candPredInSameNP = false;

	private boolean candPredInSameVP = false;

	private boolean candPredInSamePP = false;

	private String shortestPathBetween = "";

	private Parse root = null;

	private String sentence = "";

	private List<Token> tokens = null;

	private Span predSpan = null;

	private boolean isSupport;

	private boolean isPred;

	public RelFeatureBuilder(List<Token> tokens) {
		this.tokens = tokens;
		this.sentence = createSentence();
		this.root = createParse();
		this.predSpan = findSpanForPRED();
	}

	private Span findSpanForPRED() {
		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).isPRED()) {
				return findSpanByIndex(i);
			}
		}
		return null;
	}

	private Span findSpanByIndex(int index) {
		int start = 0;
		for (int i = 0; i < index; i++) {
			String word = tokens.get(i).getOriginWord();
			// 1 more character for blank
			start += (word.length() + 1);
		}
		int end = start + tokens.get(index).getOriginWord().length();
		return new Span(start, end);
	}

	private RelFeature build() {
		RelFeature feature = new RelFeature();
		// current tag
		feature.candTokenPOS = this.candTokenPOS;
		feature.lastWordOfNounGroup = this.lastWordOfNounGroup;
		feature.followingSupport = this.followingSupport;
		feature.withinINGroup = this.withinINGroup;
		feature.candToken = this.candToken;
		feature.tokenBeforeCand = this.tokenBeforeCand;
		feature.tokenAfterCand = this.tokenAfterCand;
		feature.posBeforeCand = this.posBeforeCand;
		feature.posAfterCand = this.posAfterCand;
		feature.exisitVerbBetweenCandPred = this.exisitVerbBetweenCandPred;
		feature.exisitSUPPORTBetweenCandPred = this.exisitSUPPORTBetweenCandPred;
		feature.numberOfTokensBetween = numberOfTokensBetween;
		feature.tokensBetweenCandPRED = this.tokensBetweenCandPRED;
		feature.possBetweenCandPRED = this.possBetweenCandPRED;
		feature.BIOChunkChain = this.BIOChunkChain;
		feature.chunkChain = this.chunkChain;
		feature.candPredInSameNP = this.candPredInSameNP;
		feature.candPredInSameVP = this.candPredInSameVP;
		feature.candPredInSamePP = this.candPredInSamePP;
		feature.shortestPathBetween = this.shortestPathBetween;
		feature.isPred = this.isPred;
		feature.isSupport = this.isSupport;
		feature.goal = this.goal;
		return feature;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public RelFeature buildWithIndex(int i) {
		Token token = tokens.get(i);
		this.candTokenPOS = token.getPosTag();
		this.lastWordOfNounGroup = lastWordOfNounGroup(i);
		this.followingSupport = followingSupport(i);
		this.withinINGroup = withinINGroup(i);

		this.candToken = token.getLiteral().toLowerCase();
		this.tokenBeforeCand = tokenBeforeCand(i);
		this.tokenAfterCand = tokenAfterCand(i);
		this.posBeforeCand = posBeforeCand(i);
		this.posAfterCand = posAfterCand(i);

		this.isSupport = token.isSUPPORT();
		this.isPred = token.isPRED();

		tokensBetweenCandPRED(i);

		this.shortestPathBetween = shortestPathBetweenCondPred(i);
		return build();
	}

	private String shortestPathBetweenCondPred(int i) {
		Span currSpan = findSpanByIndex(i);
		Parse par1 = findParseBySpan(root, currSpan);
		Parse par2 = findParseBySpan(root, predSpan);

		return shortestPathBetweenParses(par1, par2);
	}

	private Parse createParse() {
		Parser parser = CreateModel.instance.buildParser();
		try {
			Parse[] topParses = ParserTool.parseLine(sentence, parser, 1);
			return topParses[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String createSentence() {
		StringBuffer sb = new StringBuffer();
		for (Token token : tokens) {
			String word = token.getOriginWord();
			if ("COMMA".equals(word)) {
				word = ",";
			}
			sb.append(word + " ");
		}
		return sb.toString();
	}

	private String shortestPathBetweenParses(Parse p1, Parse p2) {
		if (p1 == null || p2 == null) {
			return "NONE";
		}
		Parse parent = p1.getCommonParent(p2);
		if (parent == null) {
			return "NONE";
		}
		// make sure correct order
		if (p1.getSpan().getStart() > p2.getSpan().getStart()) {
			Parse tmp = p1;
			p1 = p2;
			p2 = tmp;
		}
		String[] path1 = getPathBetween(p1, parent);
		String[] path2 = getPathBetween(p2, parent);
		return combinePaths(path1, path2, parent.getType());
	}

	private String combinePaths(String[] path1, String[] path2, String type) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < path1.length; i++) {
			sb.append(path1[i] + "_");
		}
		sb.append(type);
		for (int i = path2.length - 1; i >= 0; i--) {
			sb.append("_" + path2[i]);
		}
		return sb.toString();
	}

	private String[] getPathBetween(Parse child, Parse parent) {
		List<String> res = new ArrayList<String>();
		Parse tmpParent = child.getParent();
		while (!tmpParent.equals(parent)) {
			if (!tmpParent.isPosTag()) {
				res.add(tmpParent.getType());
			}
			tmpParent = tmpParent.getParent();
		}
		return res.toArray(new String[0]);
	}

	private Parse search(Parse root, Span target) {
		Parse[] parts = root.getChildren();
		for (Parse c : parts) {
			Span s = c.getSpan();
			if (s.equals(target)) {
				return c;
			}
			Parse next = search(c, target);
			if (next != null)
				return next;
		}
		return null;
	}

	private Parse findParseBySpan(Parse root, Span span) {
		if (root == null) {
			return null;
		}
		return search(root, span);
	}

	private String posAfterCand(int i) {
		if (i < tokens.size() - 1) {
			return tokens.get(i + 1).getPosTag();
		}
		return "NONE";
	}

	private String posBeforeCand(int i) {
		if (i > 0) {
			return tokens.get(i - 1).getPosTag();
		}
		return "NONE";
	}

	private void tokensBetweenCandPRED(int i) {
		int indexOfPRED = findIndexOfPred(tokens);
		if (indexOfPRED < 0) {
			return;
		}
		int start = Math.min(i, indexOfPRED);
		int end = Math.max(i, indexOfPRED);
		StringBuffer tokensBetween = new StringBuffer();
		StringBuffer possBetween = new StringBuffer();
		StringBuffer BIOChain = new StringBuffer();
		String firstBIO = tokens.get(start).getBIOChunk();
		// BIOChain and chunk include the head and tail
		BIOChain.append(firstBIO);
		if (firstBIO.endsWith("NP")) {
			this.candPredInSameNP = true;
		} else if (firstBIO.endsWith("VP")) {
			this.candPredInSameVP = true;
		} else if (firstBIO.endsWith("PP")) {
			this.candPredInSamePP = true;
		}
		String chunk = (firstBIO.startsWith("O")) ? "O" : firstBIO.substring(2);
		boolean firstWord = true;
		int numBetween = 0;
		for (start++; start < end; start++) {
			Token token = tokens.get(start);
			if (!firstWord) {
				tokensBetween.append("_");
				possBetween.append("_");
			}
			firstWord = false;
			tokensBetween.append(token.getLiteral().toLowerCase());
			possBetween.append(token.getPosTag());
			String tBIO = token.getBIOChunk();
			BIOChain.append("_" + tBIO);
			if (!tBIO.endsWith("NP")) {
				this.candPredInSameNP = false;
			} else if (!tBIO.endsWith("VP")) {
				this.candPredInSameVP = false;
			} else if (!firstBIO.endsWith("PP")) {
				this.candPredInSamePP = false;
			}
			if (tBIO.startsWith("O")) {
				tBIO = "O";
			} else {
				tBIO = tBIO.substring(2);
			}
			if (!chunk.endsWith(tBIO)) {
				chunk = chunk + "_" + tBIO;
			}
			// exisitVerbBetweenCandPred
			String ngType = token.getBIOChunk();
			if (!TextTool.isEmpty(ngType) && ngType.contains("V")) {
				this.exisitVerbBetweenCandPred = true;
			}
			String relType = token.getOriginalRelType();
			// exisitSUPPORTBetweenCandPred
			if (!TextTool.isEmpty(relType) && relType.equals("SUPPORT")) {
				this.exisitSUPPORTBetweenCandPred = true;
			}
			// number of tokens between
			numBetween++;
		}
		this.numberOfTokensBetween = numBetween;
		this.tokensBetweenCandPRED = tokensBetween.toString();
		this.possBetweenCandPRED = possBetween.toString();
		this.BIOChunkChain = BIOChain.toString();
		this.chunkChain = chunk.toString();
	}

	private int findIndexOfPred(List<Token> tokens) {
		int j = 0;
		while (j < tokens.size()) {
			Token token = tokens.get(j);
			if ("PRED".equals(token.getOriginalRelType())) {
				return j;
			}
			j++;
		}
		return -1;
	}

	private String tokenAfterCand(int i) {
		if (i < tokens.size() - 1) {
			return tokens.get(i + 1).getLiteral().toLowerCase();
		}
		return "NONE";
	}

	private String tokenBeforeCand(int i) {
		if (i > 0) {
			return tokens.get(i - 1).getLiteral().toLowerCase();
		}
		return "NONE";
	}

	private boolean withinINGroup(int i) {
		if (i == 0) {
			return false;
		}
		Token token = tokens.get(i);
		String nounGroupTag = token.getBIOChunk();
		while ("I-NP".equals(nounGroupTag) && i > 1) {
			i = i - 1;
			nounGroupTag = tokens.get(i).getBIOChunk();
		}
		String lastWord = tokens.get(i - 1).getLiteral();
		if ("B-NP".equals(nounGroupTag) && "IN".equalsIgnoreCase(lastWord)) {
			return true;
		}
		return false;
	}

	private boolean followingSupport(int i) {
		for (int j = 0; j < i; j++) {
			Token token = tokens.get(j);
			if ("SUPPORT".equals(token.getPredictRelType())) {
				return true;
			}
		}
		return false;
	}

	private boolean lastWordOfNounGroup(int i) {
		Token token = tokens.get(i);
		String nounGroupTag = token.getBIOChunk();
		if ("B-NP".equals(nounGroupTag) || "I-NP".equals(nounGroupTag)) {
			if (i < (tokens.size() - 1)) {
				Token nextToken = tokens.get(i + 1);
				String nextTag = nextToken.getBIOChunk();
				return !("I-NP".equals(nextTag));
			}
		}
		return false;
	}

}
