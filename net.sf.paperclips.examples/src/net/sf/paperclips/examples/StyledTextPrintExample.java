/************************************************************************************************************
 * Copyright (c) 2007 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
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
import net.sf.paperclips.TextStyle;
import net.sf.paperclips.ui.PrintPreview;

/**
 * Demonstrates use of the StyledTextPrint class.
 * @author Matthew
 */
public class StyledTextPrintExample implements Print {
  /**
   * Executes the StyledTextPrint example.
   * 
   * @param args the command line arguments.
   */
  public static void main( String[] args ) {
    final Display display = new Display();

    Shell shell = new Shell( display, SWT.SHELL_TRIM );
    shell.setText( "StyledTextPrintExample.java" );
    shell.setLayout( new GridLayout() );
    shell.setSize( 600, 800 );

    final PrintJob job = new PrintJob( "StyledTextPrintExample.java", new StyledTextPrintExample() );

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

  public PrintIterator iterator( Device device, GC gc ) {
    return createPrint().iterator( device, gc );
  }

  private Print createPrint() {
    StyledTextPrint doc = new StyledTextPrint();

    TextStyle normal = new TextStyle().font( "Arial", 14, SWT.NORMAL );
    TextStyle bold = normal.fontStyle( SWT.BOLD );
    TextStyle big = normal.fontHeight( 20 );
    TextStyle italic = normal.fontStyle( SWT.ITALIC );
    TextStyle monospace = normal.fontName( "Courier" );
    TextStyle underline = normal.underline();
    TextStyle strikeout = normal.strikeout();

    doc.setStyle( normal )
       .append( "This snippet demonstrates the use of " )
       .append( "StyledTextPrint", monospace )
       .append( " for creating bodies of styled text." )
       .newline()
       .newline()
       .append( "StyledTextPrint", monospace )
       .append( " makes sure that " )
       .append( "text ", bold )
       .append( "of ", italic )
       .append( "different ", normal.fontHeight( 20 ) )
       .append( "font ", normal.fontHeight( 42 ) )
       .append( "names,", normal.fontName( "Courier" ) )
       .append( " sizes, ", normal.fontHeight( 10 ) )
       .append( "and " )
       .append( "styles", normal.underline() )
       .append( " are aligned correctly along the base line." )
       .newline()
       .newline()
       .append( "With " )
       .append( "StyledTextPrint", monospace )
       .append( " you can embed any other printable element alongside the text.  " )
       .append( "For example, here is an image " )
       .append( createSampleImage() )
       .append( " and a horizontal line" )
       .append( new LinePrint( SWT.HORIZONTAL ) )
       .newline()
       .setStyle( italic )
       .append( "Note that some elements like GridPrint tend to be broken unnaturally across lines, and "
           + "therefore may not be suitable for use in a StyledTextPrint." )
       .setStyle( normal )
       .newline()
       .newline()
       .append( "Many text styles are possible such as " )
       .append( "bold print", bold )
       .append( ", " )
       .append( "italic print", italic )
       .append( ", " )
       .append( "strikeout text", strikeout )
       .append( ", " )
       .append( "underlined text", underline )
       .append( ", or " )
       .append( "any combination of the above",
                normal.fontStyle( SWT.BOLD | SWT.ITALIC ).strikeout().underline() )
       .append( "." )
       .newline()
       .newline()
       .append( "You can also set " )
       .append( "foreground colors", normal.foreground( 0x00A000 ) )
       .append( " or " )
       .append( "background colors", normal.background( 0xFFFFA0 ) )
       .append( " on the text through the TextStyle class." )
       .newline()
       .newline()
       .append( "Enjoy!", big );
    return doc;
  }

  private ImagePrint createSampleImage() {
    return new ImagePrint( new ImageData( getClass().getResourceAsStream( "sp.png" ) ), new Point( 600, 600 ) );
  }
}
