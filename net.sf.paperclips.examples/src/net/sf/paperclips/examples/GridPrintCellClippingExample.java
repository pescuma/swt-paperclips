/*
 * Created on Sep 1, 2005
 */

package net.sf.paperclips.examples;

import net.sf.paperclips.DefaultGridLook;
import net.sf.paperclips.GridPrint;
import net.sf.paperclips.LineBorder;
import net.sf.paperclips.PaperClips;
import net.sf.paperclips.Print;
import net.sf.paperclips.PrintJob;
import net.sf.paperclips.TextPrint;
import net.sf.paperclips.ui.PrintPreview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Example for the GridPrint class using.
 * @author Matthew
 */
public class GridPrintCellClippingExample {
  /**
   * Executes the GridPrintNoBreak example.
   * @param args the command line arguments.
   */
  public static void main(String[] args) {
    final Display display = new Display();

    Shell shell = new Shell(display, SWT.SHELL_TRIM);
    shell.setLayout(new GridLayout());
    shell.setSize(600, 600);

    PrintJob job = new PrintJob("GridPrintNoBreakExample", createPrint());

    final PrintPreview preview = new PrintPreview(shell, SWT.BORDER);
    preview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    preview.setPrintJob(job);

    shell.open();

    while (!shell.isDisposed())
      if (!display.readAndDispatch())
        display.sleep();

    PaperClips.print(job, new PrinterData());
  }

  protected static Print createPrint() {
  	GridPrint doc = new GridPrint("d:g, d:g", new DefaultGridLook(5, 2));
  	doc.add(createGrid(true));
  	doc.add(createGrid(false));
    return doc;
  }

	private static GridPrint createGrid(boolean cellClippingEnabled) {
		DefaultGridLook look = new DefaultGridLook();
  	look.setCellBorder(new LineBorder());
    GridPrint grid = new GridPrint("d, d, d, d", look);
    grid.setCellClippingEnabled(cellClippingEnabled);

    for (int i = 0; i < 200; i++)
    	grid.add(new TextPrint("Text cell\n#"+i));
		return grid;
	}
}
