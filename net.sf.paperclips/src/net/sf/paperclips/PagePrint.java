/*
 * Created on Oct 19, 2005
 */
package net.sf.paperclips;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/**
 * A decorator Print which displays page headers and footers around a document body, with page
 * numbering capabilities.
 * <p>
 * PagePrint is horizontally and vertically greedy.  Greedy prints take up all the available space
 * on the page.
 * @author Matthew
 */
public class PagePrint implements Print {
  Print body;

  PageDecoration header;
  PageDecoration footer;

  int headerGap = 1; // in points
  int footerGap = 1; // in points

  /**
   * Constructs a PagePrint with the given body, header and footer.
   * @param body the Print being decorated.
   * @param header a PageDecoration for creating the header. May be null.
   * @param footer a PageDecoration for creating the footer. may be null.
   */
  public PagePrint (Print body, PageDecoration header, PageDecoration footer) {
    if (body == null)
      throw new NullPointerException();
    this.body = body;
    this.header = header;
    this.footer = footer;
  }

  /**
   * Constructs a PagePrint with the given body, and no header or footer.
   * @param body the Print being decorated.
   */
  public PagePrint (Print body) {
    if (body == null)
      throw new NullPointerException();
    this.body = body;
    this.header = null;
    this.footer = null;
  }

  private int checkGap (int gap) {
    if (gap < 0)
      throw new IllegalArgumentException ("Gap must be >= 0 (value is " + gap
          + ")");
    return gap;
  }

  /**
   * Sets the page body to the argument.
   * @param body the new page body.
   */
  public void setBody (Print body) {
    if (body == null)
      throw new NullPointerException();
    this.body = body;
  }

  /**
   * Sets the page header to the argument.
   * @param header a PageDecoration which creates the header. May be null.
   */
  public void setHeader (PageDecoration header) {
    this.header = header;
  }

  /**
   * Sets the gap between the header and body to the argument, expressed in
   * points.
   * @param points the new gap between the header and body (if there is a header).
   */
  public void setHeaderGap (int points) {
    this.headerGap = checkGap (points);
  }

  /**
   * Sets the page footer to the argument.
   * @param footer a PageDecoration which creates the footer. May be null.
   */
  public void setFooter (PageDecoration footer) {
    this.footer = footer;
  }

  /**
   * Sets the gap between the body and footer to the argument, expressed in
   * points.
   * @param points the new gap between the body and footer (if there is a footer).
   */
  public void setFooterGap (int points) {
    this.footerGap = checkGap (points);
  }

  /**
   * Returns the page body.
   * @return the page body.
   */
  public Print getBody () {
    return body;
  }

  /**
   * Returns the page header.
   * @return the page header.
   */
  public PageDecoration getHeader () {
    return header;
  }

  /**
   * Returns the gap between the header and body, expressed in points.
   * @return the gap between the header and body, expressed in points.
   */
  public int getHeaderGap () {
    return headerGap;
  }

  /**
   * Returns the page footer.
   * @return the page footer.
   */
  public PageDecoration getFooter () {
    return footer;
  }

  /**
   * Returns the gap between the body and footer, expressed in points.
   * @return the gap between the body and footer, expressed in points.
   */
  public int getFooterGap () {
    return footerGap;
  }

  public PrintIterator iterator (Device device, GC gc) {
    PageDecoration header = this.header;
    PageDecoration footer = this.footer;
    // If there is no header or footer, just fall through to the body iterator.
    if (header == null && footer == null)
      return body.iterator(device, gc);

    return new PageIterator (this, device, gc);
  }
}

class PageNumberer {
  int pageCount = 0;

  synchronized PageNumber next () {
    return new InnerPageNumber ();
  }

  class InnerPageNumber implements PageNumber {
    final int pageNumber = pageCount++; // POST-increment

    public int getPageCount () {
      return pageCount;
    }

    public int getPageNumber () {
      return pageNumber;
    }
  }

  PageNumberer copy () {
    PageNumberer result = new PageNumberer ();
    result.pageCount = this.pageCount;
    return result;
  }
}

class PageIterator implements PrintIterator {
  final Device device;
  final GC gc;
  final Point dpi;

  final PageDecoration header;
  final int headerGap; // pixels

  final PrintIterator body;

  final PageDecoration footer;
  final int footerGap; // pixels

  final PageNumberer numberer;

  Point minimumSize;
  Point preferredSize;

  PageIterator (PagePrint print, Device device, GC gc) {
    this.device = device;
    this.gc = gc;
    this.dpi = device.getDPI ();

    body = print.body.iterator (device, gc);
    header = print.header;
    headerGap = header == null ? 0 : print.headerGap * dpi.y / 72;
    footer = print.footer;
    footerGap = footer == null ? 0 : print.footerGap * dpi.y / 72;

    this.numberer = new PageNumberer ();
  }

