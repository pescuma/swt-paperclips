/************************************************************************************************************
 * Copyright (c) 2006 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.graphics.*;

import net.sf.paperclips.internal.EqualsUtil;
import net.sf.paperclips.internal.NullUtil;

/**
 * A print wrapper which prevents its target from being broken into multiple pieces when printed. If there
 * isn't enough room to print the target in one piece on the current page (or column, if it's inside a
 * ColumnPrint), it will be printed on the next page (or column).
 * 
 * <p>
 * Care must be taken when using this class to avoid unprintable documents. If the target of a NoBreakPrint
 * does not fit in the available space on the print device, the entire document will fail to print.
 * @author Matthew Hall
 */
public class NoBreakPrint implements Print {
  private final Print target;

  /**
   * Constructs a NoBreakPrint with the given target.
   * @param target the print to
   */
  public NoBreakPrint( Print target ) {
    NullUtil.notNull( target );
    this.target = target;
  }

  public boolean equals( Object obj ) {
    if ( !EqualsUtil.sameClass( this, obj ) )
      return false;

    NoBreakPrint that = (NoBreakPrint) obj;
    return EqualsUtil.areEqual( this.target, that.target );
  }

  /**
   * Returns the print which will not be broken across pages.
   * @return the print which will not be broken across pages.
   */
  public Print getTarget() {
    return target;
  }

  public PrintIterator iterator( Device device, GC gc ) {
    return new NoBreakIterator( target.iterator( device, gc ) );
  }
}

class NoBreakIterator implements PrintIterator {
  private PrintIterator target;

  NoBreakIterator( PrintIterator target ) {
    NullUtil.notNull( target );
    this.target = target;
  }

  public PrintIterator copy() {
    return new NoBreakIterator( target.copy() );
  }

  public boolean hasNext() {
    return target.hasNext();
  }

  public Point minimumSize() {
    return target.minimumSize();
  }

  public Point preferredSize() {
    return target.preferredSize();
  }

  public PrintPiece next( int width, int height ) {
    // Use a test iterator so we preserve the original iterator
    PrintIterator testIterator = target.copy();

    PrintPiece result = PaperClips.next( testIterator, width, height );
    if ( result == null )
      return result;

    if ( testIterator.hasNext() ) // Failed to print the whole thing
      return null;

    this.target = testIterator;
    return result;
  }
}