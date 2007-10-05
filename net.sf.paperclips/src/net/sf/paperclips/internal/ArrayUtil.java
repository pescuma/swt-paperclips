package net.sf.paperclips.internal;

import java.util.List;

/**
 * Provides convenience methods for dealing with arrays
 * @author Matthew Hall
 */
public class ArrayUtil {
  private ArrayUtil() {} // no instances

  /**
   * Returns a copy of the array.
   * @param array the array to copy
   * @return a copy of the array.
   */
  public static int[] defensiveCopy( int[] array ) {
    NullUtil.notNull( array );
    return (int[]) array.clone();
  }

  /**
   * Returns a deep copy of the array.
   * @param array the array to copy
   * @return a copy of the array.
   */
  public static int[][] defensiveCopy( int[][] array ) {
    NullUtil.notNull( array );
    int[][] result = (int[][]) array.clone();
    for ( int i = 0; i < result.length; i++ )
      result[i] = defensiveCopy( result[i] );
    return result;
  }

  /**
   * Returns the sum of all elements in the array.
   * @param array the array
   * @return the sum of all elements in the array.
   */
  public static int sum( int[] array ) {
    return sum( array, 0, array.length );
  }

  /**
   * Returns the sum of all elements in the array in the range <code>[start, start+count)</code>.
   * @param array the array containing the elements to add up.
   * @param start the index of the first element to add.
   * @param count the number of elements to add.
   * @return the sum of all elements in the array in the specified range.
   */
  public static int sum( final int[] array, final int start, final int count ) {
    NullUtil.notNull( array );
    int result = 0;
    final int end = start + count;
    for ( int i = start; i < end; i++ )
      result += array[i];
    return result;
  }

  /**
   * Returns the sum of all elements in the array at the given indices.
   * @param array the array of elements to add up.
   * @param indices the indices of the elements in the array to add up.
   * @return the sum of all elements in the array at the given indices.
   */
  public static int sumByIndex( final int[] array, final int[] indices ) {
    NullUtil.notNull( array );
    int result = 0;
    for ( int i = 0; i < indices.length; i++ )
      result += array[indices[i]];
    return result;
  }

  /**
   * Converts the argument to an int[] array.
   * @param list a List of Integers.
   * @return a primitive int[] array.
   */
  public static int[] toPrimitiveIntArray( List list ) {
    final int[] array = new int[list.size()];
    for ( int i = 0; i < array.length; i++ )
      array[i] = ( (Integer) list.get( i ) ).intValue();
    return array;
  }

  /**
   * Converts the argument to an int[][] array.
   * @param list a List of int[] arrays.
   * @return a primitive int[][] array.
   */
  public static int[][] toPrimitiveIntIntArray( List list ) {
    final int[][] array = new int[list.size()][];
    for ( int i = 0; i < array.length; i++ )
      array[i] = (int[]) list.get( i );
    return array;
  }
}