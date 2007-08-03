/*******************************************************************************
 * Copyright (c) 2006 Woodcraft Mill & Cabinet Corporation.  All rights
 * reserved.  This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ******************************************************************************/
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

  private static boolean debug = false;

  /**
   * <b>EXPERIMENTAL</b>: Sets whether debug mode is enabled.  This mode may be used for troubleshooting
   * documents that cannot be laid out for some reason (e.g. a RuntimeException "cannot layout page
   * x" is thrown).
   *
   * <p><b>THIS API IS EXPERIMENTAL AND MAY BE REMOVED OR CHANGED IN THE FUTURE.</b>
   * @param debug true to enable debug mode, false to disable it.
   */
  public static void setDebug(boolean debug) {
  	PaperClips.debug = debug;
  }

  /**
   * <b>EXPERIMENTAL</b>: Returns whether debug mode is enabled.
   *
   * <p><b>THIS API IS EXPERIMENTAL AND MAY BE REMOVED OR CHANGED IN THE FUTURE.</b>
   * @return whether debug mode is enabled.
   */
  public static boolean getDebug() {
  	return debug;
  }

  /**
   * Calls iterator.next(width, height) and returns the result, or throws a RuntimeException if
   * the returned PrintPiece is larger than the width or height given.
   * <p>
   * This method is intended to be used by PrintIterator classes, as a results-checking alternative
   * to calling next(int, int) directly on the target iterator.  All PrintIterator classes in the
   * PaperClips library use this method instead of directly calling the
   * {@link PrintIterator#next(int, int)} method.
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
    } else if (debug) {
    	return new NullPrintPiece();
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
   * @throws RuntimeException if the document could not be printed for any reason.
   */
  public static void print(PrintJob printJob, Printer printer) {
  	// Bug in SWT on OSX: If Printer.startJob() is not called first, the GC will be disposed by default.
  	if (!printer.startJob(printJob.getName()))
  		throw new RuntimeException("Unable to start print job");

		try {
	  	GC gc = new GC(printer);
			try {
				print(printJob, printer, gc);
			} finally {
				gc.dispose();
			}
			printer.endJob();
  	} catch (RuntimeException e) {
  		printer.cancelJob();
  		throw e;
  	}
  }

  /**
   * Prints the print job to the specified printer using the GC.  This method does not manage the print job lifecycle
   * (it does not call startJob or endJob).
   * @param printJob the print job
   * @param printer the printer
   * @param gc the GC
   */
  private static void print(PrintJob printJob, Printer printer, final GC gc) {
    final PrinterData printerData = printer.getPrinterData();

    PrintPiece[] pages = getPages(printJob, printer, gc);

    // Determine the page range to print based on PrinterData.scope
    int startPage = 0;
    int endPage   = pages.length-1;
    if (printerData.scope == PrinterData.PAGE_RANGE) {
      // Convert from PrinterData's one-based indices to our zero-based indices
      startPage = Math.max(startPage, printerData.startPage-1);
      endPage   = Math.min(endPage,   printerData.endPage  -1);
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

    // Dispose the unused pages (those outside the page range) up front.
    for (int i = 0; i < startPage; i++)
      pages[i].dispose();
    for (int i = endPage+1; i < pages.length; i++)
      pages[i].dispose();

    Rectangle paperBounds = getPaperBounds(printer);
    final int x = paperBounds.x;
    final int y = paperBounds.y;

    try {
      for (int collated = 0; collated < collatedCopies; collated++) {
        for (int pageIndex = startPage; pageIndex <= endPage; pageIndex++) {
          for (int noncollated = 0; noncollated < noncollatedCopies; noncollated++) {
            if (printer.startPage()) {
              pages[pageIndex].paint(gc, x, y);
              pages[pageIndex].dispose();
              printer.endPage();
            } else {
            	throw new RuntimeException("Unable to start page "+pageIndex);
            }
          }
        }
      }
    } finally {
      for (int i = 0; i < pages.length; i++)
        pages[i].dispose();
    }
  }

  private static PrintJob applyOrientation(PrintJob printJob, Printer printer) {
  	int orientation = printJob.getOrientation();

  	Rectangle paperBounds = getPaperBounds(printer);
  	if (((orientation == ORIENTATION_LANDSCAPE) &&
  			 (paperBounds.width < paperBounds.height)) ||
  			((orientation == ORIENTATION_PORTRAIT) &&
  			 (paperBounds.height < paperBounds.width))) {
  		String name = printJob.getName();
  		Print document = new RotatePrint(printJob.getDocument());
  		Margins margins = printJob.getMargins().rotate();
  		printJob = new PrintJob(name, document).setMargins(margins).setOrientation(ORIENTATION_DEFAULT);
  	}

  	return printJob;
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
   * @return an array of all pages of the print job.  Each element of the returned array represents
   *         one page in the printed document.
   * @throws RuntimeException if the document could not be properly laid out for any reason.
   */
  public static PrintPiece[] getPages(PrintJob printJob, Printer printer) {
  	// Bug in SWT on OSX: If Printer.startJob() is not called first, the GC will be disposed by default.
  	if (!printer.startJob(""))
  		throw new RuntimeException("Unable to start print job");

  	try {
    	GC gc = new GC(printer);
    	try {
    		return getPages(printJob, printer, gc);
    	} finally {
    		gc.dispose();
    	}
  	} finally {
  		// 2007-04-30: Attempting to endJob() instead of cancelJob() to see if that gets around the
  		// OSX bug that renders a Printer useless after cancelJob().
			printer.endJob();
  	}
  }

  private static PrintPiece[] getPages(PrintJob printJob, Printer printer, GC gc) {
    // Rotate the document (and margins with it) depending on print job orientation.
  	printJob = applyOrientation(printJob, printer);
    Margins margins = printJob.getMargins();

    Rectangle marginBounds = getMarginBounds(margins, printer);
    Rectangle paperBounds = getPaperBounds(printer);

    PrintIterator iterator = printJob.getDocument().iterator(printer, gc);
    List pages = new ArrayList();
    while (iterator.hasNext()) {
      PrintPiece page = next(iterator, marginBounds.width, marginBounds.height);
      if (page == null) {
        for (Iterator it = pages.iterator(); it.hasNext(); )
          ((PrintPiece)it.next()).dispose();
        pages.clear();
        throw new RuntimeException("Unable to layout page "+(pages.size()+1));
      }
      pages.add(createPagePiece(page, marginBounds, paperBounds));
      if (debug && page instanceof NullPrintPiece)
      	break;
    }

    return (PrintPiece[]) pages.toArray(new PrintPiece[pages.size()]);
  }

  private static PrintPiece createPagePiece(PrintPiece page, Rectangle marginBounds, Rectangle paperBounds) {
  	return new CompositePiece(
  		new CompositeEntry[] {
				new CompositeEntry(
						page,
						new Point(
								marginBounds.x-paperBounds.x,
								marginBounds.y-paperBounds.y ))
			},
  		new Point(paperBounds.width, paperBounds.height)
		);
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

  private static final class NullPrintPiece implements PrintPiece {
		public Point getSize() { return new Point(0,0); }

		public void paint(GC gc, int x, int y) {}

		public void dispose() {}
	}
}