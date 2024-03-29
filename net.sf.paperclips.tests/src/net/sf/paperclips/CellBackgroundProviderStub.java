/*
 * Copyright (c) 2007 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package net.sf.paperclips;

import net.sf.paperclips.internal.Util;

import org.eclipse.swt.graphics.RGB;

class CellBackgroundProviderStub implements CellBackgroundProvider {
	public boolean equals(Object obj) {
		return Util.sameClass(this, obj);
	}

	public RGB getCellBackground(int row, int column, int colspan) {
		return null;
	}
}