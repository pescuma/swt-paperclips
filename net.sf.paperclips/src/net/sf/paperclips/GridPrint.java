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
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

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
 * Because GridPrint scales columns according to their minimum sizes in the
 * worst-case scenario, the absolute minimum size of a GridPrint is dependant on
 * its child prints and is not clearly defined.
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

  /**
   * The horizontal spacing between adjacent cells, in points. 72 points = 1".
   * The default value is BORDER_OVERLAP.
   * @see #BORDER_OVERLAP
   * @deprecated use {@link #setHorizontalSpacing(int)} and
   *             {@link #getHorizontalSpacing()} instead.  This field will be
   *             made private in a future release.
   */
  @Deprecated
  public int horizontalSpacing;

  /**
   * The vertical spacing between adjacent cells, in points. 72 points = 1". The
   * default value is BORDER_OVERLAP.
   * @see #BORDER_OVERLAP
   * @deprecated use {@link #setVerticalSpacing(int)} and
   *             {@link #getVerticalSpacing()} instead.  This field will be
   *             made private in a future release.
   */
  @Deprecated
  public int verticalSpacing;

  /** The columns for this grid. */
  final GridColumn[] columns;

  /** Array of column groups. */
  int[][] columnGroups = new int[0][];

  /** The border used around each grid cell. */
  Border cellBorder = new GapBorder (0);

  /**
   * Two-dimensional list of all GridPrintEntrys. Each element of this list
   * represents a row in the Grid. Each element in the rows represents a
   * cellspan in the row.
   */
  final List <List <GridEntry>> rows = new ArrayList <List <GridEntry>> ();

  /** Column cursor - the column that the next added print will go into. */
  private int col = 0;

  /**
   * Constructs a GridPrint with the given columns.
   * @param columns a comma-separated list of column specs.
   * @see GridColumn#parse(String)
   */
  public GridPrint (String columns) {
    this (parseColumns (columns));
  }

  /**
   * Constructs a GridPrint with the given columns and spacing.
   * @param columns a comma-separated list of column specs.
   * @param spacing the spacing (in points) between grid cells.
   * @see #BORDER_OVERLAP
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
   */
  public GridPrint (String columns, int horizontalSpacing, int verticalSpacing) {
    this (parseColumns (columns), horizontalSpacing, verticalSpacing);
  }

  /**
   * Constructs a GridPrint with the given columns.
   * @param columns the columns for the new grid.
   */
  public GridPrint (GridColumn... columns) {
    this (columns, BORDER_OVERLAP, BORDER_OVERLAP);
  }

  /**
   * Constructs a GridPrint with the given columns and spacing.
   * @param columns the columns for the new grid.
   * @param spacing the spacing (in points) between grid cells.
   * @see #BORDER_OVERLAP
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
   */
  public GridPrint (GridColumn[] columns,
                    int horizontalSpacing,
                    int verticalSpacing) {

    this.columns = BeanUtils.checkNull (columns);
    for (GridColumn col : columns)
      BeanUtils.checkNull (col);
    if (columns.length == 0)
      throw new IllegalArgumentException ("Must specify at least one column.");

    this.horizontalSpacing = checkSpacing (horizontalSpacing);
    this.verticalSpacing = checkSpacing (verticalSpacing);
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
   * Returns the spacing if is it valid, or throws an exception not.
   */
  static int checkSpacing (int spacing) {
    if (spacing < 0 && spacing != BORDER_OVERLAP)
      throw new IllegalArgumentException (
          "Spacing must be a non-negative value or equal to BORDER_OVERLAP.");

    return spacing;
  }

  /**
   * Adds the specified Print to this grid, using the default alignment and a
   * colspan of 1.
   * @param print the print to add to this grid.
   */
  public void add (Print print) {
    add (print, 1, SWT.DEFAULT);
  }

  /**
   * Adds the specified Print to this grid, using the specified colspan and the
   * default alignment.
   * @param print the print to add to this grid.
   * @param colspan the number of columns to span, or
   *          {@link GridPrint#REMAINDER } to span the rest of the row.
   */
  public void add (Print print, int colspan) {
    add (print, colspan, SWT.DEFAULT);
  }

  /**
   * Adds the specified Print to this grid, using the specified colspan and
   * alignment.
   * @param print the print to add to this grid.
   * @param colspan the number of columns to span, or
   *          {@link GridPrint#REMAINDER } to span the rest of the row.
   * @param align the alignment of the print within the grid cell. One of
   *          {@link SWT#LEFT }, {@link SWT#CENTER } or {@link SWT#RIGHT }.
   */
  public void add (Print print, int colspan, int align) {
    // Make sure the colspan would not exceed the number of columns
    if (col + colspan > columns.length)
      throw new IllegalArgumentException ("Colspan " + colspan
          + " too wide at column " + col + " (" + columns.length
          + " columns total)");

    // Start a new row if back at column 0.
    if (col == 0) rows.add (new ArrayList <GridEntry> ());

    // Get the last row.
    List <GridEntry> row = rows.get (rows.size () - 1);

    // Convert REMAINDER to the actual # of columns
    if (colspan == REMAINDER) colspan = columns.length - col;

    // Add the new Print
    GridEntry entry = new GridEntry (print, align, colspan);
    row.add (entry);

    // Adjust the column cursor by the span of the added Print
    col += colspan;

    // If we've filled the row, the next add(...) should start a new row
    if (col == columns.length) col = 0;

    // Make sure column number is valid.
    if (col > columns.length) {
      // THIS SHOULD NOT HAPPEN--ABOVE LOGIC SHOULD PREVENT THIS CASE
      // ..but just in case.

      // Roll back operation.
      col -= colspan;
      row.remove (entry);
      if (row.size () == 0) rows.remove (row);

      // Report error
      throw new IllegalArgumentException ("Colspan " + colspan
          + " too wide at column " + col + " (" + columns.length
          + " columns total)");
    }
  }

  /**
   * Returns the current column groups.  The returned array may be modified
   * without affecting this GridPrint.
   */
  int[][] getColumnGroups () {
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
   * @param columnGroups
   * @throws IndexOutOfBoundsException if any of the column indices are out of
   *           bounds [0 , columnCount-1].
   */
  public void setColumnGroups (int[][] columnGroups) {
    checkColumnGroups (columnGroups);
    this.columnGroups = cloneColumnGroups (columnGroups);
  }

  void checkColumnGroups (int[][] columnGroups) {
    for (int[] group : columnGroups)
      for (int col : group)
        if (col < 0 || col >= columns.length)
          throw new IndexOutOfBoundsException (
              "Column index in column group must be " + "0 <= " + col + " < "
                  + columns.length);
  }

  static int[][] cloneColumnGroups (int[][] columnGroups) {
    int[][] result = columnGroups.clone ();
    for (int i = 0; i < result.length; i++)
      result[i] = result[i].clone ();
    return result;
  }

  /**
   * Sets the border around each of the grid's cells to the argument.
   * @param border the new cell border.
   */
  public void setCellBorder (Border border) {
    this.cellBorder = BeanUtils.checkNull (border);
  }

  /**
   * Returns the border used around each grid cell.
   * @return the border used around each grid cell.
   */
  public Border getCellBorder () {
    return cellBorder;
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new GridIterator (this, device, gc);
  }

  /**
   * Returns the horizontal spacing between grid cells.
   * @return the horizontal spacing between grid cells.
   */
  public int getHorizontalSpacing () {
    return horizontalSpacing;
  }

  /**
   * Sets the horizontal spacing between grid cells.
   * @param horizontalSpacing the new horizontal spacing.  A value of
   *        {@link #BORDER_OVERLAP} indicates that the borders should be
   *        overlapped instead of spaced.
   */
  public void setHorizontalSpacing (int horizontalSpacing) {
    this.horizontalSpacing = horizontalSpacing;
  }

  /**
   * Returns the vertical spacing between grid cells.
   * @return the vertical spacing between grid cells.
   */
  public int getVerticalSpacing () {
    return verticalSpacing;
  }

  /**
   * Sets the vertical spacing between grid cells.
   * @param verticalSpacing the new vertical spacing.  A value of
   *        {@link #BORDER_OVERLAP} indicates that the borders should be
   *        overlapped instead of spaced.
   */
  public void setVerticalSpacing (int verticalSpacing) {
    this.verticalSpacing = verticalSpacing;
  }
}

class GridEntry {
  final Print print;

  final int align;

  final int colspan;

  PrintIterator iterator;

  GridEntry (Print print, int align, int colspan) {
    this.print = BeanUtils.checkNull (print);
    this.align = checkAlign (align);
    this.colspan = checkColspan (colspan);
  }

  private int checkColspan (int colspan) {
    if (colspan > 0 || colspan == GridPrint.REMAINDER) return colspan;

    throw new IllegalArgumentException (
        "colspan must be a positive number or GridPrint.REMAINDER");
  }

  private int checkAlign (int align) {
    if ((align & SWT.DEFAULT) == SWT.DEFAULT)
      return SWT.DEFAULT;
    else if ((align & SWT.LEFT) == SWT.LEFT)
      return SWT.LEFT;
    else if ((align & SWT.CENTER) == SWT.CENTER)
      return SWT.CENTER;
    else if ((align & SWT.RIGHT) == SWT.RIGHT)
      return SWT.RIGHT;
    else
      throw new IllegalArgumentException (
          "Align must be one of SWT.LEFT, SWT.CENTER, SWT.RIGHT, or SWT.DEFAULT");
  }

  GridEntry copy () {
    return new GridEntry (print, align, colspan);
  }
}

class GridIterator implements PrintIterator {
  static GridEntry[][] cloneRows (GridEntry[][] rows) {
    GridEntry[][] result = rows.clone ();
    for (int i = 0; i < result.length; i++)
      result[i] = cloneRow (result[i]);
    return result;
  }

  static GridEntry[] cloneRow (GridEntry[] row) {
    GridEntry[] result = row.clone ();
    for (int i = 0; i < result.length; i++) {
      GridEntry entry = result[i];
      result[i] = entry.copy ();
      result[i].iterator = entry.iterator.copy ();
    }
    return result;
  }

  final GridColumn[] columns;

  final int[][] columnGroups;

  final GridEntry[][] rows;

  final BorderPainter cellBorder;

  final Point dpi; // PIXELS

  final Point spacing; // PIXELS

  final int[] minimumColSizes; // PIXELS

  final int[] preferredColSizes; // PIXELS

  final Point minimumSize; // PIXELS

  final Point preferredSize; // PIXELS

  // This is the cursor!
  private int row;

  // Determines whether top edge of cell border is drawn open or closed.
  private boolean rowStarted;

  GridIterator (GridPrint grid, Device device, GC gc) {
    this.columns = grid.columns;
    this.columnGroups = grid.getColumnGroups ();

    this.rows = new GridEntry[grid.rows.size ()][];
    for (int i = 0; i < rows.length; i++) {
      GridEntry[] row = rows[i] = grid.rows.get (i).toArray (
          new GridEntry[grid.rows.get (i).size ()]);

      for (int j = 0; j < row.length; j++) {
        GridEntry entry = row[j].copy ();
        entry.iterator = entry.print.iterator (device, gc);
        row[j] = entry;
      }
    }

    cellBorder = grid.cellBorder.createPainter (device, gc);

    // Double-check these values -- they are public fields and may have been
    // set to an invalid value.
    int hSpacing = GridPrint.checkSpacing (grid.horizontalSpacing);
    int vSpacing = GridPrint.checkSpacing (grid.verticalSpacing);

    // Compute all the variables we need to perform layout.
    BeanUtils.checkNull (device);
    this.dpi = device.getDPI ();
    // Note that the cell border is INCLUDED in cell spacing!
    this.spacing = new Point (Math.round (hSpacing * dpi.x / 72f)
        + cellBorder.getWidth (), Math.round (vSpacing * dpi.y / 72f)
        + cellBorder.getHeight (false, false));

    Point overlap = cellBorder.getOverlap ();
    if (hSpacing == GridPrint.BORDER_OVERLAP)
      spacing.x = cellBorder.getWidth () - overlap.x;
    if (vSpacing == GridPrint.BORDER_OVERLAP)
      spacing.y = cellBorder.getHeight (false, false) - overlap.y;

    this.minimumColSizes = computeColumnSizes (PrintSizeStrategy.MINIMUM, gc);
    this.preferredColSizes = computeColumnSizes (PrintSizeStrategy.PREFERRED, gc);

    this.minimumSize = computeSize (PrintSizeStrategy.MINIMUM, minimumColSizes);
    this.preferredSize = computeSize (PrintSizeStrategy.PREFERRED, preferredColSizes);

    row = 0;
    rowStarted = false;
  }

  /** Copy constructor (used by copy() only) */
  GridIterator (GridIterator that) {
    this.columns = that.columns;
    this.columnGroups = that.columnGroups;

    this.rows = cloneRows (that.rows);
    this.dpi = that.dpi;
    this.spacing = that.spacing;
    this.minimumColSizes = that.minimumColSizes;
    this.preferredColSizes = that.preferredColSizes;

    this.cellBorder = that.cellBorder;

    this.minimumSize = that.minimumSize;
    this.preferredSize = that.preferredSize;

    this.row = that.row;
    this.rowStarted = that.rowStarted;
  }

  /**
   * Compute the size of a column, respecting the constraints of the GridColumn.
   */
  int computeColumnSize (GridEntry entry,
                         GridColumn col,
                         PrintSizeStrategy strategy,
                         GC gc) {
    if (col.size == SWT.DEFAULT)
      return strategy.computeSize (entry.iterator).x;
    if (col.size == GridPrint.PREFERRED)
      return entry.iterator.preferredSize ().x;
    return Math.round (col.size * dpi.x / 72f);
  }

  static boolean isExplicitSize (GridColumn col) {
    return col.size > 0;
  }

  void groupColumns (int[] columnSizes) {
    for (int[] group : columnGroups) {
      int maxSize = 0;
      for (int col : group)
        maxSize = Math.max (maxSize, columnSizes[col]);

      for (int col : group)
        columnSizes[col] = maxSize;
    }
  }

  boolean isGrouped (int col) {
    for (int[] group : columnGroups)
      for (int c : group)
        if (c == col) return true;

    return false;
  }

  static boolean isGroupMember (int col, int[] group) {
    for (int c : group)
      if (c == col) return true;

    return false;
  }

  int[] computeColumnSizes (PrintSizeStrategy strategy, GC gc) {
    int[] colSizes = new int[columns.length];

    // First pass - find widths for all explicitly sized columns.
    for (int i = 0; i < columns.length; i++)
      if (isExplicitSize (columns[i]))
        colSizes[i] = Math.round (columns[i].size * dpi.x / 72f);

    // Second pass - find the column widths for all cells that span a single
    // column. (Skip explicitly sized columns)
    for (GridEntry[] row : rows) {
      int col = 0;
      for (GridEntry entry : row) {
        // ignore explicitly sized cols
        if (entry.colspan == 1 && !isExplicitSize (columns[col])) { 
          colSizes[col] = Math.max (
              colSizes[col],
              computeColumnSize (entry, columns[col], strategy, gc));
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
    for (GridEntry[] row : rows) {
      int col = 0;
      for (GridEntry entry : row) {
        if (entry.colspan > 1) {
          // Calculate the current total width of column span.
          int currentSpanWidth = 0; // neglect column spacing
          for (int i = col; i < col + entry.colspan; i++)
            currentSpanWidth += colSizes[i];

          // Calculate the minimum width of the print in this cell.
          int minimumSpanWidth = strategy.computeSize (entry.iterator).x
              - spacing.x * (entry.colspan - 1); // subtract column spacing

          // Note that we omitted column spacing so the weighted distribution
          // of any extra width doesn't get thrown off.

          if (minimumSpanWidth > currentSpanWidth) {
            // We need more space in these columns. Distribute the extra
            // width between them, proportionately to their current sizes.
            // Smaller columns are thus affected less than larger columns.
            int extraWidth = minimumSpanWidth - currentSpanWidth;

            // Expand each column in the cell span proportionately.
            // First determine which cells in the cell span should be
            // expanded.
            List <Integer> expandableColumns = new ArrayList <Integer> ();
            int expandableColumnsWidth = 0; // for scaling of expansion

            abstract class ColumnFilter {
              abstract boolean accept (int col);
            }

            ColumnFilter[] filters = {
                // Ungrouped columns with nonzero weight are first choice for
                // expansion.
                new ColumnFilter () {
                  @Override
                  boolean accept (int col) {
                    return !isGrouped (col) && columns[col].weight > 0;
                  }
                },

                // Grouped columns with nonzero weight are next choice
                new ColumnFilter () {
                  @Override
                  boolean accept (int col) {
                    return isGrouped (col) && columns[col].weight > 0;
                  }
                },

                // Ungrouped columns with GridPrint.PREFERRED size are next
                // choice.
                new ColumnFilter () {
                  @Override
                  boolean accept (int col) {
                    return !isGrouped (col)
                        && columns[col].size == GridPrint.PREFERRED;
                  }
                },

                // Grouped columns with GridPrint.PREFERRED size are next
                // choice.
                new ColumnFilter () {
                  @Override
                  boolean accept (int col) {
                    return isGrouped (col)
                        && columns[col].size == GridPrint.PREFERRED;
                  }
                },

                // Ungrouped columns with SWT.DEFAULT size are last choice.
                new ColumnFilter () {
                  @Override
                  boolean accept (int col) {
                    return !isGrouped (col) && columns[col].size == SWT.DEFAULT;
                  }
                },

                // Grouped columns with SWT.DEFAULT size are last choice.
                new ColumnFilter () {
                  @Override
                  boolean accept (int col) {
                    return isGrouped (col) && columns[col].size == SWT.DEFAULT;
                  }
                } };

            // Use column filters to determine which columns should be expanded.
            for (ColumnFilter filter : filters) {
              for (int i = col; i < col + entry.colspan; i++)
                if (filter.accept (i)) {
                  expandableColumns.add (i);
                  expandableColumnsWidth += colSizes[i];
                }
              // If the filter matched 1 or more columns in this iteration,
              // expand the matched columns.
              if (expandableColumns.size () > 0) break;
            }

            // If the expandable columns are zero width, expand them equally
            if (expandableColumnsWidth == 0) {
              int expandableCols = expandableColumns.size ();
              for (int i : expandableColumns) {
                int addedWidth = extraWidth / expandableCols;

                colSizes[i] = addedWidth;
                extraWidth -= addedWidth;
                expandableCols--;
              }
            }
            // Otherwise expand them proportionately.
            else {
              for (int i : expandableColumns) {
                if (expandableColumnsWidth == 0) break;

                int addedWidth = extraWidth * colSizes[i]
                    / expandableColumnsWidth;

                // Adjust expandableColumnsWidth and extraWidth for future
                // iterations.
                expandableColumnsWidth -= colSizes[i];
                extraWidth -= addedWidth;

                // NOW we can add the added width.
                colSizes[i] += addedWidth;
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

  private Point computeSize (PrintSizeStrategy strategy, int[] colSizes) {
    // Calculate width from column sizes and spacing.
    int width = spacing.x * (colSizes.length - 1);
    // Add width of cell border at far left and far right.
    width += cellBorder.getWidth ();
    for (int col : colSizes)
      width += col;

    int height = 0;
    for (GridEntry[] row : rows) {
      int col = 0;
      for (GridEntry entry : row) {
        // Determine cell width for this entry's cell span.
        int cellWidth = spacing.x * (entry.colspan - 1); // spacing between
                                                          // columns
        for (int i = col; i < col + entry.colspan; i++)
          // add size of each column.
          cellWidth += colSizes[i];

        // Find the greatest height of all cells' calculated sizes.
        height = Math.max (height, strategy.computeSize (entry.iterator).y);
      }
    }

    // Add height of tallest possible cell borders.
    return new Point (width, height + cellBorder.getMaxHeight ());
  }

  public Point minimumSize () {
    return new Point (minimumSize.x, minimumSize.y);
  }

  public Point preferredSize () {
    return new Point (preferredSize.x, preferredSize.y);
  }

  private int[] getShrinkableColumns (int extraWidth) {
    List <Integer> shrinkableColumns = new ArrayList <Integer> ();

    int shrinkableWidth = 0;

    // Search first for columns with DEFAULT size.
    for (int i = 0; i < columns.length; i++)
      if (columns[i].size == SWT.DEFAULT) {
        shrinkableColumns.add (i);
        shrinkableWidth += minimumColSizes[i];
      }

    // If the shrinkable columns we found are not at least as wide as the
    // width we need to lose, throw away the results and broaden the search
    // below.
    if (shrinkableWidth < extraWidth) {
      shrinkableColumns.clear ();
      shrinkableWidth = 0;
    }

    // If no DEFAULT size columns turn up, try PREFERRED size columns instead.
    if (shrinkableColumns.size () == 0)
      for (int i = 0; i < columns.length; i++)
        if (columns[i].size == GridPrint.PREFERRED
            || columns[i].size == SWT.DEFAULT) {
          shrinkableColumns.add (i);
          shrinkableWidth += minimumColSizes[i];
        }

    // If the shrinkable columns we found are not at least as wide as the
    // width we need to lose, throw away the results and broaden the search
    // below.
    if (shrinkableWidth < extraWidth) {
      shrinkableColumns.clear ();
      shrinkableWidth = 0;
    }

    // If no PREFERRED size columns were found, shrink all columns.
    if (shrinkableColumns.size () == 0)
      for (int i = 0; i < columns.length; i++)
        shrinkableColumns.add (i);

    int[] result = new int[shrinkableColumns.size ()];
    for (int i = 0; i < result.length; i++)
      result[i] = shrinkableColumns.get (i);

    return result;
  }

  private int[] computeAdjustedColumnSizes (int width) {
    // Remove column spacing from equation first off
    width = width - spacing.x * (columns.length - 1);

    // Remove width of far left and far right border (all other
    // borders were included for in the cell spacing).
    width -= cellBorder.getWidth ();

    int minimumWidth = 0;
    int preferredWidth = 0;
    for (int i = 0; i < columns.length; i++) {
      minimumWidth += minimumColSizes[i];
      preferredWidth += preferredColSizes[i];
    }

    // Case 1: width < minimum width
    // Start with minimum column sizes. Determine which columns should
    // shrink, and reduce them proportionately to their minimum sizes.
    if (width < minimumWidth) {
      int[] colSizes = minimumColSizes.clone ();

      // How much wider is the minimum width than the available width?
      int extraWidth = minimumWidth - width;

      // Determine which columns are shrinkable.
      int[] shrinkableCols = getShrinkableColumns (extraWidth);

      // How wide are the shrinkable columns put together.
      int shrinkableWidth = 0;
      for (int col : shrinkableCols)
        shrinkableWidth += colSizes[col];

      for (int i = 0; i < shrinkableCols.length; i++) {
        int col = shrinkableCols[i];

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
      int widthDifference = preferredWidth - minimumWidth;

      int[] colSizes = new int[columns.length];
      for (int i = 0; i < columns.length; i++) {
        int colDifference = preferredColSizes[i] - minimumColSizes[i];

        int addedWidth = (widthDifference > 0) ? extraWidth * colDifference
            / widthDifference : 0;

        colSizes[i] = minimumColSizes[i] + addedWidth;

        // adjust extraWidth and widthDifference - eliminates round-off error
        extraWidth -= addedWidth;
        widthDifference -= colDifference;
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
      List <Integer> weightedCols = new ArrayList <Integer> ();
      for (int i = 0; i < columns.length; i++)
        if (columns[i].weight > 0) {
          weight += columns[i].weight;
          weightedCols.add (i);
        }

      // Start with preferred column sizes.
      int[] colSizes = preferredColSizes.clone ();
      // Expand weighted columns according to their weights.
      for (int i : weightedCols) {
        int colWeight = columns[i].weight;

        int addWidth = colWeight * extraWidth / weight;

        colSizes[i] += addWidth;

        // adjust extraWidth and growColumns - eliminates round-off error
        extraWidth -= addWidth;
        weight -= colWeight;
      }

      return colSizes;
    }

    throw new RuntimeException (
        "GridPrintIterator.adjustColumnSizes(...) logic is flawed.");
  }

  public boolean hasNext () {
    return row < rows.length;
  }

  static class ByRef <T> {
    T value;

    ByRef (T value) {
      set (value);
    }

    void set (T value) {
      this.value = value;
    }

    T get () {
      return value;
    }
  }

  /**
   * Iterates through the current row and returns a CompositeEntry array of the
   * PrintPieces generated .
   * @param width the available width on the print device.
   * @param height the available height on the print device.
   * @param colSizes the width of the grid columns.
   * @param yOffset the y offset to give the PrintPieces.
   * @param topOpen whether the cell border is open at the top.
   * @param bottomOpen whether the cell border is open at the bottom. If false,
   *          this method must return null if one or more cells cannot consume
   *          all its content in this iteration.
   * @param rowHeight a ByRef parameter for reporting the height of the row back
   *          to the caller.
   * @param hasNext a ByRef parameter for reporting back to the caller whether
   *          any of the cells have more content.
   * @return A CompositeEntry array resulting from the iteration. Returns null
   *         if the iteration failed. This happens if bottomOpen is false and
   *         one or more cells could not be consume within the available area.
   */
  CompositeEntry[] iterateRow (int width,
                               int height,
                               int[] colSizes,
                               int yOffset,
                               boolean topOpen,
                               boolean bottomOpen,
                               final ByRef <Integer> rowHeight,
                               final ByRef <Boolean> hasNext) {

    GridEntry[] rowEntries = cloneRow (rows[row]);

    int[] cellOffsets = new int[rowEntries.length];
    int[] pieceOffsets = new int[rowEntries.length];
    int[] widths = new int[rowEntries.length];
    PrintPiece[] rowPieces = new PrintPiece[rowEntries.length];

    int x = 0;
    int col = 0;
    rowHeight.set (0);
    hasNext.set (false);
    boolean hasPieces = false;

    for (int i = 0; i < rowEntries.length; i++) {
      cellOffsets[i] = x;

      GridEntry entry = rowEntries[i];
      PrintIterator iter = rowEntries[i].iterator;

      // Determine width of the cell span, including spacing between cells.
      int cellspanWidth = (entry.colspan - 1) * spacing.x;
      for (int j = col; j < col + entry.colspan; j++)
        cellspanWidth += colSizes[j];
      widths[i] = cellspanWidth;

      // Skip this cell if is already consumed.
      if (!iter.hasNext ()) {
        // But advance the column cursor
        x = x + cellspanWidth + spacing.x;
        col += entry.colspan;
        continue;
      }

      // Iterate the current cell.
      PrintPiece piece = rowPieces[i] = iter.next (cellspanWidth,
          height - cellBorder.getHeight (topOpen, bottomOpen));

      // If bottomOpen is false, then all of the row's content must be
      // consumed in this iteration. Therefore if the iterator has more
      // content, this iteration fails. Dispose any print pieces from
      // previous loops and return null.
      if (!bottomOpen && iter.hasNext ()) {
        for (int j = 0; j <= i; j++)
          if (rowPieces[j] != null) rowPieces[j].dispose ();
        return null;
      }

      // Update hasNext for this cell's iterator.
      if (iter.hasNext ()) hasNext.set (true);

      // Update hasPieces for this cell's print piece.
      hasPieces = hasPieces || piece != null;

      // Determine the alignment for this cell.
      int align = entry.align;
      if (align == SWT.DEFAULT) align = columns[col].align;

      if (piece != null) {
        // Calculate the X offset of the PrintPiece within the cellspan,
        // according to the alignment.
        int offset = 0;
        if (align == SWT.CENTER)
          offset = (cellspanWidth - piece.getSize ().x) / 2;
        else if (align == SWT.RIGHT)
          offset = cellspanWidth - piece.getSize ().x;
        pieceOffsets[i] = offset;

        // Update the row height
        rowHeight.set (Math.max (rowHeight.get (), piece.getSize ().y));
      }

      // Adjust x offset and column number.
      x = x + cellspanWidth + spacing.x;
      col += entry.colspan;
    }

    // If the row was not consumed and no print pieces were created, iteration
    // fails.
    if (hasNext.get () && !hasPieces) {
      for (PrintPiece piece : rowPieces)
        if (piece != null) piece.dispose ();
      return null;
    }

    // If the row WAS consumed, then it doesn't matter if no print pieces were
    // created. In this case the cells from this iteration are simply drawn
    // as an empty cell with a bottom border around it.

    // Now that we've successfully iterated through the row, and now
    // rowEntries contains the most recent iterators for the current state.
    // Replace the row in the original array with the updated row from our
    // iteration.
    rows[row] = rowEntries;

    // Construct and return the result.
    CompositeEntry[] result = new CompositeEntry[rowEntries.length];
    for (int i = 0; i < result.length; i++) {
      Point size = new Point (widths[i] + cellBorder.getWidth (), rowHeight
          .get ()
          + cellBorder.getHeight (topOpen, bottomOpen));
      Point offset = new Point (cellOffsets[i], yOffset);

      result[i] = new CompositeEntry (new BorderedPrintPiece (size, cellBorder,
          topOpen, bottomOpen, pieceOffsets[i], rowPieces[i]), offset);
    }
    return result;
  }

  public PrintPiece next (int width, int height) {
    if (!hasNext ()) throw new IllegalStateException ();

    // Compute column sizes for the available width.
    int[] colSizes = computeAdjustedColumnSizes (width);

    List <CompositeEntry> pieces = new ArrayList <CompositeEntry> ();

    int y = 0;

    while (hasNext ()) {
      ByRef <Boolean> rowHasNext = new ByRef <Boolean> (false);
      ByRef <Integer> rowHeight = new ByRef <Integer> (0);

      // First attempt to iterate the row with a closed bottom border.
      CompositeEntry[] rowEntries = iterateRow (width, height - y, colSizes, y,
          rowStarted, false, rowHeight, rowHasNext);

      // If the iteration failed, or the row has more content (which it
      // shouldn't when the bottom border is closed) then try the iteration
      // again with an the bottom border open.
      if (rowEntries == null) {
        rowHeight.set (0);
        rowHasNext.set (false);
        rowEntries = iterateRow (width, height - y, colSizes, y, rowStarted,
            true, rowHeight, rowHasNext);
      }

      // If both attempts failed on the current row, terminate iteration.
      // (Break instead of return, because there may be previous rows in this
      // that should be returned.)
      if (rowEntries == null) break;

      for (CompositeEntry entry : rowEntries)
        pieces.add (entry);

      y += rowHeight.get () + spacing.y;

      // If the row we just iterated has more content, then this iteration
      // is complete. Set the rowStarted flag so the next iteration shows
      // an open top border in the cells.
      if (rowHasNext.get ()) {
        rowStarted = true;
        break;
      }

      // If we get to here then the row complete. Clear the rowStarted flag
      // and advance to the next row.
      rowStarted = false;
      row++;
    }

    if (pieces.size () == 0) return null;

    Point size = new Point ((colSizes.length - 1) * spacing.x, 0);
    for (int col : colSizes)
      size.x += col;
    return new CompositePiece (pieces, size);
  }

  public PrintIterator copy () {
    return new GridIterator (this);
  }
}

class BorderedPrintPiece implements PrintPiece {
  final Point size;
  final BorderPainter border;
  final boolean topOpen;
  final boolean bottomOpen;
  final int pieceOffset;
  final PrintPiece target;

  BorderedPrintPiece (Point size,
                      BorderPainter border,
                      boolean topOpen,
                      boolean bottomOpen,
                      int pieceOffset,
                      PrintPiece target) {
    this.size = new Point (size.x, size.y);
    this.border = BeanUtils.checkNull (border);
    this.topOpen = topOpen;
    this.bottomOpen = bottomOpen;
    this.pieceOffset = pieceOffset;
    this.target = target;
  }

  public Point getSize () {
    return size;
  }

  public void paint (GC gc, int x, int y) {
    border.paint (gc, x, y, size.x, size.y, topOpen, bottomOpen);
    if (target != null)
      target.paint (gc, x + border.getLeft () + pieceOffset, y
          + border.getTop (topOpen));
  }

  public void dispose () {
    border.dispose ();
    if (target != null)
      target.dispose ();
  }
}