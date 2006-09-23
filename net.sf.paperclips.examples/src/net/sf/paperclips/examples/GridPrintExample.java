/*
 * Created on Sep 1, 2005
 */

package net.sf.paperclips.examples;

import net.sf.paperclips.DefaultGridLook;
import net.sf.paperclips.FactoryPrint;
import net.sf.paperclips.GridPrint;
import net.sf.paperclips.ImagePrint;
import net.sf.paperclips.LinePrint;
import net.sf.paperclips.PaperClips;
import net.sf.paperclips.Print;
import net.sf.paperclips.TextPrint;
import net.sf.paperclips.ui.PrintViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Example for the GridPrint class.
 * @author Matthew
 */
public class GridPrintExample extends FactoryPrint {
  /**
   * Executes the GridPrint example.
   * @param args the command line arguments.
   */
  public static void main(String[] args) {
    final Display display = new Display();

    Shell shell = new Shell(display, SWT.SHELL_TRIM);
    shell.setLayout(new FillLayout());
    shell.setSize(600, 600);

    final PrintViewer preview = new PrintViewer(shell, SWT.BORDER);
    preview.setPrint(new GridPrintExample());

    shell.open();

    while (!shell.isDisposed())
      if (!display.readAndDispatch())
        display.sleep();

    PaperClips.print("GridPrintExample", new GridPrintExample());
  }

  protected Print createPrint() {
    GridPrint grid = new GridPrint("r:72, p, d, r:d:g(3), r:d:g", new DefaultGridLook(5, 5));

    ImageData imageData = new ImageData(
        GridPrintExample.class.getResourceAsStream("logo.png"));
    ImagePrint image = new ImagePrint(imageData);
    image.setDPI(300, 300);

    grid.add(image, GridPrint.REMAINDER, SWT.CENTER);

    FontData fontData = new FontData("Arial", 10, SWT.BOLD);

    grid.add(new TextPrint("This column is 72 pts wide no matter what", fontData, SWT.RIGHT));
    grid.add(new TextPrint("Preferred size", fontData));
    grid.add(new TextPrint("Default width column", fontData));
    grid.add(new TextPrint("This is another default width column", fontData, SWT.CENTER));
    grid.add(new TextPrint("Default width column", fontData, SWT.RIGHT), GridPrint.REMAINDER);
    grid.add(new LinePrint(), GridPrint.REMAINDER);
    grid.add(new TextPrint("LOTS AND LOTS AND LOTS AND LOTS AND LOTS OF TEXT", fontData, SWT.CENTER), GridPrint.REMAINDER, SWT.CENTER);

    GridPrint child = new GridPrint("d:g, d:g", new DefaultGridLook(10, 10));
    child.add(new TextPrint("This is a line with some text.", fontData));
    child.add(new TextPrint("This is a line with lots of text.  Where is all this text coming from??", fontData));

    grid.add(child, GridPrint.REMAINDER, SWT.LEFT);

    return grid;
  }
}
