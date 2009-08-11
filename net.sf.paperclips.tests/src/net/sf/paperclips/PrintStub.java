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

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;

final class PrintStub implements Print {
	private int id;

	public PrintStub() {
		this(0);
	}

	public PrintStub(int id) {
		this.id = id;
	}

	public boolean equals(Object obj) {
		if (!Util.sameClass(this, obj))
			return false;

		PrintStub that = (PrintStub) obj;
		return this.id == that.id;
	}

	public PrintIterator iterator(Device device, GC gc) {
		return null;
	}
}