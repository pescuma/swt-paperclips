package net.sf.paperclips;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;

/**
 * This class contains static constants and methods for preparing and printing documents.  Methods
 * in this class supersede those in PrintUtil.
 * @author Matthew Hall
 */
public class PaperClips {
  private PaperClips() {} // no instances

  /**
   * Indicates that the printer's default page orientation should be used.
   */
  public static final int ORIENTATION_DEFAULT = SWT.DEFAULT;

  /**
   * Indicates portrait page orientation.
   */
  public static final int ORIENTATION_PORTRAIT = SWT.VERTICAL;

  /**
   * Indicates landscape page orientation.
   */
  public static final int ORIENTATION_LANDSCAPE = SWT.HORIZONTAL;

  /**
   * Calls iterator.next(width, height) and returns the result, or throws a RuntimeException if
   * the returned PrintPiece is larger than the width or height given.
   * <p>
   * This method is intended to be used by PrintIterator classes, as a results-checking alternative
   * to calling next(int, int) directly on the target iterator.  All PrintIterator classes in the
   * PaperClips library use this method.
   * @param iterator the PrintIterator
   * @param width the available width.
   * @param height the available height.
   * @return the next portion of the Print, or null if the width and height are not enough to
   *         display any of the iterator's contents.
   * @throws RuntimeException if the iterator returns a PrintPiece that is larger than the width
   *         or height given.
   */
  public static PrintPiece next(PrintIterator iterator, int width, int height) {
    PrintPiece result = iterator.next(width, height);
    if (result != null) {
      Point size = result.getSize();
      if (size.x > width || size.y > height)
        throw new RuntimeException(
            iterator+" produced a "+size.x+"x"+size.y+" piece for a "+width+"x"+height+" area.");
    }
    return result;
  }

  /**
   * Prints the print job to the given printer.  This method constructs a Printer, forwards to
   * {@link #print(PrintJob, Printer) }, and disposes the printer before returning.
   * @param printJob the print job.
   * @param printerData the PrinterData of the selected printer.
   */
  public static void print(PrintJob printJob, PrinterData printerData) {
    Printer printer = new Printer(printerData);
    try {
      print(printJob, printer);
    } finally {
      printer.dispose();
    }
  }

  /**
   * Prints the print job to the given printer.
   * @param printJob the print job.
   * @param printer the printer device.
   */
  public static void print(PrintJob printJob, Printer printer) {
    final PrinterData printerData = printer.getPrinterData();

    PrintPiece[] pages = getPages(printJob, printer);

    // Determine the page range to print based on PrinterData.scope
    final int startPage;
    final int endPage;
    if (printerData.scope == PrinterData.PAGE_RANGE) {
      // Convert from PrinterData's one-based page indices to zero-based page indices 
      startPage = Math.max(printerData.startPage-1, 0);
      endPage   = Math.min(printerData.endPage  -1, pages.length-1);
    } else {
      startPage = 0;
      endPage   = pages.length-1;
    }

    // Determine the number of copies and collation.
    final int collatedCopies;
    final int noncollatedCopies;
    if (printerData.collate) { // always false if printer driver performs collation
      collatedCopies = printerData.copyCount; // always 1 if printer driver handles copy count
      noncollatedCopies = 1;
    } else {
      noncollatedCopies = printerData.copyCount; // always 1 if printer driver handles copy count
      collatedCopies = 1;
    }

    for (int i = 0; i < startPage; i++)
      pages[i].dispose();
    for (int i = endPage+1; i < pages.length; i++)
      pages[i].dispose();

    GC gc = new GC(printer);

    Rectangle paperBounds = getPaperBounds(printer);
    final int x = paperBounds.x;
    final int y = paperBounds.y;

    try {
      if (printer.startJob(printJob.getName())) {
        for (int collated = 0; collated < collatedCopies; collated++)
          for (int i = startPage; i <= endPage; i++)
            for (int noncollated = 0; noncollated < noncollatedCopies; noncollated++)
              if (printer.startPage()) {
                pages[i].paint(gc, x, y);
                pages[i].dispose();
                printer.endPage();
              } else {
                printer.cancelJob();
                break;
              }
        printer.endJob();
      }
    } catch (RuntimeException re) {
      printer.cancelJob();
      throw re;
    } finally {
      gc.dispose();
      for (int i = 0; i < pages.length; i++)
        pages[i].dispose();
    }
  }

