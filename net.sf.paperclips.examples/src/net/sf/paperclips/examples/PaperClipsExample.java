/*
 * Created on Oct 21, 2005
 */
package net.sf.paperclips.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

import net.sf.paperclips.*;

/**
 * Example for PaperClips.
 * @author Matthew
 */
public class PaperClipsExample {
  /**
   * Executes the PaperClips example.
   * @param args the command line arguments.
   */
  public static void main(String[] args) {
    GridPrint grid = new GridPrint("p, d, c:d:g, 72", 5, 5);

    //  Now add some items to the grid
    grid.add(new ImagePrint(new ImageData(
        PaperClipsExample.class.getResourceAsStream("logo.png")),
        new Point(300, 300))); // The point indicates DPI.
    grid.add(new TextPrint(
        "Column 2 will shrink if space is scarce."));
    grid.add(new TextPrint(
        "Column 3 will shrink too, and it also grows to fill excess page width."));
    grid.add(new TextPrint(
        "Column 4 will be 1\" wide (72 points) wide no matter what."));
    grid.add(new TextPrint("This is in column 1 on the 2nd row"));
    grid.add(new TextPrint("Often is it useful for a document element "+
        "to span several cells in a grid."), 3);
    grid.add(new TextPrint("And sometimes you may want to override the "+
        "default alignment", SWT.RIGHT), 4, SWT.RIGHT);
    grid.add(new TextPrint("A handy shorthand for 'the rest of the row' is "+
        "to use GridPrint.REMAINDER as the colSpan argument."),
        GridPrint.REMAINDER);

    //  When the document is ready to go, pass it to the PrintUtil.print(Print) method.
    PrintUtil.print("PaperClipsExample", grid);
  }
}
