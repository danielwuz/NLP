package edu.nyu.cs.ne;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.util.Span;

import org.junit.Test;

public class ParserTest {

	String sentence = "The quick brown fox jumps over the lazy dog . ";

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
		return search(root, span);
	}

	@Test
	public void test() throws FileNotFoundException {
		InputStream modelIn = new FileInputStream(
				"resources/en-parser-chunking.bin");
		try {
			ParserModel model = new ParserModel(modelIn);
			Parser parser = ParserFactory.create(model);
			Parse[] topParses = ParserTool.parseLine(sentence, parser, 1);
			Parse root = topParses[0];
			Parse par1 = findParseBySpan(root, new Span(0, 3));
			Parse par2 = findParseBySpan(root, new Span(31, 34));
			Parse parent = par1.getCommonParent(par2);
			System.out.println(parent + " " + parent.getType());
			System.out.println(shortestPathBetweenParses(par1, par2));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
