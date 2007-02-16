/*******************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation and others. All
 * rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Woodcraft Mill &
 * Cabinet Corporation - initial API and implementation
 ******************************************************************************/
package net.sf.paperclips;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;

/**
 * A Print which arranges child prints into a grid. A grid is initialized with a
 * series of GridColumns, and child prints are laid out into those columns by
 * invoking the add(...) methods.
 * <p>
 * GridPrint uses a column sizing algorithm based on the <a
 * href=http://www.w3.org/TR/html4/appendix/notes.html#h-B.5.2.2>W3C
 * recommendation</a> for automatic layout of tables. GridPrint deviates from
 * the recommendation on one important point: if there is less width available
 * on the print device than the calculated "minimum" size of the grid, the
 * columns will be scaled down to <em>less</em> than their calculated minimum
 * widths. Only when one of the columns goes below its "absolute minimum" will
 * the grid fail to print ( {@link PrintIterator#next(int, int) } returns null).
 * <p>
 * GridPrint offers three basic methods of specifying column size.
 * <ol>
 * <li>Default size.  The column will be somewhere between it's minimum and preferred width.
 *     GridPrint will determine the optimum widths for all default size columns, using the modified
 *     W3C recommendation described above.  This is the recommended option for most cases.
 * <li>Preferred size.  The column will be sized to it's preferred width.  This option is sometimes
 *     appropriate, for example when certain portions of text should not be allowed to line-wrap.
 *     In cases where only a few cells in a column need to be prevented from line wrapping,
 *     consider wrapping them in a NoBreakPrint instead.
 * <li>Explicit size.  The column will be the size you specify, expressed in points.
 *     72 points = 1".
 * </ol>
 * Example: GridPrint grid = new GridPrint("d, p, 72pts");
 * <p>
 * In addition, any column can be given a grow attribute.  In the event a grid is not as wide as
 * the page, those columns with the grow attribute set will be widened to fill the extra space.
 * <p>
 * Because GridPrint scales columns according to their minimum sizes in the
 * worst-case scenario, the absolute minimum size of a GridPrint is dependant on
 * its child prints and is not clearly defined.
 * <p>
 * If a grid has one of more columns with the grow attribute set, the grid is horizontally greedy.
 * Greedy prints take up all the available space on the page.  
 * @author Matthew
 * @see GridColumn
 * @see PrintIterator#minimumSize()
 * @see PrintIterator#preferredSize()
 */
public final class GridPrint implements Print {
  /**
   * Constant colspan value indicating that all remaining columns in the row
   * should be used.
   */
  public static final int REMAINDER = -1;

  /**
   * Constant column size value indicating that the column should be given its
   * preferred size. (In the context of W3C's autolayout recommendation, this
   * has the effect of setting the columns minimum width to its preferred width.
   * This value is used in the GridColumn constructor.
   */
  public static final int PREFERRED = 0;

  /**
   * Constant cell spacing value indicating that the borders of adjacent cells
   * should overlap.
   */
  public static final int BORDER_OVERLAP = -1;

  private final DefaultGridLook defaultLook;

  private GridLook look;

  /** The columns for this grid. */
  final List columns; // List<GridColumn>

  /** Array of column groups. */
  int[][] columnGroups = new int[0][];

  /**
   * Two-dimension list of all header cells.  Each element of this list represents a row in the
   * header.  Each element of a row represents a cellspan in that row.  
   */
  // List <List <GridCell>>
  final List header = new ArrayList ();

  /** Column cursor - the column that the next added header cell will go into. */
  private int headerCol = 0;

  /**
   * Two-dimensional list of all body cells. Each element of this list represents a row in the
   * body.  Each element of a row represents a cellspan in that row.
   */
  // List <List <GridCell>>
  final List body = new ArrayList ();

  /** Column cursor - the column that the next added print will go into. */
  private int bodyCol = 0;

  /**
   * Two-dimension list of all footer cells.  Each element of this list represents a row in the
   * footer.  Each element of a row represents a cellspan in that row.
   */
  // List <List <GridCell>>
  final List footer = new ArrayList ();

  /** Column cursor - the column that the next added footer cell will go into. */
  private int footerCol = 0;

  /**
   * Constructs a GridPrint with no columns and a default look.
   */
  public GridPrint() {
    this(new GridColumn[0]);
  }

  /**
   * Constructs a GridPrint with no columns and the given look.
   * @param look the look to apply to the constructed grid.
   */
  public GridPrint(GridLook look) {
    this(new GridColumn[0], look);
  }

  /**
   * Constructs a GridPrint with the given columns and a default look.
   * @param columns a comma-separated list of parseable column specs.
   * @see GridColumn#parse(String)
   */
  public GridPrint (String columns) {
    this (parseColumns (columns));
  }

  /**
   * Constructs a GridPrint with the given columns and look.
   * @param columns a comma-separated list of parseable column specs.
   * @param look the look to apply to the constructed grid.
   * @see GridColumn#parse(String)
   */
  public GridPrint(String columns, GridLook look) {
    this(parseColumns (columns), look);
  }

  /**
   * Constructs a GridPrint with the given columns and spacing.
   * @param columns a comma-separated list of column specs.
   * @param spacing the spacing (in points) between grid cells.
   * @see #BORDER_OVERLAP
   * @deprecated use GridPrint(String) instead, then set a DefaultGridLook on the grid with the
   *             desired cell spacing.
   */
  public GridPrint (String columns, int spacing) {
    this (parseColumns (columns), spacing, spacing);
  }

  /**
   * Constructs a GridPrint with the given columns and spacing.
   * @param columns a comma-separated list of column specs.
   * @param horizontalSpacing the horizontal spacing (in points) between grid
   *          cells.
   * @param verticalSpacing the vertical spacing (in points) between grid cells.
   * @see GridColumn#parse(String)
   * @see #BORDER_OVERLAP
   * @deprecated use GridPrint(String) instead, then set a DefaultGridLook on the grid with the
   *             desired cell spacing.
   */
  public GridPrint (String columns, int horizontalSpacing, int verticalSpacing) {
    this (parseColumns (columns), horizontalSpacing, verticalSpacing);
  }

  /**
   * Constructs a GridPrint with the given columns and a default look.
   * @param columns the columns for the new grid.
   */
  public GridPrint (GridColumn[] columns) {
    for (int i = 0; i < columns.length; i++)
      if (columns[i] == null)
        throw new NullPointerException();

    this.columns = new ArrayList();
    for (int i = 0; i < columns.length; i++)
      this.columns.add(columns[i]);
    this.look = defaultLook = new DefaultGridLook();
  }

  /**
   * Constructs a GridPrint with the given columns and look.
   * @param columns the columns for the new grid.
   * @param look the look to apply to the constructed grid.
   */
  public GridPrint (GridColumn[] columns, GridLook look) {
    this(columns);
    setLook(look);
  }

  /**
   * Constructs a GridPrint with the given columns and spacing.
   * @param columns the columns for the new grid.
   * @param spacing the spacing (in points) between grid cells.
   * @see #BORDER_OVERLAP
   * @deprecated use GridPrint(GridColumn[]) instead, then set a DefaultGridLook on the grid with
   *             the desired cell spacing.
   */
  public GridPrint (GridColumn[] columns, int spacing) {
    this (columns, spacing, spacing);
  }

  /**
   * Construct a GridPrint with the given columns and spacing.
   * @param columns the columns for the new grid.
   * @param horizontalSpacing the horizontal spacing (in points) between grid
   *          cells.
   * @param verticalSpacing the vertical spacing (in points) between grid cells.
   * @see #BORDER_OVERLAP
   * @deprecated use GridPrint(GridColumn[]) instead, then set a DefaultGridLook on the grid with
   *             the desired cell spacing.
   */
  public GridPrint (GridColumn[] columns, int horizontalSpacing, int verticalSpacing) {
    this(columns);
    defaultLook.setCellSpacing(horizontalSpacing, verticalSpacing);
  }

  /**
   * Adds the column on the right edge of the grid.  Any cells which have been added to the grid
   * prior to adding the column will be adjusted as follows: the right-hand cell of each completed
   * row will have it's colspan expanded to fill the added column.
   * @param column the column to add to the grid.
   * @see GridColumn#parse(String)
   */
  public void addColumn(String column) {
    addColumn(columns.size(), GridColumn.parse(column));
  }

  /**
   * Adds the column on the right edge of the grid.  Any cells which have been added to the grid
   * prior to adding the column will be adjusted as follows: the right-hand cell of each completed
   * row will have it's colspan expanded to fill the added column.
   * @param column the column to add to the grid.
   */
  public void addColumn(GridColumn column) {
    addColumn(columns.size(), column);
  }

  /**
   * Inserts the column at the specified position in the grid.  Any cells which have been added to
   * the grid prior to adding the column will be adjusted as follows: on each row, the cell which
   * overlaps or whose right edge touches the insert position will be expanded to fill the added
   * column.
   * @param index the insert position.
   * @param column the column to be inserted.
   * @see GridColumn#parse(String)
   */
  public void addColumn(int index, String column) {
    addColumn(index, GridColumn.parse(column));
  }

  /**
   * Inserts the column at the specified position in the grid.  Any cells which have been added to
   * the grid prior to adding the column will be adjusted as follows: on each row, the cell which
   * overlaps or whose right edge touches the insert position will be expanded to fill the added
   * column.
   * @param index the insert position.
   * @param column the column to be inserted.
   */
  public void addColumn(int index, GridColumn column) {
    checkColumnInsert(index);
    if (column == null) throw new NullPointerException();

    this.columns.add(index, column);

    adjustForColumnInsert(index, 1);
  }

