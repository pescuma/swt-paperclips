/*
 * Created on Oct 19, 2005
 */
package net.sf.paperclips;

/**
 * An interface for creating page decorations. Instances of this interface are
 * used as headers and footers in conjunction with the PagePrint class.
 * @see PagePrint
 * @see SimplePageDecoration
 * @see PageNumberPageDecoration
 * @author Matthew
 */
public interface PageDecoration {
  /**
   * Returns a decorator Print for the page with the given page number, or null if no decoration is
   * provided for the given page.
   * @param pageNumber the page number of the page being decorated.
   * @return a decorator Print for the page with the given page number, or null if no decoration is
   *         provided for the given page.
   */
  public Print createPrint (PageNumber pageNumber);
}
