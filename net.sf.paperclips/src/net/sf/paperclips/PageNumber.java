/*
 * Created on Oct 19, 2005
 */
package net.sf.paperclips;

/**
 * An interface for reporting the current page number and the total number of
 * pages.
 * @author Matthew
 */
public interface PageNumber {
  /**
   * Returns the current page number.
   * @return the current page number.
   */
  public int getPageNumber ();

  /**
   * Returns the total number of pages. Note that this method may not return an
   * accurate page count until the PagePrint has finished iterating.
   * @return the total number of pages.
   */
  public int getPageCount ();
}
