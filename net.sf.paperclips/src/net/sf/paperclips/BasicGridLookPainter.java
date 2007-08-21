/************************************************************************************************************
 * Copyright (c) 2007 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public abstract class BasicGridLookPainter implements GridLookPainter {
  protected final Device device;

  public BasicGridLookPainter( Device device ) {
    if ( device == null )
      throw new NullPointerException();
    this.device = device;
  }

  public void paint( GC gc,
                     int x,
                     int y,
                     int[] columns,
                     int[] headerRows,
                     int[][] headerCellSpans,
                     int firstRowIndex,
                     boolean topOpen,
                     int[] bodyRows,
                     int[][] bodyCellSpans,
                     boolean bottomOpen,
                     int[] footerRows,
                     int[][] footerCellSpans ) {
    GridMargins margins = getMargins();

    final boolean headerPresent = headerRows.length > 0;
    final boolean footerPresent = footerRows.length > 0;

    // Cursor variables
    int X;
    int Y = y;

    // HEADER LOOK
    if ( headerPresent ) {
      Y += margins.getHeaderTop();

      for ( int rowIndex = 0; rowIndex < headerRows.length; rowIndex++ ) {
        X = x + margins.getLeft();

        int col = 0;

        // Height of all cells on current row.
        final int H = headerRows[rowIndex];

        for ( int cellIndex = 0; cellIndex < headerCellSpans[rowIndex].length; cellIndex++ ) {
          int cellSpan = headerCellSpans[rowIndex][cellIndex];

          // Compute cellspan width.
          int W = ( cellSpan - 1 ) * margins.getHorizontalSpacing();
          for ( int j = 0; j < cellSpan; j++ )
            W += columns[col + j];

          paintHeaderCell( gc, new Rectangle( X, Y, W, H ), rowIndex, col, cellSpan );

          // Advance horizontal cursors
          col += cellSpan;
          X += W + margins.getHorizontalSpacing();
        }

        // Advanced vertical cursor
        Y += H + margins.getHeaderVerticalSpacing();
      }
      // After all header rows, subtract the header row spacing added in the last row.
      Y -= margins.getHeaderVerticalSpacing();
    }

    // BODY LOOK
    Y += margins.getBodyTop( headerPresent, topOpen );
    for ( int rowIndex = 0; rowIndex < bodyRows.length; rowIndex++ ) {
      X = x + margins.getLeft();

      int col = 0;

      // Height of all cells on current row.
      final int H = bodyRows[rowIndex];

      final boolean rowTopOpen = rowIndex == 0 ? topOpen : false;
      final boolean rowBottomOpen = rowIndex == bodyRows.length - 1 ? bottomOpen : false;

      for ( int cellIndex = 0; cellIndex < bodyCellSpans[rowIndex].length; cellIndex++ ) {
        int cellSpan = bodyCellSpans[rowIndex][cellIndex];

        // Compute cellspan width.
        int W = ( cellSpan - 1 ) * margins.getHorizontalSpacing();
        for ( int j = 0; j < cellSpan; j++ )
          W += columns[col + j];

        paintBodyCell( gc,
                       new Rectangle( X, Y, W, H ),
                       firstRowIndex + rowIndex,
                       col,
                       cellSpan,
                       rowTopOpen,
                       rowBottomOpen );

        // Advance horizontal cursors
        col += cellSpan;
        X += W + margins.getHorizontalSpacing();
      }

      // Advanced vertical cursor
      Y += H + margins.getBodyVerticalSpacing();
    }
    Y -= margins.getBodyVerticalSpacing();
    Y += margins.getBodyBottom( footerPresent, bottomOpen );

    // FOOTER LOOK
    if ( footerPresent ) {
      for ( int rowIndex = 0; rowIndex < footerRows.length; rowIndex++ ) {
        X = x + margins.getLeft();

        int col = 0;

        // Height of all cells on current row.
        final int H = footerRows[rowIndex];
        for ( int cellIndex = 0; cellIndex < footerCellSpans[rowIndex].length; cellIndex++ ) {
          int cellSpan = footerCellSpans[rowIndex][cellIndex];

          // Compute cellspan width.
          int W = ( cellSpan - 1 ) * margins.getHorizontalSpacing();
          for ( int j = 0; j < cellSpan; j++ )
            W += columns[col + j];

          paintFooterCell( gc, new Rectangle( X, Y, W, H ), rowIndex, col, cellSpan );

          // Advance horizontal cursors
          col += cellSpan;
          X += W + margins.getHorizontalSpacing();
        }

        // Advanced vertical cursor
        Y += H + margins.getFooterVerticalSpacing();
      }
    }
  }

  /**
   * Paint the decorations for the described header cell.
   * @param gc the graphics context to use for painting.
   * @param bounds the bounds of the cell, excluding margins.
   * @param row the row offset of the cell within the header.
   * @param col the column offset of the cell within the header.
   * @param colspan the number of columns that this cell spans.
   */
  protected abstract void paintHeaderCell( GC gc, Rectangle bounds, int row, int col, int colspan );

  /**
   * Paint the decorations for the described body cell.
   * @param gc the graphics context to use for painting.
   * @param bounds the bounds of the cell, excluding margins.
   * @param row the row offset of the cell within the header.
   * @param col the column offset of the cell within the header.
   * @param colspan the number of columns that this cell spans.
   * @param topOpen whether the cell should be drawn with the top edge of the cell border "open." An open top
   *        border is a visual cue that the cell is being continued from the previous page.
   * @param bottomOpen whether the cell should be drawn with the bottom edge of the cell border "open." An
   *        open bottom border is a visual cue that the cell will be continued on the next page.
   */
  protected abstract void paintBodyCell( GC gc,
                                         Rectangle bounds,
                                         int row,
                                         int col,
                                         int colspan,
                                         boolean topOpen,
                                         boolean bottomOpen );

  /**
   * Paint the decorations for the described footer cell.
   * @param gc the graphics context to use for painting.
   * @param bounds the bounds of the cell, excluding margins.
   * @param row the row offset of the cell within the header.
   * @param col the column offset of the cell within the header.
   * @param colspan the number of columns that this cell spans.
   */
  protected abstract void paintFooterCell( GC gc, Rectangle bounds, int row, int col, int colspan );
}