package ca.on.oicr.gsi.status;

import java.time.Instant;
import java.util.stream.Stream;

import ca.on.oicr.gsi.Pair;

public interface SectionRenderer {

	/**
	 * Write a row in the table with text that executes some JavaScript code when
	 * clicked in the second column
	 */
	void javaScriptLink(String header, String code, String value);

	/**
	 * Write a row in the table with the supplied value in the second column; apply
	 * the supplied attributes to the <tt>TR</tt>
	 */
	void line(Stream<Pair<String, String>> attributes, String header, String value);

	/**
	 * Write a row in the table with the supplied value in the second column
	 */
	void line(String header, Instant value);

	/**
	 * Write a row in the table with the supplied value in the second column
	 */
	void line(String header, int value);

	/**
	 * Write a row in the table with the supplied value in the second column
	 */
	void line(String header, long value);

	/**
	 * Write a row in the table with the supplied value in the second column
	 */
	void line(String header, String value);

	/**
	 * Write a row in the table with the delay between the present time and the
	 * supplied value in the second column
	 */
	void lineSpan(String header, Instant startTime);

	/**
	 * Write a row in the table with a link in the second column
	 */
	void link(String header, String href, String value);

}
