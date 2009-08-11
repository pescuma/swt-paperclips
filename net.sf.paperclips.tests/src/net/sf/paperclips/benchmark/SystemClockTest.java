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
package net.sf.paperclips.benchmark;

import junit.framework.TestCase;

public class SystemClockTest extends TestCase {
	public void testGetTime() {
		final Clock clock = new SystemClock();

		long before = System.currentTimeMillis();
		long clockTime = clock.getTime();
		long after = System.currentTimeMillis();
		assertTrue(before <= clockTime && clockTime <= after);
	}
}
