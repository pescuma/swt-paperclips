**Q**: Why does PaperClips throw an exception when I try to print?

**A**: This happens whenever you're trying to print something that's too big to fit on paper the way you've designed it. This occurs most commonly with GridPrints with preferred width columns, instead of default width. Some options:
  * Look through your GridPrint constructor calls and make sure you set up your columns using "d", "def", or "default" size instead of "p", "pref", or "preferred" size. Default size columns are the same as preferred, but will shrink down to their minimum size if there's not enough room on paper. A preferred column will **not** shrink, which is a problem when you put a TextPrint into that column that has a really, really long string with no line breaks.
  * Wrap the offending Print in a BigPrint, which breaks really huge prints across pages.
  * Wrap the offending Print in a ScalePrint, which automatically scales a print down to fit on the page.

---

**Q**: How do I show the printer dialog?

**A**: Assuming the following variables, you can show the print dialog (and then print if the user chooses to) as follows:
  * Shell shell
  * Print print
  * String jobName
  * int margin (expressed in points, where 72 points = 1")
```
PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
PrinterData printerData = dialog.open ();
if (printerData != null) {
 PrintJob job = new PrintJob(jobName, print).setMargins(margins);
 PaperClips.print(job, printerData);
}
```

---

**Q**: Does PaperClips work on Linux?

**A**: Theoretically PaperClips works everywhere that SWT does. However there are SWT implementations that do not implement SWT's printing API as advertised. The SWT port on Linux/GTK does not support printing (since GTK provided no printing API for SWT to use until just recently). There may be other ports where SWT printing is not supported, but I have not heard of any others.

**UPDATE**: As of the 2006-07-17 SWT integration build, SWT supports printing on GTK+ version 2.10.0 or later. This feature was just recently added and should be considered experimental until SWT 3.3 release.

---

**Q**: Does PaperClips support the TableWrapLayout from the org.eclipse.ui.forms package?

**A**: Use the GridPrint, it is very similar to a TableWrapLayout (both adhere to the W3C recommendations on layout rendering).

---

**Q**: Is it possible to change the color of a Font?

**A**: Yes:
```
TextPrint textPrint = new TextPrint( "My white sentence" );
textPrint.setRGB( new RGB( 255, 255, 255 ) );
```

---

**Q**: How do I print a headercell with a dark background that fills up the entire page width?

**A1**: Create a GridPrint that has its first column set to be greedy on its width: GridPrint grid = new GridPrint( "p:g"); And use the following method
to create a header:
```
private GridPrint createHeader( String text )
{
 DefaultGridLook headerLook = new DefaultGridLook();
 LineBorder lineBorder = new LineBorder();
 headerLook.setCellBorder(lineBorder);
 headerLook.setBodyBackground(new RGB (200, 200, 200));

 GridPrint gridHeader = new GridPrint("p:g", headerLook);
 gridHeader.add( new TextPrint( text, GridPrint.REMAINDER ) );
 return gridHeader;
} 
```
Now add the header to your gridprint: grid.add( createHeader( "MyHeader" ), GridPrint.REMAINDER );
  * _The use of preferred size columns is discouraged in the absence of a specific need for it. You are almost always better off with a default size column. -- MatthewHall_

**A2**: If you're trying to add repeating headers to a GridPrint, use the addHeader(...) methods. DefaultGridLook has separate background color properties for the header, body, and footer:
```
DefaultGridLook look = new DefaultGridLook();
look.setHeaderBackground(new RGB(0x40, 0x40, 0x40)); // dark gray

GridPrint grid = new GridPrint("d, d:g, d", look); // <--- you can pass the GridLook in to the GridPrint constructor.
grid.addHeader(new TextPrint("the header row gets printed on every page!"), GridPrint.REMAINDER);

for (Item item : itemsToPrint) {
 grid.add(new TextPrint(item.toString()));
 // etc..
} 
```

**A3**: Another way to add a header is to use a PagePrint. PagePrint adds headers and/or footers to every page of a document, with the capability to print page numbers. See Snippet6 on the [[Snippets](Snippets.md)] topic for sample code.

---

**Q**: How do you install this as a plugin inside of eclipse? (i.e. what steps are required). Following proper installation, how do you use it from within eclipse? (i.e is there a menu somewhere to set line numbers, etc?).

**A**: The only install step necessary is to put the net.sf.paperclips_[version](version.md).jar file in the plugins folder of your PDE's target environment (although you might have to reload the target environment for PDE to notice it).  The SWT plug-in must be present at runtime._

As far as using PaperClips from within Eclipse goes, it's important to understand that PaperClips is a library for you to use in your code, not a plug-in that contributes perspectives or views or editors. PaperClips is for printing things in your own programs; it does not contribute anything back to the Eclipse workbench.

Personally I develop Eclipse applications using the PDE, and add net.sf.paperclips as a dependency for my plugins that require printing capabilities. I hope this answers your question. -- MatthewHall

---

**Q**: Why isn't PaperClips spelled with a trailing 'e' (PaperClips\*e**) like other Eclipse plugins?**

**A**: Basically I thought that would have been just slightly too trendy. 

&lt;soapbox&gt;

Open-source project naming in general is starting to get trendy and elitist. Recursive acronyms are a perfect example (e.g. "GNU's Not Unix"); they were mildly funny at first, but it's been done to death.

&lt;/soapbox&gt;

 "PaperClips" seemed to have just the right ring, vaguely conveying the project's purpose, but without feeling like an Eclipse fanboy. -- MatthewHall

---

**Q**: My print job is taking a really long time to print. What do I do?

**A**: A possible reason is that you are rotating image content. I've had reports of slow printing when printing very large images and using either a RotatePrint, or setting PrintJob.setOrientation.

I've had one user who reported that rotating the ImageData by hand (and then _not_ wrapping the ImagePrint in a RotatePrint) resulted in a dramatic speed improvement for his print job.