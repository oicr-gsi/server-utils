package ca.on.oicr.gsi.status;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/** Helper to write an HTML table */
public interface TableWriter {

  /**
   * Add the table to the document
   *
   * @throws XMLStreamException
   */
  public static void render(XMLStreamWriter writer, TableWriter generator)
      throws XMLStreamException {
    writer.writeStartElement("table");
    generator.writeRows(new TableRowWriter(writer));
    writer.writeEndElement();
  }

  void writeRows(TableRowWriter writer);
}
