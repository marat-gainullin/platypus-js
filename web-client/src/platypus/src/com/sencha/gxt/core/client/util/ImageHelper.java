/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.util;

import com.google.gwt.core.client.GWT;

public final class ImageHelper {

  public static String createModuleBasedUrl(String path) {
    return "url('" + GWT.getModuleBaseURL() + path + "');";
   }
}