  /**
   * Adds the columns on the right edge of the grid.  Any cells which have been added to the grid
   * prior to adding the columns will be adjusted as follows: the right-hand cell of each completed
   * row will have it's colspan expanded to fill the added columns.
   * @param columns the columns to add to the grid.
   * @see GridColumn#parse(String)
   */
  public void addColumns(String columns) {
    addColumns(this.columns.size(), parseColumns(columns));
  }

  /**
   * Adds the columns on the right edge of the grid.  Any cells which have been added to the grid
   * prior to adding the columns will be adjusted as follows: the right-hand cell of each completed
   * row will have it's colspan expanded to fill the added columns.
   * @param columns the columns to add to the grid.
   */
  public void addColumns(GridColumn[] columns) {
    addColumns(this.columns.size(), columns);
  }

  /**
   * Inserts the columns at the specified position in the grid.  Any cells which have been added to
   * the grid prior to adding the columns will be adjusted as follows: on each row, the cell which
   * overlaps or whose right edge touches the insert position will be expanded to fill the added
   * columns.
   * @param index the insert position.
   * @param columns the columns to be inserted.
   * @see GridColumn#parse(String)
   */
  public void addColumns(int index, String columns) {
    addColumns(index, parseColumns(columns));
  }

  /**
   * Inserts the columns at the specified position in the grid.  Any cells which have been added to
   * the grid prior to adding the columns will be adjusted as follows: on each row, the cell which
   * overlaps or whose right edge touches the insert position will be expanded to fill the added
   * columns.
   * @param index the insert position.
   * @param columns the columns to be inserted.
   * @see GridColumn#parse(String)
   */
  public void addColumns(int index, GridColumn[] columns) {
    checkColumnInsert(index);
    checkColumns(columns);

    this.columns.addAll(index, Arrays.asList(columns));

    adjustForColumnInsert(index, columns.length);
  }

  private void checkColumnInsert(int index) {
    if (index < 0 || index > this.columns.size())
      throw new IndexOutOfBoundsException(
          "index = " + index + ", size = " + this.columns.size());
  }

  private void checkColumns(GridColumn[] columns) {
    for (int i = 0; i < columns.length; i++)
      if (columns[i] == null)
        throw new NullPointerException();
  }

  private void adjustForColumnInsert(int index, int count) {
    adjustCellsForColumnInsert(header, index, count);
    adjustCellsForColumnInsert(body,   index, count);
    adjustCellsForColumnInsert(footer, index, count);

    adjustColumnGroupsForColumnInsert(index, count);

    if (bodyCol > index)
      bodyCol += count;
    if (headerCol > index)
      headerCol += count;
    if (footerCol > index)
      footerCol += count;
  }

  private void adjustCellsForColumnInsert(List rows, int index, int count) {
    for (int rowI = 0; rowI < rows.size(); rowI++) {
      List row = (List) rows.get(rowI);
      int col = 0;
      for (int cellI = 0; cellI < row.size(); cellI++) {
        GridCell cell = (GridCell) row.get(cellI);
        col += cell.colspan;

        // Adjust the cell which extends through the insert point, or whose right side touches the
        // insert point.  Except on the last row, don't adjust the final cell if it only touches the
        // insert point (the user may be adding columns right before s/he adds column headers).
        if (// cell overlaps insert point, or
            (col > index) ||
            // right side touches insert point but is not the final cell.
            (col == index && (rowI + 1 < rows.size() || cellI + 1 < row.size()))) {
          row.set(cellI, new GridCell(cell.hAlignment, cell.vAlignment, cell.target, cell.colspan + count));
          break;
        }
      }
    }
  }

  private void adjustColumnGroupsForColumnInsert(int index, int count) {
    for (int groupI = 0; groupI < columnGroups.length; groupI++) {
      int[] group = columnGroups[groupI];
      for (int i = 0; i < group.length; i++)
        if (group[i] >= index)
          group[i] += count;
    }
      
  }

  /**
   * Separates the comma-separated argument and parses each piece to obtain an
   * array of GridColumns.
   * @param columns the comma-separated list of column specs.
   * @return GridColumn array with the requested columns.
   */
  private static GridColumn[] parseColumns (String columns) {
    String[] cols = columns.split ("\\s*,\\s*");

    GridColumn[] result = new GridColumn[cols.length];
    for (int i = 0; i < cols.length; i++)
      result[i] = GridColumn.parse (cols[i]);

    return result;
  }

  /**
   * Returns an array of <code>GridColumn</code>s which are the columns in the receiver. 
   * @return an array of <code>GridColumn</code>s which are the columns in the receiver.
   */
  public GridColumn[] getColumns() {
  	return (GridColumn[]) columns.toArray(new GridColumn[columns.size()]);
  }

  /**
   * Adds the Print to the grid header, with default alignment and a colspan of 1.
   * @param cell the print to add.
   */
  public void addHeader (Print cell) {
    headerCol = add (header, headerCol, SWT.DEFAULT, SWT.DEFAULT, cell, 1);
  }

  /**
   * Adds the Print to the grid header, using the given alignment.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   * @param cell the print to add.
   */
  public void addHeader(int hAlignment, Print cell) {
  	headerCol = add (header, headerCol, hAlignment, SWT.DEFAULT, cell, 1);
  }

  /**
   * Adds the Print to the grid header, using the given alignment.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   * @param vAlignment the vertical alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#TOP }, {@link SWT#CENTER }, {@link SWT#BOTTOM }, or {@link SWT#FILL }.  A value of FILL
   *        indicates that the cell is vertically greedy, so GridPrint will limit the cell's height to the tallest
   *        non-FILL cell in the row.
   * @param cell the print to add.
   */
  public void addHeader(int hAlignment, int vAlignment, Print cell) {
  	headerCol = add (header, headerCol, hAlignment, vAlignment, cell, 1);
  }

  /**
   * Adds the Print to the grid header, with the given colspan and the default alignment.
   * @param cell the print to add.
   * @param colspan the number of columns to span, or {@link GridPrint#REMAINDER } to span the rest
   *        of the row.
   */
  public void addHeader (Print cell, int colspan) {
    headerCol = add (header, headerCol, SWT.DEFAULT, SWT.DEFAULT, cell, colspan);
  }

  /**
   * Adds the Print to the grid header, using the given colspan and alignment.
   * @param cell the print to add.
   * @param colspan the number of columns to span, or {@link GridPrint#REMAINDER } to span the rest
   *        of the row.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   */
  public void addHeader(int hAlignment, Print cell, int colspan) {
  	headerCol = add (header, headerCol, hAlignment, SWT.DEFAULT, cell, colspan);
  }

  /**
   * Adds the Print to the grid header, using the given colspan and alignment.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   * @param vAlignment the vertical alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#TOP }, {@link SWT#CENTER }, {@link SWT#BOTTOM }, or {@link SWT#FILL }.  A value of FILL
   *        indicates that the cell is vertically greedy, so GridPrint will limit the cell's height to the tallest
   *        non-FILL cell in the row.
   * @param cell the print to add.
   * @param colspan the number of columns to span, or {@link GridPrint#REMAINDER } to span the rest
   *        of the row.
   */
  public void addHeader(int hAlignment, int vAlignment, Print cell, int colspan) {
  	headerCol = add (header, headerCol, hAlignment, vAlignment, cell, colspan);
  }

  /**
   * Adds the Print to the grid header, using the given colspan and alignment.
   * @param cell the print to add.
   * @param colspan the number of columns to span, or {@link GridPrint#REMAINDER } to span the rest
   *        of the row.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   * @deprecated Use {@link #addHeader(int, Print, int)} instead.  GridPrint's addHeader method signatures have been
   *             rearranged to coincide with the GridColumn column spec format: [alignment]:content:[colspan]
   */
  public void addHeader (Print cell, int colspan, int hAlignment) {
    headerCol = add (header, headerCol, hAlignment, SWT.DEFAULT, cell, colspan);
  }

  /**
   * Returns an array containing the header cells in this grid.  Each inner array represents one row in the header.
   * @return an array containing the header cells in this grid.
   */
  public GridCell[][] getHeaderCells() {
  	return getGridCellArray(header);
  }

  /**
   * Returns an array containing the body cells in the grid.  Each inner array represents one row in the body.
   * @return an array containing the body cells in the grid.
   */
  public GridCell[][] getBodyCells() {
  	return getGridCellArray(body);
  }

  /**
   * Returns an array containing the footer cells in the grid.  Each inner array represents one row in the footer.
   * @return an array containing the footer cells in the grid.
   */
  public GridCell[][] getFooterCells() {
  	return getGridCellArray(footer);
  }

  private static GridCell[][] getGridCellArray(List list) {
  	GridCell[][] cells = new GridCell[list.size()][];
  	for (int rowIndex = 0; rowIndex < cells.length; rowIndex++) {
  		List row = (List) list.get(rowIndex);
  		GridCell[] rowCells = new GridCell[row.size()];
  		for (int cellIndex = 0; cellIndex < rowCells.length; cellIndex++)
  			rowCells[cellIndex] = (GridCell) row.get(cellIndex);
  		cells[rowIndex] = rowCells;
  	}
  	return cells;
  }

  /**
   * Returns the background color of the header cells (defaults to the body background if null).  
   * @return the background color of the header cells (defaults to the body background if null).
   * @deprecated this functionality has been moved to DefaultGridLook.
   */
  public RGB getHeaderBackground() {
    return defaultLook.getHeaderBackground();
  }

