/************************************************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;

import net.sf.paperclips.internal.NullUtil;
import net.sf.paperclips.internal.TextStyleResource;

class TextPiece implements TextPrintPiece {
  private final Point             size;
  private final String[]          lines;
  private final TextStyleResource style;
  private final int               ascent;

  TextPiece( Device device, Point size, TextIterator iter, String[] text, int ascent ) {
    NullUtil.notNull( device, size, iter );
    NullUtil.noNulls( text );
    this.size = size;
    this.lines = text;
    this.style = new TextStyleResource( device, iter.style );
    this.ascent = ascent;
  }

  public Point getSize() {
    return new Point( size.x, size.y );
  }

  public int getAscent() {
    return ascent;
  }

  public void paint( final GC gc, final int x, final int y ) {
    Font oldFont = gc.getFont();
    Color oldForeground = gc.getForeground();
    Color oldBackground = gc.getBackground();

    final int width = getSize().x;
    final int align = style.getAlignment();

    try {
      setFont( gc );
      setForeground( gc );
      boolean transparent = setBackground( gc );

      FontMetrics fm = gc.getFontMetrics();
      int lineHeight = fm.getHeight();

      boolean strikeout = style.getStrikeout();
      boolean underline = style.getUnderline();
      int lineThickness = Math.max( 1, fm.getDescent() / 3 );
      int strikeoutOffset = fm.getLeading() + fm.getAscent() / 2;
      int underlineOffset = ascent + lineThickness;

      for ( int i = 0; i < lines.length; i++ ) {
        String line = lines[i];
        int lineWidth = gc.stringExtent( line ).x;
        int offset = getHorzAlignmentOffset( align, lineWidth, width );

        gc.drawString( lines[i], x + offset, y + lineHeight * i, transparent );
        if ( strikeout || underline ) {
          Color saveBackground = gc.getBackground();
          gc.setBackground( gc.getForeground() );
          if ( strikeout )
            gc.fillRectangle( x + offset, y + lineHeight * i + strikeoutOffset, lineWidth, lineThickness );
          if ( underline )
            gc.fillRectangle( x + offset, y + lineHeight * i + underlineOffset, lineWidth, lineThickness );
          gc.setBackground( saveBackground );
        }
      }
    }
    finally {
      restoreGC( gc, oldFont, oldForeground, oldBackground );
    }
  }

  private void restoreGC( final GC gc, Font font, Color foreground, Color background ) {
    gc.setFont( font );
    gc.setForeground( foreground );
    gc.setBackground( background );
  }

  private int getHorzAlignmentOffset( int align, int lineWidth, int totalWidth ) {
    if ( align == SWT.CENTER )
      return ( totalWidth - lineWidth ) / 2;
    else if ( align == SWT.RIGHT )
      return totalWidth - lineWidth;
    return 0;
  }

  private boolean setBackground( GC gc ) {
    Color background = style.getBackground();
    boolean transparent = ( background == null );
    if ( !transparent )
      gc.setBackground( background );
    return transparent;
  }

  private void setForeground( GC gc ) {
    Color foreground = style.getForeground();
    if ( foreground != null )
      gc.setForeground( foreground );
  }

  private void setFont( GC gc ) {
    Font font = style.getFont();
    if ( font != null )
      gc.setFont( font );
  }

  public void dispose() {
    style.dispose();
  }
}