package edu.nyu.cs.pub;

/**
 * Decoder interface
 * 
 * @author Daniel Wu
 * 
 */
public interface Decoder {

	/**
	 * Decoding given sentence based on probability model.<br/>
	 * Part-of-Speech is decoded within POS tagger task.<br/>
	 * Named entity is decoded within Noun group tagger task.<br/>
	 * 
	 * @param sentence
	 * @return original sentence after decoding
	 * @see edu.nyu.cs.pub.AbstractModel
	 * @see edu.nyu.cs.pos.HiddenMarkovModel
	 * @see edu.nyu.cs.ne.MaxEntMarkovModel
	 */
	public Sentence decode(Sentence sentence);
}
