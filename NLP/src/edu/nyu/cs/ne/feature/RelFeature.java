package edu.nyu.cs.ne.feature;

import java.util.ArrayList;
import java.util.List;

import edu.nyu.cs.pub.TextTool;

/**
 * 
 * Feature model for relation tagging.
 * <p>
 * Following context are used as features:
 * <ul>
 * <li>current token</li>
 * <li>token before candidate</li>
 * <li>token next to candidate</li>
 * <li>list of tokens between candidate and predict</li>
 * <li>number of tokens between candidate and predict</li>
 * <li>is verb existing between candidate and predict</li>
 * <li>is support word existing between candidate and predict</li>
 * <li>POS of candidate token</li>
 * <li>POS of the word before candidate token</li>
 * <li>POS of the word after candidate token</li>
 * <li>POS sequence of the words between candidate and predict</li>
 * <li>Begin-In-Other chunk chain</li>
 * <li>chunk chain</li>
 * <li>whether candidate and predict are in the same noun phrase</li>
 * <li>whether candidate and predict are in the same verb phrase</li>
 * <li>whether candidate and predict are in the same prop phrase</li>
 * <li>whether candidate is the last word of noun group</li>
 * <li>whether candidate follows support word</li>
 * <li>whether candidate is within noun group</li>
 * <li>shortest path between candidate and predict via least common ancestor</li>
 * <li>is current candidate a support word</li>
 * <li>is current candidate a predict word</li>
 * </ul>
 * 
 * @author Daniel Wu
 * 
 */
public class RelFeature {

	// candidate token
	protected String candToken = "";

	// token before candidate
	protected String tokenBeforeCand = "";

	// token next to candidate
	protected String tokenAfterCand = "";

	// list of tokens between candidate and predict
	protected String tokensBetweenCandPRED = "";

	// number of tokens between candidate and predict
	protected int numberOfTokensBetween = -1;

	// is verb existing between candidate and predict
	protected boolean exisitVerbBetweenCandPred = false;

	// is support word existing between candidate and predict
	protected boolean exisitSUPPORTBetweenCandPred = false;

	// POS of candidate token
	protected String candTokenPOS = "";

	// POS of the word before candidate token
	protected String posBeforeCand = "";

	// POS of the word after candidate token
	protected String posAfterCand = "";

	// POS sequence of the words between candidate and predict
	protected String possBetweenCandPRED = "";

	// Begin-In-Other chunk chain
	protected String BIOChunkChain = "";

	// chunk chain
	protected String chunkChain = "";

	// whether candidate and predict are in the same noun phrase
	protected boolean candPredInSameNP;

	// whether candidate and predict are in the same verb phrase
	protected boolean candPredInSameVP;

	// whether candidate and predict are in the same prop phrase
	protected boolean candPredInSamePP;

	// whether candidate is the last word of noun group
	protected boolean lastWordOfNounGroup;

	// whether candidate follows support word
	protected boolean followingSupport;

	// whether candidate is within noun group
	protected boolean withinINGroup;

	// shortest path between candidate and predict via least common ancestor
	protected String shortestPathBetween = "";

	// is current candidate a support word
	protected boolean isSupport;

	// is current candidate a predict word
	protected boolean isPred;

	// Target
	protected String goal = "";

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public String[] genContext() {
		List<String> strs = new ArrayList<String>();
		strs.add("candTokenPOS=" + candTokenPOS);
		strs.add("candToken=" + candToken);
		strs.add("lastWordOfNounGroup=" + lastWordOfNounGroup);
		strs.add("tokenBeforeCand=" + tokenBeforeCand);
		strs.add("tokenAfterCand=" + tokenAfterCand);
		strs.add("posBeforeCand=" + posBeforeCand);
		strs.add("posAfterCand=" + posAfterCand);
		strs.add("tokensBetweenCandPRED=" + tokensBetweenCandPRED);
		strs.add("followingSupport=" + followingSupport);
		strs.add("withinINGroup=" + withinINGroup);
		strs.add("numberOfTokensBetween=" + numberOfTokensBetween);
		strs.add("exisitVerbBetweenCandPred=" + exisitVerbBetweenCandPred);
		strs.add("exisitSUPPORTBetweenCandPred=" + exisitSUPPORTBetweenCandPred);
		if (!TextTool.isEmpty(possBetweenCandPRED)) {
			strs.add("possBetweenCandPRED=" + possBetweenCandPRED);
		} else {
			strs.add("possBetweenCandPRED=NONE");
		}
		if (!TextTool.isEmpty(BIOChunkChain)) {
			strs.add("BIOChunkChain=" + BIOChunkChain);
		} else {
			strs.add("BIOChunkChain=NONE");
		}
		if (!TextTool.isEmpty(chunkChain)) {
			strs.add("chunkChain=" + chunkChain);
		} else {
			strs.add("chunkChain=NONE");
		}
		strs.add("shortestPathBetween=" + shortestPathBetween);
		strs.add("candPredInSameNP=" + candPredInSameNP);
		strs.add("candPredInSameVP=" + candPredInSameVP);
		strs.add("candPredInSamePP=" + candPredInSamePP);
		strs.add("isSupport=" + isSupport);
		strs.add("isPred=" + isPred);

		if (!TextTool.isEmpty(goal)) {
			strs.add(goal);
		}

		return strs.toArray(new String[0]);
	}

	public String toString() {
		String[] features = genContext();
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (String str : features) {
			if (!first) {
				sb.append(" ");
			}
			first = false;
			sb.append(str);
		}
		return sb.toString();
	}

}
