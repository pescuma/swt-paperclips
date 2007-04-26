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
    		invalidatePageDisplayBounds();
    		redraw();
      }
   });

    addListener(SWT.Dispose, new Listener() {
      public void handleEvent(Event event) {
        disposeResources();
      }
    });
  }

  private PrintJob    printJob            = null;
  private PrinterData printerData         = Printer.getDefaultPrinterData();
  private int         pageIndex           = 0;
  private boolean     fitHorizontal       = true;
  private boolean     fitVertical         = true;
  private float       scale               = 1.0f;
  private int         horizontalPageCount = 1;
  private int         verticalPageCount   = 1;

  private Printer printer   = null;
  private Point   paperSize = null; // The bounds of the paper on the printer device.

  private PrintPiece[] pages                = null;
  private Point        pageDisplaySize      = null;
  private Point[]      pageDisplayLocations = null;

  /**
   * Returns the print job.
   * @return the print job.
   */
  public PrintJob getPrintJob() {
  	checkWidget();
    return printJob;
  }

  /**
   * Sets the print job to preview.
   * @param printJob the print job to preview.
   */
  public void setPrintJob(PrintJob printJob) {
  	checkWidget();
    this.printJob = printJob;
    this.pageIndex = 0;
    disposePrinter(); // disposes pages too
    invalidatePageDisplayBounds();
		redraw();
  }

  /**
   * Returns the PrinterData for the printer to preview on.
   * @return the PrinterData for the printer to preview on.
   */
  public PrinterData getPrinterData() {
  	checkWidget();
    return printerData;
  }

  /**
   * Sets the PrinterData for the printer to preview on.
   * @param printerData the PrinterData for the printer to preview on.
   */
  public void setPrinterData(PrinterData printerData) {
  	checkWidget();
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
  	checkWidget();
    return pageIndex;
  }

  /**
   * Sets the page index.
   * @param pageIndex the new page index.
   */
  public void setPageIndex(int pageIndex) {
  	checkWidget();
    this.pageIndex = pageIndex;
    redraw();
  }

  /**
   * Returns the number of pages.  This method returns 0 when {@link #getPrintJob()} is null or
   * {@link #getPrinterData()} is null.
   * @return the number of pages.
   */
  public int getPageCount() {
  	checkWidget();
  	getPages();
    return pages == null ? 0 : pages.length;
  }

  /**
   * Returns whether the page scales to fit the document horizontally.
   * @return whether the page scales to fit the document horizontally.
   */
  public boolean isFitHorizontal() {
  	checkWidget();
    return fitHorizontal;
  }

  /**
   * Sets whether the page scales to fit the document horizontally.
   * @param fitHorizontal whether the page scales to fit the document horizontally.
   */
  public void setFitHorizontal(boolean fitHorizontal) {
  	checkWidget();
    if (this.fitHorizontal != fitHorizontal) {
      this.fitHorizontal = fitHorizontal;
      invalidatePageDisplayBounds();
      redraw();
    }
  }

  /**
   * Returns whether the page scales to fit the document vertically.
   * @return whether the page scales to fit the document vertically.
   */
  public boolean isFitVertical() {
  	checkWidget();
    return fitVertical;
  }

  /**
   * Sets whether the page scales to fit the document vertically.
   * @param fitVertical whether the page scales to fit the document vertically.
   */
  public void setFitVertical(boolean fitVertical) {
  	checkWidget();
    if (this.fitVertical != fitVertical) {
      this.fitVertical = fitVertical;
      invalidatePageDisplayBounds();
      redraw();
    }
  }

  /**
   * Returns the view scale.  The document displays at this scale when !(isFitHorizontal() ||
   * isFitVertical()). 
   * @return the view scale.  
   */
  public float getScale() {
  	checkWidget();
    return scale;
  }

  /**
   * Sets the view scale.
   * @param scale the view scale.  A scale of 1.0 causes the document to appear at full size on the
   *        computer screen.
   */
  public void setScale(float scale) {
  	checkWidget();
    if (scale > 0) {
      this.scale = scale;
      if (!(fitVertical || fitHorizontal)) {
        invalidatePageDisplayBounds();
      	redraw();
      }
    } else
      throw new IllegalArgumentException("Scale must be > 0");
  }

  public int getHorizontalPageCount() {
  	return horizontalPageCount;
  }

  public void setHorizontalPageCount(int horizontalPages) {
  	if (horizontalPages < 1) horizontalPages = 1;
  	this.horizontalPageCount = horizontalPages;
  	invalidatePageDisplayBounds();
  	redraw();
  }

  public int getVerticalPageCount() {
  	return verticalPageCount;
  }

  public void setVerticalPageCount(int verticalPages) {
  	if (verticalPages < 1) verticalPages = 1;
  	this.verticalPageCount = verticalPages;
  	invalidatePageDisplayBounds();
  	redraw();
  }

	private void invalidatePageDisplayBounds() {
		pageDisplaySize = null;
		pageDisplayLocations = null;
	}

  private void paint(PaintEvent event) {
    drawBackground(event);

    if (printJob == null || printerData == null)
      return;

    getPrinter();
    getPaperSize();
    getPages();

    getPageDisplaySize();
    getPageDisplayLocations();

    if (printer              == null ||
        paperSize            == null ||
        pages                == null ||
        pageDisplaySize      == null ||
        pageDisplayLocations == null ||
        pageIndex < 0 || pageIndex >= pages.length)
      return;

    int count = Math.min(verticalPageCount * horizontalPageCount, pages.length - pageIndex);
    for (int i = 0; i < count; i++)
    	paintPage(event, pages[pageIndex+i], pageDisplayLocations[i]);
  }

  private void paintPage(PaintEvent event, PrintPiece page, Point location) {
    drawPageOutline(event, location);

    // Check whether any "paper" is in the dirty region
    Rectangle rectangle = new Rectangle(location.x, location.y, pageDisplaySize.x, pageDisplaySize.y);
  	Rectangle dirtyBounds = new Rectangle(event.x, event.y, event.width, event.height);
  	Rectangle dirtyPaperBounds = dirtyBounds.intersection(rectangle);
  	if (dirtyPaperBounds.width == 0 || dirtyPaperBounds.height == 0)
  		return;

  	Image printerImage = null;
    GC printerGC = null;
    Transform printerTransform = null;
    Image displayImage = null;

    try {
      printerImage = new Image(printer, dirtyPaperBounds.width, dirtyPaperBounds.height);
      printerGC = new GC(printerImage);
      printerTransform = new Transform(printer);

      printerGC.getTransform(printerTransform);
      printerTransform.translate(rectangle.x-dirtyPaperBounds.x,
                                 rectangle.y-dirtyPaperBounds.y);
      printerTransform.scale(
          (float) rectangle.width  / (float) paperSize.x,
          (float) rectangle.height / (float) paperSize.y);
      printerGC.setTransform(printerTransform);
      page.paint(printerGC, 0, 0);
 
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

  private Printer getPrinter() {
    if (printer == null && printerData != null) {
      printer = new Printer(printerData);
      disposePages(); // just in case
      pageDisplaySize = null;
      pageDisplayLocations = null;
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
    if (pages == null && printJob != null && printerData != null) {
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

  private static final int PAPER_MARGIN = 10;
  private static final int PAPER_BORDER_WIDTH = 1;
  private static final int PAPER_SHADOW_WIDTH = 3;
  private static final int PAPER_SPACING = 10;

  private void drawPageOutline(PaintEvent event, Point location) {
    if (location == null) return;

    Color black  = event.display.getSystemColor(SWT.COLOR_BLACK);
    Color shadow = event.display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW);

    // Drop shadow
    event.gc.setBackground(shadow);
    event.gc.fillRectangle(
    		location.x - PAPER_BORDER_WIDTH + PAPER_SHADOW_WIDTH,
    		location.y - PAPER_BORDER_WIDTH + PAPER_SHADOW_WIDTH,
    		pageDisplaySize.x  + PAPER_BORDER_WIDTH * 2,
    		pageDisplaySize.y + PAPER_BORDER_WIDTH * 2);

    // Page border
    event.gc.setForeground(black);
    for (int i = 1; i <= PAPER_BORDER_WIDTH; i++)
      event.gc.drawRectangle(
      		location.x-i,
      		location.y-i,
      		pageDisplaySize.x+2*i-1,
      		pageDisplaySize.y+2*i-1);
  }

  /**
   * Calculates the absolute scale that the print preview is displaying at.  If either of the
   * fitHorizontal or fitVertical properties are true, this is the scale allows the page to fit
   * within this control's current bounds.  Otherwise the value of the scale property is returned. 
   * @return the absolute scale that the print preview is displaying at.
   */
  public float getAbsoluteScale() {
  	checkWidget();
    return getAbsoluteScale(getSize());
  }

  private static final int PAPER_BOILERPLATE = PAPER_BORDER_WIDTH * 2 + PAPER_SHADOW_WIDTH;

  private Point getBoilerplateSize() {
  	return new Point(
  			2*PAPER_MARGIN + horizontalPageCount*PAPER_BOILERPLATE + (horizontalPageCount-1)*PAPER_SPACING,
  			2*PAPER_MARGIN + verticalPageCount  *PAPER_BOILERPLATE + (verticalPageCount  -1)*PAPER_SPACING);
  }

  private float getAbsoluteScale(Point controlSize) {
  	float result = scale;

    if (getPrinter() != null && (fitHorizontal || fitVertical)) {
      Point displayDPI = getDisplay().getDPI();
      Point printerDPI = getPrinter().getDPI();
      Point paperSize = getPaperSize();
      Point boilerplate = getBoilerplateSize();
      controlSize.x -= boilerplate.x;
      controlSize.x /= horizontalPageCount;
      controlSize.y -= boilerplate.y;
      controlSize.y /= verticalPageCount;

      if (fitHorizontal) {
        float screenWidth = (float) controlSize.x / (float) displayDPI.x; // inches
        float paperWidth  = (float) paperSize.x   / (float) printerDPI.x; // inches
        float scaleX = screenWidth / paperWidth;
        if (fitVertical) {
          float screenHeight = (float) controlSize.y / (float) displayDPI.y; // inches
          float paperHeight  = (float) paperSize.y   / (float) printerDPI.y; // inches
          float scaleY = screenHeight / paperHeight;
          result = Math.min(scaleX, scaleY);
        } else {
        	result = scaleX;
        }
      } else {
      	float screenHeight = (float) controlSize.y / (float) displayDPI.y; // inches
      	float paperHeight  = (float) paperSize.y   / (float) printerDPI.y; // inches
      	float scaleY = screenHeight / paperHeight;
      	result = scaleY;
      }
    }
    return result;
  }

  private Point getPageDisplaySize() {
  	if (pageDisplaySize == null) {
  		Point size = getSize();
  		Point displayDPI = getDisplay().getDPI();
  		Point printerDPI = printer.getDPI();
  		float absoluteScale = getAbsoluteScale(size);
  		float scaleX = absoluteScale * displayDPI.x / printerDPI.x;
  		float scaleY = absoluteScale * displayDPI.y / printerDPI.y;

  		pageDisplaySize = new Point(
  				(int) (scaleX * paperSize.x),
  				(int) (scaleY * paperSize.y));
  	}
  	return pageDisplaySize;
  }

  private Point[] getPageDisplayLocations() {
  	if (pageDisplayLocations == null) {
  		// Center pages horizontally
  		Point size = getSize();
  		int x0 = PAPER_MARGIN + PAPER_BORDER_WIDTH;
  		size.x -= getBoilerplateSize().x;
  		size.x -= (pageDisplaySize.x * horizontalPageCount);
  		if (size.x > 0)
  			x0 += size.x/2;

  		pageDisplayLocations = new Point[horizontalPageCount * verticalPageCount];

  		int y = PAPER_MARGIN + PAPER_BORDER_WIDTH;
  		for (int r = 0; r < verticalPageCount; r++) {
  			int x = x0;
  			for (int c = 0; c < horizontalPageCount; c++) {
  				pageDisplayLocations[r*horizontalPageCount+c] = new Point(x, y);
  				x += pageDisplaySize.x + PAPER_BOILERPLATE + PAPER_SPACING;
  			}
  			y += pageDisplaySize.y + PAPER_BOILERPLATE + PAPER_SPACING;
  		}
  	}
  	return pageDisplayLocations;
  }

  private void disposePages() {
    if (pages != null) {
      for (int i = 0; i < pages.length; i++)
        pages[i].dispose();
      pages = null;
      invalidatePageDisplayBounds();
    }
  }

  private void disposePrinter() {
    disposePages();
    if (printer != null) {
    	printer.cancelJob();
    	printer.endJob();
      printer.dispose();
      printer = null;
      paperSize = null;
    }
  }

  private void disposeResources() {
    disposePages();
    disposePrinter();
  }

  public Point computeSize(int wHint, int hHint, boolean changed) {
    checkWidget();

    Point size = new Point(wHint, hHint);

    if (getPrinter() == null) {
    	Point boilerplate = getBoilerplateSize();
    	if (wHint == SWT.DEFAULT) size.x = boilerplate.x;
    	if (hHint == SWT.DEFAULT) size.y = boilerplate.y;
    	return size;
    }

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
  	checkWidget();

    Point size = getBoilerplateSize();

    if (getPrinter() != null) {
    	Point displayDPI = getDisplay().getDPI();
    	Point printerDPI = getPrinter().getDPI();
    	Point paperSize = getPaperSize();

    	size.x += horizontalPageCount * (int) ( scale * paperSize.x * displayDPI.x / printerDPI.x );
    	size.y += verticalPageCount   * (int) ( scale * paperSize.y * displayDPI.y / printerDPI.y );
    }

    return size;
  }
}