  PageIterator (PageIterator that) {
    this.device = that.device;
    this.gc = that.gc;
    this.dpi = that.dpi;

    this.body = that.body.copy ();
    this.header = that.header;
    this.headerGap = that.headerGap;
    this.footer = that.footer;
    this.footerGap = that.footerGap;

    this.numberer = that.numberer.copy ();
    this.pageNumber = that.pageNumber;

    this.minimumSize = that.minimumSize;
    this.preferredSize = that.preferredSize;
  }

  private void computeSizes() {
  	if (minimumSize != null && preferredSize != null) return;

    // Calculate the minimum and preferred size.
    Point minSize = body.minimumSize ();
    Point prefSize = body.preferredSize ();

    PageNumber samplePageNumber = new PageNumber () {
      public int getPageCount () { return 1; }
      public int getPageNumber() { return 0; }
    };

    if (header != null) {
    	Print headerPrint = header.createPrint(samplePageNumber);
    	if (headerPrint != null) {
        PrintIterator iter = headerPrint.iterator (device, gc);

        minSize.y += headerGap;
        prefSize.y += headerGap;

        Point headerMinSize = iter.minimumSize ();
        minSize.y += headerMinSize.y;
        minSize.x = Math.max (minSize.x, headerMinSize.x);

        Point headerPrefSize = iter.preferredSize ();
        prefSize.y += headerPrefSize.y;
        prefSize.x = Math.max (prefSize.x, headerPrefSize.x);
    	}
    }

    if (footer != null) {
    	Print footerPrint = footer.createPrint(samplePageNumber);
    	if (footerPrint != null) {
        PrintIterator iter = footerPrint.iterator (device, gc);

        minSize.y += footerGap;
        prefSize.y += footerGap;

        Point footerMinSize = iter.minimumSize ();
        minSize.y += footerMinSize.y;
        minSize.x = Math.max (minSize.x, footerMinSize.x);

        Point footerPrefSize = iter.preferredSize ();
        prefSize.y += footerPrefSize.y;
        prefSize.x = Math.max (prefSize.x, footerPrefSize.x);
    	}
    }

    this.minimumSize = minSize;
    this.preferredSize = prefSize;
  }

  public boolean hasNext () {
    return body.hasNext ();
  }

  public Point minimumSize () {
  	if (minimumSize == null)
  		computeSizes();
    return minimumSize;
  }

  public Point preferredSize () {
  	if (preferredSize == null)
  		computeSizes();
    return preferredSize;
  }

  private PageNumber pageNumber = null;

  public PrintPiece next (int width, int height) {
    // Remembering the page number in an instance field--this way if the
    // iteration fails, the page number will be available for the next
    // iteration (so page numbers don't get skipped).
    if (pageNumber == null) pageNumber = numberer.next ();

    // y offset
    int y = 0;

    List entries = new ArrayList ();

    // HEADER
    if (header != null) {
      Print headerPrint = header.createPrint(pageNumber);
      if (headerPrint != null) {
        PrintIterator headerIterator = headerPrint.iterator(device, gc);
        PrintPiece headerPiece = PaperClips.next(headerIterator, width, height);

        if (headerPiece == null) return null;

        entries.add (new CompositeEntry (headerPiece, new Point (0, 0)));

        int headerSize = headerPiece.getSize().y + headerGap;
        y += headerSize;
        height -= headerSize;
      }
    }

    // FOOTER
    if (footer != null) {
      Print footerPrint = footer.createPrint(pageNumber);
      if (footerPrint != null) {
        PrintIterator footerIterator = footerPrint.iterator(device, gc);
        PrintPiece footerPiece = PaperClips.next(footerIterator, width, height);

        if (footerPiece == null) {
          for (Iterator iter = entries.iterator(); iter.hasNext(); ) {
            CompositeEntry entry = (CompositeEntry) iter.next();
            entry.piece.dispose();
          }
          return null;
        }

        entries.add (new CompositeEntry (footerPiece, new Point (0, y + height
            - footerPiece.getSize ().y)));

        int footerSize = footerPiece.getSize().y + footerGap;

        height -= footerSize;
      }
    }

    // BODY
    PrintPiece bodyPiece = PaperClips.next(body, width, height);

    if (bodyPiece == null) {
      for (Iterator iter = entries.iterator(); iter.hasNext(); ) {
        CompositeEntry entry = (CompositeEntry) iter.next();
        entry.piece.dispose();
      }
      return null;
    }

    entries.add (new CompositeEntry (bodyPiece, new Point (0, y)));

    // Compile and return page.
    PrintPiece result = new CompositePiece (entries);

    // Iteration successful.  Null the pageNumber field so the next iteration
    // advances to the next page.
    pageNumber = null;

    return result;
  }

  public PrintIterator copy () {
    return new PageIterator (this);
  }
}