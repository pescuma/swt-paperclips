==================================
PaperClips 0.1
http://paperclips.sourceforge.net/
==================================

Copyright (c) 2005 Woodcraft Mill & Cabinet Corporation and others.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

A copy is found in the file epl-v10.html distributed in this package.

Contributors:
  Woodcraft Mill & Cabinet Corporation - initial API and implementation
  (matthall@woodcraftmill.com)

This copyright notice MUST APPEAR in all copies of the file!

Introduction
------------

PaperClips is a printing abstraction layer written in Java, using SWT as the
graphic library.  PaperClips simplifies the task of printing complex documents
by encapsulating common document elements (such as text and images) into a
light-weight API, allowing you to focus on the content and organization of
your documents.  The underlying mechanics of printing your document are
handled internally.

See the API javadocs in doc/api/index.html.

Sample programs are provided in the doc/examples folder.

System Requirements
-------------------

* Java 2 Runtime Environment 1.5 or higher
* Eclipse SWT 3.1 (earlier releases may be compatible)

Installation
------------

Put the paperclips_{version}.jar file in your project classpath.

SWT must be installed on your system, and the Jar file must be available to
PaperClips in your project classpath.
SWT may be downloaded at http://www.eclipse.org/swt/

Compiling the Source Code
-------------------------

A build.xml file is in the install directory. If you have installed ANT
(http://ant.apache.org) go to the install directory und run "ant".

Credits
-------

PaperClips development team:
    Matthew Hall (Woodcraft Mill & Cabinet) - Developer / Project Admin