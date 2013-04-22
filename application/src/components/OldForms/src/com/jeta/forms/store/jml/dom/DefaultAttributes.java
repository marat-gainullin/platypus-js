package com.jeta.forms.store.jml.dom;

import org.xml.sax.Attributes;

public class DefaultAttributes implements JMLAttributes {

    protected Attributes attrs = null;

    public DefaultAttributes(Attributes aAttrs) {
        super();
        attrs = aAttrs;
    }

    //private HashMap<String, String> m_attribs;
    /**
     * Return the number of attributes in the list.
     */
    @Override
    public int getLength() {
        //return m_attribs == null ? 0 : m_attribs.size();
        return attrs.getLength();
    }

    /**
     * Look up an attribute's value by XML 1.0 qualified name.
     */
    @Override
    public String getValue(String qName) {
        //return m_attribs == null ? null : m_attribs.get(qName);
        return attrs.getValue(qName);
    }
    /*
    public void setAttribute(String qname, String value) {
    if (m_attribs == null) {
    m_attribs = new HashMap<String, String>();
    }
    m_attribs.put(qname, value);
    }
     */
}
