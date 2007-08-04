/*******************************************************************************
 * Copyright (c) 2006 Woodcraft Mill & Cabinet Corporation.  All rights
 * reserved.  This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ******************************************************************************/
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
  Rectangle cellPadding = new Rectangle(0, 0, 0, 0);
  int headerGap = BORDER_OVERLAP;
  int footerGap = BORDER_OVERLAP;

  Border cellBorder = new GapBorder();

  DefaultCellBackgroundProvider defaultBodyBackgroundProvider;
  DefaultCellBackgroundProvider defaultHeaderBackgroundProvider;
  DefaultCellBackgroundProvider defaultFooterBackgroundProvider;

  CellBackgroundProvider bodyBackgroundProvider;
  CellBackgroundProvider headerBackgroundProvider;
  CellBackgroundProvider footerBackgroundProvider;

  /**
   * Constructs a DefaultGridLook with no border, no cell spacing, and no background colors.
   */
  public DefaultGridLook() {
    this.bodyBackgroundProvider = defaultBodyBackgroundProvider =
      new DefaultCellBackgroundProvider();
    this.headerBackgroundProvider = defaultHeaderBackgroundProvider =
      new DefaultCellBackgroundProvider(bodyBackgroundProvider);
    this.footerBackgroundProvider = defaultFooterBackgroundProvider =
      new DefaultCellBackgroundProvider(bodyBackgroundProvider);
  }

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
   * Returns a rectangle whose public fields denote the left (x), top (y), right (width) and
   * bottom (height) cell padding, expressed in points.  72 points = 1" = 2.54cm.
   * @return a rectangle whose public fields denote the cell padding at each edge.
   */
  public Rectangle getCellPadding() {
  	return new Rectangle(cellPadding.x, cellPadding.y, cellPadding.width, cellPadding.height);
  }

  /**
   * Sets the cell padding to the values in the public fields of the argument.
   * @param cellPadding the new cell padding.
   */
  public void setCellPadding(Rectangle cellPadding) {
  	setCellPadding(cellPadding.x, cellPadding.y, cellPadding.width, cellPadding.height);
  }

  /**
   * Sets the cell padding to the given horizontal and vertical values.  This is equivalent to
   * calling setCellPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding).
   * @param horizontalPadding the amount of padding to add to the left and right of each cell, in
   *        points.
   * @param verticalPadding the amount padding to add to the top and bottom each cell, in points.
   */
  public void setCellPadding(int horizontalPadding, int verticalPadding) {
  	setCellPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
  }

  /**
   * Sets the cell padding to the specified values.
   * @param left the left cell padding, in points.
   * @param top the top cell padding, in points.
   * @param right the right cell padding, in points.
   * @param bottom the bottom cell padding, in points.
   */
  public void setCellPadding(int left, int top, int right, int bottom) {
  	cellPadding.x = left;
  	cellPadding.y = top;
  	cellPadding.width = right;
  	cellPadding.height = bottom;
  }
  
  /**
   * Returns the header background color.  If null, the body background color is used.  Default is
   * null.
   * @return the header background color.
   */
  public RGB getHeaderBackground() {
    return defaultHeaderBackgroundProvider.getBackground();
  }

  /**
   * Sets the header background color.  Calls to this method override any previous calls to
   * setHeaderBackgroundProvider(...). 
   * @param headerBackground the new background color.  If null, the body background color will be
   *        used.
   */
  public void setHeaderBackground(RGB headerBackground) {
    defaultHeaderBackgroundProvider.setBackground(headerBackground);
    this.headerBackgroundProvider = defaultHeaderBackgroundProvider;
  }

  /**
   * Returns the header background color provider.
   * @return the header background color provider.
   */
  public CellBackgroundProvider getHeaderBackgroundProvider() {
    return headerBackgroundProvider;
  }

  /**
   * Sets the header background color provider.  Calls to this method override any previous calls
   * to setHeaderBackground(RGB).  Setting this property to null restores the default background
   * provider.
   * @param headerBackgroundProvider the new background color provider.
   */
  public void setHeaderBackgroundProvider(CellBackgroundProvider headerBackgroundProvider) {
    this.headerBackgroundProvider = headerBackgroundProvider == null ?
        defaultHeaderBackgroundProvider : headerBackgroundProvider;
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
    return defaultBodyBackgroundProvider.getBackground();
  }

  /**
   * Sets the body background color.  Calls to this method override any previous calls to
   * setBodyBackgroundProvider(...).
   * @param bodyBackground the new background color.
   */
  public void setBodyBackground(RGB bodyBackground) {
    defaultBodyBackgroundProvider.setBackground(bodyBackground);
    this.bodyBackgroundProvider = defaultBodyBackgroundProvider;
  }

  /**
   * Returns the body background color provider.
   * @return the body background color provider.
   */
  public CellBackgroundProvider getBodyBackgroundProvider() {
    return bodyBackgroundProvider;
  }

  /**
   * Sets the body background color provider.  Calls to this method override any previous calls to
   * setBodyBackground(RGB).  Setting this property to null restores the default background
   * provider.
   * @param bodyBackgroundProvider the new background color provider.
   */
  public void setBodyBackgroundProvider(CellBackgroundProvider bodyBackgroundProvider) {
    this.bodyBackgroundProvider = bodyBackgroundProvider == null ?
        defaultBodyBackgroundProvider : bodyBackgroundProvider;
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
    return defaultFooterBackgroundProvider.getBackground();
  }

  /**
   * Sets the footer background color.  Calls to this method override any previous calls to
   * setFooterBackgroundProvider(...).
   * @param footerBackground the new background color.  If null, the body background color will be
   *        used.
   */
  public void setFooterBackground(RGB footerBackground) {
    defaultFooterBackgroundProvider.setBackground(footerBackground);
    this.footerBackgroundProvider = defaultFooterBackgroundProvider;
  }

  /**
   * Returns the footer background color provider.
   * @return the footer background color provider.
   */
  public CellBackgroundProvider getFooterBackgroundProvider() {
    return footerBackgroundProvider;
  }

  /**
   * Sets the footer background color provider.  Calls to this method override any previous calls
   * to setFooterBackground(RGB).  Setting this property to null restores the default background
   * provider.
   * @param footerBackgroundProvider the new background color provider.
   */
  public void setFooterBackgroundProvider(CellBackgroundProvider footerBackgroundProvider) {
    this.footerBackgroundProvider = footerBackgroundProvider == null ?
        defaultFooterBackgroundProvider : footerBackgroundProvider;
  }

  public GridLookPainter getPainter (Device device, GC gc) {
    return new DefaultGridLookPainter(this, device, gc);
  }
}

