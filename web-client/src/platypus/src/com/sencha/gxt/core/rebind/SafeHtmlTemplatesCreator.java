/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.rebind;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.dev.util.Name;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

public class SafeHtmlTemplatesCreator extends AbstractCreator {
  private final JMethod method;
  private final Set<String> methodNames = new LinkedHashSet<String>();
  private final Map<String, String> templates = new HashMap<String, String>();
  private final Map<String, List<String>> paramLists = new HashMap<String, List<String>>();

  public SafeHtmlTemplatesCreator(GeneratorContext ctx, TreeLogger l, JMethod method) {
    super(ctx, l);
    this.method = method;
  }

  /**
   * Creates a new method in the generated template.
   * 
   * @param template - the content to use for the template
   * @param params - the ordered types that will be used as parameters
   * @return the name of the method that has been created
   */
  public String addTemplate(String template, List<String> params) {
    String name = method.getName() + methodNames.size();
    assert !methodNames.contains(name) : "Method name already exists!";

    methodNames.add(name);
    templates.put(name, template);
    paramLists.put(name, params);

    return name;
  }

  @Override
  protected void configureFactory(ClassSourceFileComposerFactory factory) throws UnableToCompleteException {
    super.configureFactory(factory);
    factory.addImport(Name.getSourceNameForClass(SafeHtml.class));
    factory.addImport(Name.getSourceNameForClass(Template.class));
    factory.makeInterface();
  }

  @Override
  protected void create(SourceWriter sw) throws UnableToCompleteException {
    for (String method : methodNames) {
      sw.println("@Template(\"%1$s\")", escape(templates.get(method)));
      sw.println("SafeHtml %1$s(%2$s);", method, makeArgs(paramLists.get(method)));
    }
  }

  private static String escape(String string) {
    return string.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
  }

  private String makeArgs(List<String> list) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (int i = 0; i < list.size(); i++) {
      if (!first) {
        sb.append(", ");
      }
      first = false;
      sb.append(list.get(i)).append(" arg").append(i);
    }
    return sb.toString();
  }

  @Override
  protected JClassType getSupertype() {
    return getContext().getTypeOracle().findType(Name.getSourceNameForClass(SafeHtmlTemplates.class));
  }

  @Override
  public String getInstanceExpression() {
    return "GWT.<" + getPackageName() + "." + getSimpleName() + ">create(" + getPackageName() + "." + getSimpleName()
        + ".class)";
  }

  @Override
  protected String getPackageName() {
    return method.getEnclosingType().getPackage().getName();
  }

  @Override
  protected String getSimpleName() {
    StringBuilder sb = new StringBuilder(method.getEnclosingType().getName().replace('.', '_')).append("_");

    sb.append(method.getName()).append("_");

    sb.append(method.getReturnType().getSimpleSourceName()).append("__");

    for (JParameter p : method.getParameters()) {
      sb.append(p.getType().getSimpleSourceName()).append("_").append(p.getName()).append("__");
    }

    return sb.append("_SafeHtmlTemplates").toString();
  }

}
