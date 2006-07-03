/*******************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation and others. All
 * rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Woodcraft Mill &
 * Cabinet Corporation - initial API and implementation
 ******************************************************************************/
package net.sf.paperclips;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;

/**
 * A static class for printing Print objects.
 * @author Matthew
 */
public class PrintUtil {
  private PrintUtil () {}

  /**
   * Prints the argument to the default printer with 1" margins. The Print's
   * toString() result will be used as the print job name.
   * @param print the item to print.
   * @deprecated use {@link PrintUtil#print(String, Print) } instead.
   */
  @Deprecated
  public static void print (Print print) {
    print (print.toString (), print, 72);
  }

  /**
   * Prints the argument to the default printer. The Print's toString() result
   * will be used as the print job name.
   * @param print the item to print.
   * @param margins the page margins, in points.
   * @deprecated use {@link PrintUtil#print(String, Print, int) } instead.
   */
  @Deprecated
  public static void print (Print print, int margins) {
    print (print.toString (), print, margins);
  }

  /**
   * Prints the argument to the given printer with 1" margins. The Print's
   * toString() result will be used as the print job name.
   * @param printer the device to print on.
   * @param print the item to print.
   * @deprecated Use {@link PrintUtil#printTo(String, Printer, Print) } instead.
   */
  @Deprecated
  public static void printTo (Printer printer, Print print) {
    printTo (printer, print, 72);
  }

  /**
   * Prints the argument to the given printer. The Print's toString() result
   * will be used as the print job name.
   * @param printer the device to print on.
   * @param print the item to print.
   * @param margins the page margins, in points.
   * @deprecated Use {@link PrintUtil#printTo(String, Printer, Print, int) }
   *             instead.
   */
  @Deprecated
  public static void printTo (Printer printer, Print print, int margins) {
    printTo (print.toString (), printer, print, margins);
  }

  /**
   * Prints the argument to the default printer with 1" margins.
   * @param jobName the print job name.
   * @param print the item to print.
   */
  public static void print (String jobName, Print print) {
    print (jobName, print, 72);
  }

  /**
   * Prints the argument to the default Printer.
   * @param jobName the print job name.
   * @param print the item to print.
   * @param margins the page margins, in points. 72 pts = 1".
   */
  public static void print (String jobName, Print print, int margins) {
    printTo (jobName, new PrinterData(), print, margins);
  }

  /**
   * Prints the argument to the given printer, with 1" margins.
   * @param jobName the print job name.
   * @param printerData the printer to print to.
   * @param print the item to print.
   */
  public static void printTo(String jobName, PrinterData printerData, Print print) {
    printTo (jobName, printerData, print, 72);
  }

  /**
   * Prints the argument to the given printer.
   * @param jobName the print job name.
   * @param printerData PrinterData of the printer to print to.
   * @param print the item to print.
   * @param margins the page margins, in points.  72 pts = 1".
   */
  public static void printTo(String jobName, PrinterData printerData, Print print, int margins) {
    Printer printer = new Printer(printerData);
    try {
      printTo(jobName, printer, print, margins);
    } finally {
      printer.dispose();
    }
  }

  /**
   * Prints the argument to the given printer with 1" margins.
   * @param jobName the print job name.
   * @param printer the device to print on.
   * @param print the item to print.
   */
  public static void printTo (String jobName, Printer printer, Print print) {
    printTo (jobName, printer, print, 72);
  }

  /**
   * Print the argument to the given Printer.
   * @param jobName the print job name.
   * @param printer the device to print on.
   * @param print the item to print.
   * @param margins the page margins, in points. 72 pts = 1".
   */
  public static void printTo (String jobName,
                              Printer printer,
                              Print print,
                              int margins) {
    final PrinterData printerData = printer.getPrinterData();

    if (printer.startJob (jobName)) {
      GC gc = null;
      Transform transform = null;
      List <PrintPiece> pages = new ArrayList <PrintPiece> ();

      try {
        gc = new GC (printer);

        Rectangle bounds = computePrintArea (printer, margins);
        gc.setClipping (bounds);

        PrintIterator iterator = print.iterator (printer, gc);

        // Iterate through all pages. Must complete the iteration before
        // sending any pages to the printer, so that PageNumberPrints have
        // the correct total page count.
        while (iterator.hasNext ()) {
          PrintPiece page = iterator.next (bounds.width, bounds.height);
          if (page == null) {
            printer.cancelJob ();
            throw new RuntimeException ("Print is too large to fit on paper.");
          }
          pages.add (page);
        }

        // Determine the page range to print based on PrinterData.scope
        final int startPage;
        final int endPage;
        if (printerData.scope == PrinterData.PAGE_RANGE) {
          // Convert from PrinterData's one-based page indices to zero-based page indices 
          startPage = printerData.startPage-1;
          endPage   = printerData.endPage  -1;
        } else {
          startPage = 0;
          endPage   = pages.size()-1;
        }

        // Dispose pages outside the selected page range.
        for (int i = 0; i < startPage; i++)
          pages.get(i).dispose();
        for (int i = endPage+1; i < pages.size(); i++)
          pages.get(i).dispose();

        for (int i = startPage; i <= endPage; i++) {
          PrintPiece page = pages.get(i);

          printer.startPage ();
          page.paint (gc, bounds.x, bounds.y);
          page.dispose(); // reclaim system resources to keep system resource usage lean.
          printer.endPage ();
        }
        pages.clear();

        printer.endJob ();
      } finally {
        for (PrintPiece page : pages)
          page.dispose();
        if (gc != null)
          gc.dispose ();
        if (transform != null)
          transform.dispose();
      }
    }
  }

  private static Rectangle computePrintArea (Printer printer, int margins) {
    // Printer's DPI
    Point dpi = printer.getDPI ();

    // Convert margins from points to pixels
    int marginX = dpi.x * margins / 72;
    int marginY = dpi.y * margins / 72;

    // Printable area
    Rectangle rect = printer.getClientArea ();

    // Compute trim
    Rectangle trim = printer.computeTrim (0, 0, 0, 0);

    // Calculate printable area, with 1" margins
    int left = trim.x + marginX;
    if (left < rect.x) left = rect.x;

    int right = (rect.width + trim.x + trim.width) - marginX;
    if (right > rect.width) right = rect.width;

    int top = trim.y + marginY;
    if (top < rect.y) top = rect.y;

    int bottom = (rect.height + trim.y + trim.height) - marginY;
    if (bottom > rect.height) bottom = rect.height;

    return new Rectangle (left, top, right - left, bottom - top);
  }
}