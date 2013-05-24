/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.util.Name;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.rebind.AbstractCreator;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 *
 */
public class PropertyAccessGenerator extends Generator {
  private JClassType propertyAccessInterface;
  private JClassType modelKeyProviderInterface;
  private JClassType labelProviderInterface;
  private JClassType valueProviderInterface;

  @Override
  public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
    // make sure it is an interface
    TypeOracle oracle = context.getTypeOracle();

    propertyAccessInterface = oracle.findType(Name.getSourceNameForClass(PropertyAccess.class));
    modelKeyProviderInterface = oracle.findType(Name.getSourceNameForClass(ModelKeyProvider.class));
    valueProviderInterface = oracle.findType(Name.getSourceNameForClass(ValueProvider.class));
    labelProviderInterface = oracle.findType(Name.getSourceNameForClass(LabelProvider.class));
    JClassType toGenerate = oracle.findType(typeName).isInterface();
    if (toGenerate == null) {
      logger.log(TreeLogger.ERROR, typeName + " is not an interface type");
      throw new UnableToCompleteException();
    }
    if (!toGenerate.isAssignableTo(propertyAccessInterface)) {
      logger.log(Type.ERROR, "This isn't a PropertyAccess subtype...");
      throw new UnableToCompleteException();
    }

    // Get the name of the new type
    String packageName = toGenerate.getPackage().getName();
    String simpleSourceName = toGenerate.getName().replace('.', '_') + "Impl";
    PrintWriter pw = context.tryCreate(logger, packageName, simpleSourceName);
    if (pw == null) {
      return packageName + "." + simpleSourceName;
    }

    // start making the class, with basic imports
    ClassSourceFileComposerFactory factory = new ClassSourceFileComposerFactory(packageName, simpleSourceName);
    factory.addImplementedInterface(typeName);
    SourceWriter sw = factory.createSourceWriter(context, pw);


    // for each method,
    for (JMethod m : toGenerate.getOverridableMethods()) {
      TreeLogger l = logger.branch(Type.DEBUG, "Building method: " + m.getReadableDeclaration());

      // no support for params at this time
      if (m.getParameters().length != 0) {
        l.log(Type.ERROR, "Method " + m.toString() + " must not have parameters.");
        throw new UnableToCompleteException();
      }

      // ask for the types that provide the property data
      JClassType ret = m.getReturnType().isClassOrInterface();
      final AbstractCreator c;
      if (ret.isAssignableTo(valueProviderInterface)) {
        c = new ValueProviderCreator(context, l, m);
      } else if (ret.isAssignableTo(modelKeyProviderInterface)) {
        c = new ModelKeyProviderCreator(context, l, m);
      } else if (ret.isAssignableTo(labelProviderInterface)) {
        c = new LabelProviderCreator(context, l, m);
      } else {
        logger.log(Type.ERROR, "Method uses a return type that cannot be generated");
        throw new UnableToCompleteException();
      }
      c.create();
      // build the method
      // public ValueProvider<T, V> name() { return NameValueProvider.instance;
      // }
      sw.println("public %1$s %2$s() {", m.getReturnType().getQualifiedSourceName(), m.getName());
      sw.indentln("return %1$s;", c.getInstanceExpression());
      sw.println("}");
    }

    sw.commit(logger);

    return factory.getCreatedClassName();
  }

}
