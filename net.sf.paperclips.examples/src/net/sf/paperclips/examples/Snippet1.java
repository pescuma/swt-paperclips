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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import net.sf.paperclips.GridColumn;
import net.sf.paperclips.GridPrint;
import net.sf.paperclips.ImagePrint;
import net.sf.paperclips.LineBorder;
import net.sf.paperclips.Print;
import net.sf.paperclips.PrintIterator;
import net.sf.paperclips.PrintUtil;
import net.sf.paperclips.TextPrint;

/**
 * Prints the contents of a Table widget.
 * @author Matthew
 */
public class Snippet1 implements Print {
  private final Table table;

  /**
   * Constructs a Snippet1.
   * @param table the table whose contents should be printed.
   */
  public Snippet1(Table table) {
    if (table == null) throw new NullPointerException();

    this.table = table;
  }

  private Print createPrint () {
    // Create GridPrint with all columns at default size.
    GridColumn[] cols = new GridColumn[table.getColumnCount()];
    final GridColumn defaultColumn = GridColumn.parse("d");
    for (int i = 0; i < cols.length; i++)
      cols[i] = defaultColumn;
    GridPrint grid = new GridPrint(cols);
    grid.setCellBorder (new LineBorder());

    // Add header row
    for (TableColumn col : table.getColumns ())
      grid.add(createCell(col.getImage (), col.getText()));

    // Add content rows
    for (TableItem item : table.getItems())
      for (int i = 0; i < cols.length; i++)
        grid.add(createCell(item.getImage(i), item.getText(i)));

    return grid;
  }

  private Print createCell(Image image, String text) {
    if (image == null)
      return new TextPrint(text);

    GridPrint grid = new GridPrint("p, d");
    grid.add(new ImagePrint(image.getImageData (), image.getDevice ().getDPI()));
    grid.add(new TextPrint(text));
    return grid;
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
    Shell shell = new Shell (display);
    shell.setBounds (100, 100, 640, 480);
    shell.setLayout (new GridLayout());

    Button button = new Button (shell, SWT.PUSH);
    button.setLayoutData (new GridData (SWT.FILL, SWT.DEFAULT, true, false));
    button.setText ("Print the table");

    final Table table = new Table (shell, SWT.BORDER);
    table.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));

    for (int i = 0; i < 5; i++)
      new TableColumn (table, SWT.LEFT).setText ("Column " + i);

    for (int row = 0; row < 10; row++) {
      TableItem item = new TableItem(table, SWT.NONE);
      for (int col = 0; col < 5; col++)
        item.setText (col, "Cell ["+col+", "+row+"]");
    }

    table.setHeaderVisible (true);
    for (TableColumn col : table.getColumns())
      col.pack();

    button.addListener(SWT.Selection, new Listener() {
      public void handleEvent (Event event) {
        PrintUtil.print ("Snippet1.java", new Snippet1(table));
      }
    });

    shell.setVisible (true);

    while (!shell.isDisposed ())
      if (!display.readAndDispatch ())
        display.sleep();

    display.dispose ();
  }
}
