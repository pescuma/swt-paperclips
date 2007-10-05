package net.sf.paperclips.internal;

import java.util.Iterator;
import java.util.List;

import net.sf.paperclips.PrintPiece;

/**
 * Convenience methods for disposing of PrintPieces.
 * @author Matthew Hall
 */
public class PrintPieceUtil {
  private PrintPieceUtil() {} // no instances

  /**
   * Disposes the print piece if not null.
   * @param piece the print piece to dispose.
   */
  public static void dispose( final PrintPiece piece ) {
    if ( piece != null )
      piece.dispose();
  }

  /**
   * Disposes the arguments that are not null.
   * @param p1 print piece to dispose
   * @param p2 print piece to dispose
   */
  public static void dispose( PrintPiece p1, PrintPiece p2 ) {
    dispose( p1 );
    dispose( p2 );
  }

  /**
   * Disposes the print pieces that are not null.
   * @param pieces array of print pieces to dispose.
   */
  public static void dispose( final PrintPiece[] pieces ) {
    if ( pieces != null )
      for ( int i = 0; i < pieces.length; i++ )
        dispose( pieces[i] );
  }

  /**
   * Disposes the print pieces in the array from start (inclusive) to end (exclusive).
   * @param pages array of print pieces to dispose.
   * @param start the start index.
   * @param end the end index.
   */
  public static void dispose( PrintPiece[] pages, int start, int end ) {
    for ( int i = start; i < end; i++ )
      pages[i].dispose();
  }

  /**
   * Disposes the print pieces in the list.
   * @param pages list of print pieces to dispose.
   */
  public static void dispose( List pages ) {
    for ( Iterator it = pages.iterator(); it.hasNext(); )
      ( (PrintPiece) it.next() ).dispose();
    pages.clear();
  }

  /**
   * Disposes the print pieces that are not null.
   * @param piece a print piece to dispose
   * @param pieces array of print pieces to dispose
   */
  public static void dispose( PrintPiece piece, final PrintPiece[] pieces ) {
    dispose( piece );
    dispose( pieces );
  }
}
