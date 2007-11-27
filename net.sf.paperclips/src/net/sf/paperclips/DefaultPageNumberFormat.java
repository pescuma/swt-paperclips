/************************************************************************************************************
 * Copyright (c) 2006 Woodcraft Mill & Cabinet Corporation. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Woodcraft Mill & Cabinet Corporation - initial API and implementation
 ***********************************************************************************************************/
package net.sf.paperclips;

import net.sf.paperclips.internal.EqualsUtil;

/**
 * The default PageNumberFormat used by PageNumberPrints.
 * <p>
 * This class formats page numbers as "Page x of y".
 * @author Matthew Hall
 */
public final class DefaultPageNumberFormat implements PageNumberFormat {
  public String format( PageNumber pageNumber ) {
    return "Page " + ( pageNumber.getPageNumber() + 1 ) + " of " + pageNumber.getPageCount();
  }

  public boolean equals( Object obj ) {
    return EqualsUtil.sameClass( this, obj );
  }
}