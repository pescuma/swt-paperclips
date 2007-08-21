/************************************************************************************************************
 * Copyright (c) 2006 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

/**
 * A PageDecoration which displays the same decoration on every page (ignoring the page number).
 * <p>
 * Typically the page number will be in either the header or footer, but not in both. Often the page number
 * is the only thing that changes from page to page in a header. Use this class for a header or footer which
 * does not display the page number.
 * @author Matthew Hall
 */
public class SimplePageDecoration implements PageDecoration {
  private final Print print;

  /**
   * Constructs a BasicPageDecoration.
   * @param print the decoration which will appear on every page.
   */
  public SimplePageDecoration( Print print ) {
    if ( print == null )
      throw new NullPointerException();
    this.print = print;
  }

  public Print createPrint( PageNumber pageNumber ) {
    return print;
  }
}
