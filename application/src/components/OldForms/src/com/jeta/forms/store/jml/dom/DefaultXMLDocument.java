package com.jeta.forms.store.jml.dom;

public class DefaultXMLDocument implements JMLDocument {

    @Override
    public JMLNode createNode(String nodeName) {
        return new DefaultJMLNode(nodeName);
    }

    @Override
    public JMLNode createTextNode(String nodeValue) {
        return new TextJMLNode(nodeValue);
    }
}
