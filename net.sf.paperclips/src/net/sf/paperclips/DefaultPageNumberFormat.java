package net.sf.paperclips;

/**
 * The default PageNumberFormat used by PageNumberPrints.
 * <p>
 * This class formats page numbers as "Page x of y".
 * @author Matthew Hall
 */
public final class DefaultPageNumberFormat implements PageNumberFormat {
  public String format (PageNumber pageNumber) {
    return "Page " + (pageNumber.getPageNumber () + 1) + " of "
        + pageNumber.getPageCount ();
  }
}