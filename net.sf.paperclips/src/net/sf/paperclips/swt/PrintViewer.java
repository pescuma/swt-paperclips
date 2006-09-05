/*
 * Created on Sep 27, 2005
 */
package net.sf.paperclips.swt;

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
 * A JFace-style {@link Print} viewer which displays a Print in a scrollable pane.
 * @author Matthew
 */
public class PrintViewer {
  ScrolledComposite sc;
  PrintPieceCanvas canvas;
  Print print;

  int canvasWidth;

  /**
   * Constructs a PrintPreview with the given parent and style.
   * @param parent the parent component of the scroll pane.
   * @param style the style of the scroll pane.
   */
  public PrintViewer(Composite parent, int style) {
    sc = new ScrolledComposite(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
    sc.setExpandHorizontal(true);
    sc.setExpandVertical(true);
    sc.addControlListener(new ControlAdapter() {
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

      PrintPiece piece = iter.copy().next(canvasWidth, Integer.MAX_VALUE);

      // If the print is vertically greedy, find the smallest height that will fit the print's
      // complete contents onto one tall page.
      if (piece != null &&
          piece.getSize().y == Integer.MAX_VALUE) {

        int low = iter.preferredSize().y;
        int high = iter.preferredSize().y;
        
        // First geometrically increase the range low-high until we find the range that the print
        // fits in, in one piece.
        while (true) {
          PrintIterator testIter = iter.copy();
          PrintPiece test = testIter.next(canvasWidth, high);
          if (test == null) {
            low = high;
            high *= 4;
          } else if (testIter.hasNext()) {
            low = high;
            high *= 4;
            test.dispose();
          } else {
            // Once hasNext returns false we have found the range the print fits within
            piece.dispose();
            piece = test;
            break;
          }
        }

        // Now narrow down the best height within the range found in the last loop.
        while (high - low > 1) {
          int height = (low+high)/2;
          PrintIterator testIter = iter.copy();
          PrintPiece test = testIter.next(canvasWidth, height);

          if (test == null) {
            low = height;
          } else if (testIter.hasNext()) {
            low = height;
            test.dispose();
          } else {
            // replace previous best fit with new best fit
            high = height;
            piece.dispose();
            piece = test;
          }
        }
      }

      sc.setMinSize(piece == null ? new Point(0, 0) : piece.getSize());

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
