/************************************************************************************************************
 * Copyright (c) 2007 Woodcraft Mill & Cabinet Corporation and others. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/

package net.sf.paperclips;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

class DefaultGridLookPainter extends BasicGridLookPainter {
  private final Rectangle              cellPadding;

  private final BorderPainter          border;

  private final CellBackgroundProvider headerBackground;
  private final CellBackgroundProvider bodyBackground;
  private final CellBackgroundProvider footerBackground;

  private final GridMargins            margins;

  DefaultGridLookPainter( DefaultGridLook look, Device device, GC gc ) {
    super( device );

    Point dpi = device.getDPI();

    this.border = look.cellBorder.createPainter( device, gc );
    this.cellPadding = calculateCellPadding( look, dpi );
    this.margins = calculateGridMargins( look, dpi );

    this.bodyBackground = look.bodyBackgroundProvider;
    this.headerBackground = look.headerBackgroundProvider;
    this.footerBackground = look.footerBackgroundProvider;
  }

  private Rectangle calculateCellPadding( DefaultGridLook look, Point dpi ) {
    Rectangle cellPadding = new Rectangle( look.cellPadding.x * dpi.x / 72,
                                           look.cellPadding.y * dpi.y / 72,
                                           look.cellPadding.width * dpi.x / 72,
                                           look.cellPadding.height * dpi.y / 72 );
    return cellPadding;
  }

  private GridMargins calculateGridMargins( DefaultGridLook look, Point dpi ) {
    final Point cellSpacing = new Point( border.getWidth()
        + ( look.cellSpacing.x == DefaultGridLook.BORDER_OVERLAP ? -border.getOverlap().x : dpi.x
            * look.cellSpacing.x / 72 ), border.getHeight( false, false )
        + ( look.cellSpacing.y == DefaultGridLook.BORDER_OVERLAP ? -border.getOverlap().y : dpi.y
            * look.cellSpacing.y / 72 ) );

    final int headerClosedSpacing = border.getHeight( false, false )
        + ( look.headerGap == DefaultGridLook.BORDER_OVERLAP ? -border.getOverlap().y : dpi.y
            * look.headerGap / 72 );
    final int headerOpenSpacing = border.getHeight( true, false )
        + ( look.headerGap == DefaultGridLook.BORDER_OVERLAP ? dpi.y / 72 : dpi.y * look.headerGap / 72 );
    final int footerClosedSpacing = border.getHeight( false, false )
        + ( look.footerGap == DefaultGridLook.BORDER_OVERLAP ? -border.getOverlap().y : dpi.y
            * look.footerGap / 72 );
    final int footerOpenSpacing = border.getHeight( false, true )
        + ( look.footerGap == DefaultGridLook.BORDER_OVERLAP ? dpi.y / 72 : dpi.y * look.footerGap / 72 );

    return new DefaultGridMargins( border,
                                   cellSpacing,
                                   cellPadding,
                                   headerClosedSpacing,
                                   headerOpenSpacing,
                                   footerClosedSpacing,
                                   footerOpenSpacing );
  }

  public GridMargins getMargins() {
    return margins;
  }

  final Map colorMap = new HashMap();

  // colorMap maps RGBs to Color instances to avoid creating a lot of Color objects on the device.
  private Color getColor( RGB rgb ) {
    if ( rgb == null )
      return null;

    Color result = (Color) colorMap.get( rgb );
    if ( result == null ) {
      result = new Color( device, rgb );
      colorMap.put( new RGB( rgb.red, rgb.green, rgb.blue ), result );
    }
    return result;
  }

  protected void paintHeaderCell( GC gc, Rectangle bounds, int row, int col, int colspan ) {
    RGB background = headerBackground.getCellBackground( row, col, colspan );
    paintCell( gc, background, bounds, false, false );
  }

  protected void paintBodyCell( GC gc,
                                Rectangle bounds,
                                int row,
                                int col,
                                int colspan,
                                boolean topOpen,
                                boolean bottomOpen ) {
    RGB background = bodyBackground.getCellBackground( row, col, colspan );
    paintCell( gc, background, bounds, topOpen, bottomOpen );
  }

  protected void paintFooterCell( GC gc, Rectangle bounds, int row, int col, int colspan ) {
    RGB background = footerBackground.getCellBackground( row, col, colspan );
    paintCell( gc, background, bounds, false, false );
  }

  private void paintCell( GC gc, RGB background, Rectangle bounds, boolean topOpen, boolean bottomOpen ) {
    // Compute effective cell rectangle
    int x = bounds.x - border.getLeft() - cellPadding.x;
    int y = bounds.y - border.getTop( topOpen ) - ( topOpen ? 0 : cellPadding.y );
    int width = bounds.width + border.getWidth() + cellPadding.x + cellPadding.width;
    int height = bounds.height + border.getHeight( topOpen, bottomOpen )
        + ( bottomOpen ? 0 : cellPadding.y + cellPadding.height );

    // Paint background
    Color backgroundColor = getColor( background );
    if ( backgroundColor != null ) {
      Color oldBackground = gc.getBackground();
      gc.setBackground( backgroundColor );
      gc.fillRectangle( x, y, width, height );
      gc.setBackground( oldBackground );
    }

    // Paint border
    border.paint( gc, x, y, width, height, topOpen, bottomOpen );
  }

  public void dispose() {
    for ( Iterator iter = colorMap.entrySet().iterator(); iter.hasNext(); ) {
      Map.Entry entry = (Map.Entry) iter.next();
      Color color = (Color) entry.getValue();
      color.dispose();
      iter.remove();
    }
    border.dispose();
  }
}