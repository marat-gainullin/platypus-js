package com.jeta.forms.store.jml.dom;

import java.util.Collection;
import com.jeta.open.support.EmptyCollection;

public class TextJMLNode extends AbstractJMLNode {

    private String m_textValue;

    public TextJMLNode(String textValue) {
        m_textValue = textValue;
    }

    @Override
    public void appendChild(JMLNode childNode) {
        // ignore
        assert (false);
    }

    public String getTextValue() {
        return m_textValue;
    }

    @Override
    public String getNodeName() {
        return "#text";
    }

    @Override
    public Collection getChildren() {
        return EmptyCollection.getInstance();
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public JMLNode getNode(int index) {
        throw new IndexOutOfBoundsException();
    }
}
