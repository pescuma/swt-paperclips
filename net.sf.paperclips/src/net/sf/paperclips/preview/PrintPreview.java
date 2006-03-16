/*
 * Created on Sep 27, 2005
 */
package net.sf.paperclips.preview;

import net.sf.paperclips.Print;
import net.sf.paperclips.PrintIterator;
import net.sf.paperclips.PrintPiece;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A JFace-style print preview manager which displays a Print in a scrollable
 * pane.
 * @author Matthew
 */
public class PrintPreview {
  ScrolledComposite sc;
  PrintPieceCanvas canvas;
  Print print;

  int canvasWidth;

  /**
   * Constructs a PrintPreview with the given parent and style.
   * @param parent the parent component of the scroll pane.
   * @param style the style of the scroll pane.
   */
  public PrintPreview(Composite parent, int style) {
    sc = new ScrolledComposite(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
    sc.setExpandHorizontal(true);
    sc.setExpandVertical(true);
    sc.addControlListener(new ControlAdapter() {
      @Override
      public void controlResized(ControlEvent e) {
        if (sc.getClientArea().width != canvasWidth)
          updateCanvas();
      }
    });
    sc.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        PrintPiece piece = canvas.getPrintPiece();
        if (piece != null)
          piece.dispose();
      }
    });
    canvas = new PrintPieceCanvas(sc, SWT.NONE);
    sc.setContent(canvas);
  }

  /**
   * Returns the viewer component wrapped by this PrintPreview.
   * @return the viewer component wrapped by this PrintPreview.
   */
  public Control getControl() {
    return sc;
  }

  /**
   * Sets the Print to be displayed.
   * @param print the Print to display.
   */
  public void setPrint(Print print) {
    this.print = print;
    updateCanvas();
  }

  /**
   * Returns the Print being displayed.
   * @return the Print being displayed.
   */
  public Print getPrint() {
    return print;
  }

  void updateCanvas() {
    if (print == null) {
      canvas.setPrintPiece(null);
      return;
    }

    GC gc = null;
    try {
      gc = new GC(canvas);

      PrintIterator iter = print.iterator(canvas.getDisplay(), gc);

      int minWidth = iter.minimumSize().x;
      int visibleWidth = sc.getClientArea().width;
      canvasWidth = Math.max(minWidth, visibleWidth);

      PrintPiece piece = iter.next(
          canvasWidth, Integer.MAX_VALUE);

      sc.setMinSize(piece == null ?
          new Point(0, 0) : piece.getSize());

      PrintPiece old = canvas.getPrintPiece();
      if (old != null)
        old.dispose();

      canvas.setPrintPiece(piece);
    } finally {
      if (gc != null)
        gc.dispose();
    }
  }
}
