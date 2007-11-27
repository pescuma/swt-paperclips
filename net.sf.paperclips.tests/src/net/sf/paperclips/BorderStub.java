/**
 * 
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

import net.sf.paperclips.internal.EqualsUtil;

class BorderStub implements Border {
  public boolean equals( Object obj ) {
    return EqualsUtil.sameClass( this, obj );
  }

  public BorderPainter createPainter( Device device, GC gc ) {
    return null;
  }
}