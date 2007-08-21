/************************************************************************************************************
 * Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

/**
 * Interface for formatting a PageNumber instance into a printable string.
 * @author Matthew Hall
 */
public interface PageNumberFormat {
  /**
   * Returns a formatted String representing the pageNumber argument.
   * @param pageNumber the page number to be formatted into a String.
   * @return a formatted String representing the pageNumber argument.
   */
  public String format( PageNumber pageNumber );
}
