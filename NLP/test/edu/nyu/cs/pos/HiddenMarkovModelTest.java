package edu.nyu.cs.pos;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class HiddenMarkovModelTest {

	private HiddenMarkovModel model = null;

	private String trainingFile = "test/edu/nyu/cs/pos/training.pos";

	private String devFile = "test/edu/nyu/cs/pos/development.text";

	@Before
	public void setUp() {
		this.model = new HiddenMarkovModel();
	}

	@Test
	public void testTrain() throws Exception {
		model.train(trainingFile);
	}

	@Test
	public void testTag() throws Exception {
		model.train(trainingFile);
		model.tag(devFile);
	}

	@Test
	public void testGetDecoder() {
		Assert.assertNotNull(model.getDecoder());
	}

}
