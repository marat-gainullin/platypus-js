/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.rebind;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.sencha.gxt.core.client.XTemplates.FormatterFactories;
import com.sencha.gxt.core.client.XTemplates.FormatterFactory;
import com.sencha.gxt.data.rebind.ValueProviderCreator;
import com.sencha.gxt.data.rebind.ValueProviderCreator.RequiredReadability;

/**
 * Wraps up locally scoped variables used in creating a xtemplate method. Can be
 * nested, to allow for local scopes that shouldn't affect outer scopes.
 */
public class Context {
  // child-only members, used for getting vars that aren't scoped
  private final Context parent;// parent scope
  private final String scopedVarDeref;// parent expr that leads to current
                                      // scope
  private final JType scopedVarType;
  private String countVar;

  private final Map<String, JType> knownValues;
  // TODO make this set checked globally, and modified locally
  private final Set<String> localNames;

  private GeneratorContext ctx;
  private TreeLogger l;

  private FormatCollector formatters;

  /** value:<format-name>[(<format-params>)] */
  private static final Pattern FORMAT_PATTERN = Pattern.compile("([^\\:]+)\\:([a-zA-Z_0-9]+)(?:\\(([^\\)]+)\\))?");

  protected static String cap(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  public Context(Context parent, String childDeref, JType childType) {
    this.parent = parent;
    this.knownValues = new HashMap<String, JType>();
    setCountVar(parent.countVar);

    scopedVarDeref = childDeref;
    scopedVarType = childType;
    if (childType.isClassOrInterface() != null) {
      // TODO field?
      for (JMethod m : childType.isClassOrInterface().getInheritableMethods()) {
        if (m.getName().startsWith("get") && m.getName().length() > 3) {
          String prop = m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
          knownValues.put(prop, m.getReturnType());
        }
      }
    }

    localNames = null; // only global names for now
  }

  public Context(GeneratorContext ctx, TreeLogger l, Map<String, JType> params, FormatCollector formatters) {
    parent = null;
    scopedVarDeref = null;
    scopedVarType = null;

    // TODO if params is a singleton map, don't make users refer by name to
    // the only param
    knownValues = Collections.unmodifiableMap(params);
    localNames = new HashSet<String>(knownValues.keySet());

    this.ctx = ctx;
    this.l = l;
    this.formatters = formatters;
  }

  public String declareLocalVariable(String name) {
    return getRoot().createName(name);
  }

  /**
   * Gets a java expression that will return a value for the local name from
   * within the current context, including any formatting.
   * 
   * @param localName the name of the content
   * @return the Java expression that returns the value
   * @throws UnableToCompleteException if the Java expression cannot be returned
   */
  public String deref(String localName) throws UnableToCompleteException {
    // peel off formatter
    Matcher hasFormatter = FORMAT_PATTERN.matcher(localName);
    if (hasFormatter.matches()) {
      localName = hasFormatter.group(1);
    }

    final String getterExpression;
    // check magic values
    if (localName.matches("^\\.\\.$") || localName.equals("parent")) {
      getterExpression = parent.deref(".");
    } else if (localName.matches("#")) {
      getterExpression = "(" + countVar + " + 1)";
    } else if (localName.matches("\\.")) {
      getterExpression = this.scopedVarDeref;
    } else {
      // look in local context
      List<String> path = new LinkedList<String>(Arrays.asList(localName.split("\\.")));
      if (knownValues.containsKey(path.get(0))) {
        final JType pathRootType;
        final String pathRootAccessor;
        if (isRoot()) {
          pathRootType = knownValues.get(path.get(0));
          pathRootAccessor = path.get(0);
          path.remove(0);
        } else {// else we are scoped, and need to get data in some other way
          pathRootType = this.scopedVarType;
          pathRootAccessor = this.scopedVarDeref;
        }
        JType valueType = getType(localName);
        JClassType valueClassType = valueType.isClassOrInterface();
        if (valueClassType == null) {
          if (valueType.isArray() != null) {
            // array, use it
            valueClassType = valueType.isArray();
          } else if (valueType.isPrimitive() != null) {
            // primitive, box it
            valueClassType = getContext().getTypeOracle().findType(
                valueType.isPrimitive().getQualifiedBoxedSourceName());
          }
        }
        // if the data can be accessed directly, do that
        if (path.size() == 0) {
          getterExpression = pathRootAccessor;
        } else {
          // otherwise, use a value provider based on the path
          ValueProviderCreator vpc = new ValueProviderCreator(getContext(), getLogger(), path,
              pathRootType.isClassOrInterface(), valueClassType);
          vpc.setReadability(RequiredReadability.GET);
          vpc.create();
          getterExpression = String.format("%1$s.getValue(%2$s)", vpc.getInstanceExpression(), pathRootAccessor);
        }
      } else if (!isRoot()) {
        if (localName.startsWith("parent.") && null == parent.deref(localName)) {
          // if it starts with parent and the parent cant find a value , then
          // hack
          // off "parent.", and look again
          getterExpression = parent.deref(localName.substring("parent.".length()));
        } else {
          // ask parent
          getterExpression = parent.deref(localName);
        }
      } else {
        getterExpression = null;
      }
    }
    // if we have a good way to format, use it
    if (getterExpression != null) {
      if (hasFormatter.matches()) {
        String formatterName = hasFormatter.group(2);
        String formatterParams = hasFormatter.group(3);

        // Formatter Factories
        // DateTimeFormat.getFormat(String)
        // DateTimeFormat.getFormat(PredefinedFormat)
        // NumberFormat.getCurrencyFormat()
        // NumberFormat.getScientificFormat()
        // NumberFormat.getFormat(String)
        // NumberFormat.getDecimalFormat()

        // Formatter instances
        // NumberFormat.format(Number)
        // DateTimeFormat.format(Date)

        // parse out formatter contents if necessary (at least 3 chars)
        if (formatterParams != null && formatterParams.length() >= 3) {
          Matcher paramReplacement = Pattern.compile("\\{([^\\}]+)\\}").matcher(formatterParams);
          StringBuffer sb = new StringBuffer();
          while (paramReplacement.find()) {
            String var = paramReplacement.group(1);
            paramReplacement.appendReplacement(sb, deref(var));
          }
          paramReplacement.appendTail(sb);
          formatterParams = sb.toString();
        }

        return getFormatterExpression(formatterName, formatterParams, getterExpression, getType(localName).isPrimitive() == null);
      } else {
        return getterExpression;
      }
    }

    // otherwise fail
    // TODO fail better
    return null;
  }

  /**
   * Gets a java expression for the number of items in the given localName,
   * assuming it is an Array or List
   * 
   * @param localName the name of the content
   * @return number of items in localName
   * @throws UnableToCompleteException if the number of items cannot be returned
   */
  public String derefCount(String localName) throws UnableToCompleteException {
    // assert getType(localName) is array or list
    // count is from 2.x's xcount
    JType container = getType(localName);
    if (container.isArray() != null) {
      return deref(localName) + ".length";
    } else {// assert List
      return deref(localName) + ".size()";
    }
  }

  /**
   * Gets the type of content available at this path, within the current
   * context.
   * 
   * @param localName the name of the content
   * @return the type of the content
   * @throws UnableToCompleteException if the type cannot be returned
   */
  public JType getType(String localName) throws UnableToCompleteException {
    // magic vars (\\.|\\.\\.|#), illegal in other contexts
    if (localName.matches("^#$")) {// row number
      return getContext().getTypeOracle().findType("java.lang.Integer");
    } else if (localName.matches("^\\.\\.$")) {// parent
      return parent.getType(".");
    } else if (localName.matches("^\\.$")) {// this
      return scopedVarType;
    }

    // formats .*\\:(<format-name>?:(:(<format-params>))?)
    if (FORMAT_PATTERN.matcher(localName).matches()) {
      return getContext().getTypeOracle().findType("java.lang.String");
    }

    // look in local context
    String[] localPath = localName.split("\\.");
    if (knownValues.containsKey(localPath[0])) {
      // if we have the key, then run with it -
      JType type = knownValues.get(localPath[0]);
      for (int i = 1; i < localPath.length; i++) {
        // TODO field

        JMethod[] possibleGetters = type.isClassOrInterface().getInheritableMethods();
        for (JMethod possible : possibleGetters) {
          // TODO this is wrong, if we intend to support getProperty() and
          // property(), and evaluate to the most specific method
          if (isMatchingGetter(possible, localPath[i])) {
            type = possible.getReturnType();
            break;
          }
        }
      }
      return type;
    }

    // ask parent, if any
    if (!isRoot()) {
      JType possibleType = parent.getType(localName);
      if (possibleType != null) {
        return possibleType;
      }
    }

    // magic vars, only replace if they don't have other meaning
    if (localName.startsWith("parent") && !isRoot()) {
      if (localName.length() == "parent".length()) {
        return parent.getType(".");
      }
      if (localName.startsWith("parent.")) {
        return parent.getType(localName.substring("parent.".length()));
      }
    }
    if (localName.endsWith("count")) {
      return getContext().getTypeOracle().findType("java.lang.Integer");
    }

    // fail
    // TODO find a better way to end this recursive call - only fail locally
    return null;
  }

  /**
   * Explicitly set the local count variable. This can (must) be set for each
   * context, and can be referenced with the # magic variable.
   * 
   * @param countVar the local count variable
   */
  public void setCountVar(String countVar) {
    this.countVar = countVar;
  }

  private String createName(String name) {
    assert isRoot();

    String tryName = name;
    if (localNames.contains(name)) {
      int i = 1;
      while (localNames.contains(tryName = (name + i++)));
    }
    localNames.add(tryName);
    return tryName;
  }

  private GeneratorContext getContext() {
    return getRoot().ctx;
  }

  /**
   * Expected to return a java expression that will format the given expression using the named
   * format, and passing the given parameters to that formatter.
   * 
   * @param formatterName the name of the formatter to run, as collected from 
   * {@link FormatterFactory} and {@link FormatterFactories} annotations
   * @param formatterParams parameters to pass to the formatter factory method
   * @param expressionToFormat the java expression to be formatted
   * @return
   * @throws UnableToCompleteException
   */
  private String getFormatterExpression(String formatterName, String formatterParams, String expressionToFormat, boolean canBeNull)
      throws UnableToCompleteException {
    return getRoot().formatters.getFormatExpression(formatterName, formatterParams, expressionToFormat, canBeNull);
  }

  private TreeLogger getLogger() {
    return getRoot().l;
  }

  private Context getRoot() {
    Context ctx = this;
    while (!ctx.isRoot()) {
      ctx = ctx.parent;
    }
    return ctx;
  }

  private boolean isMatchingGetter(JMethod possible, String property) {
    if (possible.getParameters().length != 0) {
      return false;
    }

    if (possible.getName().equals(property)) {
      return true;
    }
    String cap = cap(property);
    if (possible.getName().equals("get" + cap)) {
      return true;
    }
    if (possible.getName().equals("is" + cap) || possible.getName().equals("has" + cap)) {
      return true;
    }

    return false;
  }

  private boolean isRoot() {
    return parent == null;
  }
}