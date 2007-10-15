/************************************************************************************************************
 * Copyright (c) 2006 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.*;

import net.sf.paperclips.*;
import net.sf.paperclips.ui.PrintViewer;

/**
 * Demonstrate use of BackgroundColorPrint.
 * 
 * @author Matthew
 */
public class Snippet2 implements Print {
  private Print createPrint() {
    DefaultGridLook look = new DefaultGridLook();
    look.setCellBorder( new LineBorder() );
    GridPrint grid = new GridPrint( "d, d, d, d", look );

    // Light gray background on header
    for ( int i = 0; i < 4; i++ )
      grid.add( new BackgroundPrint( new TextPrint( "Column " + i ), new RGB( 200, 200, 200 ) ) );

    // Even rows light yellow, odd rows light blue
    RGB evenRows = new RGB( 255, 255, 200 );
    RGB oddRows = new RGB( 200, 200, 255 );
    for ( int r = 0; r < 20; r++ )
      for ( int c = 0; c < 4; c++ )
        grid.add( new BackgroundPrint( new TextPrint( "Row " + r + " Col " + c ), ( r % 2 == 0 )
            ? evenRows
            : oddRows ) );

    // Give entire grid a light green background.
    return new BackgroundPrint( grid, new RGB( 200, 255, 200 ) );
  }

  public PrintIterator iterator( Device device, GC gc ) {
    return createPrint().iterator( device, gc );
  }

  /**
   * Executes the snippet.
   * 
   * @param args command-line args.
   */
  public static void main( String[] args ) {
    Display display = Display.getDefault();
    final Shell shell = new Shell( display );
    shell.setText( "Snippet2.java" );
    shell.setBounds( 100, 100, 640, 480 );
    shell.setLayout( new GridLayout() );

    Button button = new Button( shell, SWT.PUSH );
    button.setLayoutData( new GridData( SWT.FILL, SWT.DEFAULT, true, false ) );
    button.setText( "Print" );

    PrintViewer viewer = new PrintViewer( shell, SWT.BORDER );
    viewer.getControl().setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    final Print print = new Snippet2();
    viewer.setPrint( print );

    button.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        PrintDialog dialog = new PrintDialog( shell, SWT.NONE );
        PrinterData printerData = dialog.open();
        if ( printerData != null )
          PaperClips.print( new PrintJob( "Snippet2.java", print ).setMargins( 72 ), printerData );
      }
    } );

    shell.setVisible( true );

    while ( !shell.isDisposed() )
      if ( !display.readAndDispatch() )
        display.sleep();

    display.dispose();
  }
}
