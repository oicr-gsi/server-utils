package ca.on.oicr.gsi.status;

import java.util.stream.Stream;

/**
 * A description of a server for generating all pages
 */
public interface ServerConfig {
	/**
	 * A collection of headers that must be included on all pages
	 */
	Stream<Header> headers();

	/**
	 * The name of the server
	 * 
	 * This should be the name of the server software rather than an instance
	 */
	String name();

	/**
	 * A collection of navigation links to display at the top of every page
	 */
	Stream<NavigationMenu> navigation();
	
	/**
	 * @return the URL path to API documentation, relative to the context root. Defaults to "api-docs/index.html"
	 */
	default String documentationUrl() {
	  return "api-docs/index.html";
	}
}