  /**
   * Sets the background color of the header cells.
   * @param headerBackground the new background color (defaults to the body background if null).
   * @deprecated this functionality has been moved to DefaultGridLook.  Set a DefaultGridLook on
   *             the grid, then call setHeaderBackground on the grid look.
   */
  public void setHeaderBackground(RGB headerBackground) {
    defaultLook.setHeaderBackground(headerBackground);
  }

  /**
   * Adds the Print to the grid body, with the default alignment and a colspan of 1.
   * @param cell the print to add.
   */
  public void add (Print cell) {
    bodyCol = add (body, bodyCol, SWT.DEFAULT, SWT.DEFAULT, cell, 1);
  }

  /**
   * Adds the Print to the grid body, using the given colspan and alignment.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   * @param cell the print to add.
   */
  public void add (int hAlignment, Print cell) {
  	bodyCol = add (body, bodyCol, hAlignment, SWT.DEFAULT, cell, 1);
  }

  /**
   * Adds the Print to the grid body, using the given colspan and alignment.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   * @param vAlignment the vertical alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#TOP }, {@link SWT#CENTER }, {@link SWT#BOTTOM }, or {@link SWT#FILL }.  A value of FILL
   *        indicates that the cell is vertically greedy, so GridPrint will limit the cell's height to the tallest
   *        non-FILL cell in the row.
   * @param cell the print to add.
   */
  public void add (int hAlignment, int vAlignment, Print cell) {
  	bodyCol = add (body, bodyCol, hAlignment, vAlignment, cell, 1);
  }

  /**
   * Adds the Print to the grid body, with the given colspan and the default alignment.
   * @param cell the print to add.
   * @param colspan the number of columns to span, or {@link GridPrint#REMAINDER } to span the rest
   *        of the row.
   */
  public void add (Print cell, int colspan) {
    bodyCol = add (body, bodyCol, SWT.DEFAULT, SWT.DEFAULT, cell, colspan);
  }

  /**
   * Adds the Print to the grid body, using the given colspan and alignment.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   * @param cell the print to add.
   * @param colspan the number of columns to span, or {@link GridPrint#REMAINDER } to span the rest
   *        of the row.
   */
  public void add (int hAlignment, Print cell, int colspan) {
  	bodyCol = add (body, bodyCol, hAlignment, SWT.DEFAULT, cell, colspan);
  }

  /**
   * Adds the Print to the grid body, using the given colspan and alignment.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   * @param vAlignment the vertical alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#TOP }, {@link SWT#CENTER }, {@link SWT#BOTTOM }, or {@link SWT#FILL }.  A value of FILL
   *        indicates that the cell is vertically greedy, so GridPrint will limit the cell's height to the tallest
   *        non-FILL cell in the row.
   * @param cell the print to add.
   * @param colspan the number of columns to span, or {@link GridPrint#REMAINDER } to span the rest
   *        of the row.
   */
  public void add (int hAlignment, int vAlignment, Print cell, int colspan) {
  	bodyCol = add (body, bodyCol, hAlignment, vAlignment, cell, colspan);
  }

  /**
   * Adds the Print to the grid body, using the given colspan and alignment.
   * @param cell the print to add.
   * @param colspan the number of columns to span, or {@link GridPrint#REMAINDER } to span the rest
   *        of the row.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   * @deprecated Use {@link #add(int, Print, int)} instead.  GridPrint's add method signatures have been rearranged to
   *             coincide with the GridColumn column spec format: [alignment]:content:[colspan]
   */
  public void add (Print cell, int colspan, int hAlignment) {
    bodyCol = add (body, bodyCol, hAlignment, SWT.DEFAULT, cell, colspan);
  }

  /**
   * Returns the background color of the body cells (no background color if null).
   * @return the background color of the body cells (no background color if null).
   * @deprecated this functionality has been moved to DefaultGridLook.
   */
  public RGB getBodyBackground() {
    return defaultLook.getBodyBackground();
  }

  /**
   * Sets the background color of the body cells.
   * @param bodyBackground the new background color (no background is drawn if null).
   * @deprecated this functionality has been moved to DefaultGridLook.  Set a DefaultGridLook on
   *             the grid, then call setBodyBackground on the grid look.
   */
  public void setBodyBackground(RGB bodyBackground) {
    defaultLook.setBodyBackground(bodyBackground);
  }

  /**
   * Adds the Print to the grid footer, with the default alignment and a colspan of 1.
   * @param cell the print to add.
   */
  public void addFooter (Print cell) {
    footerCol = add (footer, footerCol, SWT.DEFAULT, SWT.DEFAULT, cell, 1);
  }

  /**
   * Adds the Print to the grid footer, using the given colspan and alignment.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   * @param cell the print to add.
   */
  public void addFooter (int hAlignment, Print cell) {
  	footerCol = add (footer, footerCol, hAlignment, SWT.DEFAULT, cell, 1);
  }

  /**
   * Adds the Print to the grid footer, using the given colspan and alignment.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   * @param vAlignment the vertical alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#TOP }, {@link SWT#CENTER }, {@link SWT#BOTTOM }, or {@link SWT#FILL }.  A value of FILL
   *        indicates that the cell is vertically greedy, so GridPrint will limit the cell's height to the tallest
   *        non-FILL cell in the row.
   * @param cell the print to add.
   */
  public void addFooter (int hAlignment, int vAlignment, Print cell) {
  	footerCol = add (footer, footerCol, hAlignment, vAlignment, cell, 1);
  }

  /**
   * Adds the Print to the grid footer, with the given colspan and the default alignment.
   * @param cell the print to add.
   * @param colspan the number of columns to span, or {@link GridPrint#REMAINDER } to span the rest
   *        of the row.
   */
  public void addFooter (Print cell, int colspan) {
    footerCol = add (footer, footerCol, SWT.DEFAULT, SWT.DEFAULT, cell, colspan);
  }

  /**
   * Adds the Print to the grid footer, using the given colspan and alignment.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   * @param cell the print to add.
   * @param colspan the number of columns to span, or {@link GridPrint#REMAINDER } to span the rest
   *        of the row.
   */
  public void addFooter (int hAlignment, Print cell, int colspan) {
  	footerCol = add (footer, footerCol, hAlignment, SWT.DEFAULT, cell, colspan);
  }

  /**
   * Adds the Print to the grid footer, using the given colspan and alignment.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   * @param vAlignment the vertical alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#TOP }, {@link SWT#CENTER }, {@link SWT#BOTTOM }, or {@link SWT#FILL }.  A value of FILL
   *        indicates that the cell is vertically greedy, so GridPrint will limit the cell's height to the tallest
   *        non-FILL cell in the row.
   * @param cell the print to add.
   * @param colspan the number of columns to span, or {@link GridPrint#REMAINDER } to span the rest
   *        of the row.
   */
  public void addFooter (int hAlignment, int vAlignment, Print cell, int colspan) {
  	footerCol = add (footer, footerCol, hAlignment, vAlignment, cell, colspan);
  }

  /**
   * Adds the Print to the grid footer, using the given colspan and alignment.
   * @param cell the print to add.
   * @param colspan the number of columns to span, or {@link GridPrint#REMAINDER } to span the rest
   *        of the row.
   * @param hAlignment the horizontal alignment of the print within the grid cell. One of {@link SWT#DEFAULT },
   *        {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   * @deprecated Use {@link #addFooter(int, Print, int)} instead.  GridPrint's addFooter method signatures have been
   *             rearranged to coincide with the GridColumn column spec format: [alignment]:content:[colspan]
   */
  public void addFooter (Print cell, int colspan, int hAlignment) {
    footerCol = add (footer, footerCol, hAlignment, SWT.DEFAULT, cell, colspan);
  }

  /**
   * Returns the background color of the footer cells.
   * @return the background color of the footer cells (defaults to body background if null).
   * @deprecated this functionality has been moved to DefualtGridLook.
   */
  public RGB getFooterBackground() {
    return defaultLook.getFooterBackground();
  }

  /**
   * Sets the background color of the footer cells.
   * @param footerBackground the new background color (defaults to body background if null).
   * @deprecated this functionality has been moved to DefaultGridLook.  Set a DefaultGridLook on
   *             the grid, then call setFooterBackground on the grid look.
   */
  public void setFooterBackground(RGB footerBackground) {
    defaultLook.setBodyBackground(footerBackground);
  }

  /* Returns the column number that we've advanced to, after adding the new cell. */
  private int add (List  rows, // List of List of GridCell
                   int   startColumn,
                   int   hAlignment,
                   int   vAlignment,
                   Print cell,
                   int   colspan) {
    // If we're at the end of a row, start a new row.
    if (startColumn == columns.size()) startColumn = 0;

    // Make sure the colspan would not exceed the number of columns
    if (startColumn + colspan > columns.size())
      throw new IllegalArgumentException ("Colspan " + colspan
          + " too wide at column " + startColumn + " (" + columns.size()
          + " columns total)");

    List row; // the row we will add the cell to.
    if (startColumn == 0)
      // Start a new row if back at column 0.
      rows.add (row = new ArrayList ());
    else
      // Get the incomplete row.
      row = (List) rows.get (rows.size () - 1); // List of GridCell

    // Convert REMAINDER to the actual # of columns
    if (colspan == REMAINDER) colspan = columns.size() - startColumn;

    // Add the new Print
    GridCell entry = new GridCell (hAlignment, vAlignment, cell, colspan);
    row.add (entry);

    // Adjust the column cursor by the span of the added Print
    startColumn += colspan;

    // Make sure column number is valid.
    if (startColumn > columns.size()) {
      // THIS SHOULD NOT HAPPEN--ABOVE LOGIC SHOULD PREVENT THIS CASE
      // ..but just in case.

      // Roll back operation.
      startColumn -= colspan;
      row.remove (row.size()-1);
      if (row.size () == 0) rows.remove (row);

      // Report error
      throw new IllegalArgumentException ("Colspan " + colspan
          + " too wide at column " + startColumn + " (" + columns.size()
          + " columns total)");
    }

    return startColumn;
  }

