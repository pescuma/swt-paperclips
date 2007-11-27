/**
 * 
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.RGB;

import net.sf.paperclips.internal.EqualsUtil;

class CellBackgroundProviderStub implements CellBackgroundProvider {
  public boolean equals( Object obj ) {
    return EqualsUtil.sameClass( this, obj );
  }

  public RGB getCellBackground( int row, int column, int colspan ) {
    return null;
  }
}