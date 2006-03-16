/*******************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Woodcraft Mill & Cabinet Corporation - initial API and implementation
 *******************************************************************************/
package net.sf.paperclips.preview;

import net.sf.paperclips.PrintPiece;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * A canvas for displaying Print objects.
 * @author Matthew
 */
public class PrintPieceCanvas extends Canvas {
  PrintPiece piece = null;

  /**
   * Constructs a PrintCanvas with the given parent and style.
   * @param parent the parent Composite.
   * @param style the style parameter.
   */
  public PrintPieceCanvas(Composite parent, int style) {
    super(parent, style);

    setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
    setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND));

    addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent e) {
        if (piece == null)
          return;

        Rectangle client = getClientArea();
        piece.paint(e.gc, client.x, client.y);
      }
    });
  }

  /**
   * Displays the given Print in this PrintCanvas.
   * @param piece the PrintPiece to display.
   */
  public void setPrintPiece(PrintPiece piece) {
    this.piece = piece;
    redraw();
  }

  /**
   * Returns the PrintPiece being displayed by this PrintCanvas.
   * @return the PrintPiece being displayed by this PrintCanvas.
   */
  public PrintPiece getPrintPiece() {
    return piece;
  }
}
