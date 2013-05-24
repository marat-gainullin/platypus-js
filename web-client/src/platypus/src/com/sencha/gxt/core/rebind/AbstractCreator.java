/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Assists a {@link Generator} in building common types that may be shared
 * across several generation runs.
 * 
 * 
 */
public abstract class AbstractCreator {
  private final GeneratorContext context;
  private final TreeLogger logger;

  public AbstractCreator(GeneratorContext ctx, TreeLogger l) {
    this.context = ctx;
    this.logger = l.branch(Type.DEBUG, "Running " + this.getClass());
  }

  public final String create() throws UnableToCompleteException {

    PrintWriter pw = context.tryCreate(getLogger(), getPackageName(), getSimpleName());
    if (pw == null) {
      // someone else already generated type, no need to change it
      return getPackageName() + "." + getSimpleName();
    }

    ClassSourceFileComposerFactory factory = new ClassSourceFileComposerFactory(getPackageName(), getSimpleName());
    configureFactory(factory);

    SourceWriter sw = factory.createSourceWriter(getContext(), pw);
    create(sw);

    sw.commit(getLogger());
    return factory.getCreatedClassName();
  }

  public GeneratorContext getContext() {
    return context;
  }

  /**
   * Gets a Java expression to create an instance of this type. May be a
   * singleton or a new instance, depending on the implementation.
   * 
   * @return the instance expression
   */
  public abstract String getInstanceExpression();

  public TreeLogger getLogger() {
    return logger;
  }

  /**
   * Builds up the basics of the factory that will generate source. Should be
   * overridden to add imports.
   * 
   * @param factory the factory to configure
   */
  protected void configureFactory(ClassSourceFileComposerFactory factory) throws UnableToCompleteException {
    JClassType t = getSupertype();
    if (t.isInterface() != null) {
      factory.addImplementedInterface(getSupertype().getParameterizedQualifiedSourceName());
    } else {
      if (t.isClass() == null) {
        logger.log(Type.ERROR, "Cannot create a subtype of a non-class and non-interface type: " + t.getName());
        throw new UnableToCompleteException();
      }
      if (t.isFinal()) {
        logger.log(Type.ERROR, "Cannot create a subtype of a final class");
        throw new UnableToCompleteException();
      }
      factory.setSuperclass(t.getQualifiedSourceName());
    }
  }

  /**
   * Writes the body of the created class to the given source writer.
   * 
   * @param sw the source writer
   * @throws UnableToCompleteException if class cannot be written
   */
  protected abstract void create(SourceWriter sw) throws UnableToCompleteException;

  protected abstract String getPackageName();

  protected abstract String getSimpleName();

  /**
   * Gets the declared type that this is providing, typically made available as
   * the return type of an interface method.
   * 
   * @return the declared type
   */
  protected abstract JClassType getSupertype();

}
