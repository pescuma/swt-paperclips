package net.sf.paperclips;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Convenience methods for retrieving locale-specific messages.
 * @author Matthew Hall
 * @since 1.0.4
 */
public class Messages {
  private static final String         BUNDLE_NAME     = "net.sf.paperclips.messages";           //$NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( BUNDLE_NAME );

  /**
   * Key for "Page {x} of {y}" used by DefaultPageNumberFormat.
   */
  public static final String          PAGE_X_OF_Y     = "PAGE_X_OF_Y";                          //$NON-NLS-1$

  private Messages() {}

  /**
   * Returns the locale-specific messages for the given key.
   * @param key the key identifying the string to be retrieved.
   * @return the locale-specific messages for the given key.
   */
  public static String getString( String key ) {
    try {
      return RESOURCE_BUNDLE.getString( key );
    }
    catch ( MissingResourceException e ) {
      return '!' + key + '!';
    }
  }
}
