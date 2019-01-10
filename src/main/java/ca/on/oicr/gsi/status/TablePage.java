package ca.on.oicr.gsi.status;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Create a page that is filled with a single table.
 */
public abstract class TablePage extends BasePage {

	private final String[] headers;

	public TablePage(ServerConfig server, String... headers) {
		super(server, false);
		this.headers = headers;
	}

	@Override
	protected final void renderContent(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("table");

		final TableRowWriter rowWriter = new TableRowWriter(writer);
		rowWriter.write(true, headers);
		writeRows(rowWriter);
		writer.writeEndElement();

	}

	/**
	 * Write output rows to the page
	 */
	protected abstract void writeRows(TableRowWriter writer);

}
