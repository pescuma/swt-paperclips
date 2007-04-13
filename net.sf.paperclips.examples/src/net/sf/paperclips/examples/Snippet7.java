/*
 * Created on Apr 19, 2006
 * Author: Matthew Hall
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
    final Display display = Display.getDefault ();
    final Shell shell = new Shell (display);
    shell.setText("Snippet7.java");
    shell.setBounds (100, 100, 640, 480);
    shell.setLayout (new GridLayout(1, false));

    final PrintJob printJob = new PrintJob("Snippet7.java", new Snippet7()).setMargins(108); //1.5"

    Composite buttonPanel = new Composite(shell, SWT.NONE);
    buttonPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    RowLayout layout = new RowLayout(SWT.HORIZONTAL);
    layout.pack = false;
    layout.fill = true;
    buttonPanel.setLayout(layout);

    Button prevPage  = new Button (buttonPanel, SWT.PUSH);
    Button nextPage  = new Button (buttonPanel, SWT.PUSH);
    Button fitHorz   = new Button (buttonPanel, SWT.PUSH);
    Button fitVert   = new Button (buttonPanel, SWT.PUSH);
    Button fitBest   = new Button (buttonPanel, SWT.PUSH);
    Button zoomIn    = new Button (buttonPanel, SWT.PUSH);
    Button zoomOut   = new Button (buttonPanel, SWT.PUSH);
    Button exactSize = new Button (buttonPanel, SWT.PUSH);
    Button portrait  = new Button (buttonPanel, SWT.PUSH);
    Button landscape = new Button (buttonPanel, SWT.PUSH);
    Button print     = new Button (buttonPanel, SWT.PUSH);

    final ScrolledComposite scroll = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    final PrintPreview preview = new PrintPreview(scroll, SWT.NONE);

    fitHorz.setText ("Fit Width");
    fitHorz.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        preview.setFitHorizontal(true);
        preview.setFitVertical(false);

        Rectangle bounds = scroll.getClientArea();
        scroll.setMinSize(preview.computeSize(bounds.width, SWT.DEFAULT));
      }
    });

    fitVert.setText ("Fit Height");
    fitVert.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        preview.setFitVertical(true);
        preview.setFitHorizontal(false);

        Rectangle bounds = scroll.getClientArea();
        scroll.setMinSize(preview.computeSize(SWT.DEFAULT, bounds.height));
      }
    });

    fitBest.setText ("Best Fit");
    fitBest.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        preview.setFitVertical(true);
        preview.setFitHorizontal(true);
        scroll.setMinSize(0, 0);
      }
    });

    exactSize.setText("Exact Size");
    exactSize.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        preview.setFitVertical(false);
        preview.setFitHorizontal(false);
        preview.setScale(1);
        scroll.setMinSize(preview.computeSize(1));
      }
    });

    zoomIn.setText("Zoom In");
    zoomIn.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        float scale = preview.getAbsoluteScale();
        scale *= 1.1f;

        preview.setScale(scale);
        preview.setFitVertical(false);
        preview.setFitHorizontal(false);

        scroll.setMinSize(preview.computeSize(scale));
      }
    });

    zoomOut.setText("Zoom Out");
    zoomOut.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        float scale = preview.getAbsoluteScale();
        scale /= 1.1f;

        preview.setScale(scale);
        preview.setFitVertical(false);
        preview.setFitHorizontal(false);

        scroll.setMinSize(preview.computeSize(scale));      }
    });

    prevPage.setText ("<< Page");
    prevPage.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        preview.setPageIndex(Math.max(0, preview.getPageIndex()-1));
      }
    });

    nextPage.setText ("Page >>");
    nextPage.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        preview.setPageIndex(Math.min(preview.getPageIndex()+1, preview.getPageCount()-1));
      }
    });

    portrait.setText ("Portrait");
    portrait.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        printJob.setOrientation(PaperClips.ORIENTATION_PORTRAIT);
        preview.setPrintJob(printJob);

        Rectangle bounds = scroll.getClientArea();
        if (preview.isFitHorizontal()) {
        	if (preview.isFitVertical()) { // best fit
        		scroll.setMinSize(0, 0);
        	} else { // fit to width
        		scroll.setMinSize(preview.computeSize(bounds.width, SWT.DEFAULT));
        	}
        } else {
        	if (preview.isFitVertical()) { // fit to height
        		scroll.setMinSize(preview.computeSize(SWT.DEFAULT, bounds.height));
        	} else { // custom scale
        		scroll.setMinSize(preview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        	}
        }
      }
    });

    landscape.setText ("Landscape");
    landscape.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        printJob.setOrientation(PaperClips.ORIENTATION_LANDSCAPE);
        preview.setPrintJob(printJob);

        Rectangle bounds = scroll.getClientArea();
        if (preview.isFitHorizontal()) {
        	if (preview.isFitVertical()) { // best fit
        		scroll.setMinSize(0, 0);
        	} else { // fit to width
        		scroll.setMinSize(preview.computeSize(bounds.width, SWT.DEFAULT));
        	}
        } else {
        	if (preview.isFitVertical()) { // fit to height
        		scroll.setMinSize(preview.computeSize(SWT.DEFAULT, bounds.height));
        	} else { // custom scale
        		scroll.setMinSize(preview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        	}
        }
      }
    });

    print.setText ("Print");
    print.addListener(SWT.Selection, new Listener() {
      public void handleEvent (Event event) {
        PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
        PrinterData printerData = dialog.open ();
        if (printerData != null)
          PaperClips.print(printJob, printerData);
      }
    });

    scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    scroll.setContent(preview);
    scroll.setExpandHorizontal(true);
    scroll.setExpandVertical(true);
    Listener resizeListener = new Listener() {
      public void handleEvent(Event event) {
        Rectangle bounds = scroll.getClientArea();

        scroll.getHorizontalBar().setPageIncrement(bounds.width * 2 / 3);
        scroll.getVerticalBar().setPageIncrement(bounds.height * 2 / 3);

        if (preview.isFitHorizontal()) {
          if (preview.isFitVertical()) { // Best fit
          	scroll.setMinSize(0, 0);
          } else { // Fit to width
          	scroll.setMinSize(preview.computeSize(bounds.width, SWT.DEFAULT));
          }
        } else if (preview.isFitVertical()) { // Fit to height
        	scroll.setMinSize(preview.computeSize(SWT.DEFAULT, bounds.height));;
        } else { // 
        	scroll.setMinSize(preview.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        }
      }
    };
    scroll.addListener(SWT.Resize, resizeListener);

    preview.setFitVertical(true);
    preview.setFitHorizontal(true);
    preview.setPrintJob(printJob);

    Listener dragListener = new Listener() {
    	private boolean canScroll = false;
    	private Point dragStartScrollOrigin = null;
    	private Point dragStartMouseAnchor = null;
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Resize:
					Rectangle bounds = scroll.getClientArea();
					Point size = preview.getSize();
					canScroll = size.x > bounds.width || size.y > bounds.height;
					if (!canScroll) {
						dragStartScrollOrigin = null;
						dragStartMouseAnchor = null;
					}
					break;
				case SWT.MouseDown:
					if (canScroll && event.button == 1) {
						dragStartScrollOrigin = scroll.getOrigin();
						dragStartMouseAnchor = preview.toDisplay(event.x, event.y);
					}
					break;
				case SWT.MouseMove:
					if (dragStartMouseAnchor != null && dragStartScrollOrigin != null) {
						Point point = preview.toDisplay(event.x, event.y);
						scroll.setOrigin(dragStartScrollOrigin.x + dragStartMouseAnchor.x - point.x,
														 dragStartScrollOrigin.y + dragStartMouseAnchor.y - point.y);
					}
					break;
				case SWT.MouseUp:
					if (canScroll) {
						dragStartMouseAnchor = null;
						dragStartScrollOrigin = null;
					}
					break;
				case SWT.MouseEnter:
					if (canScroll)
						display.addFilter(SWT.MouseWheel, this);
					break;
				case SWT.MouseWheel:
					if (canScroll && event.stateMask == SWT.NONE && dragStartScrollOrigin == null) {
						bounds = scroll.getClientArea();
						size = preview.getSize();
						Point origin = scroll.getOrigin();
						int direction = event.count == 0 ? 0 : event.count > 0 ? -1 : 1; 
						if (size.y > bounds.height) { // prefer vertical over horizontal scrolling
							origin.y += direction * bounds.height / 20;
						} else if (size.x > bounds.width) {
							origin.x += direction * bounds.width / 20;
						}
						scroll.setOrigin(origin);
						event.type = SWT.None;
					}
					break;
				case SWT.MouseExit:
					if (canScroll)
						display.removeFilter(SWT.MouseWheel, this);
					break;
				}
			}
    };
    scroll .addListener(SWT.Resize,    dragListener);
    preview.addListener(SWT.MouseDown, dragListener);
    preview.addListener(SWT.MouseMove, dragListener);
    preview.addListener(SWT.MouseUp,   dragListener);

    // These are for mouse wheel handling
    preview.addListener(SWT.MouseEnter, dragListener);
    preview.addListener(SWT.MouseExit,  dragListener);

    shell.setVisible (true);

    while (!shell.isDisposed ())
      if (!display.readAndDispatch ())
        display.sleep();

    display.dispose ();
  }
}
