/************************************************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import net.sf.paperclips.internal.EqualsUtil;
import net.sf.paperclips.internal.NullUtil;

/**
 * Instances of this class represent a prepared print job.
 * @author Matthew Hall
 */
public class PrintJob {
  private final String name;
  private final Print  document;

  private Margins      margins     = new Margins();

  private int          orientation = PaperClips.ORIENTATION_DEFAULT;

  /**
   * Constructs a PrintJob for the given document.
   * @param name the name of the print job, which will appear in the print queue of the operating system.
   * @param document the document to be printed.
   */
  public PrintJob( String name, Print document ) {
    NullUtil.notNull( name, document );
    this.name = name;
    this.document = document;
  }

  public boolean equals( Object obj ) {
    if ( !EqualsUtil.sameClass( this, obj ) )
      return false;

    PrintJob that = (PrintJob) obj;
    return this.orientation == that.orientation && EqualsUtil.areEqual( this.name, that.name )
        && EqualsUtil.areEqual( this.margins, that.margins )
        && EqualsUtil.areEqual( this.document, that.document );
  }

  /**
   * Returns the name of the print job.
   * @return the name of the print job.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the document to be printed.
   * @return the document to be printed.
   */
  public Print getDocument() {
    return document;
  }

  /**
   * Returns the page orientation.
   * @return the page orientation.
   */
  public int getOrientation() {
    return orientation;
  }

  /**
   * Sets the page orientation.
   * @param orientation the page orientation. Must be one of {@link PaperClips#ORIENTATION_DEFAULT },
   *        {@link PaperClips#ORIENTATION_PORTRAIT } or {@link PaperClips#ORIENTATION_LANDSCAPE }. Values
   *        other than these choices will be automatically changed to {@link PaperClips#ORIENTATION_DEFAULT }.
   * @return this PrintJob (for chaining method calls)
   */
  public PrintJob setOrientation( int orientation ) {
    this.orientation = checkOrientation( orientation );
    return this;
  }

  private int checkOrientation( int orientation ) {
    switch ( orientation ) {
      case PaperClips.ORIENTATION_LANDSCAPE:
      case PaperClips.ORIENTATION_PORTRAIT:
      case PaperClips.ORIENTATION_DEFAULT:
        return orientation;
      default:
        return PaperClips.ORIENTATION_DEFAULT;
    }
  }

  /**
   * Returns the page margins, expressed in points. 72 points = 1".
   * @return the page margins, expressed in points. 72 points = 1".
   */
  public Margins getMargins() {
    return margins;
  }

  /**
   * Sets the page margins.
   * @param margins the new page margins.
   * @return this PrintJob (for chaining method calls)
   */
  public PrintJob setMargins( Margins margins ) {
    NullUtil.notNull( margins );
    this.margins = margins;
    return this;
  }

  /**
   * Sets the top, left, right, and bottom margins to the argument.
   * @param margins the margins, in points. 72 points = 1 inch.
   * @return this PrintJob (for chaining method calls)
   */
  public PrintJob setMargins( int margins ) {
    this.margins = new Margins( margins );
    return this;
  }
}