  /**
   * Processes the print job and returns an array of pages for the given printer device.  Each
   * element in the returned array has already had the page orientation and page margins
   * applied.  Therefore, when calling the paint(GC, int, int) method on each page, the printer's
   * trim should be provided as the x and y arguments.  In other words, the trim is taken as a
   * minimum margin while applying calculating margins, but the position where the page's content
   * is drawn is determined solely by the margin, and is not offset by the trim.  This behavior is
   * helpful for screen display, and is already compensated for in the
   * {@link #print(PrintJob, Printer) } method.
   * @param printer the printing device.
   * @param printJob the print job.
   * @return an array of all pages of the print job.  Each element of the 
   */
  public static PrintPiece[] getPages(PrintJob printJob, Printer printer) {
    int orientation = printJob.getOrientation();
    Margins margins = printJob.getMargins();
    Print document = printJob.getDocument();

    // Rotate the document (and margins with it) depending on print job orientation.
    Rectangle paperBounds = getPaperBounds(printer);
    switch (orientation) {
      case ORIENTATION_LANDSCAPE:
        if (paperBounds.width < paperBounds.height) {
          margins = margins.rotate();
          document = new RotatePrint(document);
        }
        break;
      case ORIENTATION_PORTRAIT:
        if (paperBounds.height < paperBounds.width) {
          margins = margins.rotate();
          document = new RotatePrint(document);
        }
        break;
    }
    final Rectangle marginBounds = getMarginBounds(margins, printer);

    GC gc = new GC(printer);
    List pages = new ArrayList();
    PrintIterator iter = document.iterator(printer, gc);

    try {
      while (iter.hasNext()) {
        PrintPiece page = next(iter, marginBounds.width, marginBounds.height);
        if (page == null) {
          for (Iterator it = pages.iterator(); iter.hasNext(); )
            ((PrintPiece)it.next()).dispose();
          pages.clear();
          throw new RuntimeException("Unable to layout pages");
        }
        pages.add(new PagePiece(paperBounds, marginBounds, page));
      }
    } finally {
      gc.dispose();
    }

    return (PrintPiece[]) pages.toArray(new PrintPiece[pages.size()]);
  }

  /**
   * Returns the bounding rectangle of the paper, including non-printable margins.
   * @param printer the printer device.
   * @return a rectangle whose edges correspond to the edges of the paper. 
   */
  public static Rectangle getPaperBounds(Printer printer) {
    Rectangle rect = printer.getClientArea();
    return printer.computeTrim(rect.x, rect.y, rect.width, rect.height);
  }

  /**
   * Returns the bounding rectangle of the printable area on the paper.
   * @param printer the printer device.
   * @return the bounding rectangle of the printable area on the paper.
   */
  public static Rectangle getPrintableBounds(Printer printer) {
    return printer.getClientArea();
  }

  /**
   * Returns the bounding rectangle of the printable area which is inside the given margins on the
   * paper.  The printer's minimum margins are reflected in the returned rectangle.
   * @param printer the printer device.
   * @param margins the desired page margins.
   * @return the bounding rectangle on the printable area which is within the margins.
   */
  public static Rectangle getMarginBounds(Margins margins, Printer printer) {
    Rectangle paperBounds = getPaperBounds(printer);

    // Calculate the pixel coordinates for the margins
    Point dpi = printer.getDPI();
    int top = paperBounds.y + (margins.top * dpi.y / 72);
    int left = paperBounds.x + (margins.left * dpi.x / 72);
    int right = paperBounds.x + paperBounds.width - (margins.right * dpi.x / 72);
    int bottom = paperBounds.y + paperBounds.height - (margins.bottom * dpi.y / 72);

    // Enforce the printer's minimum margins.
    Rectangle printableBounds = getPrintableBounds(printer);
    if (top < printableBounds.y)
      top = printableBounds.y;
    if (left < printableBounds.x)
      left = printableBounds.x;
    if (right > printableBounds.x + printableBounds.width)
      right = printableBounds.x + printableBounds.width;
    if (bottom > printableBounds.y + printableBounds.height)
      bottom = printableBounds.y + printableBounds.height;

    return new Rectangle(left, top, right-left, bottom-top);
  }
}

class PagePiece implements PrintPiece {
  private final Point size;
  private final Point offset;
  private final PrintPiece target;

  PagePiece(Rectangle paperBounds, Rectangle marginBounds, PrintPiece target) {
    if (paperBounds == null || marginBounds == null || target == null)
      throw new NullPointerException();
    this.size = new Point(paperBounds.width, paperBounds.height);
    this.offset = new Point(marginBounds.x-paperBounds.x, marginBounds.y-paperBounds.y);
    this.target = target;
  }

  public void dispose() {
    target.dispose();
  }

  public Point getSize() {
    return new Point(size.x, size.y);
  }

  public void paint(GC gc, int x, int y) {
    target.paint(gc, x+offset.x, x+offset.y);
  }
}