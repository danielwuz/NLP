package edu.nyu.cs.pub;

/**
 * Math utility
 * 
 * @author Daniel Wu
 * 
 */
public class MathUtil {

	/**
	 * Get maximum value
	 * 
	 * @param vals
	 *            list of numbers
	 * @return number with maximum value
	 */
	public static double max(double[] vals) {
		double maxval = 0;
		for (double d : vals) {
			if (d > maxval) {
				maxval = d;
			}
		}
		return maxval;
	}
}
