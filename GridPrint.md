GridPrint is a powerful Print class which lays out its children in a grid.  It is highly customizable, allows for fine control over alignment behavior, column sizing, column spans, as well as a pluggable "grid look" feature.

You may want to follow the BeginnerTutorial before proceeding with the GridPrint tutorial.

---

# Defining Your Grid #
The first step in using a GridPrint is to set up the grid's columns in the constructor.  This information can be in the form of a GridColumn [ ] array or as a String containing a comma-delimited list of column definitions.  If you've used Karsten Lentzsch's (of [JGoodies.com](http://www.jgoodies.com) fame) FormLayout layout manager before, you'll be right at home with the column definition format in GridPrint.  Each column is defined using the following format (square braces indicate optional settings):
```
[alignment:]size[:weight]
```
The parsing is case-insensitive so you can give column definitions in upper- or lower-case.  The alignment and weight are optional; only the column size is required.

## Column Sizing ##
```
size = D | DEF | DEFAULT |
       P | PREF | PREFERRED |
       [Positive number expressed in points (72 points = 1")]
```
The column sizes deserve an explanation.  A default width column will be displayed at a width somewhere between its minimum and preferred size.  If multiple default width columns are present, they are weighted according to the W3C recommendation on table layout, to balance the amount of line wrapping that has to happen in each cell.

A preferred size column is displayed at it's preferred width.  For a TextPrint, this is the longest single line of text (in terms of pixel width).  It is not a good idea to put long lines of text in a preferred size column.  In almost all cases it is preferable to use default size instead of preferred size.  Sometimes preferred size is useful, for example small labels that look better without line wrapping.

The last way to define a column size is to give an explicit number, which is interpreted in points.  72 points equals 1 inch (this is documented everywhere in the javadocs).  An explicitly sized column will always be displayed at the size you specify.

Actually, preferred and explicit sized columns can be smaller than those sizes if absolutely necessary.  This sometimes happens if you have enough columns in the grid that there simply isn't enough room to fit everything the way you've asked for.  GridPrint will automatically shrink those columns if possible to make everything fit on the page.

## Column Alignment ##
```
alignment = L | LEFT |
            C | CENTER |
            R | RIGHT
```
Each column can be given a default alignment.  If you leave out the alignment, it default to left.  When you add prints to a grid, they take the alignment of the column they are placed in (unless you override the default alignment).  Some prints, notably TextPrint, have alignment properties.  In order to make sure the alignment is applied properly, the same alignment should be applied to the Print as the column it is inserted into.  The reasons for this are complicated and way too boring to be included here.

## Column Weight ##
```
weight = N | NONE |
         G | GROW | G(#) | GROW(#)
```
When a grid is laid out on the page, it may not take up the entire width of the page.  You can force a grid to fill the entire width by configuring one or more columns with a "weight."  By default, columns have a weight of 0.  You can change the weight to 1 by adding :G or :GROW to the end of the column definition.  Custom weight values can be specified by putting the weight in parentheses, e.g. :G(2) or :GROW(2).  When columns are laid out on the page, each column receives a portion of that extra width according to it's portion of the total weight.  For example, if column0.weight = 1, column1.weight = 1, and column2.weight = 2, then column0 and column1 will each receive 25% of the extra width, and column2 will receive 50% of the extra width.

## Putting It Together ##
The following are some sample column definitions, created by combining the required size property with the optional alignment and weight properties.
```
LEFT:DEFAULT:GROW // left-aligned, default size, weight=1
R:72:N            // right-aligned, 72 points (1") wide, weight=0
right:72          // identical to previous line
c:pref:none       // center-aligned, preferred size, weight=0
p                 // left-aligned (default), preferred size, weight=0
```

The String passed to the GridPrint constructor is a comma-delimited list of column definitions.  These can be very concise if you use the one-letter versions of each modifier:
```
GridPrint grid = new GridPrint("r:p, d:g, c:36, c:36");
```
We just created a grid with a right-aligned preferred size column, following by a default size column which grows to fill the extra width on the page, followed by two center-aligned columns which are 36 points (half an inch) wide.

---

