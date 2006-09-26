package net.sf.paperclips.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.sf.paperclips.AlignPrint;
import net.sf.paperclips.PaperClips;
import net.sf.paperclips.Print;
import net.sf.paperclips.PrintIterator;
import net.sf.paperclips.PrintJob;

/**
 * Prints the contents of TutorialExample2, but centered horizontally and vertically on the page.
 *
 * @author Matthew
 */
public class AlignPrintExample implements Print {
  private Print createPrint () {
    Print print = new TutorialExample2();
    return new AlignPrint(print, SWT.CENTER, SWT.CENTER);
  }

  public PrintIterator iterator(Device device, GC gc) {
    return createPrint().iterator(device, gc);
  }

  /**
   * Prints the BreakPrintExample to the default printer.
   * @param args command-line args
   */
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display, SWT.SHELL_TRIM);
    PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
    PrinterData printerData = dialog.open();
    shell.dispose();
    display.dispose();
    if (printerData != null)
      PaperClips.print(new PrintJob("AlignPrintExample.java", new AlignPrintExample()),
                       printerData); 
  }

}
