package net.sf.paperclips.internal;

import junit.framework.TestCase;

public class EqualsUtilTest extends TestCase {
  public void testSameClass_same() {
    Object o1 = new Object();
    Object o2 = new Object();
    assertTrue( EqualsUtil.sameClass( o1, o2 ) );
  }

  public void testSameClass_different() {
    Object o1 = new Object();
    Object o2 = new Object() {}; // subclass
    assertFalse( EqualsUtil.sameClass( o1, o2 ) );

    assertFalse( EqualsUtil.sameClass( null, o2 ) );
    assertFalse( EqualsUtil.sameClass( o1, null ) );
  }

  public void testAreEqual_equivalent() {
    Object o1 = new Stub();
    Object o2 = new Stub();
    assertTrue( EqualsUtil.areEqual( o1, o2 ) );
  }

  public void testAreEqual_different() {
    Object o1 = new Object();
    Object o2 = new Object();
    assertFalse( EqualsUtil.areEqual( o1, o2 ) );
    assertFalse( EqualsUtil.areEqual( null, o2 ) );
    assertFalse( EqualsUtil.areEqual( o1, null ) );
  }

  public void testAreEqual_equivalentArray() {
    assertTrue( EqualsUtil.areEqual( new byte[] { 0, 1 }, new byte[] { 0, 1 } ) );
    assertTrue( EqualsUtil.areEqual( new short[] { 0, 1 }, new short[] { 0, 1 } ) );
    assertTrue( EqualsUtil.areEqual( new int[] { 0, 1 }, new int[] { 0, 1 } ) );
    assertTrue( EqualsUtil.areEqual( new long[] { 0, 1 }, new long[] { 0, 1 } ) );
    assertTrue( EqualsUtil.areEqual( new char[] { 0, 1 }, new char[] { 0, 1 } ) );
    assertTrue( EqualsUtil.areEqual( new float[] { 0, 1 }, new float[] { 0, 1 } ) );
    assertTrue( EqualsUtil.areEqual( new double[] { 0, 1 }, new double[] { 0, 1 } ) );
    assertTrue( EqualsUtil.areEqual( new boolean[] { false, true }, new boolean[] { false, true } ) );
    assertTrue( EqualsUtil.areEqual( new Object[] { new Stub(), new Stub() }, new Object[] {
        new Stub(), new Stub() } ) );
  }

  public void testAreEqual_differentArray() {
    assertFalse( EqualsUtil.areEqual( new byte[] { 0, 1 }, new byte[] { 0, 2 } ) );
    assertFalse( EqualsUtil.areEqual( new short[] { 0, 1 }, new short[] { 0, 2 } ) );
    assertFalse( EqualsUtil.areEqual( new int[] { 0, 1 }, new int[] { 0, 2 } ) );
    assertFalse( EqualsUtil.areEqual( new long[] { 0, 1 }, new long[] { 0, 2 } ) );
    assertFalse( EqualsUtil.areEqual( new char[] { 0, 1 }, new char[] { 0, 2 } ) );
    assertFalse( EqualsUtil.areEqual( new float[] { 0, 1 }, new float[] { 0, 2 } ) );
    assertFalse( EqualsUtil.areEqual( new double[] { 0, 1 }, new double[] { 0, 2 } ) );
    assertFalse( EqualsUtil.areEqual( new boolean[] { false, true }, new boolean[] { false, false } ) );
    assertFalse( EqualsUtil.areEqual( new Object[] { new Stub(), new Stub() }, new Object[] {
        new Stub(), new Object() } ) );
  }

  public void testAreEqual_equivalentNestedArray() {
    assertTrue( EqualsUtil.areEqual( new Object[] { new Object[] { new Stub() } },
                                     new Object[] { new Object[] { new Stub() } } ) );
    assertTrue( EqualsUtil.areEqual( new int[][] { { 0, 1 } }, new int[][] { { 0, 1 } } ) );
  }

  public void testAreEqual_differentNestedArray() {
    assertFalse( EqualsUtil.areEqual( new Object[] { new Object[] { new Stub() } },
                                      new Object[] { new Object[] { new Object() } } ) );
    assertFalse( EqualsUtil.areEqual( new int[][] { { 0, 1 } }, new int[][] { { 0, 2 } } ) );
  }

  public void testAreEqual_double() {
    assertTrue( EqualsUtil.areEqual( 2.0, 2.0 ) );
    assertFalse( EqualsUtil.areEqual( 2.0, 1.0 ) );
  }

  public class Stub {
    public boolean equals( Object obj ) {
      return EqualsUtil.sameClass( this, obj );
    }
  }
}
