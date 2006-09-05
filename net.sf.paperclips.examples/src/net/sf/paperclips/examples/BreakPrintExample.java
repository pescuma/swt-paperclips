/*
 * Created on Mar 21, 2006
 * Author: Matthew
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips.examples;

import net.sf.paperclips.BorderPrint;
import net.sf.paperclips.BreakPrint;
import net.sf.paperclips.ColumnPrint;
import net.sf.paperclips.DefaultGridLook;
import net.sf.paperclips.FactoryPrint;
import net.sf.paperclips.GridPrint;
import net.sf.paperclips.LineBorder;
import net.sf.paperclips.Print;
import net.sf.paperclips.PrintUtil;
import net.sf.paperclips.TextPrint;

/**
 * Prints "The quick brown fox jumps over the lazy dog." in increasingly
 * large blocks, using a BreakPrint every 5 blocks to force printing to
 * advance to the next column / page.
 *
 * @author Matthew
 */
public class BreakPrintExample extends FactoryPrint {
  protected Print createPrint () {
    GridPrint grid = new GridPrint("d:g", new DefaultGridLook(10, 10));

    String text = "The quick brown fox jumps over the lazy dog.";
    String printText = text;

    LineBorder border = new LineBorder();
    for (int i = 0; i < 15; i++, printText += "  " + text) {
      if (i > 0 && i % 5 == 0)
        grid.add(new BreakPrint());

      grid.add(new BorderPrint(new TextPrint(printText), border));
    }

    return new ColumnPrint(grid, 2, 10);
  }

  /**
   * Prints the BreakPrintExample to the default printer.
   * @param args command-line args
   */
  public static void main(String[] args) {
    PrintUtil.print("BreakPrintExample.java", new BreakPrintExample());
  }
}
