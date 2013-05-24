/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.writer;

import java.util.ArrayList;
import java.util.Collection;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.AutoBeanVisitor;

/**
 * Abstract DataWriter which writes out objects that can be wrapped as autobeans.
 * 
 * @param <M> the starting data format for the model to be inputed
 * @param <D> the data format to be used, usually to send the initial data over
 *          the wire
 */
public abstract class AutoBeanWriter<M, D> implements DataWriter<M, D> {
  private final Class<M> clazz;
  private final AutoBeanFactory factory;

  /**
   * Creates a new auto bean writer.
   * 
   * @param factory the auto bean factory
   * @param clazz the target class
   */
  public AutoBeanWriter(AutoBeanFactory factory, Class<M> clazz) {
    this.factory = factory;
    this.clazz = clazz;
  }

  /**
   * Helper method to attempt to turn a model into an autobean. Iterates through
   * all child properties and attempts to convert them as well.
   * 
   * @param model the target model
   * @return the autobean
   */
  protected AutoBean<M> getAutoBean(M model) {
    return getAutoBean(model, clazz);
  }

  private <T> AutoBean<T> getAutoBean(T model, Class<T> clazz) {
    if (model == null) {
      return factory.create(clazz);
    }
    // TODO when called recursively, this is unnecessary, as the property
    // context already has that model set.
    AutoBean<T> ab = AutoBeanUtils.getAutoBean(model);
    if (ab != null) {
      return ab;
    }
    ab = factory.create(clazz, model);
    // suppressing warnings for unchecked and raw types as the value must always
    // be of type ctx.getType
    ab.accept(new AutoBeanVisitor() {
      @SuppressWarnings({"unchecked", "rawtypes"})
      @Override
      public boolean visitReferenceProperty(String propertyName, AutoBean<?> value, PropertyContext ctx) {
        ctx.set(getAutoBean(value.as(), (Class) ctx.getType()));
        return true;
      }

      @SuppressWarnings({"rawtypes", "unchecked"})
      @Override
      public boolean visitCollectionProperty(String propertyName, AutoBean<Collection<?>> value,
          CollectionPropertyContext ctx) {
        Collection<Object> original = (Collection) value.as();
        Collection<?> c = new ArrayList<Object>(original);
        original.clear();
        for (Object obj : c) {
          AutoBean<?> ab = getAutoBean(obj, (Class<Object>) ctx.getElementType());
          original.add(ab.as());
        }
        return false;
      }
    });
    return ab;
  }
}
