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

import junit.framework.TestCase;

import org.eclipse.swt.SWT;

public class LayerEntryTest extends TestCase {
	public void testConstructor_invalidArguments() {
		try {
			new LayerEntry(null, SWT.LEFT);
			fail();
		} catch (IllegalArgumentException expected) {
		}

		assertEquals(SWT.LEFT, new LayerEntry(new PrintStub(), 0).align);
	}

	public void testEquals() {
		LayerEntry entry = new LayerEntry(new PrintStub(0), SWT.LEFT);
		assertEquals(entry, new LayerEntry(new PrintStub(0), SWT.LEFT));
		assertFalse(entry.equals(new LayerEntry(new PrintStub(1), SWT.LEFT)));
		assertFalse(entry.equals(new LayerEntry(new PrintStub(0), SWT.CENTER)));
	}
}
