/*
 * Created on Feb 25, 2006
 * Author: Matthew
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.sf.paperclips.DefaultGridLook;
import net.sf.paperclips.GridPrint;
import net.sf.paperclips.LinePrint;
import net.sf.paperclips.PaperClips;
import net.sf.paperclips.Print;
import net.sf.paperclips.PrintIterator;
import net.sf.paperclips.PrintJob;
import net.sf.paperclips.TextPrint;

/**
 * First example in the PaperClips online tutorial.
 */
public class TutorialExample2 implements Print {
  private static Print createPrint() {
    // Create a grid with the following columns:
    // Column 1: preferred width
    // Column 2: preferred width, grows to fill excess width
    // (The 5 is the grid spacing, in points.  72 points = 1".)

    GridPrint grid = new GridPrint("p, d:g", new DefaultGridLook(5, 5));

    // Now populate the grid with the text and lines
    grid.add(SWT.CENTER, new TextPrint("VITAL STATISTICS"), GridPrint.REMAINDER);

    grid.add(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);

    grid.add(new TextPrint("Name:"));       grid.add(new TextPrint("Matthew Hall"));
    grid.add(new TextPrint("Occupation:")); grid.add(new TextPrint("Programmer"));
    grid.add(new TextPrint("Eyes:"));       grid.add(new TextPrint("Blue"));
    grid.add(new TextPrint("Gender:"));     grid.add(new TextPrint("Male"));
    grid.add(new TextPrint("Spouse:"));     grid.add(new TextPrint("Sexy"));

    grid.add(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);

    return grid;
  }

  public PrintIterator iterator(Device device, GC gc) {
    return createPrint().iterator(device, gc);
  }

  /**
   * Prints a table of vital (haha) statistics about Matthew Hall.
   * @param args command-line parameters
   */
  public static void main (String[] args) {
    // Show the print dialog
    Display display = new Display();
    Shell shell = new Shell(display);
    PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
    PrinterData printerData = dialog.open ();
    shell.dispose();
    display.dispose();

    // Print the document to the printer the user selected.
    if (printerData != null) {
      PrintJob job = new PrintJob("TutorialExample2.java", createPrint());
      job.setMargins(72);
      PaperClips.print(job, printerData);
    }
  }
}