# Adding Content #
Now we get to the meat and potatoes: adding content to the grid.  GridPrint has several methods for adding contents:
```
public void add(Print print)
public void add(Print print, int colspan)
public void add(int hAlign, Print print)
public void add(int hAlign, Print print, int colspan)
public void add(int hAlign, int vAlign, Print print)
public void add(int hAlign, int vAlign, Print print, int colspan)
```
With GridPrint you add child prints from left to right, row by row.  Each successive print added to the grid goes in the column right after the previous print (or in the first column of the next line).
```
GridPrint grid = new GridPrint("p, d:g");
grid.add(new TextPrint("Chef:")); grid.add(new TextPrint("Hello children!"));
grid.add(new TextPrint("Kids:")); grid.add(new TextPrint("Hey Chef."));
grid.add(new TextPrint("Chef:")); grid.add(new TextPrint("How's it goin'?"));
grid.add(new TextPrint("Kids:")); grid.add(new TextPrint("Bad.."));
// ... the rest of the episode
```
By default each print occupies a single column; however you can override this by supplying an alternate column span in the colspan parameter.  A special colspan value, GridPrint.REMAINDER, causes the print to take up the rest of the row.  A print that spans multiple columns by default takes the alignment of the first column it occupies.

You can provide an alternate alignment for the print before the print parameter.  Possible values for this parameter are SWT.DEFAULT (which just follows the default alignment of the column), SWT.LEFT, SWT.CENTER, and SWT.RIGHT.
```
grid.add(SWT.CENTER, new TextPrint("(Chef looks concerned)"), GridPrint.REMAINDER);
grid.add(new TextPrint("Chef:")); grid.add(new TextPrint("Why bad?"));
```

---

# Headers and Footers #
GridPrint supports headers and footers which repeat on each page.  They are added to the grid in much the same way the regular grid contents are added, the only difference being the method name:
```
public void addHeader(Print print)
public void addHeader(Print print, int colspan)
public void addHeader(int hAlign, Print print)
public void addHeader(int hAlign, Print print, int colspan)
public void addHeader(int hAlign, int vAlign, Print print)
public void addHeader(int hAlign, int vAlign, Print print, int colspan)

public void addFooter(Print print)
public void addFooter(Print print, int colspan)
public void addFooter(int hAlign, Print print)
public void addFooter(int hAlign, Print print, int colspan)
public void addFooter(int hAlign, int vAlign, Print print)
public void addFooter(int hAlign, int vAlign, Print print, int colspan)
```
Continuing our example, we will add headers to each column identifying the column contents, footers to identify the copyright holders, and horizontal lines to provide a clean separation between headers and footers and the main content.
```
grid.addHeader(SWT.CENTER, new TextPrint("Character"));
grid.addHeader(SWT.CENTER, new TextPrint("Line"));
grid.addHeader(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);

grid.addFooter(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);
grid.addFooter(SWT.CENTER, new TextPrint("Copyright (c) Eric Cartman LTD"), GridPrint.REMAINDER);
```

---

# Customizing the Look #
Each GridPrint can be customized with a GridLook which controls the appearance of the grid.  A single GridLook implementation, DefaultGridLook, is provided with PaperClips.  DefaultGridLook lets you customize cell spacing, cell borders, and the background color behind each cell.

DefaultGridLook has a constructor which sets the cell spacing to an integer argument, expressed in points (again, 72 points = 1").
```
// Grid cells will be spaced apart 9 points horizontally (1/8")
// and 5 points vertically (approx. 1/16")
DefaultGridLook look = new DefaultGridLook(9, 5);
```
You have to set the grid's look to the GridLook we just created for the appearance to take effect.
```
grid.setLook(look);
```
You could also provide the GridLook as a second parameter back in the GridPrint constructor:
```
GridPrint grid = new GridPrint("p, d:g", look);
```
We can configure the grid to display a border around each cell by setting the border attribute on the DefaultGridLook
```
look.setCellBorder(new LineBorder());
```
However, if we print that it looks really ugly, so let's try cell shading instead.  Let's set the headers and footers to display a slightly off-white background:
```
RGB rgb = new RGB(0xF0, 0xF0, 0xF0);
look.setHeaderBackground(rgb);
look.setFooterBackground(rgb);
```
This is fine for setting all cells to the same color.  However, if we want the cell colors to be more dynamic, we must use a CellBackgroundProvider.  In this example, we'll set even numbered rows to light blue, and odd numbered rows to light yellow.  (Remember that the first row is number 0, making it an even numbered row.)
```
look.setBodyBackgroundProvider(new CellBackgroundProvider() {
  RGB evenRows = new RGB(0xA0, 0xA0, 0xFF); // light blue
  RGB oddRows = new RGB(0xA0, 0xFF, 0xFF); // light yellow
  public RGB getCellBackground(int row, int column, int colspan) {
    return (row % 2 == 0) ? evenRows : oddRows;
  }
});
```

---

# Conclusion #
Hopefully this tutorial will help you get started with GridPrint.  It is really quite simple once you get the hang of it.  If you have any questions or comments, please contact me through one of the support links on the left hand menu.