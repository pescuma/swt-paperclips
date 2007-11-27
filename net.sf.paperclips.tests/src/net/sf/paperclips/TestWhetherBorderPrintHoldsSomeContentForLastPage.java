/************************************************************************************************************
 * Copyright (c) 2007 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;

/**
 * Prints "The quick brown fox jumps over the lazy dog." in increasingly large blocks, using a BreakPrint
 * every 5 blocks to force printing to advance to the next column / page.
 * 
 * @author Matthew
 */
public class TestWhetherBorderPrintHoldsSomeContentForLastPage implements Print {
  protected Print createPrint() {
    GridPrint grid = new GridPrint( "d:g", new DefaultGridLook( 10, 10 ) );

    String text = "The quick brown fox jumps over the lazy dog.";
    String printText = text;

    LineBorder border = new LineBorder();
    for ( int i = 0; i < 100; i++, printText += "  " + text ) {
      grid.add( new BorderPrint( new TextPrint( printText ), border ) );
    }

    return new ColumnPrint( grid, 2, 10 );
  }

  public PrintIterator iterator( Device device, GC gc ) {
    return createPrint().iterator( device, gc );
  }

  /**
   * Prints the BreakPrintExample to the default printer.
   * 
   * @param args command-line args
   */
  public static void main( String[] args ) {
    // Workaround for SWT bug on GTK - force SWT to initialize so we don't crash.
    Display.getDefault();

    PaperClips.print( new PrintJob( "BreakPrintExample.java",
                                    new TestWhetherBorderPrintHoldsSomeContentForLastPage() ), new PrinterData() );
  }
}
