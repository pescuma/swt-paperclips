/*
 * Created on Apr 19, 2006
 * Author: Matthew Hall
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import net.sf.paperclips.DefaultGridLook;
import net.sf.paperclips.GridPrint;
import net.sf.paperclips.Print;
import net.sf.paperclips.PrintIterator;
import net.sf.paperclips.PrintUtil;
import net.sf.paperclips.TextPrint;
import net.sf.paperclips.ui.PrintPreview;

/**
 * Demonstrate use of PrintPreview control.
 * @author Matthew
 */
public class Snippet7 implements Print {
  private Print createPrint () {
    DefaultGridLook look = new DefaultGridLook();
    look.setCellSpacing(5, 2);
    GridPrint grid = new GridPrint("p:g, d:g", look);

    String text = "The quick brown fox jumps over the lazy dog.";
    for (int i = 0; i < 10; i++)
      grid.add(new TextPrint(text));

    return grid;
  }

  public PrintIterator iterator (Device device, GC gc) {
    return createPrint().iterator(device, gc);
  }

  /**
   * Executes the snippet.
   * @param args command-line args.
   */
  public static void main(String[] args) {
    Display display = Display.getDefault ();
    final Shell shell = new Shell (display);
    shell.setText("Snippet7.java");
    shell.setBounds (100, 100, 640, 480);
    shell.setLayout (new GridLayout(6, false));

    Button button = new Button (shell, SWT.PUSH);
    button.setLayoutData (new GridData (SWT.DEFAULT, SWT.DEFAULT, false, false));
    button.setText ("H. Fit");
    button.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        
      }
    });
    
    button = new Button (shell, SWT.PUSH);
    button.setLayoutData (new GridData (SWT.DEFAULT, SWT.DEFAULT, false, false));
    button.setText ("V. Fit");

    button = new Button (shell, SWT.PUSH);
    button.setLayoutData (new GridData (SWT.DEFAULT, SWT.DEFAULT, false, false));
    button.setText ("Best Fit");

    button = new Button (shell, SWT.PUSH);
    button.setLayoutData (new GridData (SWT.DEFAULT, SWT.DEFAULT, false, false));
    button.setText ("<< Page");

    button = new Button (shell, SWT.PUSH);
    button.setLayoutData (new GridData (SWT.DEFAULT, SWT.DEFAULT, false, false));
    button.setText ("Page >>");

    button = new Button (shell, SWT.PUSH);
    button.setLayoutData (new GridData (SWT.DEFAULT, SWT.DEFAULT, false, false));
    button.setText ("Print");

    PrintPreview preview = new PrintPreview(shell, SWT.BORDER);
    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
    data.horizontalSpan = 6;
    preview.setLayoutData(data);
    final Print print = new Snippet7();
    preview.setPrint (print);

    button.addListener(SWT.Selection, new Listener() {
      public void handleEvent (Event event) {
        PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
        PrinterData printerData = dialog.open ();
        if (printerData != null) {
          PrintUtil.printTo ("Snippet7.java", printerData, print, 72);
        }
      }
    });

    shell.setVisible (true);

    while (!shell.isDisposed ())
      if (!display.readAndDispatch ())
        display.sleep();

    display.dispose ();
  }
}
