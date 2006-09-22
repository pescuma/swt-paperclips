package net.sf.paperclips;

/**
 * Instances of this class represent a prepared print job.
 * @author Matthew Hall
 */
public class PrintJob {
  private final String name;
  private final Print document;

  private Margins margins;

  private int orientation = PaperClips.PORTRAIT;

  /**
   * Constructs a PrintJob for the given document.
   * @param name the name of the print job, which will appear in the print queue of the operating
   *        system.
   * @param document the document to be printed.
   */
  public PrintJob(String name, Print document) {
    this(name, document, new Margins());
  }

  /**
   * Constructs a PrintJob for the given document.
   * @param name the name of the print job, which will appear in the print queue of the operating
   *        system.
   * @param document the document to be printed.
   * @param margins the page margins.
   */
  public PrintJob(String name, Print document, Margins margins) {
    if (name == null || document == null || margins == null)
      throw new NullPointerException();
    this.name = name;
    this.document = document;
    this.margins = margins;
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
   * @param orientation the page orientation.  Must be one of {@link PaperClips#PAGE_PORTRAIT} or
   *        {@link PaperClips#PAGE_LANDSCAPE}.
   */
  public void setOrientation(int orientation) {
    this.orientation = checkOrientation(orientation);
  }

  private int checkOrientation(int orientation) {
    switch (orientation) {
      case PaperClips.DEFAULT:
      case PaperClips.LANDSCAPE:
      case PaperClips.PORTRAIT:
        return orientation;
      default:
        return PaperClips.DEFAULT;
    }
  }

  /**
   * Returns the page margins, expressed in points.  72 points = 1".
   * @return the page margins, expressed in points.  72 points = 1".
   */
  public Margins getMargins() {
    return margins;
  }

  /**
   * Sets the page margins.
   * @param margins the new page margins, expressed in points.  72 points = 1".
   */
  public void setMargins(Margins margins) {
    if (margins == null)
      throw new NullPointerException();
    this.margins = margins;
  }

  /**
   * Sets the top, left, right, and bottom margins to the argument.
   * @param margins the margins, in points.  72 points = 1 inch.
   */
  public void setMargins(int margins) {
    this.margins = new Margins(margins);
  }
}
