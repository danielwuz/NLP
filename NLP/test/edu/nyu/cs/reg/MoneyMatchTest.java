package edu.nyu.cs.reg;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class MoneyMatchTest {

	private MoneyMatch mm;

	@Before
	public void setUp() throws Exception {
		String[] args = { "", "" };
		mm = new MoneyMatch(args);
	}

	@Test
	public void testMatchEmptyString() {
		String content = "";
		String actual = "";
		String result = mm.match(content, 0);
		Assert.assertEquals(result, actual);
	}

	@Test
	public void testMatchWithDollar() {
		String content = "3 billion 3 million dollars";
		String actual = "[3 billion 3 million dollars]";
		String result = mm.match(content, 0);
		Assert.assertEquals(result, actual);
	}
	
	@Test
	public void testMatchWithDollarSign() {
		String content = "$1.3 billion for a six-day work week";
		String actual = "[$1.3 billion] for a six-day work week";
		String result = mm.match(content, 0);
		Assert.assertEquals(result, actual);
	}

}
