/*******************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation and others. All
 * rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Woodcraft Mill &
 * Cabinet Corporation - initial API and implementation
 ******************************************************************************/
package net.sf.paperclips;

import org.eclipse.swt.graphics.Point;

enum PrintSizeStrategy {
  /** Compute the minimum size */
  MINIMUM {
    @Override
    Point computeSize (PrintIterator iter) {
      return iter.minimumSize ();
    }
  },
  /** Compute the preferred size. */
  PREFERRED {
    @Override
    Point computeSize (PrintIterator iter) {
      return iter.preferredSize ();
    }
  };
  abstract Point computeSize (PrintIterator print);
}