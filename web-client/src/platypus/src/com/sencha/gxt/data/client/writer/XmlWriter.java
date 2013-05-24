/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.client.writer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.AutoBeanVisitor;
import com.sencha.gxt.data.shared.writer.AutoBeanWriter;

/**
 * A <code>AutoBeanWriter</code> that outputs XML.
 *
 * @param <M> the starting data format for the model to be inputed
 */
public class XmlWriter<M> extends AutoBeanWriter<M, String> {

  private static class PropertyGetter extends AutoBeanVisitor {
    private final Document doc = XMLParser.createDocument();
    private final Stack<AutoBean<?>> seen = new Stack<AutoBean<?>>();
    private final Stack<Element> currentPath = new Stack<Element>();

    public PropertyGetter(String rootTag) {
      doc.appendChild(doc.createElement(rootTag));
    }

    @Override
    public void endVisit(AutoBean<?> bean, Context ctx) {
      seen.pop();
    }

    @Override
    public void endVisitCollectionProperty(String propertyName, AutoBean<Collection<?>> value,
        CollectionPropertyContext ctx) {
      for (int i = 0; i < propertyName.split("/").length - 1; i++) {
        currentPath.pop();
      }
    }

    @Override
    public void endVisitReferenceProperty(String propertyName, AutoBean<?> value, PropertyContext ctx) {
      if (!"".equals(propertyName)) {
        for (int i = 0; i < propertyName.split("/").length; i++) {
          currentPath.pop();
        }
      }
    }

    public String getXml() {
      assert currentPath.size() == 0;
      return doc.toString();
    }

    @Override
    public boolean visit(AutoBean<?> bean, Context ctx) {
      if (seen.contains(bean)) {
        throw new RuntimeException("Cycle detected serializing beans to xml");
      }
      seen.push(bean);

      return true;
    }

    @Override
    public boolean visitCollectionProperty(String propertyName, AutoBean<Collection<?>> value,
        CollectionPropertyContext ctx) {
      assertValidPath(propertyName);
      List<String> subtags = Arrays.asList(propertyName.split("/"));
      List<String> childrenPath = subtags.subList(0, subtags.size() - 1);
      Element elt = getCurrentParent();
      for (String tag : childrenPath) {
        Element next = doc.createElement(tag);
        elt.appendChild(next);
        elt = next;
        currentPath.push(next);
      }
      String tag = subtags.get(subtags.size() - 1);
      for (Object v : value.as()) {
        Element next = doc.createElement(tag);
        elt.appendChild(next);
        currentPath.push(next);
        AutoBeanUtils.getAutoBean(v).accept(this);
        currentPath.pop();
      }
      return false;
    }

    @Override
    public boolean visitReferenceProperty(String propertyName, AutoBean<?> value, PropertyContext ctx) {
      if ("".equals(propertyName)) {
        return true;
      }
      assertValidPath(propertyName);
      String[] subtags = propertyName.split("/");
      Element elt = getCurrentParent();
      for (String tag : subtags) {
        Element next = doc.createElement(tag);
        elt.appendChild(next);
        elt = next;
        currentPath.push(elt);
      }

      return true;
    }

    @Override
    public boolean visitValueProperty(String propertyName, Object value, PropertyContext ctx) {
      assertValidPath(propertyName);

      assert propertyName.matches("^([^@]+|.*@[^/]+|)$") : "Invalid path to use when writing data out to XML";

      if ("".equals(propertyName)) {
        getCurrentParent().appendChild(doc.createTextNode(String.valueOf(value)));
        return true;
      }
      String[] subtags = propertyName.split("/");
      Element elt = getCurrentParent();
      String attr = null;
      for (int i = 0; i < subtags.length; i++) {
        if (subtags[i].startsWith("@")) {
          assert i + 1 == subtags.length;// this should be caught by the regex
          attr = subtags[i].substring(1);
          break;
        }
        Element next = doc.createElement(subtags[i]);
        elt.appendChild(next);
        elt = next;
      }
      if (attr != null) {
        // TODO better encoding?
        elt.setAttribute(attr, String.valueOf(value));
      } else {
        // TODO better encoding?
        elt.appendChild(doc.createTextNode(String.valueOf(value)));
      }

      return true;
    }

    protected Element getCurrentParent() {
      if (currentPath.size() == 0) {
        return doc.getDocumentElement();
      }
      return currentPath.peek();
    }

    private void assertValidPath(String path) {
      assert !path.contains("[") && !path.contains("]") : "Paths must be simple when used for writing autobeans to xml";
    }
  }

  private final String rootTag;

  /**
   * Creates a new XML writer that can format an object into XML.
   * 
   * @param factory an auto bean factory that can decode objects of type M
   * @param clazz the class to write
   * @param rootTag the root tag of the XML document
   */
  public XmlWriter(AutoBeanFactory factory, Class<M> clazz, String rootTag) {
    super(factory, clazz);

    this.rootTag = rootTag;
  }

  public String write(M model) {
    AutoBean<M> autoBean = getAutoBean(model);
    PropertyGetter xmlGetter = new PropertyGetter(rootTag);

    autoBean.accept(xmlGetter);

    return xmlGetter.getXml();
  }
}
