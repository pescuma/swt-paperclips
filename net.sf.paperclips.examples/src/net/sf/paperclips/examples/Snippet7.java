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
import net.sf.paperclips.PageNumberPageDecoration;
import net.sf.paperclips.PagePrint;
import net.sf.paperclips.PaperClips;
import net.sf.paperclips.Print;
import net.sf.paperclips.PrintIterator;
import net.sf.paperclips.PrintJob;
import net.sf.paperclips.SimplePageDecoration;
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
    for (int i = 0; i < 50; i++)
      grid.add(new TextPrint(text));

    PagePrint page = new PagePrint(grid);
    page.setHeader(new SimplePageDecoration(new TextPrint("Snippet7.java", SWT.CENTER)));
    page.setFooter(new PageNumberPageDecoration(SWT.CENTER));
    page.setHeaderGap(5);
    page.setFooterGap(5);

    return page;
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
    shell.setLayout (new GridLayout(8, false));

    final PrintJob printJob = new PrintJob("Snippet7.java", new Snippet7()).setMargins(108); //1.5"

    Button hFit = new Button (shell, SWT.PUSH);
    Button vFit = new Button (shell, SWT.PUSH);
    Button bFit = new Button (shell, SWT.PUSH);
    Button prev = new Button (shell, SWT.PUSH);
    Button next = new Button (shell, SWT.PUSH);
    Button port = new Button (shell, SWT.PUSH);
    Button land = new Button (shell, SWT.PUSH);
    Button print = new Button (shell, SWT.PUSH);
    final PrintPreview preview = new PrintPreview(shell, SWT.BORDER);

    hFit.setLayoutData (new GridData (SWT.DEFAULT, SWT.DEFAULT, false, false));
    hFit.setText ("H. Fit");
    hFit.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        preview.setFitHorizontal(true);
        preview.setFitVertical(false);
      }
    });
    
    vFit.setLayoutData (new GridData (SWT.DEFAULT, SWT.DEFAULT, false, false));
    vFit.setText ("V. Fit");
    vFit.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        preview.setFitVertical(true);
        preview.setFitHorizontal(false);
      }
    });

    bFit.setLayoutData (new GridData (SWT.DEFAULT, SWT.DEFAULT, false, false));
    bFit.setText ("Best Fit");
    bFit.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        preview.setFitVertical(true);
        preview.setFitHorizontal(true);
      }
    });

    prev.setLayoutData (new GridData (SWT.DEFAULT, SWT.DEFAULT, false, false));
    prev.setText ("<< Page");
    prev.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        preview.setPageIndex(Math.max(0, preview.getPageIndex()-1));
      }
    });

    next.setLayoutData (new GridData (SWT.DEFAULT, SWT.DEFAULT, false, false));
    next.setText ("Page >>");
    next.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        preview.setPageIndex(Math.min(preview.getPageIndex()+1, preview.getPageCount()-1));
      }
    });

    port.setLayoutData (new GridData (SWT.DEFAULT, SWT.DEFAULT, false, false));
    port.setText ("Portrait");
    port.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        printJob.setOrientation(PaperClips.ORIENTATION_PORTRAIT);
        preview.setPrintJob(printJob);
      }
    });

    land.setLayoutData (new GridData (SWT.DEFAULT, SWT.DEFAULT, false, false));
    land.setText ("Landscape");
    land.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        printJob.setOrientation(PaperClips.ORIENTATION_LANDSCAPE);
        preview.setPrintJob(printJob);
      }
    });

    print.setLayoutData (new GridData (SWT.DEFAULT, SWT.DEFAULT, false, false));
    print.setText ("Print");
    print.addListener(SWT.Selection, new Listener() {
      public void handleEvent (Event event) {
        PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
        PrinterData printerData = dialog.open ();
        if (printerData != null)
          PaperClips.print(printJob, printerData);
      }
    });

    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
    data.horizontalSpan = 8;
    preview.setLayoutData(data);
    preview.setPrintJob(printJob);

    shell.setVisible (true);

    while (!shell.isDisposed ())
      if (!display.readAndDispatch ())
        display.sleep();

    display.dispose ();
  }
}
