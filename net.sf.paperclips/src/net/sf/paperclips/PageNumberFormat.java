/*
 * Created on Oct 19, 2005
 */
package net.sf.paperclips;

/**
 * Interface for formatting a PageNumber instance into a printable string.
 * @author Matthew
 */
public interface PageNumberFormat {
  /**
   * Returns a formatted String representing the pageNumber argument.
   * @param pageNumber the page number to be formatted into a String.
   * @return a formatted String representing the pageNumber argument.
   */
  public String format (PageNumber pageNumber);
}
