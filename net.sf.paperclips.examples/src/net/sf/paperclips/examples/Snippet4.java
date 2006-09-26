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
import net.sf.paperclips.LineBorder;
import net.sf.paperclips.PaperClips;
import net.sf.paperclips.Print;
import net.sf.paperclips.PrintIterator;
import net.sf.paperclips.PrintJob;
import net.sf.paperclips.ScalePrint;
import net.sf.paperclips.TextPrint;
import net.sf.paperclips.ui.PrintViewer;

/**
 * Demonstrate use of ScalePrint.
 * @author Matthew
 */
public class Snippet4 implements Print {
  private Print createPrint () {
    // Using "preferred" size columns, to force the document to be wider than the page. In most
    // cases it is recommended to use "d" for "default" columns, which can shrink when needed.
    DefaultGridLook look = new DefaultGridLook();
    look.setCellBorder(new LineBorder());
    GridPrint grid = new GridPrint("p, p, p, p, p, p, p, p, p, p", look);

    for (int r = 0; r < 50; r++)
      for (int c = 0; c < 10; c++)
        grid.add(new TextPrint ("Row "+r+" Col "+c));

    return new ScalePrint(grid);
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
    shell.setText("Snippet4.java");
    shell.setBounds (100, 100, 640, 480);
    shell.setLayout (new GridLayout());

    Button button = new Button (shell, SWT.PUSH);
    button.setLayoutData (new GridData (SWT.FILL, SWT.DEFAULT, true, false));
    button.setText ("Print");

    PrintViewer viewer = new PrintViewer(shell, SWT.BORDER);
    viewer.getControl ().setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
    final Print print = new Snippet4();
    viewer.setPrint (print);

    button.addListener(SWT.Selection, new Listener() {
      public void handleEvent (Event event) {
        PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
        PrinterData printerData = dialog.open ();
        if (printerData != null)
          PaperClips.print(new PrintJob("Snippet4.java", print).setMargins(72),
                           printerData);
      }
    });

    shell.setVisible (true);

    while (!shell.isDisposed ())
      if (!display.readAndDispatch ())
        display.sleep();

    display.dispose ();
  }
}