  /**
   * Returns current column groups.  The returned array may be modified
   * without affecting this GridPrint.
   * @return the column groups.
   */
  public int[][] getColumnGroups () {
    return cloneColumnGroups (columnGroups);
  }

  /**
   * Sets the column groups to the given two-dimension array. Each int[] array
   * is a group. Columns in a group will be the same size when laid out on the
   * print device.
   * <p>
   * The following statement causes columns 0 and 2 to be the same size, and
   * columns 1 and 3 to be the same size.
   * 
   * <pre>
   * grid.setColumnGroups (new int[][] {
   *     { 0, 2 },
   *     { 1, 3 } });
   * </pre>
   * 
   * <p>
   * The behavior of this property is undefined when a column belongs to more
   * than one group.
   * <p>
   * <b>Note:</b> Column grouping is enforced <i>before</i> column weights.
   * Therefore, columns in the same group should be given the same weight to
   * ensure they are laid out at the same width.
   * @param columnGroups the new column groups.
   * @throws IndexOutOfBoundsException if any of the column indices are out of
   *           bounds [0 , columnCount-1].
   */
  public void setColumnGroups (int[][] columnGroups) {
    checkColumnGroups (columnGroups);
    this.columnGroups = cloneColumnGroups (columnGroups);
  }

  void checkColumnGroups (int[][] columnGroups) {
    for (int groupIndex = 0; groupIndex < columnGroups.length; groupIndex++) {
      int[] group = columnGroups[groupIndex];
      for (int columnInGroupIndex = 0; columnInGroupIndex < group.length; columnInGroupIndex++) {
        int col = group[columnInGroupIndex];
        if (col < 0 || col >= columns.size())
          throw new IndexOutOfBoundsException (
              "Column index in column group must be " + "0 <= " + col + " < "
                  + columns.size());
      }
    }
  }

  static int[][] cloneColumnGroups (int[][] columnGroups) {
    int[][] result = (int[][]) columnGroups.clone ();
    for (int groupIndex = 0; groupIndex < result.length; groupIndex++)
      result[groupIndex] = (int[]) result[groupIndex].clone ();
    return result;
  }

  /**
   * Returns the border used around each cell.
   * @return the border used around each cell.
   * @deprecated this functionality has been moved to DefaultGridLook.
   */
  public Border getCellBorder () {
    return defaultLook.getCellBorder();
  }

  /**
   * Sets the border around each of the grid's cells to the argument.
   * @param border the new body cell border.
   * @deprecated this functionality has been moved to DefaultGridLook.  Set a DefaultGridLook on
   *             the grid, then call setCellBorder on the grid look.
   */
  public void setCellBorder (Border border) {
    defaultLook.setCellBorder(border);
  }

  /**
   * Returns the horizontal spacing between grid cells.
   * @return the horizontal spacing between grid cells.
   * @deprecated this functionality has been moved to DefaultGridLook.
   */
  public int getHorizontalSpacing () {
    return defaultLook.getCellSpacing().x;
  }

  /**
   * Sets the horizontal spacing between grid cells.
   * @param horizontalSpacing the new horizontal spacing.  A value of
   *        {@link #BORDER_OVERLAP} indicates that the borders should be
   *        overlapped instead of spaced.
   * @deprecated this functionality has been moved to DefaultGridLook.  Set a DefaultGridLook on
   *             the grid, then call setCellSpacing(Point) on the grid look.
   */
  public void setHorizontalSpacing (int horizontalSpacing) {
    defaultLook.setCellSpacing(horizontalSpacing, defaultLook.getCellSpacing().y);
  }

  /**
   * Returns the vertical spacing between grid cells.
   * @return the vertical spacing between grid cells.
   * @deprecated this functionality has been moved to DefaultGridLook.
   */
  public int getVerticalSpacing () {
    return defaultLook.getCellSpacing().y;
  }

  /**
   * Sets the vertical spacing between grid cells.
   * @param verticalSpacing the new vertical spacing.  A value of
   *        {@link #BORDER_OVERLAP} indicates that the borders should be
   *        overlapped instead of spaced.
   * @deprecated this functionality has been moved to DefaultGridLook.  Set a DefaultGridLook on
   *             the grid, then call setCellSpacing(Point) on the grid look.
   */
  public void setVerticalSpacing (int verticalSpacing) {
    defaultLook.setCellSpacing(defaultLook.getCellSpacing().x, verticalSpacing);
  }

  /**
   * Returns the grid's look.  A GridLook determines what decorations will appear around the grid's
   * contents.  Default is a DefaultGridLook with no cell spacing, no cell borders, and no
   * background colors.
   * @return the look of this grid.
   */
  public GridLook getLook() {
    return look;
  }

  /**
   * Sets the grid's look.
   * @param look the new look.
   */
  public void setLook(GridLook look) {
    if (look == null) throw new NullPointerException();

    this.look = look;
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new GridIterator (this, device, gc);
  }
}

class GridCellIterator {
  final int           hAlignment;
  final int           vAlignment;
  final PrintIterator target;
  final int           colspan;

  GridCellIterator(GridCell cell, Device device, GC gc) {
    this.hAlignment = cell.hAlignment;
    this.vAlignment = cell.vAlignment;
    this.target     = cell.target.iterator (device, gc);
    this.colspan    = cell.colspan;
  }

  GridCellIterator(GridCellIterator that) {
    this.hAlignment = that.hAlignment;
    this.vAlignment = that.vAlignment;
    this.target     = that.target.copy();
    this.colspan    = that.colspan;
  }

  GridCellIterator copy() {
    return new GridCellIterator(this);
  }
}

class GridIterator implements PrintIterator {
  static GridCellIterator[][] cloneRows (GridCellIterator[][] rows, int firstRow) {
    GridCellIterator[][] result = (GridCellIterator[][]) rows.clone ();
    for (int i = firstRow; i < result.length; i++)
      result[i] = cloneRow (result[i]);
    return result;
  }

  static GridCellIterator[] cloneRow (GridCellIterator[] row) {
    GridCellIterator[] result = (GridCellIterator[]) row.clone ();
    for (int i = 0; i < result.length; i++)
      result[i] = result[i].copy ();
    return result;
  }

  final Device device;
  final Point  dpi;

  final GridColumn[] columns;
  final int[][]      columnGroups;

  final GridLookPainter look;

  final GridCellIterator[][] header;
  final GridCellIterator[][] body;
  final GridCellIterator[][] footer;

  final int[] minimumColSizes;   // PIXELS
  final int[] preferredColSizes; // PIXELS

  final Point minimumSize;   // PIXELS
  final Point preferredSize; // PIXELS

  // This is the cursor!
  private int row;

  // Determines whether top edge of cell border is drawn open or closed for current row.
  private boolean rowStarted;

  GridIterator (GridPrint grid, Device device, GC gc) {
    this.device = device;
    this.dpi    = device.getDPI();

    this.columns      = (GridColumn[]) grid.columns.toArray(new GridColumn[grid.columns.size()]);
    this.columnGroups = grid.getColumnGroups ();

    this.header = new GridCellIterator[grid.header.size ()][];
    for (int rowIndex = 0; rowIndex < header.length; rowIndex++) {
      List row = (List) grid.header.get(rowIndex); // List of GridCell
      header[rowIndex] = new GridCellIterator[row.size()];
      for (int cellIndex = 0; cellIndex < row.size(); cellIndex++)
        header[rowIndex][cellIndex] = ((GridCell) row.get(cellIndex)).iterator(device, gc);
    }

    this.body = new GridCellIterator[grid.body.size ()][];
    for (int rowIndex = 0; rowIndex < body.length; rowIndex++) {
      List row = (List) grid.body.get(rowIndex); // List of GridCell
      body[rowIndex] = new GridCellIterator[row.size()];
      for (int cellIndex = 0; cellIndex < row.size(); cellIndex++)
        body[rowIndex][cellIndex] = ((GridCell) row.get(cellIndex)).iterator(device, gc);
    }

    this.footer = new GridCellIterator[grid.footer.size ()][];
    for (int rowIndex = 0; rowIndex < footer.length; rowIndex++) {
      List row = (List) grid.footer.get(rowIndex); // List of GridCell
      footer[rowIndex] = new GridCellIterator[row.size()];
      for (int cellIndex = 0; cellIndex < row.size(); cellIndex++)
        footer[rowIndex][cellIndex] = ((GridCell) row.get(cellIndex)).iterator (device, gc);
    }

    look = grid.getLook().getPainter(device, gc);

    this.minimumColSizes   = computeColumnSizes (PrintSizeStrategy.MINIMUM);
    this.preferredColSizes = computeColumnSizes (PrintSizeStrategy.PREFERRED);

    this.minimumSize   = computeSize (PrintSizeStrategy.MINIMUM,   minimumColSizes);
    this.preferredSize = computeSize (PrintSizeStrategy.PREFERRED, preferredColSizes);

    row = 0;
    rowStarted = false;
  }

  /** Copy constructor (used by copy() only) */
  GridIterator (GridIterator that) {
    this.device = that.device;
    this.dpi    = that.dpi;

    this.columns      = that.columns;
    this.columnGroups = that.columnGroups;

    this.header = that.header;                     // never directly modified, clone not necessary
    this.body   = cloneRows (that.body, that.row); // Only need to deep copy the unconsumed rows.
    this.footer = that.footer;                     // never directly modified, clone not necessary

    this.look = that.look;

    this.minimumColSizes   = that.minimumColSizes;
    this.preferredColSizes = that.preferredColSizes;

    this.minimumSize   = that.minimumSize;
    this.preferredSize = that.preferredSize;

    this.row        = that.row;
    this.rowStarted = that.rowStarted;
  }

