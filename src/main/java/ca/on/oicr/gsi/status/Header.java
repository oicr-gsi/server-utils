package ca.on.oicr.gsi.status;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/** An entry to be included in the header of the HTML page */
public abstract class Header {

  /** Include raw CSS styling */
  public static Header css(String css) {
    return new Header() {

      @Override
      void append(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("style");
        writer.writeAttribute("type", "text/css");
        writer.writeCharacters("/*");
        writer.writeCData("*/" + css + "/*");
        writer.writeCharacters("*/");
        writer.writeEndElement();
      }
    };
  }

  /** Include CSS styling in a separate file */
  public static Header cssFile(String url) {
    return new Header() {

      @Override
      void append(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("link");
        writer.writeAttribute("type", "text/css");
        writer.writeAttribute("rel", "stylesheet");
        writer.writeAttribute("href", url);
        writer.writeEndElement();
      }
    };
  }

  /**
   * Include a favicon
   *
   * @param url the URL to the image
   * @param size the size of the icon (assumed to be square)
   * @param mimeType the MIME type of the image file
   */
  public static Header favicon(String url, int size, String mimeType) {
    return new Header() {

      @Override
      void append(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("link");
        writer.writeAttribute("type", mimeType);
        writer.writeAttribute("rel", "icon");
        writer.writeAttribute("href", url);
        writer.writeAttribute("sizes", size + "x" + size);
        writer.writeEndElement();
      }
    };
  }

  /** Include a fav icon that is a PNG hosted at <tt>/favicon.png</tt> */
  public static Header faviconPng(int size) {
    return favicon("/favicon.png", size, "image/png");
  }

  private static Header js(String type, String code) {
    return new Header() {

      @Override
      void append(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("script");
        writer.writeAttribute("type", type);
        writer.writeCharacters("//");
        writer.writeCData("\n" + code + "\n//");
        writer.writeEndElement();
      }
    };
  }

  /** Include a JavaScript file */
  public static Header jsFile(String url) {
    return new Header() {

      @Override
      void append(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("script");
        writer.writeAttribute("src", url);
        writer.writeEndElement();
      }
    };
  }

  /** Include an ES6-style JavaScript module */
  public static Header jsModule(String code) {
    return js("module", code);
  }

  /** Include raw JavaScript code */
  public static Header jsScript(String code) {
    return js("text/javascript", code);
  }

  abstract void append(XMLStreamWriter writer) throws XMLStreamException;
}
