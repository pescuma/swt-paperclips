/*
 * Created on Oct 18, 2005
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A decorator that draws a border around the target print.
 * @see BorderDecorator
 * @author Matthew
 */
public class BorderPrint implements Print {
  final Print target;
  final Border border;

  /**
   * Constructs a BorderPrint with the given target and border.
   * @param target the print to decorate with a border.
   * @param border the border which will be drawn around the target.
   * @throws NullPointerException if either argument is null.
   */
  public BorderPrint (Print target, Border border) {
    this.target = BeanUtils.checkNull (target);
    this.border = BeanUtils.checkNull (border);
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new BorderIterator (this, device, gc);
  }
}

class BorderIterator implements PrintIterator {
  private/* final */PrintIterator target;

  private final BorderPainter border;

  // Quasi-cursor
  private boolean opened;

  BorderIterator (BorderPrint print, Device device, GC gc) {
    this.target = print.target.iterator (device, gc);
    this.border = print.border.createPainter (device, gc);

    this.opened = false;
  }

  BorderIterator (BorderIterator that) {
    this.target = that.target.copy ();
    this.border = that.border;

    this.opened = that.opened;
  }

  public boolean hasNext () {
    return target.hasNext ();
  }

  public Point minimumSize () {
    Point targetSize = target.minimumSize ();
    return new Point (targetSize.x + border.getWidth (), targetSize.y
        + border.getMaxHeight ());
  }

  public Point preferredSize () {
    Point targetSize = target.preferredSize ();
    return new Point (targetSize.x + border.getWidth (), targetSize.y
        + border.getMaxHeight ());
  }

  private PrintPiece next(int width, int height, boolean bottomOpen) {
    // Adjust iteration area for border dimensions.
    width -= border.getWidth();
    height -= border.getHeight(opened, bottomOpen);
    if (width < 0 || height < 0) return null;

    PrintIterator testIterator = target.copy();
    PrintPiece piece = testIterator.next (width, height);

    if (piece == null) return null;

    // If bottom border is closed, testIterator must be consumed in this
    // iteration (don't close the border until the target is consumed).
    if (!bottomOpen && testIterator.hasNext()) {
      piece.dispose();
      return null;
    }

    // Wrap the print piece with border
    piece = new BorderPiece (piece, border, opened, bottomOpen);

    this.target = testIterator;
    return piece;
  }

  public PrintPiece next (int width, int height) {
    if (!hasNext ()) throw new IllegalStateException ();

    // Try iterating with a closed bottom border first.
    PrintPiece piece = next(width, height, false);

    // If iteration failed with a closed bottom border, try again with an
    // open bottom border instead.
    if (piece == null) {
      piece = next(width, height, true);

      // If we still get null, then there isn't enough room to iterate the
      // target with the correct borders. Iteration fails.
      if (piece == null)
        return null;
    }

    // Iteration successful.  Set the topOpen field so it is correct for the
    // next iteration (if any).
    this.opened = true;

    return piece;
  }

  public PrintIterator copy () {
    return new BorderIterator (this);
  }
}

class BorderPiece implements PrintPiece {
  private final PrintPiece target;

  private final BorderPainter border;

  private final boolean topOpen;

  private final boolean bottomOpen;

  private final Point size;

  BorderPiece (PrintPiece piece,
               BorderPainter border,
               boolean topOpen,
               boolean bottomOpen) {
    this.target = BeanUtils.checkNull (piece);
    this.border = BeanUtils.checkNull (border);

    this.topOpen = topOpen;
    this.bottomOpen = bottomOpen;

    Point targetSize = target.getSize ();
    this.size = new Point (
        targetSize.x + border.getWidth (),
        targetSize.y + border.getHeight (topOpen, bottomOpen));
  }

  public Point getSize () {
    return new Point (size.x, size.y);
  }

  public void paint (GC gc, int x, int y) {
    border.paint (gc, x, y, size.x, size.y, topOpen, bottomOpen);
    target.paint (gc, x + border.getLeft (), y + border.getTop (topOpen));
  }

  public void dispose () {
    border.dispose ();
    target.dispose ();
  }
}