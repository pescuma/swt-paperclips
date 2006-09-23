/*
 * Created on Nov 18, 2005
 */
package net.sf.paperclips.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.sf.paperclips.BorderPrint;
import net.sf.paperclips.ColumnPrint;
import net.sf.paperclips.FactoryPrint;
import net.sf.paperclips.LineBorder;
import net.sf.paperclips.PaperClips;
import net.sf.paperclips.Print;
import net.sf.paperclips.TextPrint;
import net.sf.paperclips.ui.PrintViewer;

/**
 * Example for the ColumnPrint class.
 * @author Matthew
 */
public class ColumnPrintExample extends FactoryPrint {
  /**
   * Executes the ColumnPrint example.
   * @param args the command line arguments.
   */
  public static void main(String[] args) {
    final Display display = new Display();

    Shell shell = new Shell(display, SWT.SHELL_TRIM);
    shell.setLayout(new FillLayout());
    shell.setSize(600, 600);

    final PrintViewer preview = new PrintViewer(shell, SWT.BORDER);
    preview.setPrint(new ColumnPrintExample());

    shell.open();

    while (!shell.isDisposed())
      if (!display.readAndDispatch())
        display.sleep();

    PaperClips.print("ColumnPrintExample", new ColumnPrintExample());
  }

  protected Print createPrint() {
    StringBuffer buf = new StringBuffer(11000);
    for (int i = 1; i <= 500; i ++) {
      buf.append("This is sentence #").append(i).append(".  ");
      if (i % 20 == 0) buf.append("\n\n");
    }

    return new ColumnPrint(
        new BorderPrint(new TextPrint(buf.toString()), new LineBorder()),
        3, 18);
  }
}
