/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.messages.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * Default locale-sensitive messages for GXT. This class uses
 * {@link GWT#create(Class)} to create an instance of an automatically generated
 * subclass that implements the {@link XMessages} interface. See the package
 * containing {@link XMessages} for the property files containing the translated
 * messages. See {@link Messages} for more information.
 */
public class DefaultMessages {

  private static XMessages instance;

  /**
   * Returns an instance of an automatically generated subclass that implements
   * the {@link XMessages} interface containing default locale-sensitive
   * messages for GXT.
   * 
   * @return locale-sensitive messages for GXT
   */
  public static XMessages getMessages() {
    if (instance == null) {
      instance = GWT.create(XMessages.class);
    }
    return instance;
  }

}
