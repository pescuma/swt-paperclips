**12 August 2009 -- PaperClips 1.0.4 released**
  * Bugs fixed:
    * [OSX](Mac.md) Disposing a PrintPreview sends a blank page to the printer.
    * Autoscaled ScalePrints were not computing minimum sizes correctly which distorted some layouts
    * PaperClips and PrintPreview now use the first system printer listed if no system default printer has been selected (can be the case on Linux, Mac)
    * Fixed compiler compliance levels so PaperClips can run in Java 1.4 JREs.
    * GridColumn's conversion of inches, millimeters and centimeters sizes to points was wrong, causing those columns to be displayed at the wrong size.
    * Fixed GridColumn's recognition of column sizes with a decimal part.
  * New Features:
    * Page numbers are internationalized in English, French and German.
    * TextStyle.create(String) creates a TextPrint
    * Added DebugPrint helper class for troubleshooting documents that won't print
  * Known issues:
    * [GTK](Linux.md) Printed text scales up or down depending on what DPI the screen is configured to
    * [GTK](Linux.md) When a PrintPreview changes printers, print jobs, or is disposed, a blank page is emitted on the printer.  This is due to missing API in GTK for cancelling a print job.

**3 April 2008 -- PaperClips 1.0.3 released**
  * Bugs fixed:
    * Fixed integer overflow error in GridPrint which sometimes caused printing of very long strings to fail.
    * Fixed column size distribution in GridPrint when paper space is scarce.
  * New Features
    * PrintPreview now supports lazy layout of print jobs.  This can be used to speed up previews of very large print jobs.
  * Changes:
    * Implement equals() and hashCode() in all Print objects (useful for unit testing)
    * Performance tuning in text and grid layouts
    * Snippets no longer implement Print.  This was an unnecessary detail and tends to confuse newcomers.
  * Known issues:
    * Linux: PrintPreview issues a page feed whenever a PrintPreview control is disposed or changes printers.  This is due to missing API in GTK for cancelling a print job.
[PaperClips 1.0.3](http://sourceforge.net/project/showfiles.php?group_id=148509|Download)

---

**2 November 2007 -- PaperClips 1.0.2 released**
  * Bugs fixed:
    * Clipping problems on Mac OS X.
    * PrintPreview.getPageCount() returns 0 before pages are first drawn.
    * PrintPreview spits out a blank page on Linux when the window is closed.
    * Changed ImageCaptureExample.java to capture JPG since PNG was not fully supported until SWT version 3.3 (PaperClips is developed against 3.2).
    * BorderPrint sometimes showed an open bottom border even though the target was completely shown.
    * PrintViewer performance improvements when print document is vertically greedy.
  * New Features:
    * GridPrint.setCellClippingEnabled() controls whether grid cells may be broken across pages.  See GridPrintCellClippingExample.java.
    * DefaultGridLook.setCellPadding()
    * PrintPreview.setHorizontalPageCount() and setVerticalPageCount() controls how many pages are shown on screen.
    * Experimental PaperClips.setDebug() API helps troubleshoot documents that won't lay out properly ("Unable to layout on page X" errors).
    * BasicGridLookPainter simplifies implementing custom GridLooks.
    * StyledTextPrint for mixing text with different font sizes, styles, colors and decorations.  Other printable objects such as ImagePrint may be embedded inline with the text.
    * TextPrint and StyledTextPrint support underline and strikeout text.
    * TextPrint.setWordSplitting() controls whether words may be split between rows.  This feature only applies when space is very limited.
    * Unified error reporting to PaperClips.error() methods. Custom Print implementations should use these methods to act uniformly with the rest of the library.
  * Enhanced print preview snippet (Snippet7):
    * Support scrolling with the mouse wheel (horizontally with Shift+Wheel)
    * Support zooming with Ctrl+Wheel
[Download PaperClips 1.0.2](http://sourceforge.net/project/showfiles.php?group_id=148509)

---

**8 March 2007 -- PaperClips 1.0.1 released**

This is a maintenance release to address printing issues on Mac OS X.  Many thanks to the all the Mac users at EclipseCon who graciously provided assistance in fixing this problem.

  * Resolved printing problems on Mac OS X.
  * Added public accessor APIs for all Print classes.

[Download PaperClips 1.0.1](http://sourceforge.net/project/showfiles.php?group_id=148509)

---

[[News|OldNews](Older.md)]