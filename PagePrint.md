The PagePrint class allows you to easily add page headers and footers to any document.  Page numbering is supported, and the page number format is customizable.

---

# Defining headers and footers using PageDecoration #

PagePrint decorates every page with a header and/or footer.  Headers and footers are instances of the PageDecoration interface, which you implement.  PageDecoration has only a single method:

```
public Print createPrint (PageNumber pageNumber);
```

For each page, PagePrint will call `createPrint(PageNumber)` on your decoration.  The Print returned by your PageDecoration will be used as the header or footer for that page.

An example page decoration:

```
public class MyPageDecoration implements PageDecoration {
  public Print createPrint (PageNumber pageNumber) {
    return new PageNumberPrint(pageNumber);
  }
}

PageDecoration footer = new MyPageDecoration();
```

PageNumberPrint is a special Print class which prints (you guess it!) the page number.  The PageNumber is supplied by the PagePrint class, so you do not need to implement it yourself.  PageNumberPrint has properties for setting the font, color, horizontal alignment, and text formatting.  See the Javadocs for more detail.

Let's try changing our footer to display a timestamp on the left side, and a page number on the right side:

```
public class MypageDecoration implements PageDecoration {
  String now = new Date().toString();
  public Print createPrint (PageNumber pageNumber) {
    GridPrint grid = new GridPrint("d:g, r:d");
    grid.add(new TextPrint(now));
    grid.add(new PageNumberPrint(pageNumber, SWT.RIGHT));
    return grid;
  }
}
```

It is somewhat common to have a header or footer with only the page number, so PaperClips includes a special page decoration for this case: PageNumberPageDecoration.  This class has the same properties as PageNumberPrint for customizing the appearance of the page number.

Most document headers and footers are exactly the same, except for the page number.  It is usually the case that either the header or footer is exactly the same on every page.  PaperClips provides a special page decoration for this case: SimplePageDecoration.

For example, let's design a static header with some title text on the left side, and a company logo on the right side.  To do this, simply construct a GridPrint with the text and image, then pass that grid to SimplePageDecoration's constructor:

```
// Construct the header
GridPrint grid = new GridPrint("d:g, p");
grid.add(new TextPrint(title));
grid.add(new ImagePrint(logo));

// Create the page decoration from the header
PageDecoration header = new SimplePageDecoration(grid);
```

---

# Putting It Together #

Once you have your header and/or footer ready, you are ready to decorate your document.

```
Print body = ...
PageDecoration header = ... (null if no header)
PageDecoration footer = ... (null if no footer)
PagePrint print = new PagePrint(body, header, footer);

// Set the vertical gap between the header, body, and footer.
// The header gap only applies if there is actually a header
print.setHeaderGap(9); // 9 points = 9/72" = 1/8" = 3.175mm
// The footer gap only applies if there is actually a footer
print.setFooterGap(18) // 18 points = 18/72" = 1/4" = 6.35mm

// At this point you are ready to print the document
PrintJob job = new PrintJob("Headers and footers!", print);
job.setMargins(36); // 36 points = 36/72" = 1/2" = 12.7mm
PaperClips.print(job, new PrinterData());
```

---

# Conclusion #

This document should help you get started using page numbers.

However this is a first draft, so if there's any details I've left out, please post them on the Sourceforge forums and I will do my best to fill in the blanks.