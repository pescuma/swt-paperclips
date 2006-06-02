/*
 * Created on May 17, 2006
 * Author: Administrator
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

/**
 * A GridLook which draws a border around grid cells, with configurable background colors for body,
 * header, and footer cells. 
 * @author Matthew Hall
 */
public class DefaultGridLook implements GridLook {
  /**
   * Constant cell spacing value indicating that the borders of adjacent cells should overlap so
   * the appear continuous.
   */
  public static final int BORDER_OVERLAP = -1;

  Point cellSpacing = new Point(BORDER_OVERLAP, BORDER_OVERLAP);
  int headerGap = BORDER_OVERLAP;
  int footerGap = BORDER_OVERLAP;

  Border cellBorder = new GapBorder();

  RGB headerBackground = null; // defaults to body background when null
  RGB bodyBackground   = null;
  RGB footerBackground = null; // defaults to body background when null

  /**
   * Constructs a DefaultGridLook with no border, no cell spacing, and no background colors.
   */
  public DefaultGridLook() {}

  /**
   * Constructs a DefaultGridLook with the given cell spacing, and no border or background colors.
   * @param horizontalSpacing the horizontal cell spacing.
   * @param verticalSpacing the vertical cell spacing.
   */
  public DefaultGridLook(int horizontalSpacing, int verticalSpacing) {
    this();
    setCellSpacing(horizontalSpacing, verticalSpacing);
  }

  /**
   * Returns the cell border.  Default is an empty border with no margins.
   * @return the cell border.
   */
  public Border getCellBorder() {
    return cellBorder;
  }

  /**
   * Sets the cell border.
   * @param border the cell border.
   */
  public void setCellBorder(Border border) {
    this.cellBorder = border;
  }

  /**
   * Returns the border spacing, in points, between adjacent grid cells.  Default is
   * (x=BORDER_OVERLAP, y=BORDER_OVERLAP).
   * @return the border spacing, in points, between adjacent grid cells.
   */
  public Point getCellSpacing() {
    return new Point(cellSpacing.x, cellSpacing.y);
  }

  /**
   * Sets the border spacing, in points, between adjacent grid cells.  A value of
   * {@link #BORDER_OVERLAP} causes the borders to overlap, making the border appear continuous
   * throughout the grid.  A value of 0 or more causes the cell borders to be spaced that many
   * points apart.  72 points = 1". 
   * @param cellSpacing a point whose x and y elements indicate the horizontal and vertical spacing
   *        between grid cells.
   */
  public void setCellSpacing(Point cellSpacing) {
    setCellSpacing(cellSpacing.x, cellSpacing.y);
  }

  /**
   * Sets the border spacing, in points, between adjacent grid cells.  A value of
   * {@link #BORDER_OVERLAP} causes the borders to overlap, making the border appear continuous
   * throughout the grid.  A value of 0 or more causes the cell borders to be spaced that many
   * points apart.  72 points = 1".
   * @param horizontal the horizontal cell spacing.
   * @param vertical the vertical cell spacing.
   */
  public void setCellSpacing(int horizontal, int vertical) {
    if (horizontal == BORDER_OVERLAP || horizontal >= 0)
      this.cellSpacing.x = horizontal;
    if (vertical == BORDER_OVERLAP || vertical >= 0)
      this.cellSpacing.y = vertical;
  }

  /**
   * Returns the header background color.  If null, the body background color is used.  Default is
   * null.
   * @return the header background color.
   */
  public RGB getHeaderBackground() {
    return headerBackground;
  }

  /**
   * Sets the header background color.
   * @param headerBackground the new background color.  If null, the body background color will be
   *        used.
   */
  public void setHeaderBackground(RGB headerBackground) {
    this.headerBackground = headerBackground;
  }

  /**
   * Returns the vertical gap between the header and body cells.  Default is BORDER_OVERLAP.
   * @return the vertical gap between the header and body cells.
   */
  public int getHeaderGap() {
    return headerGap;
  }

  /**
   * Sets the vertical gap between the header and body cells.  A value of {@link #BORDER_OVERLAP}
   * causes the borders to overlap, making the border appear continuous in the transition from the
   * header cells to the body cells.
   * @param headerGap the new header gap.
   */
  public void setHeaderGap(int headerGap) {
    this.headerGap = headerGap;
  }

  /**
   * Returns the body background color.  Default is null (no background color).  
   * @return the body background color.
   */
  public RGB getBodyBackground() {
    return bodyBackground;
  }

  /**
   * Sets the body background color.
   * @param bodyBackground the new background color.
   */
  public void setBodyBackground(RGB bodyBackground) {
    this.bodyBackground = bodyBackground;
  }

  /**
   * Returns the vertical gap between the body and footer cells.  Default is BORDER_OVERLAP.  
   * @return the vertical gap between the header and body cells.
   */
  public int getFooterGap() {
    return footerGap;
  }

