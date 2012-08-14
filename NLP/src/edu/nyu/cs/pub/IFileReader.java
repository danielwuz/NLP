package edu.nyu.cs.pub;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * File reader interface
 * 
 * @author Daniel Wu
 * 
 */
public interface IFileReader {

	/**
	 * Read file into list of sentence
	 * 
	 * @param file
	 * @return list of sentence
	 * @throws IOException
	 */
	List<Sentence> read(File file) throws IOException;

}
