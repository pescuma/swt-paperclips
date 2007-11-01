package net.sf.paperclips.internal;

import java.util.List;

import org.eclipse.swt.SWT;

import net.sf.paperclips.PaperClips;

/**
 * Provides methods for performing sanity checks on arguments.
 * @author Matthew Hall
 */
public class NullUtil {
  private NullUtil() {} // No instances

  /**
   * Triggers a SWT.ERROR_NULL_ARGUMENT exception if the argument or any of its elements is null.
   * @param list a list to test for null elements.
   */
  public static void noNulls( List list ) {
    notNull( list );
    if ( list.contains( null ) )
      PaperClips.error( SWT.ERROR_NULL_ARGUMENT );
  }

  /**
   * Triggers a SWT.ERROR_NULL_ARGUMENT exception if the argument or any of its elements is null.
   * @param objs an array to test for null elements.
   */
  public static void noNulls( Object[] objs ) {
    notNull( objs );
    for ( int i = 0; i < objs.length; i++ )
      notNull( objs[i] );
  }

  /**
   * Triggers a SWT.ERROR_NULL_ARGUMENT exception if the argument is null.
   * @param obj the object to test for null.
   */
  public static void notNull( Object obj ) {
    if ( obj == null )
      PaperClips.error( SWT.ERROR_NULL_ARGUMENT );
  }

  /**
   * Triggers a SWT.ERROR_NULL_ARGUMENT exception if any argument is null.
   * @param o1 an object to test for null.
   * @param o2 an object to test for null.
   */
  public static void notNull( Object o1, Object o2 ) {
    notNull( o1 );
    notNull( o2 );
  }

  /**
   * Triggers a SWT.ERROR_NULL_ARGUMENT exception if any argument is null.
   * @param o1 an object to test for null.
   * @param o2 an object to test for null.
   * @param o3 an object to test for null.
   */
  public static void notNull( Object o1, Object o2, Object o3 ) {
    notNull( o1 );
    notNull( o2 );
    notNull( o3 );
  }
}
