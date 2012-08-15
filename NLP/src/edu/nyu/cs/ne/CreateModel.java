package edu.nyu.cs.ne;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import opennlp.maxent.BasicEventStream;
import opennlp.maxent.GIS;
import opennlp.maxent.GISModel;
import opennlp.maxent.PlainTextByLineDataStream;
import opennlp.maxent.io.GISModelWriter;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.model.EventStream;
import opennlp.model.MaxentModel;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

/**
 * Create maximum entropy model from features. This model is provided to
 * OpenNLP/Maxent library.
 * 
 * @author Daniel Wu
 * 
 */
public class CreateModel {

	public static CreateModel instance = new CreateModel();

	private Parser parser = null;

	private CreateModel() {

	}

	public MaxentModel build(String dataFilePath) throws Exception {
		// features model
		FileReader datafr = new FileReader(new File(dataFilePath));
		EventStream es = new BasicEventStream(new PlainTextByLineDataStream(
				datafr));
		GISModel model = GIS.trainModel(es, 100, 0);
		File outputFile = new File(dataFilePath + ".model");
		GISModelWriter writer = new SuffixSensitiveGISModelWriter(model,
				outputFile);
		writer.persist();
		return model;
	}

	/**
	 * Build part-of-speech parser.
	 * <p>
	 * This parser is used to parse English sentence, and find the shortest path
	 * between two components in a sentence via least common ancestor in the
	 * parse tree as a feature in relation tagging task.
	 * <p>
	 * More information about this parser can be found in OpenNLP/Tool package.
	 * {@link http://opennlp.sourceforge.net/api/opennlp/tools/parser/package-summary.html}
	 * 
	 * @return an English parser
	 */
	public Parser buildParser() {
		if (parser != null) {
			return parser;
		}
		InputStream modelIn = null;
		try {
			modelIn = new FileInputStream("resources/en-parser-chunking.bin");
			ParserModel model = new ParserModel(modelIn);
			parser = ParserFactory.create(model);
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
		return parser;
	}

}
