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
   * Prints the words, "Hello PaperClips!" 
   * @param args command-line arguments.
   */
  public static void main (String[] args) {
    // Create the document
    TextPrint text = new TextPrint("Hello PaperClips!");

    // Print it to the default printer (no prompt).  The print job
    // name in the printer status window will be "TutorialExample1".
    PrintUtil.print("TutorialExample1", text);
  }
}
