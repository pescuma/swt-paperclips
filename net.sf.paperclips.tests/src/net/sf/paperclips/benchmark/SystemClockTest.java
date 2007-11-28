package net.sf.paperclips.benchmark;

import junit.framework.TestCase;

public class SystemClockTest extends TestCase {
  public void testGetTime() {
      final Clock clock = new SystemClock();
  
      long before = System.currentTimeMillis();
      long clockTime = clock.getTime();
      long after = System.currentTimeMillis();
      assertTrue( before <= clockTime && clockTime <= after );
    }
}
