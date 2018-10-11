package ca.on.oicr.gsi.status;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import ca.on.oicr.gsi.Pair;

/**
 * Helper to write an HTML row to a table
 */
public final class TableRowWriter {
	private final XMLStreamWriter writer;

	public TableRowWriter(XMLStreamWriter writer) {
		super();
		this.writer = writer;
	}

	/**
	 * Write a single row to a table
	 *
	 * @param header
	 *            whether the row should be a header <tt>TR</tt> or a data row
	 *            <tt>TD</tt>
	 * @param row
	 *            the values that appear in the columns
	 */
	public void write(boolean header, String... row) {
		if (row.length == 0) {
			return;
		}
		try {
			writer.writeStartElement("tr");
			for (final String value : row) {
				writer.writeStartElement(header ? "th" : "td");
				writer.writeCharacters(value);
				writer.writeEndElement();
			}
			writer.writeEndElement();
		} catch (final XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Write a single data row to a table
	 *
	 * @param attributes
	 *            any HTML attribute that should be applied to the <tt>TR</tt> tag
	 * @param row
	 *            the values that appear in the columns
	 */
	public void write(Iterable<Pair<String, String>> attributes, String... row) {
		if (row.length == 0) {
			return;
		}
		try {
			writer.writeStartElement("tr");
			for (final Pair<String, String> attribute : attributes) {
				writer.writeAttribute(attribute.first(), attribute.second());
			}
			for (final String value : row) {
				writer.writeStartElement("td");
				writer.writeCharacters(value);
				writer.writeEndElement();
			}
			writer.writeEndElement();
		} catch (final XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
}
