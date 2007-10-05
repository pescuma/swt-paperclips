package net.sf.paperclips.internal;

/**
 * Convenience methods for working with magic-number type integers.
 * @author Matthew Hall
 */
public class BitUtil {
  private BitUtil() {} // no instances

  /**
   * Returns the first element in masks where (value & mask[index]) == mask[index].
   * @param value the value to match
   * @param masks the possible values.
   * @param defaultMask the value to return if no match is found.
   * @return the first value in possibleValues which is a bitwise match to value, or 0 if none is found.
   */
  public static int firstMatch( int value, int[] masks, int defaultMask ) {
    NullUtil.notNull( masks );
    for ( int i = 0; i < masks.length; i++ ) {
      int mask = masks[i];
      if ( ( value & mask ) == mask )
        return mask;
    }
    return defaultMask;
  }
}
