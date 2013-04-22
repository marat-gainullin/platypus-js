package com.jeta.forms.store.jml.dom;

import java.util.Collection;
import java.util.LinkedHashMap;

import com.jeta.open.support.EmptyCollection;

public abstract class AbstractJMLNode implements JMLNode {

    private LinkedHashMap<String, String> m_attribs;

    @Override
    public void setAttribute(String attribName, String attribValue) {
        if (m_attribs == null) {
            m_attribs = new LinkedHashMap<String, String>();
        }

        m_attribs.put(attribName, attribValue);
    }

    @Override
    public Collection getAttributeNames() {
        if (m_attribs == null) {
            return EmptyCollection.getInstance();
        }

        return m_attribs.keySet();
    }

    @Override
    public String getAttribute(String attribName) {
        return m_attribs.get(attribName);
    }
}
