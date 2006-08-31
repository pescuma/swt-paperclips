/*
 * Created on Nov 7, 2005
 */
package net.sf.paperclips;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A wrapper print that aligns its target vertically and horizontally.
 * @author Matthew
 */
public class AlignPrint implements Print {
  final Print target;
  final int hAlign;
  final int vAlign;

  /**
   * Constructs a new AlignPrint.
   * @param target the print being aligned.
   * @param hAlign the horizontal alignment.
   * @param vAlign the vertical alignment.
   */
  public AlignPrint (Print target, int hAlign, int vAlign) {
    this.target = BeanUtils.checkNull (target);
    this.hAlign = checkHAlign (hAlign);
    this.vAlign = checkVAlign (vAlign);
  }

  private static int checkHAlign (int hAlign) {
    if (hAlign == SWT.LEFT || hAlign == SWT.CENTER || hAlign == SWT.RIGHT)
      return hAlign;
    throw new IllegalArgumentException (
        "hAlign must be one of SWT.LEFT, SWT.CENTER or SWT.RIGHT");
  }

  private static int checkVAlign (int vAlign) {
    if (vAlign == SWT.TOP || vAlign == SWT.CENTER || vAlign == SWT.BOTTOM)
      return vAlign;
    throw new IllegalArgumentException (
        "vAlign must be one of SWT.TOP, SWT.CENTER or SWT.BOTTOM");
  }

  public PrintIterator iterator (Device device, GC gc) {
    return new AlignIterator (this, device, gc);
  }
}

class AlignIterator implements PrintIterator {
  private final PrintIterator target;
  private final int hAlign;
  private final int vAlign;

  AlignIterator (AlignPrint print, Device device, GC gc) {
    this.target = print.target.iterator (device, gc);
    this.hAlign = print.hAlign;
    this.vAlign = print.vAlign;
  }

  AlignIterator (AlignIterator that) {
    this.target = that.target.copy ();
    this.hAlign = that.hAlign;
    this.vAlign = that.vAlign;
  }

  public boolean hasNext () {
    return target.hasNext ();
  }

  public Point minimumSize () {
    return target.minimumSize ();
  }

  public Point preferredSize () {
    return target.preferredSize ();
  }

  public PrintPiece next (int width, int height) {
    PrintPiece piece = target.next (width, height);
    if (piece == null) return null;

    Point size = piece.getSize ();
    Point offset = new Point (0, 0);

    if (hAlign == SWT.CENTER)
      offset.x = (width - size.x) / 2;
    else if (hAlign == SWT.RIGHT)
      offset.x = width - size.x;

    if (hAlign != SWT.LEFT)
      size.x = width;

    if (vAlign == SWT.CENTER)
      offset.y = (height - size.y) / 2;
    else if (vAlign == SWT.BOTTOM)
      offset.y = height - size.x;

    if (vAlign != SWT.TOP)
      size.y = height;

    CompositeEntry entry = new CompositeEntry (piece, offset);

    return new CompositePiece (new CompositeEntry[] { entry }, size);
  }

  public PrintIterator copy () {
    return new AlignIterator (this);
  }
}