class DefaultGridLookPainter extends BasicGridLookPainter {
  private final Rectangle cellPadding;

  private final BorderPainter border;

  private final CellBackgroundProvider headerBackground;
  private final CellBackgroundProvider bodyBackground;
  private final CellBackgroundProvider footerBackground;

  private final GridMargins margins;

  DefaultGridLookPainter(DefaultGridLook look, Device device, GC gc) {
  	super(device);

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
    cellPadding = new Rectangle(
    		look.cellPadding.x      * dpi.x / 72,
    		look.cellPadding.y      * dpi.y / 72,
    		look.cellPadding.width  * dpi.x / 72,
    		look.cellPadding.height * dpi.y / 72);
    final int headerClosedSpacing =
      border.getHeight(false, false) +
      (look.headerGap == DefaultGridLook.BORDER_OVERLAP
          ? -border.getOverlap().y
              : dpi.y * look.headerGap / 72);
    final int headerOpenSpacing =
      border.getHeight(true, false) +
      (look.headerGap == DefaultGridLook.BORDER_OVERLAP
        ? dpi.y / 72
        : dpi.y * look.headerGap / 72);
    final int footerClosedSpacing =
      border.getHeight(false, false) +
      (look.footerGap == DefaultGridLook.BORDER_OVERLAP
        ? -border.getOverlap().y
        : dpi.y * look.footerGap / 72);
    final int footerOpenSpacing =
      border.getHeight(false, true) +
      (look.footerGap == DefaultGridLook.BORDER_OVERLAP
        ? dpi.y / 72
        : dpi.y * look.footerGap / 72);

    this.margins = new DefaultGridMargins(border,
                                          cellSpacing,
                                          cellPadding,
                                          headerClosedSpacing,
                                          headerOpenSpacing,
                                          footerClosedSpacing,
                                          footerOpenSpacing);

    this.bodyBackground   = look.bodyBackgroundProvider;
    this.headerBackground = look.headerBackgroundProvider;
    this.footerBackground = look.footerBackgroundProvider;
  }

  public GridMargins getMargins () {
    return margins;
  }

	final Map colorMap = new HashMap();

  // colorMap maps RGBs to Color instances to avoid creating a lot of Color objects on the device.
  private Color getColor(RGB rgb) {
    if (rgb == null) return null;

    Color result = (Color) colorMap.get(rgb);
    if (result == null) {
      result = new Color(device, rgb);
      colorMap.put(new RGB(rgb.red, rgb.green, rgb.blue), result);
    }
    return result;
  }

