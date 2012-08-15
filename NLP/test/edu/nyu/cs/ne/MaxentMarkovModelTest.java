package edu.nyu.cs.ne;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.nyu.cs.pub.AbstractModel;
import edu.nyu.cs.pub.Sentence;

public class MaxentMarkovModelTest {

	private AbstractModel model = null;

	private String trainingFile = "resources/named_entity/train.np";

	private String devFile = "resources/named_entity/dev.np";

	@Before
	public void setUp() throws Exception {
		model = new MaxEntMarkovModel();
	}

	public void testTraining() throws Exception {
		model.train(trainingFile);
	}

	@Test
	public void testTag() throws Exception {
		model.train(trainingFile);
		List<Sentence> sentences = model.tag(devFile);
		for (Sentence sentence : sentences) {
			System.out.println(sentence + "\n");
		}
	}

}
