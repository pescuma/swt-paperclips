/*
 * Created on Feb 25, 2006
 * Author: Matthew
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips.examples;

import org.eclipse.swt.SWT;

import net.sf.paperclips.GridPrint;
import net.sf.paperclips.LinePrint;
import net.sf.paperclips.PrintUtil;
import net.sf.paperclips.TextPrint;

/**
 * First example in the PaperClips online tutorial.
 */
public class TutorialExample2 {

  /**
   * Prints the words, "My first PaperClips print job." 
   * @param args
   */
  public static void main (String[] args) {
    // Create a grid with the following columns:
    // Column 1: preferred width
    // Column 2: preferred width, grows to fill excess width
    // (The 5 is the grid spacing, in points.  72 points = 1".)

    GridPrint grid = new GridPrint("p, p:g", 5, 5);

    // Now populate the grid with the text and lines
    grid.add(new TextPrint("VITAL STATISTICS"), GridPrint.REMAINDER, SWT.CENTER);

    grid.add(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);

    grid.add(new TextPrint("Name:"));       grid.add(new TextPrint("Matthew Hall"));
    grid.add(new TextPrint("Occupation:")); grid.add(new TextPrint("Programmer"));
    grid.add(new TextPrint("Eyes:"));       grid.add(new TextPrint("Blue"));
    grid.add(new TextPrint("Gender:"));     grid.add(new TextPrint("Male"));
    grid.add(new TextPrint("Spouse:"));     grid.add(new TextPrint("Sexy"));

    grid.add(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);

    PrintUtil.print("TutorialExample2", grid);
  }
}
