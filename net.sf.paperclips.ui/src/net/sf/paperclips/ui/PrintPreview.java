package net.sf.paperclips.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import net.sf.paperclips.PaperClips;
import net.sf.paperclips.PrintJob;
import net.sf.paperclips.PrintPiece;

/**
 * A WYSIWYG (what you see is what you get) print preview panel.  This control displays a preview
 * of what a PrintJob will look like on paper, depending on the selected printer.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * @author Matthew Hall
 */
public class PrintPreview extends Canvas {
  /**
   * Constructs a PrintPreview control.
   * @param parent the parent control.
   * @param style the control style.
   */
  public PrintPreview(Composite parent, int style) {
    super(parent, style | SWT.DOUBLE_BUFFERED);

    addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent e) {
        paint(e);
      }
    });

    addListener(SWT.Resize, new Listener() {
      public void handleEvent(Event event) {
        paperDisplayBounds = null;
        redraw();
      }
    });

    addListener(SWT.Dispose, new Listener() {
      public void handleEvent(Event event) {
        disposeResources();
      }
    });
  }

  PrintJob    printJob      = null;
  PrinterData printerData   = new PrinterData();
  int         pageIndex     = 0;
  boolean     fitHorizontal = true;
  boolean     fitVertical   = true;
  float       scale         = 1.0f;

  Printer printer   = null;
  Point   paperSize = null; // The bounds of the paper on the printer device.

  PrintPiece[] pages              = null;
  Rectangle    paperDisplayBounds = null; // Where the paper is drawn within this control.

  /**
   * Returns the print job.
   * @return the print job.
   */
  public PrintJob getPrintJob() {
    return printJob;
  }

  /**
   * Sets the print job to preview.
   * @param printJob the print job to preview.
   */
  public void setPrintJob(PrintJob printJob) {
    this.printJob = printJob;
    this.pageIndex = 0;
    disposePages();
    paperSize = null; // in case the orientation changed
    paperDisplayBounds = null;
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
    this.pageIndex = 0;
    disposePrinter(); // disposes pages too
    redraw();
  }

  /**
   * Returns the page index.
   * @return the page index.
   */
  public int getPageIndex() {
    return pageIndex;
  }

  /**
   * Sets the page index.
   * @param pageIndex the new page index.
   */
  public void setPageIndex(int pageIndex) {
    this.pageIndex = pageIndex;
    redraw();
  }

  /**
   * Returns the number of pages.  This method may return 0 if the selected page has not yet been
   * displayed.
   * @return the number of pages.
   */
  public int getPageCount() {
    return pages == null ? 0 : pages.length;
  }

  /**
   * Returns whether the page scales to fit the document horizontally.
   * @return whether the page scales to fit the document horizontally.
   */
  public boolean isFitHorizontal() {
    return fitHorizontal;
  }

  /**
   * Sets whether the page scales to fit the document horizontally.
   * @param fitHorizontal whether the page scales to fit the document horizontally.
   */
  public void setFitHorizontal(boolean fitHorizontal) {
    if (this.fitHorizontal != fitHorizontal) {
      this.fitHorizontal = fitHorizontal;
      paperDisplayBounds = null;
      redraw();
    }
  }

  /**
   * Returns whether the page scales to fit the document vertically.
   * @return whether the page scales to fit the document vertically.
   */
  public boolean isFitVertical() {
    return fitVertical;
  }

  /**
   * Sets whether the page scales to fit the document vertically.
   * @param fitVertical whether the page scales to fit the document vertically.
   */
  public void setFitVertical(boolean fitVertical) {
    if (this.fitVertical != fitVertical) {
      this.fitVertical = fitVertical;
      paperDisplayBounds = null;
      redraw();
    }
  }

  /**
   * Returns the view scale.  The document displays at this scale when !(isFitHorizontal() ||
   * isFitVertical()). 
   * @return the view scale.  
   */
  public float getScale() {
    return scale;
  }

  /**
   * Sets the view scale.
   * @param scale the view scale.  A scale of 1.0 causes the document to appear at full size on the
   *        computer screen.
   */
  public void setScale(float scale) {
    if (scale > 0) {
      this.scale = scale;
      paperDisplayBounds = null;
      redraw();
    } else
      throw new IllegalArgumentException("Scale must be > 0");
  }

  void paint(PaintEvent event) {
    Image printerImage = null;
    GC printerGC = null;
    Transform printerTransform = null;

    Image displayImage = null;

    try {
      drawBackground(event);

      if (printJob == null || printerData == null)
        return;

      getPrinter();
      getPaperSize();
      getPages();
      getPaperDisplayBounds();

      if (printer == null ||
          paperSize == null ||
          pages == null ||
          paperDisplayBounds == null ||
          pageIndex < 0 ||
          pageIndex >= pages.length)
        return;

      drawPaper(event);

      Rectangle dirtyBounds = new Rectangle(event.x, event.y, event.width, event.height);

      // The portion of the dirty bounds which is displaying "paper"
      Rectangle dirtyPaperBounds = dirtyBounds.intersection(paperDisplayBounds);

      // Dirty region has no "paper"
      if (dirtyPaperBounds.width == 0 || dirtyPaperBounds.height == 0)
        return;

      printerImage = new Image(printer, dirtyPaperBounds.width, dirtyPaperBounds.height);
      printerGC = new GC(printerImage);
      printerTransform = new Transform(printer);

      printerGC.getTransform(printerTransform);
      printerTransform.translate(paperDisplayBounds.x-dirtyPaperBounds.x,
                                 paperDisplayBounds.y-dirtyPaperBounds.y);
      printerTransform.scale(
          (float) paperDisplayBounds.width  / (float) paperSize.x,
          (float) paperDisplayBounds.height / (float) paperSize.y);
      printerGC.setTransform(printerTransform);
      pages[pageIndex].paint(printerGC, 0, 0);
 
      displayImage = new Image(event.display, printerImage.getImageData());
      event.gc.drawImage(displayImage, dirtyPaperBounds.x, dirtyPaperBounds.y);
    } finally {
      if (printerImage != null)
        printerImage.dispose();
      if (displayImage != null)
        displayImage.dispose();
      if (printerGC != null)
        printerGC.dispose();
      if (printerTransform != null)
        printerTransform.dispose();
    }
  }

  private static final int PAPER_MARGIN = 10;
  private static final int PAPER_BORDER_WIDTH = 1;
  private static final int PAPER_SHADOW_WIDTH = 3;

  private final int BOILERPLATE_SIZE =
    2*PAPER_MARGIN + 2*PAPER_BORDER_WIDTH + PAPER_SHADOW_WIDTH;

  private Printer getPrinter() {
    if (printer == null && printerData != null) {
      printer = new Printer(printerData);
      disposePages();
      paperDisplayBounds = null;
    }
    return printer;
  }

  private boolean orientationRequiresRotate() {
    int orientation = printJob.getOrientation();
    Rectangle bounds = PaperClips.getPaperBounds(printer);
    return
        (orientation == PaperClips.ORIENTATION_PORTRAIT  && bounds.width > bounds.height) ||
        (orientation == PaperClips.ORIENTATION_LANDSCAPE && bounds.height > bounds.width);
  }

  private Point getPaperSize() {
    Printer printer = getPrinter();
    if (paperSize == null && printer != null && printJob != null) {
      Rectangle paperBounds = PaperClips.getPaperBounds(printer);
      this.paperSize = orientationRequiresRotate() ?
          new Point(paperBounds.height, paperBounds.width) :
          new Point(paperBounds.width, paperBounds.height);
    }
    return paperSize;
  }

  private PrintPiece[] getPages() {
    if (pages == null && printJob != null) {
      pages = PaperClips.getPages(printJob, getPrinter());
      if (orientationRequiresRotate())
        for (int i = 0; i < pages.length; i++)
          pages[i] = new RotateClockwisePrintPiece(printer, pages[i]);
    }
    return pages;
  }

  private void drawBackground(PaintEvent event) {
    Color oldBackground = event.gc.getBackground();
    Color bg = event.display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
    try {
      event.gc.setBackground(bg);
      event.gc.fillRectangle(event.x, event.y, event.width, event.height);
      event.gc.setBackground(oldBackground);
    } finally {
      bg.dispose();
    }
  }

  private void drawPaper(PaintEvent event) {
    if (paperDisplayBounds == null) return;

    Color black  = event.display.getSystemColor(SWT.COLOR_BLACK);
    Color shadow = event.display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW);

    // Drop shadow
    event.gc.setBackground(shadow);
    event.gc.fillRectangle(paperDisplayBounds.x+PAPER_SHADOW_WIDTH,
                     paperDisplayBounds.y+PAPER_SHADOW_WIDTH,
                     paperDisplayBounds.width+PAPER_BORDER_WIDTH,
                     paperDisplayBounds.height+PAPER_BORDER_WIDTH);
    shadow.dispose();

    // Page border
    event.gc.setForeground(black);
    for (int i = 1; i <= PAPER_BORDER_WIDTH; i++)
      event.gc.drawRectangle(paperDisplayBounds.x-i,
                             paperDisplayBounds.y-i,
                             paperDisplayBounds.width+2*i-1,
                             paperDisplayBounds.height+2*i-1);
  }

  /**
   * Calculates the absolute scale that the print preview is displaying at.  If either of the
   * fitHorizontal or fitVertical properties are true, this is the scale allows the page to fit
   * within this control's current bounds.  Otherwise the value of the scale property is returned. 
   * @param display the display device.
   * @param printer the printer device.
   * @return the absolute scale that the print preview is displaying at.
   */
  public float getAbsoluteScale() {
    return getAbsoluteScale(getSize());
  }

  private float getAbsoluteScale(Point controlSize) {
    if (fitHorizontal || fitVertical) {
      Point displayDPI = getDisplay().getDPI();
      Point printerDPI = getPrinter().getDPI();
      Point paperSize = getPaperSize();
      controlSize.x -= BOILERPLATE_SIZE;
      controlSize.y -= BOILERPLATE_SIZE;

      if (fitHorizontal) {
        float screenWidth = (float) controlSize.x / (float) displayDPI.x; // inches
        float paperWidth  = (float) paperSize.x   / (float) printerDPI.x; // inches
        float scaleX = screenWidth / paperWidth;
        if (fitVertical) {
          float screenHeight = (float) controlSize.y / (float) displayDPI.y; // inches
          float paperHeight  = (float) paperSize.y   / (float) printerDPI.y; // inches
          float scaleY = screenHeight / paperHeight;
          return Math.min(scaleX, scaleY);
        }
        return scaleX;
      }
      // fitVertical == true
      float screenHeight = (float) controlSize.y / (float) displayDPI.y; // inches
      float paperHeight  = (float) paperSize.y   / (float) printerDPI.y; // inches
      float scaleY = screenHeight / paperHeight;
      return scaleY;
    }

    // No 
    return scale;
  }

  /**
   * Returns the bounding rectangle where the paper is drawn on the control.  The paper border and
   * drop shadow are drawn immediately outside this rectangle.
   * @return the bounding rectangle where the paper is drawn on the control.
   */
  private Rectangle getPaperDisplayBounds() {
    if (paperDisplayBounds == null) {
      Point displayDPI = getDisplay().getDPI();
      Point printerDPI = printer.getDPI();
      float absoluteScale = getAbsoluteScale(getSize());
      float scaleX = absoluteScale * displayDPI.x / printerDPI.x;
      float scaleY = absoluteScale * displayDPI.y / printerDPI.y;

      int x = PAPER_MARGIN + PAPER_BORDER_WIDTH;
      int y = PAPER_MARGIN + PAPER_BORDER_WIDTH;
      int width  = (int) Math.ceil(scaleX * paperSize.x);
      int height = (int) Math.ceil(scaleY * paperSize.y);

      // Center the paper horizontally if needed 
      Point size = getSize();
      size.x -= (width + BOILERPLATE_SIZE);
      if (size.x > 0)
        x += size.x/2;

      paperDisplayBounds = new Rectangle(x, y, width, height);
    }
    return paperDisplayBounds;
  }

  private void disposePages() {
    if (pages != null) {
      for (int i = 0; i < pages.length; i++)
        pages[i].dispose();
      pages = null;
      paperDisplayBounds = null;
    }
  }

  private void disposePrinter() {
    disposePages();
    if (printer != null) {
      printer.dispose();
      printer = null;
      paperSize = null;
    }
  }

  void disposeResources() {
    disposePages();
    disposePrinter();
  }

  public Point computeSize(int wHint, int hHint, boolean changed) {
    checkWidget();

    Point size = new Point(wHint, hHint);
    double scale;
    if (wHint != SWT.DEFAULT) {
      if (hHint != SWT.DEFAULT) {
        return size;
      }
      size.y = Integer.MAX_VALUE;
      scale = getAbsoluteScale(size);
    } else if (hHint != SWT.DEFAULT) {
      size.x = Integer.MAX_VALUE;
      scale = getAbsoluteScale(size); 
    } else {
      scale = this.scale;
    }

    return computeSize(scale);
  }

  /**
   * Returns the control size needed to display a full page at the given scale.
   * @param scale the absolute scale.  A scale of 1, for example, yields a "life size" preview.
   * @return the control size needed to display a full page at the given scale. 
   */
  public Point computeSize(double scale) {
    Point size = new Point(BOILERPLATE_SIZE, BOILERPLATE_SIZE);

    Point displayDPI = getDisplay().getDPI();
    Point printerDPI = getPrinter().getDPI();
    Point paperSize = getPaperSize();

    size.x += Math.round( scale * paperSize.x * displayDPI.x / printerDPI.x );
    size.y += Math.round( scale * paperSize.y * displayDPI.y / printerDPI.y );

    return size;
  }
}

class RotateClockwisePrintPiece implements PrintPiece {
  private final Printer printer;
  private final PrintPiece target;
  private final Point size;

  RotateClockwisePrintPiece(Printer printer, PrintPiece target) {
    if (printer == null || target == null)
      throw new NullPointerException();
    this.printer = printer;
    this.target = target;
    Point targetSize = target.getSize();
    this.size = new Point(targetSize.y, targetSize.x);
  }

  public void dispose() {
    target.dispose();
  }

  public Point getSize() {
    return new Point(size.x, size.y);
  }

  public void paint(GC gc, int x, int y) {
    Transform oldTransform = null;
    Transform newTransform = null;
    try {
      oldTransform = new Transform(printer);
      gc.getTransform(oldTransform);

      newTransform = new Transform(printer);
      gc.getTransform(newTransform);
      newTransform.translate(x, y);
      newTransform.translate(size.x, 0);
      newTransform.rotate(90);
      gc.setTransform(newTransform);

      target.paint(gc, 0, 0);

      gc.setTransform(oldTransform);
    } finally {
      if (oldTransform != null)
        oldTransform.dispose();
      if (newTransform != null)
        newTransform.dispose();
    }
  }
}