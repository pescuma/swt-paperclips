/**
 * 
 */
package net.sf.paperclips;

import net.sf.paperclips.internal.EqualsUtil;

class PageNumberFormatStub implements PageNumberFormat {
  public boolean equals( Object obj ) {
    return EqualsUtil.sameClass( this, obj );
  }

  public String format( PageNumber pageNumber ) {
    return null;
  }
}