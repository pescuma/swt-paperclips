/************************************************************************************************************
 * Copyright (c) 2006 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips.examples;

import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;

import net.sf.paperclips.*;

/**
 * Prints "The quick brown fox jumps over the lazy dog." in increasingly large blocks, using a NoBreakPrint
 * to prevent each block from being broken up across page or across columns.
 * 
 * @author Matthew
 */
public class NoBreakPrintExample {
  public static Print createPrint() {
    DefaultGridLook look = new DefaultGridLook( 10, 10 );
    look.setCellBorder( new LineBorder() );
    GridPrint grid = new GridPrint( "d:g", look );

    String text = "The quick brown fox jumps over the lazy dog.";
    String printText = text;

    for ( int i = 0; i < 20; i++, printText += "  " + text ) {
      // the text
      Print print = new TextPrint( printText );

      // Wrap the text in a NoBreakPrint so it stays together on the page.
      print = new NoBreakPrint( print );
      // Comment the above line and run the program again to see the difference.

      grid.add( print );
    }

    return new ColumnPrint( grid, 2, 10 );
  }

  /**
   * Prints the NoBreakPrintExample to the default printer.
   * 
   * @param args command-line args
   */
  public static void main( String[] args ) {
    // Workaround for SWT bug on GTK - force SWT to initialize so we don't crash.
    Display.getDefault();

    PaperClips.print( new PrintJob( "NoBreakPrintExample.java", createPrint() ), new PrinterData() );
  }
}
