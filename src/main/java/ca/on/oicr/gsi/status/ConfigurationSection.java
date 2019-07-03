package ca.on.oicr.gsi.status;

import javax.xml.stream.XMLStreamException;

/**
 * For server with pluggable configuration systems, each configurable unit should create a matching
 * object to render it on the status page.
 */
public abstract class ConfigurationSection {
  private final String name;

  public ConfigurationSection(String name) {
    super();
    this.name = name;
  }

  /** Generate a table of configuration values for this section */
  public abstract void emit(SectionRenderer renderer) throws XMLStreamException;

  /** The user-visible name for the configuration unit */
  public final String name() {
    return name;
  }
}
