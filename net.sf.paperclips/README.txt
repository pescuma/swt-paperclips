==================================
PaperClips 0.5.1
http://paperclips.sourceforge.net/
==================================

Copyright (c) 2005-2006 Woodcraft Mill & Cabinet Corporation and others.  All
rights reserved. This program and the accompanying materials are made available
under the terms of the Eclipse Public License v1.0 which accompanies this
distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

A copy is found in the file epl-v10.html distributed in this package.

Contributors:
  Woodcraft Mill & Cabinet Corporation - initial API and implementation
  (matthall@woodcraftmill.com)

This copyright notice MUST APPEAR in all copies of the file!

Introduction
------------

Welcome to PaperClips: a simple, light weight, extensible Java printing
library for SWT. PaperClips hides the complexity of laying out and rendering
documents on the printer, helping you focus on what you want to print instead
of how you're going to print it.

In a nutshell, PaperClips provides an assortment of document "building
blocks", which you can tweak and combine to form a document. The assembled
document is then sent to PaperClips for printing. PaperClips includes support
for printing text, images, borders, headers and footers, column layouts and
grid layouts. PaperClips can be extended with your own printable objects.

With PaperClips you do not have to track cursors, calculate line breaking,
fool around with font metrics, or manage system resources--it's all handled
internally. And unlike report-generation tools, you are not constrained to a
predefined document structure.

Requirements:
* Java 5.0 (code named "Tiger") or later.
* SWT 3.0 or later.  SWT 3.2M3 or later is required if you use the ScalePrint
  or RotatePrint classes.  PaperClips may work on earlier versions of SWT but
  I have not tested this. SWT may be downloaded at http://www.eclipse.org/swt/.

Installation
------------

The PaperClips binary build jar can be used as a regular jar, or as an Eclipse
plugin.  If used as a regular jar, the SWT classes must be available in the
classpath.

To use PaperClips as a binary plugin, simply save the jar into the plugins
folder of your target platform.

If you are extending PaperClips (or you want the latest bleeding edge
features), your best bet is to checkout the project as a plugin project from
Subversion.

Use this URL to checkout PaperClips through Subversion.
https://svn.sourceforge.net/svnroot/paperclips/trunk/net.sf.paperclips/

See http://subversion.tigris.org/ for information about Subversion.

Credits
-------

PaperClips development team:
    Matthew Hall (Woodcraft Mill & Cabinet) - Developer / Project Admin
