package net.sf.paperclips;

/**
 * Instances of this class represent the page margins to follow when processing a print job.
 * @author Matthew Hall
 */
public class Margins {
  /** The top margin. */
  public int top;

  /** The left margin. */
  public int left;

  /** The right margin. */
  public int right;

  /** The bottom margin. */
  public int bottom;

  /**
   * Constructs a Margins with all sides set to 1" margins.
   */
  public Margins() {
    this(72);
  }

  /**
   * Constructs a Margins with all sides set to the argument. 
   * @param margins the page margins, expressed in points.  72 points = 1".
   */
  public Margins(int margins) {
    top = left = right = bottom = margins;
  }

  /**
   * Returns a Margins that is the result of rotating this Margins counter-clockwise 90 degrees.
   * A job which is rotated 90 degrees (e.g. for landscape printing) needs to have its margins
   * rotated to match.  This is a convenience method for that purpose.
   * @return a Margins that is the result of rotating this Margins counter-clockwise 90 degrees.
   */
  public Margins rotate() {
    Margins result = new Margins();
    result.top = right;
    result.left = top;
    result.right = bottom;
    result.bottom = left;
    return result;
  }
}