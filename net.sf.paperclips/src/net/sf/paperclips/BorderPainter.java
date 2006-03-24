/*
 * Created on Nov 17, 2005
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * Interface for calculating and drawing borders in a BorderPrint.
 * @author Matthew
 */
public interface BorderPainter {
  /**
   * Paints a border around the specified region. Depending on the type of
   * border, the top and bottom of may be painted differently depending on the
   * values of <code>topOpen</code> and <code>bottomOpen</code>.
   * @param gc The graphics context to paint on.
   * @param x The x coordinate of the top left corner of the border.
   * @param y The y coordinate of the top left corner of the border.
   * @param width The width of the border to paint
   * @param height The height of the border to paint
   * @param topOpen If true, the top border should be drawn "open," to indicate
   *          that this is the continuation of a border in a previous iteration.
   *          If false, the border should be drawn "closed" to indicate that
   *          this is the first iteration on the BorderPrint's target.
   * @param bottomOpen If true, the bottom border should be drawn "open," to
   *          indicate that the BorderPrint's target was not consumed in this
   *          iteration. If false, the bottom border should be drawn "closed,"
   *          to indicate that the BorderPrint's target completed during this
   *          iteration.
   */
  public void paint (GC gc,
                     int x,
                     int y,
                     int width,
                     int height,
                     boolean topOpen,
                     boolean bottomOpen);

  /**
   * Returns the border inset, in pixels, from the left.
   * @return the border inset, in pixels, from the left.
   */
  public int getLeft ();

  /**
   * Returns the border inset, in pixels, from the right.
   * @return the border inset, in pixels, from the right.
   */
  public int getRight ();

  /**
   * Returns the sum of the left and right border insets.
   * @return the sum of the left and right border insets.
   */
  public int getWidth ();

  /**
   * Returns the border inset, in pixels, from the top.
   * @param open If true, the inset of an open border will be returned. If
   *          false, the inset of a closed border will be returned.
   * @return the border inset, in pixels, from the top.
   */
  public int getTop (boolean open);

  /**
   * Returns the border inset, in pixels, from the bottom.
   * @param open If true, the inset of an open border will be returned. If
   *          false, the inset of a closed border will be returned.
   * @return the border inset, in pixels, from the bottom.
   */
  public int getBottom (boolean open);

  /**
   * Returns the sum of the top and bottom border insets.
   * @param topOpen If true, the inset of an open border will be returned. If
   *          false, the inset of a closed border will be returned.
   * @param bottomOpen If true, the inset of an open border will be returned. If
   *          false, the inset of a closed border will be returned.
   * @return the sum of the top and bottom border insets.
   */
  public int getHeight (boolean topOpen, boolean bottomOpen);

  /**
   * Returns the sum of the maximum top and bottom border insets.
   * @return the sum of the maximum top and bottom border insets.
   */
  public int getMaxHeight ();

  /**
   * Returns the x and y distance that two of the same BorderPainters would
   * overlap to create the appearance of a single border between the two. This
   * method is used by GridPrint whenever the horizontal and/or vertical spacing
   * fields are set to {@link GridPrint#BORDER_OVERLAP }.
   * @return the distance that this border painter would overlap an adjacent
   *         one.
   */
  public Point getOverlap ();
}