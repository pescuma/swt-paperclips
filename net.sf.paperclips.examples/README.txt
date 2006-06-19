==================================
PaperClips Examples 0.5.2
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

Welcome to PaperClips Examples: a collection of code snippets demonstrating
how to use various features of the PaperClips API.  This project is intended
to be imported as a plugin project in your Eclipse IDE.

Requirements:
* Java 5.0 (code named "Tiger") or later.
* SWT 3.0 or later.  SWT 3.2M3 or later is required if you use the ScalePrint
  or RotatePrint classes.  PaperClips may work on earlier versions of SWT but
  I have not tested this. SWT may be downloaded at http://www.eclipse.org/swt/.
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
* Add the net.sf.paperclips.examples plugin to the import list.
* Click Finish

Credits
-------

PaperClips development team:
    Matthew Hall (Woodcraft Mill & Cabinet) - Developer / Project Admin
