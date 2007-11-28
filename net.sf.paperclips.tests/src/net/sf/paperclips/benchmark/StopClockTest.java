package net.sf.paperclips.benchmark;

import junit.framework.TestCase;

public class StopClockTest extends TestCase {
  public void testConstructor() {
    assertEquals( 0, new StopClock().getTime() );
    assertEquals( 0, new StopClock( 0 ).getTime() );
    assertEquals( 10, new StopClock( 10 ).getTime() );
  }

  public void testGetTime() {
    StopClock clock = new StopClock();
    clock.time = 0;
    assertEquals( 0, clock.getTime() );
    clock.time = 50;
    assertEquals( 50, clock.getTime() );
  }
}