  /**
   * Sets the vertical gap between the header and body cells.  A value of {@link #BORDER_OVERLAP}
   * causes the borders to overlap, making the border appear continuous in the transition from the
   * body cells to the footer cells.
   * @param footerGap
   */
  public void setFooterGap(int footerGap) {
    this.footerGap = footerGap;
  }

  /**
   * Returns the footer background color.  If null, the body background color is used.  Default is
   * null.
   * @return the footer background color.
   */
  public RGB getFooterBackground() {
    return footerBackground;
  }

  /**
   * Sets the footer background color.
   * @param footerBackground the new background color.  If null, the body background color will be
   *        used.
   */
  public void setFooterBackground(RGB footerBackground) {
    this.footerBackground = footerBackground;
  }

  public GridLookPainter getPainter (Device device, GC gc) {
    return new DefaultGridLookPainter(this, device, gc);
  }
}

class DefaultGridLookPainter implements GridLookPainter {
  private final Device device;

  private final BorderPainter border;

  private final RGB headerBackground;
  private final RGB bodyBackground;
  private final RGB footerBackground;

  private final GridMargins margins;

  DefaultGridLookPainter(DefaultGridLook look, Device device, GC gc) {
    if (look == null || device == null || gc == null)
      throw new NullPointerException();

    this.device = device;

    this.border = look.cellBorder.createPainter (device, gc);
    Point dpi = device.getDPI();
    Point cellSpacing = new Point(
        border.getWidth() +
        (look.cellSpacing.x == DefaultGridLook.BORDER_OVERLAP
          ? -border.getOverlap().x
          : dpi.x * look.cellSpacing.x / 72),
        border.getHeight(false, false) +
        (look.cellSpacing.y == DefaultGridLook.BORDER_OVERLAP
          ? -border.getOverlap().y
          : dpi.y * look.cellSpacing.y / 72) );
    int headerSpacing =
        border.getHeight(false, false) +
        (look.headerGap == DefaultGridLook.BORDER_OVERLAP
          ? -border.getOverlap().y
          : dpi.y * look.headerGap / 72);
    int footerSpacing =
        border.getHeight(false, false) +
        (look.footerGap == DefaultGridLook.BORDER_OVERLAP
          ? -border.getOverlap().y
          : dpi.y * look.footerGap / 72);

    this.margins = new DefaultGridMargins(border,
                                          cellSpacing,
                                          headerSpacing,
                                          footerSpacing);

    this.bodyBackground   = look.bodyBackground;
    this.headerBackground = look.headerBackground;
    this.footerBackground = look.footerBackground;
  }

  public GridMargins getMargins () {
    return margins;
  }

