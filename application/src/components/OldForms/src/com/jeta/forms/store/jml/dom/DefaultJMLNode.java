package com.jeta.forms.store.jml.dom;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultJMLNode extends AbstractJMLNode {

    private String m_nodeName;
    private ArrayList<JMLNode> m_children;

    public DefaultJMLNode(String nodeName) {
        m_nodeName = nodeName;
    }

    @Override
    public Collection getChildren() {
        return m_children;
    }

    @Override
    public void appendChild(JMLNode childNode) {
        if (m_children == null) {
            m_children = new ArrayList<JMLNode>();
        }
        m_children.add(childNode);
    }

    @Override
    public String getNodeName() {
        return m_nodeName;
    }

    @Override
    public int getChildCount() {
        if (m_children == null) {
            return 0;
        }
        return m_children.size();
    }

    @Override
    public JMLNode getNode(int index) {
        if (m_children == null) {
            throw new IndexOutOfBoundsException();
        }

        return m_children.get(index);
    }
}
