==================================
PaperClips UI 1.0.0
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

Welcome to PaperClips UI.  Currently this project contains 2 main widgets for
you to use to display documents: PrintPreview (a true WYSIWYG preview) and
PrintViewer, a scrollable on-screen viewer.

Requirements:
* Java 1.4 or later.
* SWT 3.2 or later.  SWT may be downloaded at http://www.eclipse.org/swt/.
* The net.sf.paperclips plugin must be available in the classpath (for regular
  java apps) or in the plugins folder of your target platform (for Eclipse
  apps).

Installation
------------

* Ensure that the net.sf.paperclips plugin is available on the target platform
  or as a plug-in project in your workspace.
* Extract the project files to an empty folder.
* Open Eclipse.
* Select File->Import
* Select Plugin Development->Plug-ins and Fragments and click Next.
* Select the folder you extracted the project files into as the plug-in
  location.  You may have to uncheck "The target platform" first.
* Under "Import As", select "Project with source folders" and click Next.
* Add the net.sf.paperclips.ui plugin to the import list.
* Click Finish

Credits
-------

PaperClips development team:
    Matthew Hall (Woodcraft Mill & Cabinet) - Developer / Project Admin