  /** Compute the size of a column, respecting the constraints of the GridColumn. */
  int computeColumnSize (GridCellIterator entry,
                         GridColumn col,
                         PrintSizeStrategy strategy) {
    if (col.size == SWT.DEFAULT)
      return strategy.computeSize (entry.target).x;
    if (col.size == GridPrint.PREFERRED)
      return entry.target.preferredSize ().x;
    return Math.round (col.size * device.getDPI().x / 72f);
  }

  static boolean isExplicitSize (GridColumn col) {
    return col.size > 0;
  }

  void groupColumns (int[] columnSizes) {
    for (int groupIndex = 0; groupIndex < columnGroups.length; groupIndex++) {
      int[] group = columnGroups[groupIndex];
      int maxSize = 0;

      // find max column width in group
      for (int columnInGroupIndex = 0; columnInGroupIndex < group.length; columnInGroupIndex++) {
        int col = group[columnInGroupIndex];
        maxSize = Math.max (maxSize, columnSizes[col]);
      }

      // grow all columns to max column width
      for (int columnInGroupIndex = 0; columnInGroupIndex < group.length; columnInGroupIndex++) {
        int col = group[columnInGroupIndex];
        columnSizes[col] = maxSize;
      }
    }
  }

  boolean isGrouped (int col) {
    for (int groupIndex = 0; groupIndex < columnGroups.length; groupIndex++) {
      int[] group = columnGroups[groupIndex];
      for (int columnInGroupIndex = 0; columnInGroupIndex < group.length; columnInGroupIndex++) {
        int groupedColumn = group[columnInGroupIndex];
        if (groupedColumn == col) return true;
      }
    }

    return false;
  }

  static boolean isGroupMember (int col, int[] group) {
    for (int columnInGroupIndex = 0; columnInGroupIndex < group.length; columnInGroupIndex++) {
      int groupedColumn = group[columnInGroupIndex];
      if (groupedColumn == col) return true;
    }

    return false;
  }

