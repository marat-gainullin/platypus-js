/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.rebind.useragent;

import java.util.SortedSet;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.ConfigurationProperty;
import com.google.gwt.core.ext.linker.PropertyProviderGenerator;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.StringSourceWriter;

public class UserAgentPropertyGenerator implements PropertyProviderGenerator {
  @Override
  public String generate(TreeLogger logger, SortedSet<String> possibleValues, String fallback,
      SortedSet<ConfigurationProperty> configProperties) throws UnableToCompleteException {

    SourceWriter sw = new StringSourceWriter();
    sw.println("{");

    sw.println("var ua = navigator.userAgent.toLowerCase();");
    simpleStatement(sw, "chrome", "chrome");
    simpleStatement(sw, "opera", "opera");

    sw.println("if (ua.indexOf('msie') != -1) {");
    sw.indent();
    // TODO ChromeFrame?
    simpleStatement(sw, "msie 6", "ie6");
    simpleStatement(sw, "msie 7", "ie7");
    simpleStatement(sw, "msie 8", "ie8");
    // else assume newest
    // simpleStatement(sw, "msie 9", "ie9");
    sw.println("return 'ie9';");
    sw.outdent();
    sw.println("}");

    sw.println("if (ua.indexOf('safari') != -1) {");
    sw.indent();
    simpleStatement(sw, "version/3", "safari3");
    simpleStatement(sw, "version/4", "safari4");
    // else assume newest
    // simpleStatement(sw, "version/5", "safari5");
    sw.println("return 'safari5';");
    sw.outdent();
    sw.println("}");

    sw.println("if (ua.indexOf('gecko') != -1) {");
    sw.indent();
    simpleStatement(sw, "rv:1.8", "gecko1_8");
    // Don't check for rev 1.9, check instead for the newest version, and treat
    // all
    // gecko browsers that don't match a rule as the newest version
    // simpleStatement(sw, "rv:1.9", "gecko1_9");
    sw.println("return 'gecko1_9';");
    sw.outdent();
    sw.println("}");


    simpleStatement(sw, "adobeair", "air");

    sw.println("return null;}");
    return sw.toString();
  }

  private void simpleStatement(SourceWriter sw, String indexOf, String value) {
    sw.println("if (ua.indexOf('%1$s') != -1) return '%2$s';", indexOf, value);
  }
}
