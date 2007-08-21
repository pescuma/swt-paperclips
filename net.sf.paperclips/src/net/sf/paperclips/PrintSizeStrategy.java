/************************************************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.graphics.Point;

/**
 * The static instance members of this class aid in the calculation of prints and help abstract out the
 * minimum/preferred size concepts to simplify algorithms.
 * @author Matthew Hall
 */
abstract class PrintSizeStrategy {
  /** Compute the minimum size */
  public static final PrintSizeStrategy MINIMUM   = new PrintSizeStrategy() {
                                                    Point computeSize( PrintIterator iter ) {
                                                      return iter.minimumSize();
                                                    }
                                                  };
  /** Compute the preferred size. */
  public static final PrintSizeStrategy PREFERRED = new PrintSizeStrategy() {
                                                    Point computeSize( PrintIterator iter ) {
                                                      return iter.preferredSize();
                                                    }
                                                  };

  abstract Point computeSize( PrintIterator print );
}