  int[] computeColumnSizes (PrintSizeStrategy strategy) {
    GridCellIterator[][] rows =
      new GridCellIterator[this.body.length + this.header.length + this.footer.length][];
    int offset = 0;
    System.arraycopy(this.body, 0, rows, offset, this.body.length);
    offset += this.body.length;
    System.arraycopy(this.header, 0, rows, offset, this.header.length);
    offset += this.header.length;
    System.arraycopy(this.footer, 0, rows, offset, this.footer.length);

    int[] colSizes = new int[columns.length];

    final int horizontalSpacing = look.getMargins().getHorizontalSpacing();

    // First pass - find widths for all explicitly sized columns.
    for (int col = 0; col < columns.length; col++)
      if (isExplicitSize (columns[col]))
        colSizes[col] = Math.round (columns[col].size * dpi.x / 72f);

    // Second pass - find the column widths for all cells that span a single
    // column. (Skip explicitly sized columns)
    for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
      GridCellIterator[] row = rows[rowIndex];
      int col = 0;
      for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
        GridCellIterator entry = row[cellIndex];

        // ignore explicitly sized cols
        if (entry.colspan == 1 && !isExplicitSize (columns[col])) { 
          colSizes[col] = Math.max (
              colSizes[col],
              computeColumnSize (entry, columns[col], strategy));
        }
        col += entry.colspan;
      }
    }

    // Enforce column groups
    groupColumns (colSizes);

    // Third pass - check each entry spanning multiple columns. If the
    // combined column sizes (including spacing between them) is less than the
    // width of the Print, expand the columns so they accomodate the Print's
    // width. If there are columns in the cellspan with nonzero weight, they
    // will be expanded. Otherwise, if there are columns in the cellspan with
    // a width attribute of SWT.DEFAULT, they will be expanded. Otherwise, if
    // there are columns with a width attribute of GridPrint.PREFERRED, they
    // will be expanded. Otherwise, as a last resort, the columns with
    // specific point sizes will be expanded.
    for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
      GridCellIterator[] row = rows[rowIndex];
      int col = 0;
      for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
        GridCellIterator entry = row[cellIndex];
        if (entry.colspan > 1) {
          // Calculate the current total width of column span.
          int currentSpanWidth = 0; // neglect column spacing
          for (int colIndex = col; colIndex < col + entry.colspan; colIndex++)
            currentSpanWidth += colSizes[colIndex];

          // Calculate the minimum width of the print in this cell.
          int minimumSpanWidth = strategy.computeSize (entry.target).x
              - horizontalSpacing * (entry.colspan - 1); // subtract column spacing

          // Note that we omitted column spacing so the weighted distribution
          // of any extra width doesn't get thrown off.

          if (minimumSpanWidth > currentSpanWidth) {
            // We need more space in these columns. Distribute the extra
            // width between them, proportionately to their current sizes.
            // Smaller columns are thus affected less than larger columns.
            int extraWidth = minimumSpanWidth - currentSpanWidth;

            int[] expandableColumns = getExpandableColumns(col, entry.colspan);
            int expandableColumnsWidth = 0; // for scaling of expansion
            for (int expandableColIndex = 0; expandableColIndex < expandableColumns.length; expandableColIndex++)
              expandableColumnsWidth += colSizes[expandableColumns[expandableColIndex]];

            // If the expandable columns are zero width, expand them equally
            if (expandableColumnsWidth == 0) {
              int expandableCols = expandableColumns.length;
              for (int expandableColIndex = 0; expandableColIndex < expandableCols; expandableColIndex++) {
                int expandableColumn = expandableColumns[expandableColIndex];

                int addedWidth = extraWidth / expandableCols;

                colSizes[expandableColumn] = addedWidth;
                extraWidth -= addedWidth;
                expandableCols--;
              }
            }

            // Otherwise expand them proportionately.
            else {
              for (int expandableColIndex = 0; expandableColIndex < expandableColumns.length; expandableColIndex++) {
                int expandableCol = expandableColumns[expandableColIndex];

                if (expandableColumnsWidth == 0) break;

                int addedWidth = extraWidth * colSizes[expandableCol] / expandableColumnsWidth;

                // Adjust expandableColumnsWidth and extraWidth for future
                // iterations.
                expandableColumnsWidth -= colSizes[expandableCol];
                extraWidth -= addedWidth;

                // NOW we can add the added width.
                colSizes[expandableCol] += addedWidth;
              }
            }
          }
        }
        col += entry.colspan;
      }
    }

    // Enforce column groups
    groupColumns (colSizes);

    return colSizes;
  }

  private int[] getExpandableColumns(int firstColumn, int colspan) {
    // Expand each column in the cell span proportionately.
    // First determine which cells in the cell span should be
    // expanded.
    int[] expandableColumns = new int[colspan];
    int count = 0;

    abstract class ColumnFilter {
      abstract boolean accept (int col);
    }

    ColumnFilter[] filters = {
      // Ungrouped columns with nonzero weight are first choice for expansion.
      new ColumnFilter () {
        boolean accept (int col) { return !isGrouped (col) && columns[col].weight > 0; }
      },

      // Grouped columns with nonzero weight are next choice
      new ColumnFilter () {
        boolean accept (int col) { return isGrouped (col) && columns[col].weight > 0; }
      },

      // Ungrouped columns with GridPrint.PREFERRED size are next choice.
      new ColumnFilter () {
        boolean accept (int col) {
          return !isGrouped (col) && columns[col].size == GridPrint.PREFERRED;
        }
      },

      // Grouped columns with GridPrint.PREFERRED size are next choice.
      new ColumnFilter () {
        boolean accept (int col) {
          return isGrouped (col) && columns[col].size == GridPrint.PREFERRED;
        }
      },

      // Ungrouped columns with SWT.DEFAULT size are next choice.
      new ColumnFilter () {
        boolean accept (int col) {
          return !isGrouped (col) && columns[col].size == SWT.DEFAULT;
        }
      },

      // Grouped columns with SWT.DEFAULT size are last choice.
      new ColumnFilter () {
        boolean accept (int col) {
          return isGrouped (col) && columns[col].size == SWT.DEFAULT;
        }
      }
    };

    // Use column filters to determine which columns should be expanded.
    for (int filterIndex = 0; filterIndex < filters.length; filterIndex++) {
      ColumnFilter filter = filters[filterIndex];
      for (int colIndex = firstColumn; colIndex < firstColumn + colspan; colIndex++)
        if (filter.accept (colIndex)) {
          expandableColumns[count] = colIndex;
          count++;
        }
      // If the filter matched 1 or more columns in this iteration,
      // expand the matched columns.
      if (count > 0) break;
    }

    int[] result = new int[count];
    System.arraycopy(expandableColumns, 0, result, 0, count);
    return result;
  }

  private Point computeSize (PrintSizeStrategy strategy, int[] colSizes) {
    final GridMargins margins = look.getMargins();

    // Calculate width from column sizes and spacing.
    int width =
      margins.getLeft() +
      (colSizes.length - 1) * margins.getHorizontalSpacing() +
      margins.getRight();
    for (int colIndex = 0; colIndex < colSizes.length; colIndex++) {
      int col = colSizes[colIndex];
      width += col;
    }

    int height;

    // This algorithm is not strictly accurate but probably good enough.  The header and footer row
    // heights are being calculated using getMinimumSize() and getPreferredSize(), which do not
    // necessarily return the total content height.

    // HEADER
    if (header.length > 0) {
      height =
        margins.getHeaderTop() +
        margins.getHeaderVerticalSpacing() * (header.length - 1) +
        Math.max(margins.getBodyTop(true, true), margins.getBodyTop(true, false));
      for (int rowIndex = 0; rowIndex < header.length; rowIndex++) {
        GridCellIterator[] row = header[rowIndex];
        int col = 0;
        int rowHeight = 0;
        for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
          GridCellIterator entry = row[cellIndex];
          // Find tallest cell in row.
          rowHeight = Math.max(rowHeight, strategy.computeSize(entry.target).y);
          col += entry.colspan;
        }
        height += rowHeight;
      }
    } else {
      height = Math.max(margins.getBodyTop(false, true), margins.getBodyTop(false, false));
    }

    // BODY
    int maxBodyRowHeight = 0;
    for (int rowIndex = 0; rowIndex < body.length; rowIndex++) {
      GridCellIterator[] row = body[rowIndex];
      int col = 0;
      for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
        GridCellIterator entry = row[cellIndex];
        // Find the greatest height of all cells' calculated sizes.
        maxBodyRowHeight = Math.max (maxBodyRowHeight, strategy.computeSize (entry.target).y);
        col += entry.colspan;
      }
    }
    height += maxBodyRowHeight;

    // FOOTER
    if (footer.length > 0) {
      height +=
        Math.max(margins.getBodyBottom(true, false), margins.getBodyBottom(true, true)) +
        margins.getFooterVerticalSpacing() * (footer.length - 1) +
        margins.getFooterBottom();
      for (int rowIndex = 0; rowIndex < footer.length; rowIndex++) {
        GridCellIterator[] row = footer[rowIndex];
        int col = 0;
        int rowHeight = 0;
        for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
          GridCellIterator entry = row[cellIndex];
          // Find tallest cell in row.
          rowHeight = Math.max(rowHeight, strategy.computeSize(entry.target).y);
          col += entry.colspan;
        }
        height += rowHeight;
      }
    } else {
      height += Math.max(margins.getBodyBottom(false, false), margins.getBodyBottom(false, true));
    }

    return new Point (width, height);
  }

  public Point minimumSize () {
    return new Point (minimumSize.x, minimumSize.y);
  }

  public Point preferredSize () {
    return new Point (preferredSize.x, preferredSize.y);
  }

  private int[] getShrinkableColumns (int extraWidth) {
    int[] shrinkableColumns = new int[columns.length];
    int count = 0;

    int shrinkableWidth = 0;

    // Search first for columns with DEFAULT size.
    for (int colIndex = 0; colIndex < columns.length; colIndex++)
      if (columns[colIndex].size == SWT.DEFAULT) {
        shrinkableColumns[count] = colIndex;
        count++;
        shrinkableWidth += minimumColSizes[colIndex];
      }

    // If the DEFAULT columns are not wide enough to shrink as much as we need, add the PREFERRED
    // columns to the shrinkable list.
    if (shrinkableWidth < extraWidth)
      for (int colIndex = 0; colIndex < columns.length; colIndex++)
        if (columns[colIndex].size == GridPrint.PREFERRED) {
          shrinkableColumns[count] = colIndex;
          count++;
          shrinkableWidth += minimumColSizes[colIndex];
        }

    // If the DEFAULT and PREFERRED columns are still not enough to shrink as much as we need, then
    // we'll just shrink all columns.
    if (count == 0) {
      for (int colIndex = 0; colIndex < columns.length; colIndex++)
        shrinkableColumns[colIndex] = colIndex;
      count = columns.length;
    }

    int[] result = new int[count];
    for (int colIndex = 0; colIndex < result.length; colIndex++)
      result[colIndex] = shrinkableColumns[colIndex];

    return result;
  }

  private int[] computeAdjustedColumnSizes (int width) {
    GridMargins margins = look.getMargins();

    // Remove margins from width first off
    width = width -
      margins.getLeft() -
      margins.getHorizontalSpacing() * (columns.length - 1) -
      margins.getRight();

    int minimumWidth = 0;
    int preferredWidth = 0;
    for (int colIndex = 0; colIndex < columns.length; colIndex++) {
      minimumWidth += minimumColSizes[colIndex];
      preferredWidth += preferredColSizes[colIndex];
    }

    // Case 1: width < minimum width
    // Start with minimum column sizes. Determine which columns should
    // shrink, and reduce them proportionately to their minimum sizes.
    if (width < minimumWidth) {
      int[] colSizes = (int[]) minimumColSizes.clone ();

      // How much wider is the minimum width than the available width?
      int extraWidth = minimumWidth - width;

      // Determine which columns are shrinkable.
      int[] shrinkableCols = getShrinkableColumns (extraWidth);

      // How wide are the shrinkable columns put together.
      int shrinkableWidth = 0;
      for (int shrinkableColIndex = 0; shrinkableColIndex < shrinkableCols.length; shrinkableColIndex++) {
        int col = shrinkableCols[shrinkableColIndex];

        shrinkableWidth += colSizes[col];
      }

      for (int shrinkableColIndex = 0; shrinkableColIndex < shrinkableCols.length; shrinkableColIndex++) {
        int col = shrinkableCols[shrinkableColIndex];

        if (shrinkableWidth == 0) break;

        int shrinkBy = colSizes[col] * extraWidth / shrinkableWidth;

        shrinkableWidth -= colSizes[col];
        colSizes[col] -= shrinkBy;
        extraWidth -= shrinkBy;
      }

      return colSizes;
    }

    // Case 2: minimum width = width
    // Use minimum column sizes.
    if (width == minimumWidth) return minimumColSizes;

    // Case 3: minimum width < width < preferred width
    // Start with minimum column sizes. Expand the columns proportionate to
    // the difference between minimum and preferred column sizes.
    if (width < preferredWidth) {
      int extraWidth = width - minimumWidth;
      int gridDelta = preferredWidth - minimumWidth;

      int[] colSizes = (int[]) minimumColSizes.clone();
      for (int colIndex = 0; colIndex < columns.length; colIndex++) {
        if (gridDelta == 0) break;

        int colDelta = preferredColSizes[colIndex] - minimumColSizes[colIndex];

        int addedWidth = extraWidth * colDelta / gridDelta;

        colSizes[colIndex] += addedWidth;

        // adjust extraWidth and gridDelta - eliminates round-off error
        extraWidth -= addedWidth;
        gridDelta -= colDelta;
      }

      return colSizes;
    }

    // Case 4: preferred width = width
    // Use preferred column sizes.
    if (preferredWidth == width) return preferredColSizes;

    // Case 5: preferred width < width
    // Start with preferred column sizes. Expand the columns with the grow
    // option set, distributing extra space according to weight.
    if (preferredWidth < width) {
      int extraWidth = width - preferredWidth;
      int weight = 0;

      // Find the weighted columns.
      int[] weightedCols = new int[columns.length];
      int weightedColCount = 0;
      for (int colIndex = 0; colIndex < columns.length; colIndex++)
        if (columns[colIndex].weight > 0) {
          weight += columns[colIndex].weight;
          weightedCols[weightedColCount] = colIndex;
          weightedColCount++;
        }

      // Start with preferred column sizes.
      int[] colSizes = (int[]) preferredColSizes.clone ();
      // Expand weighted columns according to their weights.
      for (int weightedColIndex = 0; weightedColIndex < weightedColCount; weightedColIndex++) {
        int col = weightedCols[weightedColIndex];

        int colWeight = columns[col].weight;

        int addWidth = colWeight * extraWidth / weight;

        colSizes[col] += addWidth;

        // adjust extraWidth and growColumns - eliminates round-off error
        extraWidth -= addWidth;
        weight -= colWeight;
      }

      return colSizes;
    }

    throw new RuntimeException (
        "GridPrintIterator.computeAdjustedColumnSizes(..) logic is flawed.");
  }

  public boolean hasNext () {
    return row < body.length;
  }

  /**
   * Iterates through the current row and returns a CompositeEntry array of the PrintPieces
   * generated.  This method modifies the contents of the row argument, so backup copies should be
   * made of each cell iterator before passing a row into this method.
   * @param row an array of GridCellIterators that make up the row.
   * @param colSizes the width of the grid columns.
   * @param height the available height on the print device.
   * @param y the y offset to give the PrintPieces.
   * @param bottomOpen whether the cell border is open at the bottom. If false, this method must
   *        return null if one or more cells cannot consume all its content in this iteration.
   * @param rowHeight an int array 1 element long for reporting the height of the row back to the
   *        caller.
   * @param hasNext a boolean array 1 element long for reporting whether the cells in the row have
   *        more content to display.
   * @return A CompositeEntry array resulting from the iteration. Returns null if the iteration
   *         failed. This happens if bottomOpen is false and one or more cells could not be consume
   *         within the available area.
   */
  CompositeEntry[] iterateRow (final GridCellIterator[] row,
                               final int[] colSizes,
                               final int height,
                               final int y,
                               final boolean bottomOpen,
                               final int[] rowHeight,
                               final boolean[] hasNext) {

  	// Fail now if the bottom border is open and there are non-default vertical alignments in the row.
  	if (bottomOpen)
  		for (int i = 0; i < row.length; i++)
  			if (row[i].vAlignment != SWT.DEFAULT && row[i].vAlignment != SWT.TOP)
  				return null;

    final GridMargins margins = look.getMargins();

    int[] xOffsets = new int[row.length];
    int[] yOffsets = new int[row.length];
    int[] widths = new int[row.length];
    PrintPiece[] rowPieces = new PrintPiece[row.length];

    int x = margins.getLeft();
    int col = 0;
    rowHeight[0] = 0;
    hasNext[0] = false;

    for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
      xOffsets[cellIndex] = x;
      yOffsets[cellIndex] = y;

      GridCellIterator cell = row[cellIndex];
      PrintIterator iter = cell.target;

      // Determine width of the cell span, including spacing between cells.
      int cellspanWidth = (cell.colspan - 1) * margins.getHorizontalSpacing();
      for (int j = 0; j < cell.colspan; j++)
        cellspanWidth += colSizes[col+j];
      widths[cellIndex] = cellspanWidth;

      // Skip this cell if is already consumed.
      if (!iter.hasNext ()) {
        // But advance the column cursor
        x += cellspanWidth + margins.getHorizontalSpacing();
        col += cell.colspan;
        continue;
      }

      // Skip for now if this cell has SWT.FILL vertical alignment (indicating vertical greediness)
      if (cell.vAlignment == SWT.FILL) {
      	// Check the minimum size just in case
      	Point minSize = iter.minimumSize();
      	rowHeight[0] = Math.max(rowHeight[0], minSize.y);

      	// Advance the column cursor
      	x += cellspanWidth + margins.getHorizontalSpacing();
      	col += cell.colspan;
      	continue;
      }

      // Iterate the current cell.
      PrintPiece piece = rowPieces[cellIndex] = PaperClips.next(iter, cellspanWidth, height);

      // Two conditions that cause iteration to fail:
      // 1) piece is null.  All unconsumed cells should iterate or the whole row should wait until
      //    the next page.  Bugfix 1480013
      // 2) bottomOpen is false, and iter.hasNext().  A false value for bottomOpen means that we're
      //    drawing a closed border around the bottom.  This is visually incorrect if the iterator
      //    has more content.
      // If either case is true, dispose any print pieces from previous loops and return null.
      if ((piece == null) ||                   // case 1
          (!bottomOpen && iter.hasNext ()) ) { // case 2
        if (piece != null)
          piece.dispose();
        for (int j = 0; j <= cellIndex; j++)
          if (rowPieces[j] != null)
            rowPieces[j].dispose ();
        return null;
      }

      if ((piece.getSize().x > cellspanWidth) || (piece.getSize().y > height)) {
        piece.dispose();
        for (int j = 0; j <= cellIndex; j++)
          if (rowPieces[j] != null)
            rowPieces[j].dispose();
        System.err.println(rowPieces[cellIndex] + " iterated a larger piece than allowed: " +
            new Point(cellspanWidth, height) + " available, but " + piece.getSize() + " used.");
        return null;
      }

      // If bottomOpen is true, update hasNext argument if necessary.
      hasNext[0] = hasNext[0] || iter.hasNext();

      // Determine the alignment for this cell.
      int hAlignment = cell.hAlignment;
      if (hAlignment == SWT.DEFAULT) hAlignment = columns[col].align;

      // Calculate the X offset of the PrintPiece within the cellspan, according to the alignment.
      int offset = 0;
      if (hAlignment == SWT.CENTER)
        offset = (cellspanWidth - piece.getSize ().x) / 2;
      else if (hAlignment == SWT.RIGHT)
        offset = cellspanWidth - piece.getSize ().x;
      xOffsets[cellIndex] += offset;

      // Update the row height
      rowHeight[0] = Math.max (rowHeight[0], piece.getSize ().y);

      // Adjust x offset and column number.
      x += cellspanWidth + margins.getHorizontalSpacing();
      col += cell.colspan;
    }

    // Now that we have the final row height, loop through again to determine the y offsets of each cell, and to layout
    // any cells with SWT.FILL vertical alignment (which were skipped the first time through).
    for (int cellIndex = 0; cellIndex < row.length; cellIndex++) {
    	GridCellIterator cell = row[cellIndex];

    	// Skip cell if vertical alignment is TOP or DEFAULT
    	if (cell.vAlignment == SWT.DEFAULT || cell.vAlignment == SWT.TOP)
    		continue;

    	PrintIterator iter = cell.target;

    	int offset = 0;
    	if (cell.vAlignment == SWT.FILL) {
    		PrintPiece piece = rowPieces[cellIndex] = PaperClips.next(iter, widths[cellIndex], rowHeight[0]);

    		if (piece == null || iter.hasNext()) {
    			if (piece != null)
    				piece.dispose();
    			for (int j = 0; j <= row.length; j++)
    				if (rowPieces[j] != null)
    					rowPieces[j].dispose();
    			return null;
    		}

    		if (piece.getSize().x > widths[cellIndex] || piece.getSize().y > rowHeight[0]) {
    			piece.dispose();
    			for (int j = 0; j < row.length; j++)
    				if (rowPieces[j] != null)
    					rowPieces[j].dispose();
          System.err.println(rowPieces[cellIndex] + " iterated a larger piece than allowed: " +
              new Point(widths[cellIndex], height) + " available, but " + piece.getSize() + " used.");
    			return null;
    		}

    		if (cell.hAlignment == SWT.CENTER)
    			offset = (widths[cellIndex] - piece.getSize().x)/2;
    		else if (cell.hAlignment == SWT.RIGHT)
    			offset = widths[cellIndex] - piece.getSize().x;
    		xOffsets[cellIndex] += offset;
    	} else {
    		if (cell.vAlignment == SWT.CENTER) {
    			offset = (rowHeight[0] - rowPieces[cellIndex].getSize().y)/2;
    		} else if (cell.vAlignment == SWT.BOTTOM) {
    			offset = rowHeight[0] - rowPieces[cellIndex].getSize().y;
    		}
      	yOffsets[cellIndex] += offset;
    	}
    }

    // Construct and return the result.
    List result = new ArrayList();
    for (int cellIndex = 0; cellIndex < rowPieces.length; cellIndex++)
      if (rowPieces[cellIndex] != null)
        result.add(new CompositeEntry ( rowPieces[cellIndex],
                                        new Point (xOffsets[cellIndex], yOffsets[cellIndex])));

    return (CompositeEntry[]) result.toArray(new CompositeEntry[result.size()]);
  }

  private static void nuke (List list) {
    for (Iterator iter = list.iterator(); iter.hasNext(); ) {
      CompositeEntry entry = (CompositeEntry) iter.next();
      entry.piece.dispose();
    }
  }

  public PrintPiece next (int width, int height) {
    if (!hasNext ()) throw new IllegalStateException ();

    // Compute column sizes for the available width.
    int[] colSizes = computeAdjustedColumnSizes (width);

    final int firstRowIndex = row;
    final GridMargins margins = look.getMargins();

    boolean headerPresent = header.length > 0;
    boolean footerPresent = footer.length > 0;

    int y = 0;

    // HEADER
    final int[] headerHeights = new int[header.length];
    final int[][] headerColSpans = new int[header.length][];
    List headerCells = new ArrayList ();
    if (headerPresent) {
      height -= margins.getHeaderTop();
      height -= margins.getHeaderVerticalSpacing() * (header.length - 1);

      y += margins.getHeaderTop();

      for (int rowIndex = 0; rowIndex < header.length; rowIndex++) {
        GridCellIterator[] row = cloneRow(header[rowIndex]);

        // Track column spans for grid look.
        headerColSpans[rowIndex] = new int[row.length];
        for (int cellIndex = 0; cellIndex < row.length; cellIndex++)
          headerColSpans[rowIndex][cellIndex] = row[cellIndex].colspan;

        int[] rowHeight = new int[] { 0 };
        boolean[] hasNext = new boolean[] { false };

        CompositeEntry[] rowEntries =
          iterateRow(row, colSizes, height, y, false, rowHeight, hasNext);

        // Header must always iterate completely
        if (rowEntries == null || hasNext[0]) {
          if (hasNext[0])
            for (int cellIndex = 0; cellIndex < rowEntries.length; cellIndex++) {
              rowEntries[cellIndex].piece.dispose();
            }
          nuke(headerCells);
          return null;
        }

        // Track header row heights for grid look.
        headerHeights[rowIndex] = rowHeight[0];

        for (int cellIndex = 0; cellIndex < rowEntries.length; cellIndex++) {
          CompositeEntry entry = rowEntries[cellIndex];
          headerCells.add (entry);
        }

        // Adjust cursor for row
        y += headerHeights[rowIndex];
        height -= headerHeights[rowIndex];

        // Adjust cursor for spacing below row
        if (rowIndex < header.length-1)
          y += margins.getHeaderVerticalSpacing();
      }
    }

    // FOOTER
    final int[] footerHeights = new int[footer.length];
    final int[][] footerColSpans = new int[footer.length][];
    List footerCells = new ArrayList ();
    if (footerPresent) {
      height -= margins.getFooterVerticalSpacing() + (footer.length - 1);
      height -= margins.getFooterBottom();

      // The footer must be calculated and assigned positions before we know its vertical
      // placement.  So we keep a separate y coordinate within the footer, then aggregate the
      // footer cells into a CompositePiece once we know the footer's y offset 
      int footerY = 0;
      for (int rowIndex = 0; rowIndex < footer.length; rowIndex++) {
        GridCellIterator[] row = cloneRow(footer[rowIndex]);

        // Track column spans for grid look.
        footerColSpans[rowIndex] = new int[row.length];
        for (int cellIndex = 0; cellIndex < row.length; cellIndex++)
          footerColSpans[rowIndex][cellIndex] = row[cellIndex].colspan;

        int[] rowHeight = new int[] { 0 };
        boolean[] hasNext = new boolean[] { false };

        CompositeEntry[] rowEntries =
          iterateRow(row, colSizes, height, footerY, false, rowHeight, hasNext);

        // Footer must iterate completely! 
        if (rowEntries == null || hasNext[0]) {
          if (hasNext[0])
            for (int cellIndex = 0; cellIndex < rowEntries.length; cellIndex++) {
              rowEntries[cellIndex].piece.dispose();
            }
              
          nuke (headerCells);
          nuke (footerCells);
          return null;
        }

        // Track footer row heights for grid look.
        footerHeights[rowIndex] = rowHeight[0];

        for (int cellIndex = 0; cellIndex < rowEntries.length; cellIndex++)
          footerCells.add(rowEntries[cellIndex]);

        // Adjust cursor for row
        footerY += footerHeights[rowIndex];
        height -= footerHeights[rowIndex];

        // Adjust footer y offset cursor for spacing below row 
        footerY += margins.getFooterVerticalSpacing();
      }
    }

    // BODY
    final boolean topOpen = rowStarted;
    final int bodyTopSpacing          = margins.getBodyTop   (headerPresent, topOpen);
    final int bodyBottomSpacingOpen   = margins.getBodyBottom(footerPresent, true);
    final int bodyBottomSpacingClosed = margins.getBodyBottom(footerPresent, false);
    boolean bottomOpen = false;

    y += bodyTopSpacing;
    height -= bodyTopSpacing;

    final List bodyHeights = new ArrayList();
    final List bodyColSpans = new ArrayList();
    List bodyCells = new ArrayList ();
    while (hasNext ()) {
      int[] rowHeight = new int[] { 0 };
      boolean[] hasNext = new boolean[] { false };

      // First attempt to iterate the row with a closed bottom border.
      GridCellIterator[] thisRow = cloneRow(body[row]);

      CompositeEntry[] rowEntries = iterateRow (
          thisRow, colSizes, height - bodyBottomSpacingClosed, y, bottomOpen, rowHeight, hasNext);

      // If the iteration failed, or the row has more content (which it shouldn't when the bottom
      // border is closed) then try the iteration again with an the bottom border open.
      if (rowEntries == null || hasNext[0]) {
        rowHeight[0] = 0;
        hasNext[0] = false;
        thisRow = cloneRow(body[row]);
        bottomOpen = true;
        rowEntries = iterateRow (
            thisRow, colSizes, height - bodyBottomSpacingOpen, y, true, rowHeight, hasNext);
      }

      // If both attempts failed on the current row, halt (but not abort) iteration.  (Break, don't
      // return, because there may be previous rows in this iteration that should be returned.)
      if (rowEntries == null) {
        // Back off body vertical spacing from the last iteration and add in bottom spacing for a
        // closed border.  This positions y where the footer should start (if any).
        y -= margins.getBodyVerticalSpacing();
        y += bodyBottomSpacingClosed;
        bottomOpen = false;
        break;
      }

      // Iteration succeeded.  Add the cells from this row to the body cells array.
      for (int cellIndex = 0; cellIndex < rowEntries.length; cellIndex++) {
        CompositeEntry entry = rowEntries[cellIndex];
        bodyCells.add (entry);
      }
      body[row] = thisRow; // replace row iterators with the ones that just got consumed.

      // Track colspans for grid look.
      final int[] colspans = new int[thisRow.length];
      for (int cellIndex = 0; cellIndex < colspans.length; cellIndex++)
        colspans[cellIndex] = thisRow[cellIndex].colspan;
      bodyColSpans.add(colspans);

      // Track row height for grid look.
      bodyHeights.add(new Integer(rowHeight[0]));

      // Adjust cursors for row height.
      y += rowHeight[0];
      height -= rowHeight[0];

      // If the row we just iterated has more content, then this iteration is complete.  Set the
      // rowStarted flag so the next iteration shows an open top border in the cells.
      if (bottomOpen || hasNext[0]) {
        // Add the bottom spacing for an open border.  This positions y where the footer should
        // start (if any).
        y += bodyBottomSpacingOpen;
        height -= bodyBottomSpacingOpen;
        bottomOpen = true;
        rowStarted = true;
        break;
      }

      // If we get to here then the row completed. Clear the rowStarted flag
      // and advance to the next row.
      rowStarted = false;
      row++;

      // Advanced cursors for the spacing below the row just iterated.
      if (hasNext()) {
        y += margins.getBodyVerticalSpacing();
        height -= margins.getBodyVerticalSpacing();
      } else {
        y += bodyBottomSpacingClosed;
        height -= bodyBottomSpacingClosed;
      }
    }

    // If no body content was generated, iteration fails.  Dispose any entries from header and
    // footer cells.
    if (bodyHeights.isEmpty()) {
      nuke(headerCells);
      nuke(footerCells);
      return null;
    }

    bodyCells.addAll (headerCells);
    if (footerCells.size() > 0) {
      bodyCells.add(new CompositeEntry(new CompositePiece(footerCells), new Point(0, y)));
    }

    final int[] bodyRows = new int[bodyHeights.size()];
    for (int i = 0; i < bodyRows.length; i++)
      bodyRows[i] = ((Integer) bodyHeights.get(i)).intValue();

    final int[][] bodySpans = new int[bodyColSpans.size()][];
    for (int i = 0; i < bodySpans.length; i++)
      bodySpans[i] = (int[]) bodyColSpans.get(i);

    // Add the print piece for the grid look at the beginning so background colors and such don't
    // clobber the cell contents.
    PrintPiece lookPiece = new GridLookPainterPiece(
        look,
        colSizes,
        headerHeights, headerColSpans,
        firstRowIndex, topOpen, bodyRows, bodySpans, bottomOpen,
        footerHeights, footerColSpans); 
    bodyCells.add(0, new CompositeEntry(lookPiece, new Point(0, 0)));

    Point size = new Point (
        margins.getLeft() +
        margins.getHorizontalSpacing() * (colSizes.length - 1) +
        margins.getRight(),
        0);
    for (int i = 0; i < colSizes.length; i++)
      size.x += colSizes[i];
    return new CompositePiece (bodyCells);
  }

  public PrintIterator copy () {
    return new GridIterator (this);
  }
}

