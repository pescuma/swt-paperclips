package net.sf.paperclips.benchmark;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

public class BenchmarkTest extends TestCase {
  StopClock    clock;
  RunnableStub runnable;
  Benchmark    benchmark;
  int          executionTime;

  protected void setUp() throws Exception {
    super.setUp();
    clock = new StopClock( 0 );
    runnable = new RunnableStub();
    benchmark = new Benchmark( runnable ).setClock( clock ).setName( getName() );
    executionTime = 100;
  }

  public void testTime() {
    assertEquals( executionTime, Benchmark.time( clock, runnable ) );
    assertEquals( 1, runnable.callbackCount );
  }

  public void testExecute() {
    assertEquals( executionTime, benchmark.execute() );
  }

  public void testSetRunCount() {
    int runCount = 10;
    assertEquals( runCount * executionTime, benchmark.setRunCount( 10 ).execute() );
    assertEquals( runCount, runnable.callbackCount );
  }

  public void testOutput() throws Exception {
    ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
    String newline = System.getProperty( "line.separator" );
    String expected =
        new StringBuffer().append( "Starting benchmark \"testOutput\":" )
                          .append( newline )
                          .append( "\tRun 1/5:\t10ms" )
                          .append( newline )
                          .append( "\tRun 2/5:\t20ms" )
                          .append( newline )
                          .append( "\tRun 3/5:\t30ms" )
                          .append( newline )
                          .append( "\tRun 4/5:\t40ms" )
                          .append( newline )
                          .append( "\tRun 5/5:\t50ms" )
                          .append( newline )
                          .append( "Total:  \t150ms" )
                          .append( newline )
                          .append( "Average:\t30.0ms" )
                          .append( newline )
                          .toString();
    Runnable runnable = new Runnable() {
      int   runIndex = 0;
      int[] runTimes = { 10, 20, 30, 40, 50 };

      public void run() {
        clock.time += runTimes[runIndex++];
      }
    };

    new Benchmark( runnable ).setName( "testOutput" )
                             .setClock( clock )
                             .setPrintStream( new PrintStream( byteArrayStream ) )
                             .setRunCount( 5 )
                             .execute();

    assertEquals( expected, byteArrayStream.toString() );
  }

  class RunnableStub implements Runnable {
    int callbackCount = 0;

    public void run() {
      callbackCount++;
      clock.time += executionTime;
    }
  }
}
