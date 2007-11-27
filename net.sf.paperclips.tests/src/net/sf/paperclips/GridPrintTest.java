package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

import net.sf.paperclips.internal.EqualsUtil;

public class GridPrintTest extends TestCase {
  public void testConstructor_invalidArguments() {
    try {
      new GridPrint( (GridLook) null );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}

    try {
      new GridPrint( (GridColumn[]) null );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}

    try {
      new GridPrint( (GridColumn[]) null );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}

    try {
      new GridPrint( (String) null );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}
  }

  public void testEquals() {
    GridPrint g1 = new GridPrint();
    GridPrint g2 = new GridPrint();
    assertEquals( g1, g2 );

    g1.addColumns( "p, 80pt, d" );
    assertFalse( g1.equals( g2 ) );
    g2.addColumns( "p, 80pt, d" );
    assertEquals( g1, g2 );

    g1.setLook( new GridLookStub() );
    assertFalse( g1.equals( g2 ) );
    g2.setLook( new GridLookStub() );
    assertEquals( g1, g2 );

    g1.setCellClippingEnabled( false );
    assertFalse( g1.equals( g2 ) );
    g2.setCellClippingEnabled( false );
    assertEquals( g1, g2 );

    g1.setColumnGroups( new int[][] { { 0, 1 } } );
    assertFalse( g1.equals( g2 ) );
    g2.setColumnGroups( new int[][] { { 0, 1 } } );
    assertEquals( g1, g2 );

    g1.add( new PrintStub() );
    assertFalse( g1.equals( g2 ) );
    g2.add( new PrintStub() );
    assertEquals( g1, g2 );

    g1.addHeader( new PrintStub() );
    assertFalse( g1.equals( g2 ) );
    g2.addHeader( new PrintStub() );
    assertEquals( g1, g2 );

    g1.addFooter( new PrintStub() );
    assertFalse( g1.equals( g2 ) );
    g2.addFooter( new PrintStub() );
    assertEquals( g1, g2 );
  }

  static class GridLookStub implements GridLook {
    public boolean equals( Object obj ) {
      return EqualsUtil.sameClass( this, obj );
    }

    public GridLookPainter getPainter( Device device, GC gc ) {
      return null;
    }
  }
}