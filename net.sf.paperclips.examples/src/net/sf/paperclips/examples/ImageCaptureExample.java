package net.sf.paperclips.examples;

import net.sf.paperclips.DefaultGridLook;
import net.sf.paperclips.GridPrint;
import net.sf.paperclips.PaperClips;
import net.sf.paperclips.Print;
import net.sf.paperclips.PrintJob;
import net.sf.paperclips.PrintPiece;
import net.sf.paperclips.TextPrint;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;

/**
 * Demonstrate capturing the pages of a print job to in-memory images.
 * @author Matthew Hall
 */
public class ImageCaptureExample {
  /**
   * Returns a sample print
   * @return a sample print
   */
  public static Print createPrint() {
    GridPrint grid = new GridPrint(new DefaultGridLook());

    int COLS = 5;
    int ROWS = 50;

    for (int c = 0; c < COLS; c++) {
      grid.addColumn("d");
      grid.addHeader(new TextPrint("Column "+(c+1)));
      grid.addFooter(new TextPrint("Column "+(c+1)));
    }

    for (int r = 0; r < ROWS; r++)
      for (int c = 0; c < COLS; c++)
        grid.add(new TextPrint("Row "+(r+1)+" Col "+(c+1)));
  
    return grid;
  }

  /**
   * Captures the page to an image and returns it.
   * @param printer the printer device.
   * @param page the page to capture.
   * @return an image of the captured page.
   */
  public static Image captureImage(Printer printer, PrintPiece page) {
    GC gc = new GC(printer);

    try {
      Point size = page.getSize();
      Image image = new Image(printer, size.x, size.y);
      page.paint(gc, 0, 0);
      return image;
    } finally {
      gc.dispose();
    }
  }

  /**
   * Demonstrate capturing the pages of a print to in-memory images.
   * @param args command-line arguments (ignored)
   */
  public static void main(String[] args) {
    Printer printer = new Printer(new PrinterData());

    try {
      PrintJob job = new PrintJob("ImageCapture.java", createPrint());

      PrintPiece[] pages = PaperClips.getPages(job, printer);
      ImageData[] pageImages = new ImageData[pages.length];

      for (int i = 0; i < pages.length; i++)
        pageImages[i] = captureImage(printer, pages[i]).getImageData();

      // Now go ahead and do whatever you were planning to do with the image.
    } finally {
      printer.dispose();
    }
  }
}