class GridLookPainterPiece implements PrintPiece {
  final GridLookPainter look;

  final int[]   columns;
  final int[]   headerRows;
  final int[][] headerCellSpans;
  final int     firstRowIndex;
  final boolean topOpen;
  final int[]   bodyRows;
  final int[][] bodyCellSpans;
  final boolean bottomOpen;
  final int[]   footerRows;
  final int[][] footerCellSpans;

  final Point size;

  GridLookPainterPiece(GridLookPainter look,
                       int[]   colSizes,
                       int[]   headerRows,
                       int[][] headerCellSpans,
                       int     firstRowIndex,
                       boolean topOpen,
                       int[]   bodyRows,
                       int[][] bodyCellSpans,
                       boolean bottomOpen,
                       int[]   footerRows,
                       int[][] footerCellSpans) {
    this.look = look;
    this.columns         = (int[]) colSizes.clone();
    this.headerRows      = (int[]) headerRows.clone();
    this.headerCellSpans = (int[][]) headerCellSpans.clone();
    this.firstRowIndex   = firstRowIndex;
    this.topOpen         = topOpen;
    this.bodyRows        = (int[]) bodyRows.clone();
    this.bodyCellSpans   = (int[][]) bodyCellSpans.clone();
    this.bottomOpen      = bottomOpen;
    this.footerRows      = (int[]) footerRows.clone();
    this.footerCellSpans = (int[][]) footerCellSpans.clone();

    for (int rowIndex = 0; rowIndex < headerCellSpans.length; rowIndex++)
      headerCellSpans[rowIndex] = (int[]) headerCellSpans[rowIndex].clone();
    for (int rowIndex = 0; rowIndex < bodyCellSpans.length; rowIndex++)
      bodyCellSpans  [rowIndex] = (int[]) bodyCellSpans  [rowIndex].clone();
    for (int rowIndex = 0; rowIndex < footerCellSpans.length; rowIndex++)
      footerCellSpans[rowIndex] = (int[]) footerCellSpans[rowIndex].clone();

    GridMargins margins = look.getMargins();

    final boolean headerPresent = headerRows.length > 0;
    final boolean footerPresent = footerRows.length > 0;

    this.size = new Point(
        margins.getLeft() +
        margins.getHorizontalSpacing() * (colSizes.length - 1) +
        margins.getRight(),
        (headerPresent ?
            margins.getHeaderTop() +
            margins.getHeaderVerticalSpacing() * (headerRows.length - 1) :
            0) +
        margins.getBodyTop(headerPresent, topOpen) +
        margins.getBodyVerticalSpacing() * (bodyRows.length - 1) +
        margins.getBodyBottom(footerPresent, bottomOpen) +
        (footerPresent ?
            margins.getFooterVerticalSpacing() * (footerRows.length - 1) +
            margins.getFooterBottom() :
            0) );
    for (int colIndex = 0; colIndex < columns.length; colIndex++)
      size.x += columns[colIndex];
    for (int rowIndex = 0; rowIndex < headerRows.length; rowIndex++)
      size.y += headerRows[rowIndex];
    for (int rowIndex = 0; rowIndex < bodyRows.length; rowIndex++)
      size.y += bodyRows[rowIndex];
    for (int rowIndex = 0; rowIndex < footerRows.length; rowIndex++)
      size.y += footerRows[rowIndex];
  }

  public void dispose() {
    look.dispose();
  }

  public Point getSize() {
    return new Point(size.x, size.y);
  }

  public void paint(GC gc, int x, int y) {
    look.paint(gc,
               x,
               y,
               columns,
               headerRows,
               headerCellSpans,
               firstRowIndex,
               topOpen,
               bodyRows,
               bodyCellSpans,
               bottomOpen,
               footerRows,
               footerCellSpans);
  }
}
