/**
 * 
 */
package net.sf.paperclips.ui;

import net.sf.paperclips.PrintPiece;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.printing.Printer;

class RotateClockwisePrintPiece implements PrintPiece {
	private final Printer printer;
	private final PrintPiece target;
	private final Point size;
	
	RotateClockwisePrintPiece(Printer printer, PrintPiece target) {
		if (printer == null || target == null)
			throw new NullPointerException();
		this.printer = printer;
		this.target = target;
		Point targetSize = target.getSize();
		this.size = new Point(targetSize.y, targetSize.x);
	}
	
	public void dispose() {
		target.dispose();
	}
	
	public Point getSize() {
		return new Point(size.x, size.y);
	}
	
	public void paint(GC gc, int x, int y) {
		Transform oldTransform = null;
		Transform newTransform = null;
		try {
			oldTransform = new Transform(printer);
			gc.getTransform(oldTransform);
			
			newTransform = new Transform(printer);
			gc.getTransform(newTransform);
			newTransform.translate(x, y);
			newTransform.translate(size.x, 0);
			newTransform.rotate(90);
			gc.setTransform(newTransform);
			
			target.paint(gc, 0, 0);
			
			gc.setTransform(oldTransform);
		} finally {
			if (oldTransform != null)
				oldTransform.dispose();
			if (newTransform != null)
				newTransform.dispose();
		}
	}
}