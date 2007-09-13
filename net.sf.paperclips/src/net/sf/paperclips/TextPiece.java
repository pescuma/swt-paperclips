/************************************************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

class TextPiece extends AbstractPiece implements TextPrintPiece {
  private final String[]          lines;
  private final TextStyleResource style;
  private final int               ascent;

  TextPiece( Device device, Point size, TextIterator iter, String[] text, int ascent ) {
    super( iter, size );
    this.lines = text;
    this.style = new TextStyleResource( device, iter.style );
    this.ascent = ascent;
  }

  public int getAscent() {
    return ascent;
  }

  public void paint( final GC gc, final int x, final int y ) {
    Font oldFont = gc.getFont();
    Color oldForeground = gc.getForeground();
    Color oldBackground = gc.getBackground();

    Point size = getSize();

    int align = style.getAlignment();

    try {
      setFont( gc );
      setForeground( gc );
      boolean transparent = setBackground( gc );

      FontMetrics fm = gc.getFontMetrics();

      boolean strikeout = style.getStrikeout();
      boolean underline = style.getUnderline();
      int lineThickness = strikeout || underline ? Math.max( 1, fm.getDescent() / 3 ) : 1;

      int strikeoutOffset = strikeout ? fm.getLeading() + fm.getAscent() / 2 : 0;
      int underlineOffset = ascent + lineThickness;

      final int lineHeight = fm.getHeight();

      for ( int i = 0; i < lines.length; i++ ) {
        String line = lines[i];
        int lineWidth = gc.stringExtent( line ).x;
        int offset = getHorzAlignmentOffset( align, lineWidth, size.x );

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
      gc.setFont( oldFont );
      gc.setForeground( oldForeground );
      gc.setBackground( oldBackground );
    }
  }

  private int getHorzAlignmentOffset( int align, int lineWidth, int totalWidth ) {
    int offset = 0;
    if ( align != SWT.LEFT ) {
      if ( align == SWT.CENTER )
        offset = ( totalWidth - lineWidth ) / 2;
      else if ( align == SWT.RIGHT )
        offset = totalWidth - lineWidth;
    }
    return offset;
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