package net.sf.paperclips;

import junit.framework.TestCase;

public class PrintJobTest extends TestCase {
  public void testEquals() {
    PrintJob job1 = new PrintJob( "name", new PrintStub( 0 ) );
    PrintJob job2 = new PrintJob( "name", new PrintStub( 0 ) );
    assertEquals( job1, job2 );

    job1 = new PrintJob( "name2", new PrintStub( 0 ) );
    assertFalse( job1.equals( job2 ) );
    job2 = new PrintJob( "name2", new PrintStub( 0 ) );
    assertEquals( job1, job2 );

    job1 = new PrintJob( "name2", new PrintStub( 1 ) );
    assertFalse( job1.equals( job2 ) );
    job2 = new PrintJob( "name2", new PrintStub( 1 ) );
    assertEquals( job1, job2 );

    job1.setOrientation( PaperClips.ORIENTATION_LANDSCAPE );
    assertFalse( job1.equals( job2 ) );
    job2.setOrientation( PaperClips.ORIENTATION_LANDSCAPE );
    assertEquals( job1, job2 );

    job1.setMargins( 144 );
    assertFalse( job1.equals( job2 ) );
    job2.setMargins( 144 );
    assertEquals( job1, job2 );
  }
}
