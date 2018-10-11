package ca.on.oicr.gsi.prometheus;

import io.prometheus.client.Histogram;

/**
 * Prebuilt histogram for timing something that should be pretty fast but might
 * take forever.
 */
public class LatencyHistogram {

	private final Histogram histogram;

	public LatencyHistogram(String name, String help, String... labels) {
		histogram = Histogram.build().buckets(1, 5, 10, 30, 60, 300, 600, 3600).name(name).help(help).labelNames(labels)
				.register();
	}

	/**
	 * Start a time and finish it when the returned resource is closed
	 */
	public AutoCloseable start(final String... labels) {
		final long startTime = System.nanoTime();

		return () -> histogram.labels(labels).observe((System.nanoTime() - startTime) / 1e9);
	}

	/**
	 * Observe a time span started from a supplied start time
	 * 
	 * @param startTime
	 *            a time from {@link System#nanoTime()}
	 */
	public void observe(long startTime, final String... labels) {
		histogram.labels(labels).observe((System.nanoTime() - startTime) / 1e9);
	}
}