  public void paint (final GC      gc,
                     final int     x,
                     final int     y,
                     final int[]   columns,
                     final int[]   headerRows,
                     final int[][] headerCellSpans,
                     final boolean topOpen,
                     final int[]   bodyRows,
                     final int[][] bodyCellSpans,
                     final boolean bottomOpen,
                     final int[]   footerRows,
                     final int[][] footerCellSpans) {

    final boolean headerPresent = headerRows.length > 0;
    final boolean footerPresent = footerRows.length > 0;

    final Color bodyBackground;
    final Color headerBackground;
    final Color footerBackground;

    bodyBackground = this.bodyBackground == null ?
        null :
        new Color(device, this.bodyBackground);
    headerBackground = headerPresent ? 
        (this.headerBackground == null ?
            bodyBackground :
            new Color(device, this.headerBackground) ) :
        null;
    footerBackground = footerPresent ?
        (this.footerBackground == null ?
            bodyBackground :
            new Color(device, this.footerBackground) ) :
        null;

    // Cursor variables
    int X;
    int Y = y;

    try {
      // HEADER LOOK
      if (headerPresent) {
        Y += margins.getHeaderTop();

        for (int row = 0; row < headerRows.length; row++) {
          X = x + margins.getLeft();

          int col = 0;

          // Height of all cells on current row.
          final int H = headerRows[row];

          for (int cellSpan : headerCellSpans[row]) {
            // Compute cellspan width.
            int W = (cellSpan - 1) * margins.getHorizontalSpacing();
            for (int i = 0; i < cellSpan; i++)
              W += columns[col+i];

            // Paint background
            if (headerBackground != null) {
              Color oldBackground = gc.getBackground ();
              gc.setBackground(headerBackground);
              gc.fillRectangle (X-border.getLeft(),
                                Y-border.getTop(false),
                                W+border.getWidth(),
                                H+border.getHeight(false, false) );
              gc.setBackground(oldBackground);
            }

            // Paint border
            border.paint(gc,
                         X-border.getLeft(),
                         Y-border.getTop(false),
                         W+border.getWidth(),
                         H+border.getHeight(false, false),
                         false,
                         false);

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
      Y += margins.getBodyTop(headerPresent, topOpen);
      for (int row = 0; row < bodyRows.length; row++) {
        X = x + margins.getLeft();

        int col = 0;

        // Height of all cells on current row.
        final int H = bodyRows[row];

        final boolean rowTopOpen = row == 0 ? topOpen : false;
        final boolean rowBottomOpen = row == bodyRows.length - 1 ? bottomOpen : false;

        for (int cellSpan : bodyCellSpans[row]) {
          // Compute cellspan width.
          int W = (cellSpan - 1) * margins.getHorizontalSpacing();
          for (int i = 0; i < cellSpan; i++)
            W += columns[col+i];

          // Paint background
          if (bodyBackground != null) {
            Color oldBackground = gc.getBackground ();
            gc.setBackground(bodyBackground);
            gc.fillRectangle (X-border.getLeft(),
                              Y-border.getTop(rowTopOpen),
                              W+border.getWidth(),
                              H+border.getHeight(rowTopOpen, rowBottomOpen) );
            gc.setBackground(oldBackground);
          }

          // Paint border
          border.paint(gc,
                       X-border.getLeft(),
                       Y-border.getTop(rowTopOpen),
                       W+border.getWidth(),
                       H+border.getHeight(rowTopOpen, rowBottomOpen),
                       rowTopOpen,
                       rowBottomOpen);

          // Advance horizontal cursors
          col += cellSpan;
          X += W + margins.getHorizontalSpacing();
        }

        // Advanced vertical cursor
        Y += H + margins.getBodyVerticalSpacing();
      }
      Y -= margins.getBodyVerticalSpacing();
      Y += margins.getBodyBottom(footerPresent, bottomOpen);

      // FOOTER LOOK
      if (footerPresent) {
        for (int row = 0; row < footerRows.length; row++) {
          X = x + margins.getLeft();

          int col = 0;

          // Height of all cells on current row.
          final int H = footerRows[row];
          for (int cellSpan : footerCellSpans[row]) {
            // Compute cellspan width.
            int W = (cellSpan - 1) * margins.getHorizontalSpacing();
            for (int i = 0; i < cellSpan; i++)
              W += columns[col+i];

            // Paint background
            if (footerBackground != null) {
              Color oldBackground = gc.getBackground ();
              gc.setBackground(footerBackground);
              gc.fillRectangle (X-border.getLeft(),
                                Y-border.getTop(false),
                                W+border.getWidth(),
                                H+border.getHeight(false, false));
              gc.setBackground(oldBackground);
            }

            // Paint border
            border.paint(gc,
                         X-border.getLeft(),
                         Y-border.getTop(false),
                         W+border.getWidth(),
                         H+border.getHeight(false, false),
                         false,
                         false);

            // Advance horizontal cursors
            col += cellSpan;
            X += W + margins.getHorizontalSpacing();
          }

          // Advanced vertical cursor
          Y += H + margins.getFooterVerticalSpacing();
        }
      }
    } finally {
      if (bodyBackground != null)
        bodyBackground.dispose();
      if (headerBackground != null && headerBackground != bodyBackground)
        headerBackground.dispose();
      if (footerBackground != null && footerBackground != bodyBackground)
        footerBackground.dispose();
    }
  }

  public void dispose() {
    border.dispose();
  }

  static class DefaultGridMargins implements GridMargins {
    private final BorderPainter border;
    private final Point cellSpacing;
    private final int headerSpacing;
    private final int footerSpacing;

    DefaultGridMargins(BorderPainter border,
                       Point cellSpacing,
                       int headerSpacing,
                       int footerSpacing) {
      this.border = border;
      this.cellSpacing = cellSpacing;
      this.headerSpacing = headerSpacing;
      this.footerSpacing = footerSpacing;
    }

    public int getLeft() {
      return border.getLeft();
    }

    public int getHorizontalSpacing() {
      return cellSpacing.x;
    }

    public int getRight() {
      return border.getRight();
    }

    public int getHeaderTop() {
      return border.getTop(false);
    }

    public int getHeaderVerticalSpacing() {
      return cellSpacing.y;
    }

    public int getBodyTop(boolean headerPresent, boolean open) {
      if (headerPresent) {
        if (open)
          return border.getBottom(false) + border.getTop(true);
        return headerSpacing;
      }
      return border.getTop(open);
    }

    public int getBodyVerticalSpacing() {
      return cellSpacing.y;
    }

    public int getBodyBottom(boolean footerPresent, boolean open) {
      if (footerPresent) {
        if (open)
          return border.getBottom(true) + border.getTop(false);
        return footerSpacing;
      }
      return border.getBottom(open);
    }

    public int getFooterVerticalSpacing() {
      return cellSpacing.y;
    }

    public int getFooterBottom() {
      return border.getBottom(false);
    }
  }
}
