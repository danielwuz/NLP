package edu.nyu.cs.ne.feature;

import edu.nyu.cs.pub.TextTool;

/**
 * Feature model for named entity tagging.
 * <p>
 * Following context are used as features:
 * <ul>
 * <li>POS tag of current word</li>
 * <li>POS tag of preceding word</li>
 * <li>type of preceding word</li>
 * <li>named identity tag</li>
 * <li>POS tag of word before preceding word</li>
 * <li>whether current word is first word in sentence</li>
 * <li>whether current word is capitalized</li>
 * <li>POS tag of next word</li>
 * </ul>
 * 
 * @author Daniel Wu
 * 
 */
public class TagFeature {

	// POS tag of current word
	private String posTag;

	// POS tag of preceding word
	private String preTag;

	// type of preceding word
	private String preType;

	// named identity tag
	private String goal;

	// POS tag of word before preceding word
	private String prePreTag;

	// whether current word is first word in sentence
	private boolean firstWord;

	// whether current word is capitalized
	private boolean isCapital;

	// POS tag of next word
	private String nextTag;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("preTag=" + preTag);
		sb.append(" tag=" + posTag);
		sb.append(" nextTag=" + nextTag);
		sb.append(" prePreTag=" + prePreTag);
		sb.append(" preType=" + preType);
		sb.append(" isCapital=" + isCapital);
		sb.append(" firstWord=" + firstWord);
		if (!TextTool.isEmpty(goal)) {
			sb.append(" " + goal);
		}
		return sb.toString();
	}

	public static class FeatureBuilder {

		private String posTag;

		private String goal;

		private String preTag;

		private String nextTag;

		private String preType;

		private String prePreTag;

		private boolean firstWord;

		private boolean isCapital;

		public void setCapital(boolean isCapital) {
			this.isCapital = isCapital;
		}

		public void setGoal(String ngTag) {
			this.goal = ngTag;
		}

		public void setPosTag(String posTag) {
			this.posTag = posTag;
		}

		public void setPreTag(String preTag) {
			this.preTag = preTag;
		}

		public void setPreType(String preType) {
			this.preType = preType;
		}

		public void setPrePreTag(String prePreTag) {
			this.prePreTag = prePreTag;
		}

		public void setFirstWord(boolean firstWord) {
			this.firstWord = firstWord;
		}

		public void setNextTag(String nextTag) {
			this.nextTag = nextTag;
		}

		public TagFeature build() {
			TagFeature feature = new TagFeature();
			feature.posTag = this.posTag;
			feature.goal = this.goal;
			feature.preTag = (this.preTag == null) ? "" : this.preTag;
			feature.preType = (this.preType == null) ? "" : this.preType;
			feature.prePreTag = (this.prePreTag == null) ? "" : this.prePreTag;
			feature.firstWord = this.firstWord;
			feature.isCapital = this.isCapital;
			feature.nextTag = (this.nextTag == null) ? "" : this.nextTag;
			return feature;
		}

	}
}
