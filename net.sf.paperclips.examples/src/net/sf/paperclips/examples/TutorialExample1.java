/*
 * Created on Feb 25, 2006
 * Author: Matthew
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.sf.paperclips.PaperClips;
import net.sf.paperclips.PrintJob;
import net.sf.paperclips.TextPrint;

/**
 * First example in the PaperClips online tutorial.
 */
public class TutorialExample1 {
  /**
   * Prints the words, "Hello PaperClips!" 
   * @param args command-line arguments.
   */
  public static void main (String[] args) {
    // Create the document
    TextPrint text = new TextPrint("Hello PaperClips!");

    // Show the print dialog
    Display display = new Display();
    Shell shell = new Shell(display);
    PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
    PrinterData printerData = dialog.open ();
    shell.dispose();
    display.dispose();

    // Print the document to the printer the user selected.
    if (printerData != null) {
      PrintJob job = new PrintJob("TutorialExample1.java", text);
      job.setMargins(72);
      PaperClips.print(job, printerData);
    }
  }
}
