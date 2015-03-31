It may be helpful to have the [API javadocs](http://swt-paperclips.googlecode.com/files/net.sf.paperclips_1.0.2_javadoc.zip) open in another window during the tutorial.

In PaperClips, any object that implements the Print interface can be printed. In this tutorial and in the javadocs, these objects are referred to simply as "prints." By convention, every print's class name ends in "Print", e.g. TextPrint for text, ImagePrint for images.

Let's start with the obligatory "Hello" example, which prints the words "Hello PaperClips!"  First we must create a Print of our document (a TextPrint in this case).  Once we have the main document, we create a PrintJob and send it to the PaperClips class for printing.

```
// Create the document
TextPrint text = new TextPrint("Hello PaperClips!");

// Print it to the default printer (no prompt). The print job
// name in the printer status window will be "TutorialExample1".
PaperClips.print(new PrintJob("TutorialExample1", text), new PrinterData());
```

The above code results in the following printout: [pdf/TutorialExample1.pdf TutorialExample1.pdf]

To print more complicated documents, we create all the pieces and put them together. Then we create a PrintJob and pass it to the PaperClips class like before.

Let's print something a little more complicated:

```
              VITAL STATISTICS              
--------------------------------------------
Name:       Matthew Hall
Occupation: Programmer
Eyes:       Blue
Gender:     Male
Spouse:     Sexy
--------------------------------------------
```

We'll use a GridPrint for layout, TextPrint for text, and LinePrint for the horizontal rules. GridPrint is a composite print which arranges its children in a grid. The GridPrint constructor accepts a comma-delimited String which defines how each column in the grid behaves. Those who have used the [FormLayout](http://www.jgoodies.com/freeware/forms/index.html) layout manager by Karsten Lentzsch ([JGoodies.com](http://www.jgoodies.com)) will be familiar with the column layout format.

```
// Create a grid with the following columns:
// Column 1: default width
// Column 2: default width, grows to fill excess width
// The second argument is the grid look. The look is a pluggable grid appearance.
// Here we use a DefaultGridLook, configured with 5 point horizontal and vertical
// cell spacing. 72 points = 1".

GridPrint grid = new GridPrint("d, d:g", new DefaultGridLook(5, 5));

// Now populate the grid with the text and lines
// (GridPrint.REMAINDER is a special column span value
grid.add(new TextPrint("VITAL STATISTICS"), GridPrint.REMAINDER, SWT.CENTER);

grid.add(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);

grid.add(new TextPrint("Name:")); grid.add(new TextPrint("Matthew Hall"));
grid.add(new TextPrint("Occupation:")); grid.add(new TextPrint("Programmer"));
grid.add(new TextPrint("Eyes:")); grid.add(new TextPrint("Blue"));
grid.add(new TextPrint("Gender:")); grid.add(new TextPrint("Male"));
grid.add(new TextPrint("Spouse:")); grid.add(new TextPrint("Sexy"));

grid.add(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);

PrintJob job = new PrintJob("TutorialExample2.java", grid);
PaperClips.print(job, new PrinterData());
```

The above code results in the following printout: [pdf/TutorialExample2.pdf TutorialExample2.pdf]

This should give you a general idea of what PaperClips can do for you, and how simple it can be to create and print documents. See the API documentation on the classes below for further information.
  * BorderPrint - wraps a print with a border.
    * See also Border interface.
  * ColumnPrint - splits the wrapped print into columns
  * EmptyPrint - takes up space (these can be handy in [GridPrint](GridPrint.md)s
  * GridPrint - arranges its child prints in a grid. You will use this one a lot.
  * LinePrint - for horizontal rules.
  * PagePrint - decorates a print with page headers and footers, with page numbering capability.
  * PageNumberPrint - for printing the page number. Use this class inside the header or footer of a PagePrint.
  * SeriesPrint - prints each of its children on separate pages.
  * TextPrint - for text.

Also, check out the GridPrint for a detailed description of how to use GridPrint. You'll need to learn all of it's features sooner or later!

Don't forget: PaperClips is extensible, so you can write and use your own Print classes. PaperClips can print any object that correctly implements the Print interface.  Read the javadocs for the Print, PrintIterator, and PrintPiece interfaces to get started.