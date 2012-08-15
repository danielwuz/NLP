package edu.nyu.cs.ne;

import junit.framework.Assert;
import opennlp.model.MaxentModel;

import org.junit.Before;
import org.junit.Test;

public class CreateModelTest {
	private MaxentModel model;

	@Before
	public void setUp() {
		String dataFilePath = "src/edu/nyu/cs/ne/namedentity.dat";
		try {
			model = CreateModel.instance.build(dataFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testBuild() {
		try {
			Assert.assertNotNull(model);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

}
