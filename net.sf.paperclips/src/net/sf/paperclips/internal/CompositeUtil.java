package net.sf.paperclips.internal;

import java.util.Iterator;
import java.util.List;

import net.sf.paperclips.CompositeEntry;

/**
 * Convenience methods for working with CompositePiece and friends.
 * @author Matthew Hall
 */
public class CompositeUtil {
  private CompositeUtil() {} // no instances

  /**
   * Calls dispose() on all CompositeEntry instances in the list.
   * @param list List of CompositeEntry instances to dispose.
   */
  public static void disposeEntries( List list ) {
    for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
      CompositeEntry entry = (CompositeEntry) iter.next();
      entry.dispose();
    }
  }
}
