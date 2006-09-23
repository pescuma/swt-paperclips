package net.sf.paperclips.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
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
    super(parent, style | SWT.DOUBLE_BUFFERED);

    addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent e) {
        paint(e);
      }
    });

    addListener(SWT.Resize, new Listener() {
      public void handleEvent(Event event) {
        if (fitVertical || fitHorizontal)
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

  Printer   printer            = null;
  Rectangle paperPrinterBounds = null; // The bounds of the paper on the printer device.

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
    disposePrinter(); // disposes pages
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
      redraw();
    }
    else
      throw new IllegalArgumentException("Scale must be > 0");
  }

  void paint(PaintEvent event) {
    Image printerImage = null;
    GC printerGC = null;
    Transform printerTransform = null;

    Image displayImage = null;

    try {
      if (printJob == null || printerData == null) {
        drawBackground(event, event.display, event.gc);
        return;
      }

      if (printer == null) {
        printer = new Printer(printerData);
        paperPrinterBounds = PaperClips.getPaperBounds(printer);
      }

      if (pages == null)
        pages = PaperClips.getPages(printJob, printer);

      if (paperDisplayBounds == null)
        paperDisplayBounds = getPaperDisplayBounds();

      if (pageIndex < 0 || pageIndex >= pages.length) {
        drawBackground(event, event.display, event.gc);
        return;
      }

      printerImage = new Image(printer, event.width, event.height);
      printerGC = new GC(printerImage);

      printerTransform = new Transform(printer);
      printerGC.getTransform(printerTransform);
      printerTransform.translate(-event.x, -event.y);
      printerGC.setTransform(printerTransform);

      drawBackground(event, printer, printerGC);
      drawPaper(printer, printerGC);

      printerTransform.translate(paperDisplayBounds.x, paperDisplayBounds.y);
      float absoluteScale = getAbsoluteScale();
      Point displayDPI = event.display.getDPI();
      Point printerDPI = printer.getDPI();
      float scaleX = absoluteScale * displayDPI.x / printerDPI.x;
      float scaleY = absoluteScale * displayDPI.y / printerDPI.y;
      printerTransform.scale(scaleX, scaleY);
      printerGC.setTransform(printerTransform);
  
      pages[pageIndex].paint(printerGC, -paperPrinterBounds.x, -paperPrinterBounds.y);
  
      displayImage = new Image(event.display, printerImage.getImageData());
      event.gc.drawImage(displayImage, event.x, event.y);
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

  RGB background;

  private void drawBackground(PaintEvent event, Device device, GC gc) {
    Color oldBackground = gc.getBackground();

    if (background == null)
      background = event.display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW).getRGB();

    Color bg = new Color(device, background);

    gc.setBackground(bg);
    gc.fillRectangle(event.x, event.y, event.width, event.height);
    gc.setBackground(oldBackground);
    bg.dispose();
  }

  private void drawPaper(Device device, GC gc) {
    if (paperDisplayBounds == null) return;

    Color white = device.getSystemColor(SWT.COLOR_WHITE);
    Color black = device.getSystemColor(SWT.COLOR_BLACK);

    // Drop shadow
    gc.setBackground(black);
    gc.fillRectangle(paperDisplayBounds.x+PAPER_SHADOW_WIDTH,
                     paperDisplayBounds.y+PAPER_SHADOW_WIDTH,
                     paperDisplayBounds.width+PAPER_BORDER_WIDTH,
                     paperDisplayBounds.height+PAPER_BORDER_WIDTH);

    // White page
    gc.setBackground(white);
    gc.fillRectangle(paperDisplayBounds);

    // Page border
    gc.setForeground(black);
    for (int i = 1; i <= PAPER_BORDER_WIDTH; i++)
      gc.drawRectangle(paperDisplayBounds.x-i,
                       paperDisplayBounds.y-i,
                       paperDisplayBounds.width+2*i-1,
                       paperDisplayBounds.height+2*i-1);

  }

  /**
   * Calculates the scale that the page should be displayed at on-screen.  This value is an 
   * absolute scale based on physical measurements, and is independent of the relative DPI of the
   * display and printer devices.  This means that the GC transform must be scaled by the result,
   * as well as the display DPI to printer DPI ratio.
   * <p>
   * It is an error to call this method if the Printer field is null.
   * @param display the display device.
   * @param printer the printer device.
   * @return the absolute scale that the page should be displayed, based on properties. 
   */
  private float getAbsoluteScale() {
    if (fitHorizontal || fitVertical) {
      Point displayDPI = getDisplay().getDPI();
      Point printerDPI = printer.getDPI();
      Point screenSize = getSize();
      Rectangle paperSize = PaperClips.getPaperBounds(printer);
      screenSize.x -= BOILERPLATE_SIZE;
      screenSize.y -= BOILERPLATE_SIZE;

      if (fitHorizontal) {
        float screenWidth = (float) screenSize.x / (float) displayDPI.x; // inches
        float paperWidth = (float) paperSize.width / (float) printerDPI.x; // inches
        float scaleX = screenWidth / paperWidth;
        if (fitVertical) {
          float screenHeight = (float) screenSize.y / (float) displayDPI.y; // inches
          float paperHeight = (float) paperSize.height / (float) printerDPI.y; // inches
          float scaleY = screenHeight / paperHeight;
          return Math.min(scaleX, scaleY);
        }
        return scaleX;
      }
      // fitVertical == true
      float screenHeight = (float) screenSize.y / (float) displayDPI.y; // inches
      float paperHeight = (float) paperSize.height / (float) printerDPI.y; // inches
      float scaleY = screenHeight / paperHeight;
      return scaleY;
    }
    return scale;
  }

  /**
   * Returns the bounding rectangle where the paper is drawn on the control.  The paper border and
   * drop shadow are drawn immediately outside this rectangle.
   * @return the bounding rectangle where the paper is drawn on the control.
   */
  private Rectangle getPaperDisplayBounds() {
    Point displayDPI = getDisplay().getDPI();
    Point printerDPI = printer.getDPI();
    float absoluteScale = getAbsoluteScale();
    float scaleX = absoluteScale * displayDPI.x / printerDPI.x;
    float scaleY = absoluteScale * displayDPI.y / printerDPI.y;

    int x = PAPER_MARGIN + PAPER_BORDER_WIDTH;
    int y = PAPER_MARGIN + PAPER_BORDER_WIDTH;
    int width  = (int) Math.ceil(scaleX * paperPrinterBounds.width);
    int height = (int) Math.ceil(scaleY * paperPrinterBounds.height);

    // Center the paper horizontally if needed 
    Point size = getSize();
    size.x -= (width  + BOILERPLATE_SIZE);
    if (size.x > 0)
      x += size.x/2;

    return new Rectangle(x, y, width, height);
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
      paperPrinterBounds = null;
    }
  }

  void disposeResources() {
    disposePages();
    disposePrinter();
  }

  // TODO Implement computeSize(int, int, boolean)
}
