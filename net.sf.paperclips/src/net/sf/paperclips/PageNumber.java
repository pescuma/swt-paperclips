/*
 * Created on Oct 19, 2005
 */
package net.sf.paperclips;

/**
 * Instances of this class represent a page index in the output of a PagePrint.
 * @author Matthew
 */
public interface PageNumber {
  /**
   * Returns the zero-based page index.
   * @return the zero-based page index.
   */
  public int getPageNumber ();

  /**
   * Returns the total number of pages. Note that this method may not return an accurate value until all pages have
   * been laid out.  Therefore this method should not be used inside {@link PageDecoration#createPrint(PageNumber)}.
   * @return the total number of pages.
   */
  public int getPageCount ();
}
