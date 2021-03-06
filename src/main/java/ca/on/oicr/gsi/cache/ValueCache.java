package ca.on.oicr.gsi.cache;

import io.prometheus.client.Gauge;
import java.lang.ref.SoftReference;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * Store data that must be generated/fetched remotely and cache the results for a set period of
 * time.
 *
 * @param <V> the cached value
 */
public abstract class ValueCache<V> implements Owner {

  public static Stream<? extends ValueCache<?>> caches() {
    return CACHES.values().stream().map(SoftReference::get).filter(Objects::nonNull);
  }

  private static final Map<String, SoftReference<ValueCache<?>>> CACHES = new ConcurrentHashMap<>();
  private static final Gauge innerCount =
      Gauge.build("gsi_cache_v_max_inner_count", "The largest collection stored in a cache.")
          .labelNames("name")
          .register();
  private static final Gauge ttlValue =
      Gauge.build("gsi_cache_v_ttl", "The time-to-live of a cache, in minutes.")
          .labelNames("name")
          .register();
  private final String name;

  private int ttl;

  private final Record<V> value;

  /**
   * Create a new cache
   *
   * @param name the name, as presented to Prometheus
   * @param ttl the number of minutes an item will remain in cache
   */
  public ValueCache(String name, int ttl, BiFunction<Owner, Updater<V>, Record<V>> recordCtor) {
    super();
    this.name = name;
    this.ttl = ttl;
    this.value = recordCtor.apply(this, this::fetch);
    ttlValue.labels(name).set(ttl);
    CACHES.put(name, new SoftReference<>(this));
  }

  public int collectionSize() {
    return value.collectionSize();
  }

  /**
   * Fetch an item from the remote service (or generate it)
   *
   * @param lastUpdated the last time this item was successfully updated
   * @return the cached value
   */
  protected abstract V fetch(Instant lastUpdated) throws Exception;

  /**
   * Get an item from cache
   *
   * @return the value, if it was possible to fetch; the value may be stale if the remote end-point
   *     is in an error state
   */
  public V get() {
    final V item = value.refresh();
    innerCount.labels(name).set(value.collectionSize());
    return item;
  }
  /**
   * Get an item from cache, but do not update it
   *
   * @return the last value put in the cache
   */
  public V getStale() {
    return value.readStale();
  }

  public void invalidate() {
    value.invalidate();
  }

  public Instant lastUpdated() {
    return value.lastUpdate();
  }

  @Override
  public final String name() {
    return name;
  }

  @Override
  public final long ttl() {
    return ttl;
  }

  public final void ttl(int ttl) {
    this.ttl = ttl;
    ttlValue.labels(name).set(ttl);
  }
}
