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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import net.sf.paperclips.BackgroundColorPrint;
import net.sf.paperclips.GridPrint;
import net.sf.paperclips.LineBorder;
import net.sf.paperclips.Print;
import net.sf.paperclips.PrintIterator;
import net.sf.paperclips.PrintUtil;
import net.sf.paperclips.TextPrint;
import net.sf.paperclips.swt.PrintViewer;

/**
 * Demonstrate use of BackgroundColorPrint.
 * @author Matthew
 */
public class Snippet2 implements Print {
  private Print createPrint () {
    GridPrint grid = new GridPrint("d, d, d, d");
    grid.setCellBorder(new LineBorder());

    // Light gray background on header
    for (int i = 0; i < 4; i++)
      grid.add(
          new BackgroundColorPrint(
              new TextPrint("Column "+i),
              new RGB (200, 200, 200)));

    // Even rows light yellow, odd rows light blue
    RGB evenRows = new RGB(255, 255, 200);
    RGB oddRows = new RGB(200, 200, 255);
    for (int r = 0; r < 20; r++)
      for (int c = 0; c < 4; c++)
        grid.add(
            new BackgroundColorPrint(
                new TextPrint ("Row "+r+" Col "+c),
                (r % 2 == 0) ? evenRows : oddRows));

    // Give entire grid a light green background.
    return new BackgroundColorPrint(grid, new RGB(200, 255, 200));
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
    shell.setText("Snippet2.java");
    shell.setBounds (100, 100, 640, 480);
    shell.setLayout (new GridLayout());

    Button button = new Button (shell, SWT.PUSH);
    button.setLayoutData (new GridData (SWT.FILL, SWT.DEFAULT, true, false));
    button.setText ("Print");

    PrintViewer viewer = new PrintViewer(shell, SWT.BORDER);
    viewer.getControl ().setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
    final Print print = new Snippet2();
    viewer.setPrint (print);

    button.addListener(SWT.Selection, new Listener() {
      public void handleEvent (Event event) {
        PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
        PrinterData printerData = dialog.open ();
        if (printerData != null) {
          Printer printer = new Printer(printerData);
          PrintUtil.printTo ("Snippet2.java", printer, print, 72); // 72 = 72 points = 1" margin
          printer.dispose();
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