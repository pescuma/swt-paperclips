package net.sf.paperclips.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import net.sf.paperclips.PaperClips;
import net.sf.paperclips.Print;
import net.sf.paperclips.PrintPiece;

/**
 * A WYSIWYG (what you see is what you get) print preview panel.
 * @author Matthew Hall
 */
public class PrintPreview extends Canvas {
  /**
   * Constructs a PrintPreview control.
   * @param parent the parent control.
   * @param style the control style.
   */
  public PrintPreview(Composite parent, int style) {
    super(parent, style);

    addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent e) {
        paint(e);
      }
    });

    addListener(SWT.Resize, new Listener() {
      public void handleEvent(Event event) {
        redraw();
      }
    });

    addListener(SWT.Dispose, new Listener() {
      public void handleEvent(Event event) {
        disposeResources();
      }
    });
  }

  private Print        print       = null;
  private int          scaleMode   = SWT.HORIZONTAL | SWT.VERTICAL;

  private double       scale       = 1;
  private PrintPiece[] pages       = null;
  private PrinterData  printerData = null;
  private int          orientation = PaperClips.PORTRAIT;
  private int          pageIndex   = -1;

  /**
   * Returns the scaling mode.
   * @return the scaling mode.
   */
  public int getScaleMode() {
    return scaleMode;
  }

  /**
   * Sets the scaling mode.
   * @param mode a bitwise or of the directions which will be scaled to fit
   */
  public void setScaleMode(int mode) {
    this.scaleMode = mode & (SWT.HORIZONTAL | SWT.VERTICAL);
  }

  /**
   * Returns the view scale.
   * @return the view scale.
   */
  public double getScale() {
    return scale;
  }

  /**
   * Sets the view scale.
   * @param scale the view scale.  A scale of 1.0 causes the document to appear at full size on the
   *        computer screen.
   */
  public void setScale(double scale) {
    if (scale > 0)
      this.scale = scale;
    else
      throw new IllegalArgumentException("Scale must be > 0");
  }

  /**
   * Returns the print being previewed.
   * @return the print being previewed.
   */
  public Print getPrint() {
    return print;
  }

  /**
   * Sets the print to be previewed.
   * @param print the print being previewed.
   */
  public void setPrint(Print print) {
    this.print = print;
    this.pageIndex = -1;
    disposePages();
    redraw();
  }

  /**
   * Returns the PrinterData for the printer to preview on.
   * @return the PrinterData for the printer to preview on.
   */
  public PrinterData getPrinterData() {
    return printerData;
  }

  /**
   * Sets the PrinterData for the printer to preview on.
   * @param printerData the PrinterData for the printer to preview on.
   */
  public void setPrinterData(PrinterData printerData) {
    this.printerData = printerData;
    this.pageIndex = -1;
    disposePages();
    redraw();
  }

  /**
   * Returns the page orientation.
   * @return the page orientation.
   */
  public int getOrientation() {
    return orientation;
  }

  /**
   * Sets the page orientation.
   * @param orientation the page orientation.  Must be one of {@link PaperClips#PORTRAIT} or
   *        {@link PaperClips#LANDSCAPE}.
   */
  public void setOrientation(int orientation) {
    this.orientation = orientation;
  }

  @Override
  public Point computeSize(int wHint, int hHint) {
    return super.computeSize(wHint, hHint);
  }

  void paint(PaintEvent event) {
    Image image = null;
    GC gc = null;
    try {
      image = new Image(event.display, event.width, event.height);

      gc = new GC(image);
  
      Color oldBackground = gc.getBackground();
      gc.setBackground(event.display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
      gc.fillRectangle(event.x, event.y, event.width, event.height);
      gc.setBackground(oldBackground);

      if (print != null && printerData != null) {
        
      }
  
      event.gc.drawImage(image, event.x, event.y);
    } finally {
      if (image != null)
        image.dispose();
      if (gc != null)
        gc.dispose();
    }
    image.dispose();
  }

  private void disposePages() {
    if (pages != null) {
      for (PrintPiece page : pages)
        page.dispose();
      pages = null;
    }
  }

  void disposeResources() {
    disposePages();
  }
}
