package ca.on.oicr.gsi.status;

import ca.on.oicr.gsi.Pair;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Create a status page for a server
 *
 * <p>This should be bound to the root URL of the server
 */
public abstract class StatusPage extends BasePage {
  private static final Instant START_TIME =
      Instant.ofEpochMilli(ManagementFactory.getRuntimeMXBean().getStartTime());

  public StatusPage(ServerConfig server) {
    this(server, true);
  }

  public StatusPage(ServerConfig server, boolean autorefresh) {
    super(server, autorefresh);
  }

  @Override
  public String activeUrl() {
    return "";
  }

  /**
   * Add an extra rows to the <b>Core<b> table displayed at the top of the status page
   *
   * <p>This is where server-level metrics should go, including version and build information
   */
  protected abstract void emitCore(SectionRenderer renderer) throws XMLStreamException;

  @Override
  protected final void renderContent(XMLStreamWriter writer) throws XMLStreamException {
    final Instant now = Instant.now();
    final SectionRenderer renderer =
        new SectionRenderer() {

          @Override
          public void javaScriptLink(String header, String code, String value) {
            try {
              writer.writeStartElement("tr");
              writer.writeStartElement("td");
              writer.writeCharacters(header);
              writer.writeEndElement();
              writer.writeStartElement("td");
              writer.writeStartElement("span");
              writer.writeAttribute("class", "config-button");
              writer.writeAttribute("onclick", code);
              writer.writeCharacters(value);
              writer.writeEndElement();
              writer.writeEndElement();
              writer.writeEndElement();
            } catch (final XMLStreamException e) {
              throw new RuntimeException(e);
            }
          }

          @Override
          public void line(Stream<Pair<String, String>> attributes, String header, String value) {
            try {
              writer.writeStartElement("tr");
              attributes.forEach(
                  attribute -> {
                    try {
                      writer.writeAttribute(attribute.first(), attribute.second());
                    } catch (final XMLStreamException e) {
                      throw new RuntimeException(e);
                    }
                  });
              writer.writeStartElement("td");
              writer.writeCharacters(header);
              writer.writeEndElement();
              writer.writeStartElement("td");
              writer.writeCharacters(value);
              writer.writeEndElement();
              writer.writeEndElement();
            } catch (final XMLStreamException e) {
              throw new RuntimeException(e);
            }
          }

          @Override
          public void line(String header, Instant startTime) {
            line(header, DateTimeFormatter.ISO_INSTANT.format(startTime));
          }

          @Override
          public void line(String header, int value) {
            line(header, Integer.toString(value));
          }

          @Override
          public void line(String header, long value) {
            line(header, Long.toString(value));
          }

          @Override
          public void line(String header, String value) {
            line(Stream.empty(), header, value);
          }

          @Override
          public void lineSpan(String header, Instant startTime) {
            line(header, startTime == null ? "Never" : Duration.between(startTime, now).toString());
          }

          @Override
          public void link(String header, String href, String value) {
            try {
              writer.writeStartElement("tr");
              writer.writeStartElement("td");
              writer.writeCharacters(header);
              writer.writeEndElement();
              writer.writeStartElement("td");
              writer.writeStartElement("a");
              writer.writeAttribute("class", "config-button");
              writer.writeAttribute("href", href);
              writer.writeCharacters(value);
              writer.writeEndElement();
              writer.writeEndElement();
              writer.writeEndElement();
            } catch (final XMLStreamException e) {
              throw new RuntimeException(e);
            }
          }
        };
    writer.writeStartElement("h1");
    writer.writeCharacters("Core");
    writer.writeEndElement();

    writer.writeStartElement("table");
    writer.writeAttribute("class", "config even");
    renderer.lineSpan("Uptime", START_TIME);
    renderer.line("Start Time", START_TIME);
    emitCore(renderer);
    writer.writeEndElement();

    sections() //
        .sorted(Comparator.comparing(ConfigurationSection::name, String.CASE_INSENSITIVE_ORDER)) //
        .forEach(
            section -> {
              try {
                writer.writeStartElement("h1");
                writer.writeCharacters(section.name());
                writer.writeEndElement();
                writer.writeStartElement("table");
                writer.writeAttribute("class", "config even");
                section.emit(renderer);
                writer.writeEndElement();
              } catch (final XMLStreamException e) {
                throw new RuntimeException(e);
              }
            });
  }

  /**
   * Provide a collection of configuration information from plugins/modules/subcomponents of the
   * server
   */
  public abstract Stream<ConfigurationSection> sections();
}
