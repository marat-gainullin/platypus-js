/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.client.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Text;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.Splittable;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.dom.DomQuery;
import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.loader.AbstractAutoBeanReader;

/**
 * A <code>DataReader</code> implementation that reads XML data and build it
 * into the given {@link AutoBean} type, using other types from the given
 * factory.
 * 
 * <p />
 * These AutoBean interfaces should use
 * {@link com.google.web.bindery.autobean.shared.AutoBean.PropertyName} to
 * specify the path to properties. Collections can be referenced by specifying
 * the path to the items.
 * 
 * <p />
 * Subclasses can override {@link #createReturnData(Object, Object)} to control
 * what object is returned by the reader.
 * 
 * @param <M> the model type to return from the reader
 * @param <Base> an intermediate type that represents the data passed in as XML.
 *          May be the same as M
 */
public class XmlReader<M, Base> extends AbstractAutoBeanReader<M, Base, String> {

  /**
   * A {@link Splittable} for XML data.
   */
  public final static class XmlSplittable implements Splittable {
    private final Node node;
    private final NodeList<Node> nodes;
    private final Map<String, Object> reifiedData = new FastMap<Object>();

    /**
     * Create an XML splittable rooted at the document element of the given
     * document.
     * 
     * @param doc the document representing the root of this XML splittable
     */
    public XmlSplittable(Document doc) {
      this(getNode(doc.getDocumentElement()));
    }

    /**
     * Create an XML splittable from the given node.
     * 
     * @param node the node representing the root of this XML splittable
     */
    public XmlSplittable(Node node) {
      this.node = node;
      this.nodes = null;
    }

    /**
     * Create an XML splittable for the given list of nodes.
     * 
     * @param nodes the nodes representing this XML splittable
     */
    public XmlSplittable(NodeList<Node> nodes) {
      this.node = null;
      this.nodes = nodes;
    }

    @Override
    public boolean asBoolean() {
      return "true".equals(asString());
    }

    @Override
    public double asNumber() {
      return Double.parseDouble(asString());
    }

    @Override
    public void assign(Splittable parent, int index) {

    }

    @Override
    public void assign(Splittable parent, String propertyName) {

    }

    @Override
    public String asString() {
      if (GXT.isIE()) {
        return ieNativeAsString(getFirstNode());
      } else {
        return nativeAsString(getFirstNode());
      }
    }

    @Override
    public Splittable deepCopy() {
      throw new UnsupportedOperationException("Cannot clone XmlSplittable");
    }

    @Override
    public Splittable get(int index) {
      return new XmlSplittable(nodes.getItem(index));
    }

    @Override
    public Splittable get(String key) {
      if ("".equals(key)) {
        return new XmlSplittable(getFirstNode());
      }
      // [...@...] is a case where we are getting something which may not be an
      // attribute - only look for 'naked' @s

      // first try - look for 'anything, followed by an @, followed by not a
      // [,], or .
      if (key.matches(".*@[^\\[\\]\\.]+")) {
        String attrValue = DomQuery.selectValue(key, getFirstNode());
        if (attrValue == null) {
          return null;
        }
        return new XmlSplittable(getFirstNode().getOwnerDocument().createTextNode(attrValue));
      }
      NodeList<Node> nodes = DomQuery.select(key, (Element) getFirstNode()).cast();
      if (nodes == null || nodes.getLength() == 0) {
        return null;
      }
      return new XmlSplittable(nodes);
    }

    @Override
    public String getPayload() {
      throw new UnsupportedOperationException("Cannot convert XmlSplittable to payload");
    }

    @Override
    public List<String> getPropertyKeys() {
      NodeList<Node> children = ((Element) getFirstNode()).getChildNodes();
      List<String> keys = new ArrayList<String>();

      for (int i = 0; i < children.getLength(); i++) {
        if (Element.is(children.getItem(i))) {
          keys.add(children.getItem(i).getNodeName());
        }
      }
      return keys;
    }

    @Override
    public Object getReified(String key) {
      return reifiedData.get(key);
    }

    private native String ieNativeAsString(Node node) /*-{
			return node.text;
    }-*/;

    @Override
    public boolean isBoolean() {
      return isString();// TODO
    }

    @Override
    public boolean isIndexed() {
      return (nodes != null);
    }

    @Override
    public boolean isKeyed() {
      return (getFirstNode() instanceof Element);
    }

    @Override
    public boolean isNull(int index) {
      return get(index) == null;
    }

    @Override
    public boolean isNull(String key) {
      return get(key) == null;
    }

    @Override
    public boolean isNumber() {
      return isString();// TODO
    }

    @Override
    public boolean isReified(String key) {
      return reifiedData.containsKey(key);
    }

    @Override
    public boolean isString() {
      return getFirstNode() instanceof Text;
    }

    @Override
    public boolean isUndefined(String key) {
      // TODO
      return false;
    }

    private native String nativeAsString(Node node) /*-{
			return node.textContent;
    }-*/;

    @Override
    public void setReified(String key, Object object) {
      reifiedData.put(key, object);
    }

    @Override
    public void setSize(int i) {

    }

    @Override
    public int size() {
      int size = 0;
      for (int i = 0; i < nodes.getLength(); i++) {
        if (Element.is(nodes.getItem(i))) {
          size++;
        }
      }
      return size;
    }

    @Override
    public native String toString() /*-{
      var node = this.@com.sencha.gxt.data.client.loader.XmlReader.XmlSplittable::getFirstNode()();
      if (@com.sencha.gxt.core.client.GXT::isIE6()() || @com.sencha.gxt.core.client.GXT::isIE7()()) {
        return node.xml;
      } else {
        return new XMLSerializer().serializeToString(node);
      }
    }-*/;

    private Node getFirstNode() {
      if (node != null) {
        return node;
      }
      assert nodes != null && nodes.getLength() != 0;
      return nodes.getItem(0);
    }
  }

  private static native Node getNode(com.google.gwt.xml.client.Node n) /*-{
		return n.@com.google.gwt.xml.client.impl.DOMItem::getJsObject()();
  }-*/;

  /**
   * Creates a new XML reader that can turn XML into an AutoBean.
   * 
   * @param factory an auto bean factory capable of encoding objects of type M
   * @param rootBeanType AutoBean based type to represent the base data
   */
  public XmlReader(AutoBeanFactory factory, Class<Base> rootBeanType) {
    super(factory, rootBeanType);
  }

  @Override
  protected Splittable readSplittable(Object loadConfig, String data) {
    return new XmlSplittable(XMLParser.parse(data));
  }
}
