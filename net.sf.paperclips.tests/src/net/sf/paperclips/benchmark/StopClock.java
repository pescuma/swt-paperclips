package net.sf.paperclips.benchmark;

public class StopClock implements Clock {
  public StopClock() {
    this( 0 );
  }

  public StopClock( long initial ) {
    this.time = initial;
  }

  public long time;

  public long getTime() {
    return time;
  }
}
