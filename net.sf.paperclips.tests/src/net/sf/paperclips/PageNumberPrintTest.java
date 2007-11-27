package net.sf.paperclips;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

import net.sf.paperclips.internal.EqualsUtil;

public class PageNumberPrintTest extends TestCase {
  public void testConstructor_illegalArguments() {
    try {
      new PageNumberPrint( null );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}

    try {
      new PageNumberPrint( new PageNumberStub( 0 ), null );
      fail();
    }
    catch ( IllegalArgumentException expected ) {}

    assertEquals( SWT.LEFT, new PageNumberPrint( new PageNumberStub( 0 ), 0 ).getAlign() );
  }

  public void testEquals() {
    PageNumberPrint pageNumber1 = new PageNumberPrint( new PageNumberStub( 0 ) );
    PageNumberPrint pageNumber2 = new PageNumberPrint( new PageNumberStub( 0 ) );
    assertEquals( pageNumber1, pageNumber2 );

    pageNumber1.setAlign( SWT.CENTER );
    assertFalse( pageNumber1.equals( pageNumber2 ) );
    pageNumber2.setAlign( SWT.CENTER );
    assertEquals( pageNumber1, pageNumber2 );

    pageNumber1.setFontData( new FontData( "Arial", 12, SWT.BOLD ) );
    assertFalse( pageNumber1.equals( pageNumber2 ) );
    pageNumber2.setFontData( new FontData( "Arial", 12, SWT.BOLD ) );
    assertEquals( pageNumber1, pageNumber2 );

    pageNumber1.setPageNumber( new PageNumberStub( 1 ) );
    assertFalse( pageNumber1.equals( pageNumber2 ) );
    pageNumber2.setPageNumber( new PageNumberStub( 1 ) );
    assertEquals( pageNumber1, pageNumber2 );

    pageNumber1.setPageNumberFormat( new PageNumberFormatStub() );
    assertFalse( pageNumber1.equals( pageNumber2 ) );
    pageNumber2.setPageNumberFormat( new PageNumberFormatStub() );
    assertEquals( pageNumber1, pageNumber2 );
  }

  static class PageNumberStub implements PageNumber {
    private int id;

    public PageNumberStub( int id ) {
      this.id = id;
    }

    public boolean equals( Object obj ) {
      if ( !EqualsUtil.sameClass( this, obj ) )
        return false;

      PageNumberStub that = (PageNumberStub) obj;
      return this.id == that.id;
    }

    public int getPageCount() {
      return 0;
    }

    public int getPageNumber() {
      return 0;
    }
  }
}
