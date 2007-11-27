package net.sf.paperclips.internal;

import java.util.Arrays;

/**
 * Utility methods for implementing equals(Object)
 * 
 * @author Matthew Hall
 */
public class EqualsUtil {
  /**
   * Returns whether the objects are of the same class.
   * @param left object to test
   * @param right object to test
   * @return whether the objects are of the same class.
   */
  public static boolean sameClass( Object left, Object right ) {
    if ( left == right )
      return true;
    if ( left == null || right == null )
      return false;
    return left.getClass() == right.getClass();
  }

  /**
   * Returns whether the arguments are equal.
   * @param left object to test
   * @param right object to test
   * @return whether the arguments are equal.
   */
  public static boolean areEqual( Object left, Object right ) {
    if ( !sameClass( left, right ) )
      return false;
    if ( left == right )
      return true;
    Class clazz = left.getClass();
    if ( clazz.isArray() ) {
      Class componentType = clazz.getComponentType();
      if ( componentType.isPrimitive() ) {
        if ( componentType == Byte.TYPE )
          return Arrays.equals( (byte[]) left, (byte[]) right );
        if ( componentType == Short.TYPE )
          return Arrays.equals( (short[]) left, (short[]) right );
        if ( componentType == Integer.TYPE )
          return Arrays.equals( (int[]) left, (int[]) right );
        if ( componentType == Long.TYPE )
          return Arrays.equals( (long[]) left, (long[]) right );
        if ( componentType == Character.TYPE )
          return Arrays.equals( (char[]) left, (char[]) right );
        if ( componentType == Float.TYPE )
          return Arrays.equals( (float[]) left, (float[]) right );
        if ( componentType == Double.TYPE )
          return Arrays.equals( (double[]) left, (double[]) right );
        if ( componentType == Boolean.TYPE )
          return Arrays.equals( (boolean[]) left, (boolean[]) right );
      }
      return areEqual( (Object[]) left, (Object[]) right );
    }
    return left.equals( right );
  }

  private static boolean areEqual( Object[] left, Object[] right ) {
    int length = left.length;
    if ( length != right.length )
      return false;
    for ( int i = 0; i < length; i++ )
      if ( !areEqual( left[i], right[i] ) )
        return false;
    return true;
  }

  /**
   * Returns whether the arguments are equal.
   * @param left double value to test
   * @param right double value to test
   * @return whether the arguments are equal.
   */
  public static boolean areEqual( double left, double right ) {
    return Double.doubleToLongBits( left ) == Double.doubleToLongBits( right );
  }
}
