**10 January 2007 -- PaperClips 1.0 released**
The 1.0 release is finally ready!  Features / fixes in this release:
  * Vertical cell alignment in GridPrint, including SWT.FILL alignment to allow embedding vertically greedy prints like SWT.VERTICAL LinePrints.
  * SidewaysPrint - a non-greedy version of RotatePrint.  Very handy for putting sideways text in grids.
  * TextPrint's and PageNumberPrint's horizontally greedy behavior had to be removed in order for SidewaysPrint to work correctly.  This will not cause any compilation problems, however you may see some unexpected layout behavior.
  * Updated Snippet7 (print preview example) in examples plug-in to clean up some of the odd behavior when resizing the window in Fit-to-Width or Fit-to-Height mode.  Also added code for scrolling around the page using mouse drag.
  * Bug fix: PaperClips.getPages() throws the wrong exception when a document fails to lay out properly.
  * Bug fix: PrintPreview does not redraw when setScale is called to change the display scale.
  * Bug fix: Nothing displays after the PageNumberPrint a PagePrints headers/footers

---

**9 January 2007 -- Knock 'em down..**
While working with PaperClips today I came across a bug in PagePrint that some of you might bump into: if you use a GridPrint for your header or footer, and that grid contains a PageNumberPrint, nothing in the rows after the PageNumberPrint will be displayed in your header or footer.  As of SVN [revision 227](https://code.google.com/p/swt-paperclips/source/detail?r=227) this has been fixed.

Many thanks to Daniel Spiewak for featuring the PaperClips project on [dzone.com](http://www.dzone.com) this week!  Looking at the download stats, there was a slight anomaly on Wednesday (something like 5 times the usual download volume).  Thanks for helping spread the word!

I'm finalizing things for the 1.0 release, hopefully it will be ready for download tomorrow morning.  Of course, you could always get the code from [Subversion](Subversion.md) for the latest and greatest.

---

**29 November 2006 -- Broken links**
Just discovered that the links to the Java [Snippets](Snippets.md) were broken. Sorry about that! All the links should work now.

---

**30 October 2006 -- Suggestion Box**
At this point PaperClips does pretty much everything I need it to do! I don't want the project to stagnate, and I'm sure there are other use cases outside of my line of work that you could think of. If there is some feature that you'd really like to see added to PaperClips, please submit a support request through the [SourceForge Tracker](http://sourceforge.net/tracker/?func=add&group_id=148509&atid=771873). -- MatthewHall

---

**19 October 2006 -- PaperClips 0.6.1 Released**
Features and changes in this release:
  * PrintPreview control:
    * The computeSize method is now implemented properly, which helps determine the proper sizing of the control depending on the viewing scale. Snippet7 has been updated to demonstrate this feature.
    * The performance problems (read: major lags) when zooming in very close are resolved. Go ahead, zoom to ridiculous levels with confidence! We promise not to tell anyone.
    * Bugfix: page disappears after a call to setPrinterData.
  * ColumnPrint behavior of compressing the last page of content to the minimum possible height can now be disabled using the "compressed" property.
  * GridPrint now has addColumn and addColumns methods supporting column modifications after construction. Some of the snippets were modified to use this new API.

---

**6 October 2006 -- Status Update**
The PrintPreview control has been updated to implement Control.computeSize.  Snippet7 (see [Snippets](Snippets.md)) has been likewise updated to demonstrate how to use it in conjunction with PrintPreview's fitHorizontal, fitVertical, and scale properties.  If you need the ability to zoom in and out on the page, and to scroll around, get this update from [Subversion](Subversion.md).

One drawback right now is that the control really chokes if you zoom in too close.  The PrintPreview control works by capturing the printer output to an image, then rendering the image on the display.  Currently it allocates those images based on the control size, so it becomes quite resource intensive when you zoom in (i.e. the control gets really huge).  I will try to address this in the coming days.

---

**26 September 2006 -- Status Update**
The GridPrint tutorial is ready for the masses.

---

**25 September 2006 -- PaperClips 0.6.0 Released**

**Note: This release is API incompatible with previous releases. The UI controls have been moved to a different package, and to a different plugin (net.sf.paperclips.ui).** A simple search and replace should suffice to fix compilation problems. I try to minimize changing APIs on everybody, I sincerely hope this doesn't cause a big problem. However I believe that the improved clarity of the API will pay dividends in the long run.

Features and changes in this release:
  * JDK 1.4 compliance.
  * New WYSIWYG (what you see is what you get) PrintPreview control in the net.sf.paperclips.ui plugin.
  * Other existing UI controls (in the net.sf.paperclips.swt package) have been moved to the net.sf.paperclips.ui, into the net.sf.paperclips.ui package.
  * The PrintUtil class has been replaced with the PaperClips and PrintJob classes.
  * The new PrintJob class holds information about the job name, document, page margins, and paper orientation.
  * The new PaperClips class provides a simpler API for printing documents.
  * The new Margins class provides fine control over margins on each edge of the paper.
  * Deprecated package net.sf.paperclips.preview has been removed.

---

**23 September 2006 -- Upcoming 0.6.0 Release**
Release 0.6.0 is coming up, and will have lots of changes:
  * The functionality in PrintUtil has been moved to the PaperClips class. PrintUtil methods have been deprecated but are still functional for now. Most methods now accept a PrintJob argument, which helps replace a lot of those overloaded methods in PrintUtil.
  * The PrintJob class can be used to finely control margins and paper orientation.
  * The PrintViewer and PrintPieceCanvas classes were changed from package net.sf.paperclips.swt to net.sf.paperclips.ui (and moved into the net.sf.paperclips.ui plugin)
  * A new PrintPreview (a true WYSIWYG preview) control is almost ready. Snippet 7 demonstrates using the print preview.
  * Java 1.4 compatibility
Because of the package renaming mentioned, 0.6.0 will not be API compatible with previous releases. However current code can be updated with a simple search and replace. I try to avoid doing this but it's important to me that the API's "feel" right before we get to version 1.0.

---

**5 September 2006 -- Status Update**
I'm aware that some people are not able to use PaperClips because of the Java 5.0 requirement. Looking over the project code, this requirement is really not necessary. The majority of the 5.0 code is using enhanced-for loops and a bit of generics here and there. I have been removing the 5.0 code segments and replacing them with 1.4 compatible code over the past few days, and will release them as 0.5.5 as soon as they have been adequately tested. Users who live on the edge are invited to check out the latest from [Subversion](Subversion.md) and help test PaperClips on their projects.

Somebody left a note in the News topic a few days ago, "WHY CAN I EDIT THIS??" You can because the PaperClips home page is a wiki. I intentionally made this site world-editable to encourage community participation and discussion. So far most edits have been accidental (the 'Edit' button is so hypnotic), but some users have added some helpful hints the FrequentlyAskedQuestions topic.

---

**18 August 2006 -- PaperClips 0.5.4 Released**
This maintenance addresses a GridPrint bug, and adds a few minor features:
  * Bugfix: GridPrint fails to generate last page if the final row of content finishes on previous page, but there was only enough room to print an open bottom border. (This bug was discovered trying to run Snippet4)
  * Added SimplePageDecoration, a simple wrapper for static page headers and footers. This class can be used in lieu of creating a custom PageDecoration class for headers and footers.
  * The DefaultPageNumberFormat class (the default PageNumberFormat class used by PageNumberPrint) is now a top-level, public class.
  * Added SimplePageDecoration class, a PageDecoration which displays a Print you provide on each page.

---

**17 July 2006 -- PaperClips 0.5.3 Released**
This release fixes a semantic error introduced in 0.5.3:
  * TextPrint - was modified to be horizontally greedy in version 0.5.2. This behavior is not appropriate for left-aligned text (the default alignment), as it effectively negates the column alignment in a GridPrint cells. Therefore as of 0.5.3, center- and right-aligned text is horizontally greedy, whereas left-aligned text is not.

---

**10 July 2006 -- PaperClips 0.5.2 Released**
This release adds some features and fixes a few bugs:
  * CellBackgroundProvider interface - an interface for programmatic control of the background color in each grid cell. Default implementation is provided in the DefaultBackgroundProvider class.
  * DefaultGridLook - now supports control of the header, body, and footer background colors using either setHeaderBackground(RGB) or setHeaderBackgroundProvider(CellBackgroundProvider).
  * Prints with greedy layout behavior now have documentation indicating the behavior in the javadocs.
  * Bugfix to GridPrint which sometimes cause grids to overlap with other prints.

---

**8 June 2006 -- PaperClips 0.5.1 Released**
  * Fixed NullPointerException in GridPrint, which happened whenever the cells in a row did not all finish on the same page.
  * Added net.sf.paperclips.decorator package to exported package list in plugin descriptor (only affects those using PaperClips as an Eclipse plugin).

---

**2 June 2006 -- PaperClips 0.5.0 Released**
  * ScalePrint wrapper scales a print down to fit on the page, or scales larger or smaller depending on a scaling factor.
  * RotatePrint rotates the target by 90, 180, or 270 degrees. Since SWT doesn't provide API for setting the page orientation, this will be very useful for landscape layouts.
  * BackgroundPrint decorates a print with a background color.
  * BigPrint wrapper splits it's target across multiple pages if it's too large to fit on one. Use this if you have grids with lots of columns that won't print.
  * Decorator package (net.sf.paperclips.decorator) allows you to create decorator factories which can apply decorations to prints without explicitly calling the decorator print's constructor. Provided you use the decorator uniformly throughout your document, the style of the document can be changed by substituting another decorator.
  * TextPrint default font is the system default font instead of hard-coded "Times" font.
  * GridPrint got a massive overhaul.
    * Can now add headers and footers that repeat on every page through the addHeader(...) and addFooter(...) methods (they work just like the add(...) methods).
    * Configure the appearance of your GridPrint using the GridLook interface. A default look, DefaultGridLook, lets you configure the cell spacing, cell border, and background color of grid cells (separate color for header, body, and footer cells).
    * Public fields horizontalSpacing and verticalSpacing were deprecated.

---

**24 April 2006 -- Active Development**
  * Added BackgroundColorPrint, a print that draws a background color behind it's target. Adding example snippet Snippet2.java to demonstrate usage. Will be included in 0.5.0 release.
  * Added net.sf.paperclips.decorator package. This package simplifies using wrapper prints like BorderPrint and BackgroundColorPrint by allowing you to define the decoration once, and apply it uniformly to any number of prints (without having to explicitly call the constructor of the decoration). Example snippets to follow. Will be included in 0.5.0 release.

---

**19 April 2006 -- Status Update**
  * Added a snippets section to the web site.
  * Added Snippet1.java, demonstrating how to print the contents of a SWT Table widget.

---

**11 April 2006 -- Active Development**
  * Bugfix: renamed PrintPreview to PrintViewer (deprecated under the old name) to better reflect it's purpose: to view a print's contents, not to preview what it will look like on the printer.

---

**10 April 2006 -- Active Development**
  * Moved all the to-do items from the RoadMap topic to the sourceforge.net trackers. To submit a bug report, support request, or feature request, click the appropriate link in the lower section of the main menu.

---

**4 April 2006 -- PaperClips 0.4.3 Released**
  * Bugfix: [GridPrint](http://paperclips.sourceforge.net/api/net/sf/paperclips/GridPrint.html) would sometimes print larger than the available space when multiple columns have to shrink smaller than their minimum sizes
Other news:
  * Fixed (read: completely rebuilt) the web site so it displays correctly in Internet Explorer.

---

**23 March 2006 -- New Web Site Launched**
  * Converted web site to a database-driven wiki.  Just double-click on a topic to edit it, or click "new tiddler" to create your own topics.  Hopefully this will allow people to participate more.
  * Found a bug in [GridPrint](http://paperclips.sourceforge.net/api/net/sf/paperclips/GridPrint.html) that caused the grid to print larger than the available space when multiple columns have to shrink smaller than their minimum sizes.  Committed a fix to SVN trunk, will review for a day or so before doing a maintenance release.

---

**20 March 2006 -- PaperClips 0.4.2 Released**
This release adds Print classes for controlling page breaks and column breaks.

Changes in this release:
  * [NoBreakPrint](http://paperclips.sourceforge.net/api/net/sf/paperclips/NoBreakPrint.html) is a wrapper which prevents it's target print from being broken up between pages (or columns).
  * [BreakPrint](http://paperclips.sourceforge.net/api/net/sf/paperclips/BreakPrint.html) adds a page break or column break.
  * PaperClips sources are now version controlled through [Subversion](Subversion.md) (finally!)

---

**5 January 2006 -- PaperClips 0.4.1 Released**

This is a maintenance release to address a bug discovered shortly after the 0.4.0 release: if a GridPrint is configured with a cell border and added to a ColumnPrint, an infinite loop will occur when attempting to print.

Changes in the 0.4.0 release:
  * All public API are documented in javadocs.
  * Custom cell borders in GridPrint - uses the same Border interface as BorderPrint.