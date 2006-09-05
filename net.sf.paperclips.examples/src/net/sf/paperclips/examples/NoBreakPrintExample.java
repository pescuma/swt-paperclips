/*
 * Created on Mar 21, 2006
 * Author: Matthew
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips.examples;

import net.sf.paperclips.ColumnPrint;
import net.sf.paperclips.DefaultGridLook;
import net.sf.paperclips.FactoryPrint;
import net.sf.paperclips.GridPrint;
import net.sf.paperclips.LineBorder;
import net.sf.paperclips.NoBreakPrint;
import net.sf.paperclips.Print;
import net.sf.paperclips.PrintUtil;
import net.sf.paperclips.TextPrint;

/**
 * Prints "The quick brown fox jumps over the lazy dog." in increasingly large blocks, using a
 * NoBreakPrint to prevent each block from being broken up across page or across columns.
 *
 * @author Matthew
 */
public class NoBreakPrintExample extends FactoryPrint {
  protected Print createPrint () {
    DefaultGridLook look = new DefaultGridLook(10, 10);
    look.setCellBorder(new LineBorder());
    GridPrint grid = new GridPrint("d:g", look);

    String text = "The quick brown fox jumps over the lazy dog.";
    String printText = text;

    for (int i = 0; i < 20; i++, printText += "  " + text) {
      // the text
      Print print = new TextPrint(printText);

      // Wrap the text in a NoBreakPrint so it stays together on the page.
      print = new NoBreakPrint(print);
      // Comment the above line and run the program again to see the difference.

      grid.add(print);
    }

    return new ColumnPrint(grid, 2, 10);
  }

  /**
   * Prints the NoBreakPrintExample to the default printer.
   * @param args command-line args
   */
  public static void main(String[] args) {
    PrintUtil.print("NoBreakPrintExample.java", new NoBreakPrintExample());
  }
}
