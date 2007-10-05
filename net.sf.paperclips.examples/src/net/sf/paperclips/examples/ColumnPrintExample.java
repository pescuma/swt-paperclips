/************************************************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.sf.paperclips.*;
import net.sf.paperclips.ui.PrintViewer;

/**
 * Example for the ColumnPrint class.
 * 
 * @author Matthew
 */
public class ColumnPrintExample implements Print {
  /**
   * Executes the ColumnPrint example.
   * 
   * @param args the command line arguments.
   */
  public static void main( String[] args ) {
    final Display display = new Display();

    Shell shell = new Shell( display, SWT.SHELL_TRIM );
    shell.setLayout( new FillLayout() );
    shell.setSize( 600, 600 );

    final PrintViewer preview = new PrintViewer( shell, SWT.BORDER );
    preview.setPrint( new ColumnPrintExample() );

    shell.open();

    while ( !shell.isDisposed() )
      if ( !display.readAndDispatch() )
        display.sleep();

    PaperClips.print( new PrintJob( "ColumnPrintExample.java", new ColumnPrintExample() ), new PrinterData() );
  }

  protected Print createPrint() {
    StringBuffer buf = new StringBuffer( 11000 );
    for ( int i = 1; i <= 500; i++ ) {
      buf.append( "This is sentence #" ).append( i ).append( ".  " );
      if ( i % 20 == 0 )
        buf.append( "\n\n" );
    }

    return new ColumnPrint( new BorderPrint( new TextPrint( buf.toString() ), new LineBorder() ), 3, 18 );
  }

  public PrintIterator iterator( Device device, GC gc ) {
    return createPrint().iterator( device, gc );
  }
}
