Welcome to PaperClips: a simple, light weight, extensible Java printing plug-in for [SWT](SWT.md).  PaperClips hides the complexity of laying out and rendering documents on the printer, helping you focus on _what_ to print instead of _how_ to print it.

In a nutshell, PaperClips provides an assortment of document "building blocks," which you can tweak and combine to form a custom document. The assembled document is then sent to PaperClips for printing.  PaperClips includes support for printing text, images, borders, headers and footers, column layouts and grid layouts, to name a few.  It can also be extended with your own printable classes.

With PaperClips you do not have to track cursors, calculate line breaking, fool around with font metrics, or manage system resources--it's all handled internally.  And unlike report-generation tools, you are not constrained to a predefined document structure (like report bands).  Every document is custom and the layout is up to you.

Requirements:
  * Java 1.4 or later.
  * SWT 3.2 or later. SWT may be downloaded at http://www.eclipse.org/swt/.

PaperClips can be used as an Eclipse plug-in or as a regular Jar (provided the SWT libraries are on the classpath).