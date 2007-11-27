package net.sf.paperclips;

import junit.framework.TestCase;

import net.sf.paperclips.internal.EqualsUtil;

public class PagePrintTest extends TestCase {
  public void testEquals() {
    PagePrint page1 = new PagePrint( new PrintStub( 0 ) );
    PagePrint page2 = new PagePrint( new PrintStub( 0 ) );
    assertEquals( page1, page2 );

    page1.setBody( new PrintStub( 1 ) );
    assertFalse( page1.equals( page2 ) );
    page2.setBody( new PrintStub( 1 ) );
    assertEquals( page1, page2 );

    page1.setHeader( new PageDecorationStub() );
    assertFalse( page1.equals( page2 ) );
    page2.setHeader( new PageDecorationStub() );
    assertEquals( page1, page2 );

    page1.setHeaderGap( 10 );
    assertFalse( page1.equals( page2 ) );
    page2.setHeaderGap( 10 );
    assertEquals( page1, page2 );

    page1.setFooter( new PageDecorationStub() );
    assertFalse( page1.equals( page2 ) );
    page2.setFooter( new PageDecorationStub() );
    assertEquals( page1, page2 );

    page1.setFooterGap( 10 );
    assertFalse( page1.equals( page2 ) );
    page2.setFooterGap( 10 );
    assertEquals( page1, page2 );
  }

  static class PageDecorationStub implements PageDecoration {
    public boolean equals( Object obj ) {
      return EqualsUtil.sameClass( this, obj );
    }

    public Print createPrint( PageNumber pageNumber ) {
      return null;
    }
  }
}
