package net.sf.paperclips.benchmark;

public class SystemClock implements Clock {
  public long getTime() {
    return System.currentTimeMillis();
  }
}
