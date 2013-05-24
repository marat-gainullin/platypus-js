/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.dom.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.shared.FastMap;

public class ComputedStyleImpl {

  protected Map<String, String> camelCache = new FastMap<String>();
  protected Map<String, String> hyphenCache = new FastMap<String>();

  public FastMap<String> getStyleAttribute(Element elem, List<String> names) {
    return getComputedStyle(elem, names, checkHyphenCache(names), checkCamelCache(names), null);
  }

  public void setStyleAttribute(Element elem, String name, Object value) {
    elem.getStyle().setProperty(checkCamelCache(Collections.singletonList(name)).get(0), value == null ? "" : String.valueOf(value));
  }

  protected List<String> checkCamelCache(List<String> l) {
    List<String> list = new ArrayList<String>(l);
    for (int i = 0; i < list.size(); i++) {
      String s = list.get(i);
      String t = camelCache.get(s);
      if (t == null) {
        t = Format.camelize(getPropertyName(s));
        camelCache.put(s, t);
      }
      list.set(i, t);
    }
    return list;
  }

  protected List<String> checkHyphenCache(List<String> l) {
    List<String> list = new ArrayList<String>(l);
    for (int i = 0; i < list.size(); i++) {
      String s = list.get(i);
      String t = hyphenCache.get(s);
      if (t == null) {
        t = Format.hyphenize(getPropertyName(s));
        hyphenCache.put(s, t);
      }
      list.set(i, t);
    }
    return list;
  }

  protected String getPropertyName(String name) {
    if ("float".equals(name)) {
      return "cssFloat";
    }
    return name;
  }

  protected native FastMap<String> getComputedStyle(Element elem, List<String> originals, List<String> names,
      List<String> names2, String pseudo) /*-{
    var cStyle;
    var map = @com.sencha.gxt.core.shared.FastMap::new()();
    var size = originals.@java.util.List::size()();
    for(var i = 0;i<size;i++){
      var original = originals.@java.util.List::get(I)(i);

      var name2 = names2.@java.util.List::get(I)(i);
      var v = elem.style[name2];
      if(v){
        map.@com.sencha.gxt.core.shared.FastMap::put(Ljava/lang/String;Ljava/lang/Object;)(original,String(v));
        continue;
      }
      var name = names.@java.util.List::get(I)(i);
      if(!cStyle){
        cStyle = $doc.defaultView.getComputedStyle(elem, pseudo);
      }
      map.@com.sencha.gxt.core.shared.FastMap::put(Ljava/lang/String;Ljava/lang/Object;)(original,cStyle ? String(cStyle.getPropertyValue(name)) : null);
    }
    return map;
  }-*/;

}
