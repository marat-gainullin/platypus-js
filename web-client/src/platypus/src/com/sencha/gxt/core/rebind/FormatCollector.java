/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.rebind;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.dev.util.Name;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.XTemplates.FormatterFactories;
import com.sencha.gxt.core.client.XTemplates.FormatterFactory;
import com.sencha.gxt.core.client.XTemplates.FormatterFactoryMethod;

public class FormatCollector {
  private final TreeLogger logger;
  @SuppressWarnings("unused")
  private final GeneratorContext context;
  private final Map<String, String> formatters = new HashMap<String, String>();
  private final Map<String, String> defaultArgs = new HashMap<String, String>();
  private final Set<String> nullsAllowed = new HashSet<String>();

  public FormatCollector(GeneratorContext context, TreeLogger l, JClassType toGenerate)
      throws UnableToCompleteException {
    this.logger = l.branch(Type.DEBUG, "Collecting formatters for " + toGenerate.getName());
    this.context = context;

    // Collect factories first
    List<FormatterFactory> foundFactories = new ArrayList<XTemplates.FormatterFactory>();
    for (JClassType type : toGenerate.getFlattenedSupertypeHierarchy()) {
      if (type.isAnnotationPresent(FormatterFactories.class)) {
        foundFactories.addAll(Arrays.asList(type.getAnnotation(FormatterFactories.class).value()));
      }
    }
    for (FormatterFactory f : foundFactories) {
      if (!f.name().equals("")) {
        registerFactory(f.name(), f.factory(), "getFormat", "", f.acceptsNull());
      } else if (f.methods().length != 0) {
        for (FormatterFactoryMethod method : f.methods()) {
          registerFactory(method.name(), f.factory(), method.method(), method.defaultArgs(), f.acceptsNull());
        }
      } else {
        logger.log(Type.ERROR, "Formatter factory defined, but no way to access it (no name, no methods declared): "
            + f.factory());
        throw new UnableToCompleteException();
      }
    }
  }

  private void registerFactory(String name, Class<?> factory, String method, String defaultArgs, boolean acceptsNull) {
    // Don't overwrite an existing formatter
    if (formatters.containsKey(name)) {
      logger.log(Type.WARN,
          "Template already has a formatter with name " + name + ". Not registering " + factory.getName() + "." + method);
    } else {
      logger.branch(Type.TRACE, "New factory registered: " + name + " = " + factory.getName() + " :: " + method)
      .log(Type.TRACE, "Default args: " + defaultArgs);
      // if method is static
      formatters.put(name, String.format("%1$s.%2$s(%3$s)", Name.getSourceNameForClass(factory), method, "%1$s"));
      // TODO support non-static factory methods?
      
      if (acceptsNull) {
        this.nullsAllowed.add(name);
      }

      this.defaultArgs.put(name, defaultArgs);
    }
  }

  public String getFormatExpression(String name, String params, String expressionToFormat, boolean canBeNull) throws UnableToCompleteException {
    if (params == null) {
      // check for defaults
      params = defaultArgs.get(name);
    }
    
    String format = formatters.get(name);
    if (format == null) {
      logger.log(Type.ERROR, "Formatter with name " + name + " not found");
      throw new UnableToCompleteException();
    }
    
    String formattedExpression = String.format(format, params) + ".format(" + expressionToFormat + ")";
    if (!nullsAllowed.contains(name) && canBeNull) {
      //TODO this is running the getter twice!
      return String.format("(%1$s == null) ? \"\" : %2$s", expressionToFormat, formattedExpression);
    } else {
      return formattedExpression;
    }
  }

}
