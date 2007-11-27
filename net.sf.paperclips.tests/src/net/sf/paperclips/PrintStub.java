/**
 * 
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

import net.sf.paperclips.internal.EqualsUtil;

final class PrintStub implements Print {
  private int id;

  public PrintStub() {
    this( 0 );
  }

  public PrintStub( int id ) {
    this.id = id;
  }

  public boolean equals( Object obj ) {
    if ( !EqualsUtil.sameClass( this, obj ) )
      return false;

    PrintStub that = (PrintStub) obj;
    return this.id == that.id;
  }

  public PrintIterator iterator( Device device, GC gc ) {
    return null;
  }
}