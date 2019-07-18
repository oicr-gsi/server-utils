package ca.on.oicr.gsi.status;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/** A navigation menu at the top of a status page */
public abstract class NavigationMenu {
  /** A link to another page */
  public static NavigationMenu item(String href, String name) {
    return new NavigationMenu() {

      @Override
      void append(XMLStreamWriter writer, BasePage page) throws XMLStreamException {
        writer.writeStartElement("a");
        writer.writeAttribute("href", href);
        if (page.activeUrl().equals(href)) {
          writer.writeAttribute("style", "font-weight: bold");
        }
        writer.writeCharacters(name);
        writer.writeEndElement();
      }

      @Override
      void appendInner(XMLStreamWriter writer, BasePage page) throws XMLStreamException {
        append(writer, page);
      }
    };
  }

  /** A pop-up menu of links to other pages */
  public static NavigationMenu submenu(String name, NavigationMenu... items) {
    return new NavigationMenu() {

      @Override
      void append(XMLStreamWriter writer, BasePage page) throws XMLStreamException {
        writer.writeStartElement("div");
        writer.writeStartElement("span");
        writer.writeCharacters(name);
        writer.writeCharacters(" ▼	");
        writer.writeEndElement();
        writer.writeStartElement("div");
        for (final NavigationMenu item : items) {
          item.appendInner(writer, page);
        }
        writer.writeEndElement();
        writer.writeEndElement();
      }

      @Override
      void appendInner(XMLStreamWriter writer, BasePage page) throws XMLStreamException {
        throw new UnsupportedOperationException("Cannot nest navigation menus");
      }
    };
  }

  abstract void append(XMLStreamWriter writer, BasePage page) throws XMLStreamException;

  abstract void appendInner(XMLStreamWriter writer, BasePage page) throws XMLStreamException;
}
