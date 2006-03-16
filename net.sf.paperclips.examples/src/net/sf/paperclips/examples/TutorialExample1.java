/*
 * Created on Feb 25, 2006
 * Author: Matthew
 *
 * Copyright (C) 2006 Woodcraft Mill & Cabinet, Inc.  All Rights Reserved.
 */
package net.sf.paperclips.examples;

import net.sf.paperclips.PrintUtil;
import net.sf.paperclips.TextPrint;

/**
 * First example in the PaperClips online tutorial.
 */
public class TutorialExample1 {

  /**
   * Prints the words, "My first PaperClips print job." 
   * @param args
   */
  public static void main (String[] args) {
    // Create the document
    TextPrint text = new TextPrint("My first PaperClips print job.");

    // Print it to the default printer (no prompt).  The print job
    // name in the printer status window will be "Simple Example".
    PrintUtil.print("TutorialExample1", text);
  }
}
