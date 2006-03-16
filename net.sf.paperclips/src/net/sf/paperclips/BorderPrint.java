/*
 * Created on Oct 18, 2005
 */
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A decorator for displaying a border around a child print.
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
  private boolean topOpen;

  BorderIterator (BorderPrint print, Device device, GC gc) {
    this.target = print.target.iterator (device, gc);
    this.border = print.border.createPainter (device, gc);

    this.topOpen = false;
  }

  BorderIterator (BorderIterator that) {
    this.target = that.target.copy ();
    this.border = that.border;

    this.topOpen = that.topOpen;
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

  public PrintPiece next (int width, int height) {
    if (!hasNext ()) throw new IllegalStateException ();

    // Is area less than border size?
    if (width < border.getWidth ()
        || height < border.getHeight (topOpen, false)) return null;

    // Backup target iterator
    PrintIterator backup = target.copy ();

    // Try iterating with a closed bottom border.
    boolean bottomOpen = false;
    PrintPiece piece = target.next (width - border.getWidth (), height
        - border.getTop (topOpen) - border.getBottom (bottomOpen));

    // If the iteration did not completely consume the target, then a closed
    // border at the bottom is not correct. Restore the target from the
    // backup and clear the PrintPiece so we can try the iteration again.
    if (target.hasNext ()) {
      target = backup;

      // Dispose unused PrintPiece, if any
      if (piece != null) piece.dispose ();

      piece = null;
    }

    // Previous iteration failed. Try iterating
    // again with an open border instead.
    if (piece == null) {
      bottomOpen = true;
      piece = target.next (width - border.getWidth (), height
          - border.getTop (topOpen) - border.getBottom (bottomOpen));
    }

    // If we still get null, then there isn't enough room to iterate the
    // target with the correct borders. Iteration fails.
    if (piece == null) return null;

    // Iteration successful.
    PrintPiece result = new BorderPiece (piece, border, topOpen, bottomOpen);

    // Set the topOpen field so it is correct for the next iteration.
    this.topOpen = true;

    return result;
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
    this.size = new Point (targetSize.x + border.getWidth (), targetSize.y
        + border.getHeight (topOpen, bottomOpen));
  }

  public void dispose () {
    target.dispose ();
  }

  public Point getSize () {
    return new Point (size.x, size.y);
  }

  public void paint (GC gc, int x, int y) {
    border.paint (gc, x, y, size.x, size.y, topOpen, bottomOpen);
    target.paint (gc, x + border.getLeft (), y + border.getTop (topOpen));
  }
}