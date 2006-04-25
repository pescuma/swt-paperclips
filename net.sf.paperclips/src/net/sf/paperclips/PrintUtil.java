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
    Printer printer = new Printer ();
    try {
      printTo (jobName, printer, print, margins);
    } finally {
      printer.dispose ();
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
    if (printer.startJob (jobName)) {
      GC gc = null;
      Transform transform = null;
      List <PrintPiece> pages = new ArrayList <PrintPiece> ();

      try {
        gc = new GC (printer);
        /* This code appears to set things back to the right size, at least on
         * my WinXP machine printing on an HP LaserJet 1012.  Not tested on
         * other platforms or printers.  --Matthew
         * 
         * See also Eclipse Bug 96378:
         * https://bugs.eclipse.org/bugs/show_bug.cgi?id=96378 
         * 
         * transform = new Transform(printer);
         * gc.getTransform(transform);
         * transform.scale(0.1668f, 0.1668f);
         * gc.setTransform(transform);
         */

        Rectangle bounds = computePrintArea (printer, margins);

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

        for (PrintPiece page : pages) {
          printer.startPage ();
          page.paint (gc, bounds.x, bounds.y);
          page.dispose();
          printer.endPage ();
        }

        printer.endJob ();
      } finally {
        if (gc != null)
          gc.dispose ();
        if (transform != null)
          transform.dispose();
        //for (PrintPiece page : pages)
          //page.dispose ();
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