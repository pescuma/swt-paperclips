/************************************************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.*;

import net.sf.paperclips.*;
import net.sf.paperclips.ui.PrintPreview;

/**
 * Demonstrate use of PagePrint and friends PageDecoration, PageNumberPrint, and PageNumber.
 * 
 * @author Matthew
 */
public class PagePrintExample implements Print {
  /**
   * Executes the GridPrint example.
   * 
   * @param args the command line arguments.
   */
  public static void main( String[] args ) {
    final Display display = new Display();

    Shell shell = new Shell( display, SWT.SHELL_TRIM );
    shell.setText( "PagePrintExample.java" );
    shell.setLayout( new GridLayout() );
    shell.setSize( 600, 800 );

    final PrintJob job = new PrintJob( "PagePrintExample.java", new PagePrintExample() );

    Composite buttonPanel = new Composite( shell, SWT.NONE );
    buttonPanel.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    buttonPanel.setLayout( new RowLayout( SWT.HORIZONTAL ) );

    final PrintPreview preview = new PrintPreview( shell, SWT.BORDER );

    Button prev = new Button( buttonPanel, SWT.PUSH );
    prev.setText( "<< Prev" );
    prev.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        preview.setPageIndex( Math.max( preview.getPageIndex() - 1, 0 ) );
      }
    } );

    Button next = new Button( buttonPanel, SWT.PUSH );
    next.setText( "Next >>" );
    next.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        preview.setPageIndex( Math.min( preview.getPageIndex() + 1, preview.getPageCount() - 1 ) );
      }
    } );

    Button print = new Button( buttonPanel, SWT.PUSH );
    print.setText( "Print" );
    print.addListener( SWT.Selection, new Listener() {
      public void handleEvent( Event event ) {
        PaperClips.print( job, new PrinterData() );
      }
    } );

    preview.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    preview.setFitHorizontal( true );
    preview.setFitVertical( true );
    preview.setPrintJob( job );

    shell.open();

    while ( !shell.isDisposed() )
      if ( !display.readAndDispatch() )
        display.sleep();

    display.dispose();
  }

  protected Print createPrint() {
    PageDecoration header = createHeader();
    Print body = createBody();
    PageDecoration footer = createFooter();

    PagePrint page = new PagePrint( header, body, footer );
    page.setHeaderGap( 72 / 4 );
    page.setFooterGap( 72 / 8 );

    return page;
  }

  private PageDecoration createHeader() {
    PageDecoration header = new PageDecoration() {
      public Print createPrint( PageNumber pageNumber ) {
        // Only show a header on the first page
        if ( pageNumber.getPageNumber() == 0 ) {
          ImageData imageData = new ImageData( PagePrintExample.class.getResourceAsStream( "logo.png" ) );
          ImagePrint image = new ImagePrint( imageData );
          image.setDPI( 300, 300 );
          return image;
        }

        return null;
      }
    };
    return header;
  }

  private PageDecoration createFooter() {
    PageDecoration footer = new PageDecoration() {
      public Print createPrint( PageNumber pageNumber ) {
        GridPrint grid = new GridPrint( "d:g, r:d" );
        grid.add( new TextPrint( "Copyright 2006 ABC Corp, All Rights Reserved" ) );
        grid.add( new PageNumberPrint( pageNumber, SWT.RIGHT ) );
        return grid;
      }
    };
    return footer;
  }

  private Print createBody() {
    GridPrint grid = new GridPrint();

    final int ROWS = 200;
    final int COLS = 4;
    for ( int c = 0; c < COLS; c++ ) {
      grid.addColumn( "d:g" );
      grid.addHeader( new TextPrint( "Column " + c ) );
    }

    for ( int r = 0; r < ROWS; r++ )
      for ( int c = 0; c < COLS; c++ )
        grid.add( new TextPrint( "Cell (" + c + ", " + r + ")" ) );

    return grid;
  }

  public PrintIterator iterator( Device device, GC gc ) {
    return createPrint().iterator( device, gc );
  }
}
