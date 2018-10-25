package ca.on.oicr.gsi.status;

import java.io.OutputStream;
import java.util.stream.Stream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * A web page in the same style as the status page
 */
public abstract class BasePage {
	private static final Header CSS = Header.css("body { margin:0; font-family:sans-serif; }\n"//
			+ "h1 { font-size:120%; }\n"//
			+ "body > div { margin:1em; }\n" + "nav { background-color:#222; display:block; margin:0; padding: 1em }\n"//
			+ "nav > a, nav > div > span { padding:1em; text-decoration:none; font-weight:400; font-size:120% }\n"//
			+ "nav > a:link, nav > a:visited, nav > div > span { color:#9d9d9d; } \n"//
			+ "nav > a:hover, nav > a:active { color:#fff; }\n"//
			+ "nav > div { position: relative; display: inline-block; }\n" + "nav > div:hover div { display: block; }\n"//
			+ "nav > div > div { position:absolute; min-width: 200px; background-color: #fff; border:1px solid #000; box-shadow: 0 6px 12px rgba(0,0,0,0.175); border-radius:4px; z-index:1000; display:none; }\n"//
			+ "nav > div > div a { display:block; text-decoration:none; font-weight:400; padding: 0.5em 1em }\n"//
			+ "nav > div > div a:link, nav > div > div a:visited, nav > div > div a:hover, nav > div > div a:active { color:#000; }\n"//
			+ "nav > div > div a:hover, nav > div > div a:active { background-color:#eee; }\n"//
			+ "nav > :first-child { font-weight: bold }\n"//
			+ "table { width:100%; border-spacing:0; border-collapse:collapse; border-color:grey; margin-top:1ex; }\n"//
			+ "td,th { border:none; }\n" + "td { border-top:1px solid #ddd; }\n" + ".even td { width:50%; }\n"//
			+ "th { font-weight:700; text-align:left; }\n" + "tr:hover { background-color:#f5f5f5; }");
	private final ServerConfig server;

	public BasePage(ServerConfig server) {
		super();
		this.server = server;
	}

	/**
	 * Add additional header elements to the page
	 */
	public Stream<Header> headers() {
		return Stream.empty();
	}

	/**
	 * Write the HTML body of the page; the header and navigation links have already
	 * been supplied
	 */
	protected abstract void renderContent(XMLStreamWriter writer) throws XMLStreamException;

	/**
	 * Render the page to a client connection
	 */
	public final void renderPage(OutputStream output) {
		final XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		try {
			final XMLStreamWriter writer = outputFactory.createXMLStreamWriter(output);
			writer.writeStartDocument("utf-8", "1.0");
			writer.writeStartElement("html");

			writer.writeStartElement("head");
			writer.writeStartElement("title");
			writer.writeCharacters(server.name());
			writer.writeEndElement();

			CSS.append(writer);
			Stream.concat(headers(), server.headers()).forEach(header -> {
				try {
					header.append(writer);
				} catch (final XMLStreamException inner) {
					throw new RuntimeException(inner);
				}
			});

			writer.writeEndElement();

			writer.writeStartElement("body");
			writer.writeStartElement("nav");

			writer.writeStartElement("a");
			writer.writeAttribute("href", ".");
			writer.writeCharacters(server.name());
			writer.writeEndElement();

			server.navigation().forEach(link -> {
				try {
					link.append(writer);
				} catch (final XMLStreamException e) {
					throw new RuntimeException(e);
				}
			});
			writer.writeStartElement("a");
			writer.writeAttribute("href", server.documentationUrl());
			writer.writeCharacters("API Docs");
			writer.writeEndElement();

			writer.writeEndElement();

			writer.writeStartElement("div");
			renderContent(writer);
			writer.writeEndElement();
			writer.writeEndElement();

			writer.writeEndElement();
			writer.writeEndDocument();
		} catch (final XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}
}
