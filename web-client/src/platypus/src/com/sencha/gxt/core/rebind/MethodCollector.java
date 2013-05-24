/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.rebind;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.sencha.gxt.core.client.XTemplates.TemplateCondition;
import com.sencha.gxt.core.client.XTemplates.TemplateConditions;

/**
 * Intended as a general purpose method collector/provider to be used beyond
 * just conditional methods.
 * 
 */
public class MethodCollector {
  private final Map<String, TemplateCondition> methods = new HashMap<String, TemplateCondition>();
  // private final GeneratorContext ctx;
  private final TreeLogger logger;

  public MethodCollector(GeneratorContext context, TreeLogger logger, JClassType template) {
    // this.ctx = context;
    this.logger = logger.branch(Type.DEBUG, "Collecting methods in " + template.getName());

    for (JClassType type : template.getFlattenedSupertypeHierarchy()) {
      if (type.isAnnotationPresent(TemplateCondition.class)) {
        addMethod(type.getAnnotation(TemplateCondition.class));
      }
      if (type.isAnnotationPresent(TemplateConditions.class)) {
        for (TemplateCondition c : type.getAnnotation(TemplateConditions.class).value()) {
          addMethod(c);
        }
      }
    }
  }

  private void addMethod(TemplateCondition annotation) {
    String name = annotation.name().length() == 0 ? annotation.methodName() : annotation.name();
    if (methods.containsKey(name)) {
      logger.log(Type.WARN, "Template already has a method registered with name " + name + ". Not registering "
          + annotation.type().getName() + "." + annotation.methodName());
    } else {
      methods.put(name, annotation);
    }

  }

  public String getMethodInvocation(String name, String target, String params) throws UnableToCompleteException {
    TemplateCondition method = methods.get(name);

    if (method == null) {
      logger.log(Type.ERROR, "Method with name " + name + " not registered in this template");
      throw new UnableToCompleteException();
    }

    String methodName = method.methodName();
    
    //Commenting this out until we have a use case for it, and proper documentation
//    if (target == null) {
//      target = String.format("GWT.<%1$s>create(%1$s)", method.type().getName());
//    }
    
    return String.format("%1$s.%2$s(%3$s)", target, methodName, params);
  }
}
