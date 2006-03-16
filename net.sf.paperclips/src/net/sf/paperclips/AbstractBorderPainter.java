/*
 * Created on Oct 18, 2005
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.GC;

/**
 * Abstract implementation of BorderPainter providing
 * @author Matthew
 */
public abstract class AbstractBorderPainter implements BorderPainter {
  /**
   * Paints a border around the specified region. Depending on the type of
   * border, the top and bottom of may be painted differently depending on the
   * values of <code>topOpen</code> and <code>bottomOpen</code>.
   */
  public abstract void paint (GC gc,
                              int x,
                              int y,
                              int width,
                              int height,
                              boolean topOpen,
                              boolean bottomOpen);

  /**
   * Returns the border inset, in pixels, from the left.
   */
  public abstract int getLeft ();

  /**
   * Returns the border inset, in pixels, from the right.
   */
  public abstract int getRight ();

  /**
   * Returns the sum of the left and right border insets.
   */
  public final int getWidth () {
    return getLeft () + getRight ();
  }

  /**
   * Returns the border inset, in pixels, from the top.
   */
  public abstract int getTop (boolean open);

  /**
   * Returns the border inset, in pixels, from the bottom.
   */
  public abstract int getBottom (boolean open);

  /**
   * Returns the sum of the top and bottom border insets.
   */
  public final int getHeight (boolean topOpen, boolean bottomOpen) {
    return getTop (topOpen) + getBottom (bottomOpen);
  }

  /**
   * Returns the sum of the maximum top and bottom border insets.
   */
  public final int getMaxHeight () {
    return Math.max (getTop (false), getTop (true))
        + Math.max (getBottom (false), getBottom (true));
  }

  /**
   * Disposes all system resources allocated by this BorderPainter. This
   * implementation does nothing.
   */
  public void dispose () {}
}
