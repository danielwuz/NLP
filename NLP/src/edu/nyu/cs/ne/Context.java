package edu.nyu.cs.ne;

import java.util.ArrayList;
import java.util.List;

import edu.nyu.cs.pub.TextTool;
import edu.nyu.cs.pub.Token;

/**
 * Context in which the maximum entropy is computed.
 * <p>
 * Following contents are provided around a word as context:
 * <ul>
 * <li>current word</li>
 * <li>preceding word in sentence</li>
 * <li>next word in sentence</li>
 * <li>word before preceding word</li>
 * <li>the predicted type of preceding word</li>
 * <li>is current word the first word in sentence</li>
 * </ul>
 * 
 * @author Daniel Wu
 * @version 1.0
 */
public class Context {

	/**
	 * Context builder. To gradually build a context.
	 * 
	 * @author Daniel Wu
	 * 
	 */
	public static class Builder {

		private Token currentToken;

		private Token preToken;

		private Token prePreToken;

		private Token nextToken;

		private String preType;

		private boolean firstWord;

		public Builder setCurrentToken(Token currentToken) {
			this.currentToken = currentToken;
			return this;
		}

		public Builder setPreviousToken(Token previousToken) {
			this.preToken = previousToken;
			return this;
		}

		public Builder setPreType(String preType) {
			this.preType = preType;
			return this;
		}

		public Builder setNextToken(Token nextToken) {
			this.nextToken = nextToken;
			return this;
		}

		public void setPrePreToken(Token prePreToken) {
			this.prePreToken = prePreToken;
		}

		/**
		 * Build contexts into String array. <br/>
		 * Those strings will be written into model file as features.
		 * 
		 * @return list of String
		 */
		public String[] build() {
			List<String> context = new ArrayList<String>();
			// tag
			context.add("tag=" + currentToken.getPosTag());
			context.add("preTag=" + getPreTag());
			context.add("nextTag=" + getNextTag());
			context.add("prePreTag=" + getPrePreTag());
			context.add("preType=" + getPreType());
			context.add("firstWord=" + firstWord);
			context.add("isCapital=" + isCapital());
			return context.toArray(new String[0]);
		}

		private boolean isCapital() {
			return TextTool.startsWithCapital(currentToken.getOriginWord());
		}

		private String getPreType() {
			return (TextTool.isEmpty(preType)) ? "" : preType;
		}

		private String getPrePreTag() {
			String pretag = prePreToken == null ? "" : prePreToken.getPosTag();
			return pretag;
		}

		private String getPreTag() {
			String pretag = preToken == null ? "" : preToken.getPosTag();
			return pretag;
		}

		private String getNextTag() {
			String nexttag = nextToken == null ? "" : nextToken.getPosTag();
			return nexttag;
		}

		public void setFirstWord(boolean b) {
			this.firstWord = b;
		}

	}

}
