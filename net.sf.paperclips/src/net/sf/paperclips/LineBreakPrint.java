/************************************************************************************************************
 * Copyright (c) 2007 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.graphics.*;

import net.sf.paperclips.internal.NullUtil;
import net.sf.paperclips.internal.ResourcePool;

/**
 * A class for adding line breaks corresponding to a particular font size. Currently this class is used
 * internally by StyledTextPrint to implement the newline() feature.
 * @author Matthew Hall
 */
public class LineBreakPrint implements Print {
  final FontData font;

  /**
   * Constructs a new LineBreakPrint on the given font.
   * @param font the font which determines the height of the line break.
   */
  public LineBreakPrint( FontData font ) {
    NullUtil.notNull( font );
    this.font = font;
  }

  public PrintIterator iterator( Device device, GC gc ) {
    return new LineBreakIterator( this, device, gc );
  }
}

class LineBreakIterator implements PrintIterator {
  private static final int MIN_HEIGHT = 0;
  private static final int MIN_WIDTH  = 1;

  private final int        lineHeight;
  private boolean          hasNext    = true;

  LineBreakIterator( LineBreakPrint print, Device device, GC gc ) {
    this( calculateLineHeight( print, device, gc ) );
  }

  private LineBreakIterator( int lineHeight ) {
    this.lineHeight = lineHeight;
  }

  private static int calculateLineHeight( LineBreakPrint print, Device device, GC gc ) {
    Font oldFont = gc.getFont();

    gc.setFont( ResourcePool.forDevice( device ).getFont( print.font ) );
    int result = gc.getFontMetrics().getHeight();

    gc.setFont( oldFont );

    return result;
  }

  public Point minimumSize() {
    return new Point( MIN_WIDTH, MIN_HEIGHT );
  }

  public Point preferredSize() {
    return new Point( MIN_WIDTH, lineHeight );
  }

  public boolean hasNext() {
    return hasNext;
  }

  public PrintPiece next( int width, int height ) {
    if ( width < MIN_WIDTH || height < MIN_HEIGHT )
      return null;

    hasNext = false;
    return new EmptyPiece( new Point( width, Math.min( height, lineHeight ) ) );
  }

  public PrintIterator copy() {
    return hasNext ? new LineBreakIterator( lineHeight ) : this;
  }
}