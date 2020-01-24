package ca.on.oicr.gsi.cache;

public class InitialCachePopulationException extends RuntimeException {
  public InitialCachePopulationException(String cache) {
    super(cache);
  }
}
