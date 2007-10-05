/************************************************************************************************************
 * Copyright (c) 2007 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import net.sf.paperclips.internal.ArrayUtil;
import net.sf.paperclips.internal.NullUtil;

class GridLookPainterPiece implements PrintPiece {
  final GridLookPainter look;

  final int[]           columns;
  final int[]           headerRows;
  final int[][]         headerColSpans;
  final int             firstRowIndex;
  final boolean         topOpen;
  final int[]           bodyRows;
  final int[][]         bodyColSpans;
  final boolean         bottomOpen;
  final int[]           footerRows;
  final int[][]         footerColSpans;

  final Point           size;

  GridLookPainterPiece( GridLookPainter look,
                        int[] colSizes,
                        int[] headerRows,
                        int[][] headerColSpans,
                        int firstRowIndex,
                        boolean topOpen,
                        int[] bodyRows,
                        int[][] bodyColSpans,
                        boolean bottomOpen,
                        int[] footerRows,
                        int[][] footerColSpans ) {
    NullUtil.notNull( look );

    this.look = look;
    this.columns = ArrayUtil.defensiveCopy( colSizes );
    this.headerRows = ArrayUtil.defensiveCopy( headerRows );
    this.headerColSpans = ArrayUtil.defensiveCopy( headerColSpans );

    this.firstRowIndex = firstRowIndex;
    this.topOpen = topOpen;
    this.bodyRows = ArrayUtil.defensiveCopy( bodyRows );
    this.bodyColSpans = ArrayUtil.defensiveCopy( bodyColSpans );
    this.bottomOpen = bottomOpen;

    this.footerRows = ArrayUtil.defensiveCopy( footerRows );
    this.footerColSpans = ArrayUtil.defensiveCopy( footerColSpans );

    GridMargins margins = look.getMargins();

    Point size = calculateSize( margins, colSizes, headerRows, topOpen, bodyRows, bottomOpen, footerRows );
    this.size = size;
  }

  private static Point calculateSize( GridMargins margins,
                                      int[] columns,
                                      int[] headerRows,
                                      boolean topOpen,
                                      int[] bodyRows,
                                      boolean bottomOpen,
                                      int[] footerRows ) {
    final boolean headerPresent = headerRows.length > 0;
    final boolean footerPresent = footerRows.length > 0;

    int width = calculateWidth( margins, columns );

    int height = calculateBodyHeight( margins, topOpen, bodyRows, bottomOpen, headerPresent, footerPresent );
    if ( headerPresent )
      height += calculateHeaderHeight( margins, headerRows );
    if ( footerPresent )
      height += calculateFooterHeight( margins, footerRows );

    return new Point( width, height );
  }

  private static int calculateWidth( GridMargins margins, int[] columns ) {
    return margins.getLeft() + margins.getHorizontalSpacing() * ( columns.length - 1 ) + margins.getRight()
        + ArrayUtil.sum( columns );
  }

  private static int calculateBodyHeight( GridMargins margins,
                                          boolean topOpen,
                                          int[] bodyRows,
                                          boolean bottomOpen,
                                          final boolean headerPresent,
                                          final boolean footerPresent ) {
    return margins.getBodyTop( headerPresent, topOpen ) + margins.getBodyVerticalSpacing()
        * ( bodyRows.length - 1 ) + margins.getBodyBottom( footerPresent, bottomOpen )
        + ArrayUtil.sum( bodyRows );
  }

  private static int calculateHeaderHeight( GridMargins margins, int[] headerRows ) {
    return margins.getHeaderTop() + margins.getHeaderVerticalSpacing() * ( headerRows.length - 1 )
        + ArrayUtil.sum( headerRows );
  }

  private static int calculateFooterHeight( GridMargins margins, int[] footerRows ) {
    return margins.getFooterVerticalSpacing() * ( footerRows.length - 1 ) + margins.getFooterBottom()
        + ArrayUtil.sum( footerRows );
  }

  public void dispose() {
    look.dispose();
  }

  public Point getSize() {
    return new Point( size.x, size.y );
  }

  public void paint( GC gc, int x, int y ) {
    look.paint( gc,
                x,
                y,
                columns,
                headerRows,
                headerColSpans,
                firstRowIndex,
                topOpen,
                bodyRows,
                bodyColSpans,
                bottomOpen,
                footerRows,
                footerColSpans );
  }
}