  protected void paintHeaderCell(GC gc, Rectangle bounds, int row, int col, int colspan) {
  	RGB background = headerBackground.getCellBackground(row, col, colspan);
  	paintCell(gc, background, bounds, false, false);
	}

  protected void paintBodyCell(GC gc, Rectangle bounds, int row, int col, int colspan, boolean topOpen, boolean bottomOpen) {
		RGB background = bodyBackground.getCellBackground(row, col, colspan);
		paintCell(gc, background, bounds, topOpen, bottomOpen);
	}

  protected void paintFooterCell(GC gc, Rectangle bounds, int row, int col, int colspan) {
		RGB background = footerBackground.getCellBackground(row, col, colspan);
		paintCell(gc, background, bounds, false, false);
	}

	public void paintCell(GC gc, RGB background, Rectangle bounds, boolean topOpen, boolean bottomOpen) {
    // Compute effective cell rectangle
    int x = bounds.x-border.getLeft() - cellPadding.x;
		int y = bounds.y-border.getTop(topOpen) - (topOpen ? 0 : cellPadding.y);
		int width = bounds.width+border.getWidth()
				+ cellPadding.x + cellPadding.width;
		int height = bounds.height+border.getHeight(topOpen, bottomOpen)
				+ (bottomOpen ? 0 : cellPadding.y + cellPadding.height);

    // Paint background
    Color backgroundColor = getColor(background);
    if (backgroundColor != null) {
      Color oldBackground = gc.getBackground ();
      gc.setBackground(backgroundColor);
      gc.fillRectangle (x, y, width, height);
      gc.setBackground(oldBackground);
    }

    // Paint border
    border.paint(gc, x, y, width, height, topOpen, bottomOpen);
	}

	public void dispose() {
    for (Iterator iter = colorMap.entrySet().iterator(); iter.hasNext(); ) {
      Map.Entry entry = (Map.Entry) iter.next();
      Color color = (Color) entry.getValue();
      color.dispose();
      iter.remove();
    }
    border.dispose();
  }

  static class DefaultGridMargins implements GridMargins {
    private final BorderPainter border;
    private final Point cellSpacing;
    private final Rectangle cellPadding;
    private final int headerClosedSpacing;
    private final int headerOpenSpacing;
    private final int footerClosedSpacing;
    private final int footerOpenSpacing;

    DefaultGridMargins(BorderPainter border,
                       Point cellSpacing,
                       Rectangle cellPadding,
                       int headerClosedSpacing,
                       int headerOpenSpacing,
                       int footerClosedSpacing,
                       int footerOpenSpacing) {
      this.border = border;
      this.cellSpacing = cellSpacing;
      this.cellPadding = cellPadding;
      this.headerClosedSpacing = headerClosedSpacing;
      this.headerOpenSpacing = headerOpenSpacing;
      this.footerClosedSpacing = footerClosedSpacing;
      this.footerOpenSpacing = footerOpenSpacing;
    }

    public int getLeft() {
      return border.getLeft() + cellPadding.x;
    }

    public int getHorizontalSpacing() {
      return cellSpacing.x + cellPadding.x + cellPadding.width;
    }

    public int getRight() {
      return border.getRight() + cellPadding.width;
    }

    public int getHeaderTop() {
      return border.getTop(false) + cellPadding.y;
    }

    public int getHeaderVerticalSpacing() {
      return cellSpacing.y + cellPadding.y + cellPadding.height;
    }

    public int getBodyTop(boolean headerPresent, boolean open) {
      return headerPresent
          ? open
            ? headerOpenSpacing
            : headerClosedSpacing + cellPadding.y
          : open
          	? border.getTop(true)
            : border.getTop(false) + cellPadding.y;
    }

    public int getBodyVerticalSpacing() {
      return cellSpacing.y + cellPadding.y + cellPadding.height;
    }

    public int getBodyBottom(boolean footerPresent, boolean open) {
      return footerPresent
          ? open
            ? footerOpenSpacing
            : footerClosedSpacing + cellPadding.height
          : open
          	? border.getBottom(true)
          	: border.getBottom(false) + cellPadding.height;
    }

    public int getFooterVerticalSpacing() {
      return cellSpacing.y + cellPadding.y + cellPadding.height;
    }

    public int getFooterBottom() {
      return border.getBottom(false) + cellPadding.height;
    }
  }
}