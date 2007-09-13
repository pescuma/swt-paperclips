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
    this.look = look;
    this.columns = defensiveCopy( colSizes );
    this.headerRows = defensiveCopy( headerRows );
    this.headerColSpans = defensiveCopy( headerColSpans );

    this.firstRowIndex = firstRowIndex;
    this.topOpen = topOpen;
    this.bodyRows = defensiveCopy( bodyRows );
    this.bodyColSpans = defensiveCopy( bodyColSpans );
    this.bottomOpen = bottomOpen;

    this.footerRows = defensiveCopy( footerRows );
    this.footerColSpans = defensiveCopy( footerColSpans );

    GridMargins margins = look.getMargins();

    Point size = calculateSize( margins, colSizes, headerRows, topOpen, bodyRows, bottomOpen, footerRows );
    this.size = size;
  }

  private static int[][] defensiveCopy( int[][] colSpans ) {
    colSpans = (int[][]) colSpans.clone();
    for ( int rowIndex = 0; rowIndex < colSpans.length; rowIndex++ )
      colSpans[rowIndex] = defensiveCopy( colSpans[rowIndex] );
    return colSpans;
  }

  private static int[] defensiveCopy( int[] colSpans ) {
    return (int[]) colSpans.clone();
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

    int width = margins.getLeft() + margins.getHorizontalSpacing() * ( columns.length - 1 )
        + margins.getRight() + sum( columns );

    int height = margins.getBodyTop( headerPresent, topOpen ) + margins.getBodyVerticalSpacing()
        * ( bodyRows.length - 1 ) + margins.getBodyBottom( footerPresent, bottomOpen ) + sum( bodyRows );
    if ( headerPresent )
      height += margins.getHeaderTop() + margins.getHeaderVerticalSpacing() * ( headerRows.length - 1 )
          + sum( headerRows );
    if ( footerPresent )
      height += margins.getFooterVerticalSpacing() * ( footerRows.length - 1 ) + margins.getFooterBottom()
          + sum( footerRows );

    return new Point( width, height );
  }

  private static int sum( int[] elements ) {
    int sum = 0;
    for ( int i = 0; i < elements.length; i++ )
      sum += elements[i];
    return sum;
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
