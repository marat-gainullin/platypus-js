/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts;

import com.eas.store.Serial;
import com.eas.controls.DesignInfo;
import com.eas.store.SerialCollection;
import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;

/**
 *
 * @author mg
 */
public class GroupLayoutGroupDesignInfo extends LayoutDesignInfo {

    public static final int UNSETTED = -1;
    // gap specific
    protected String componentPlacement;
    // component
    protected String component;
    protected int min = GroupLayout.DEFAULT_SIZE;
    protected int pref = GroupLayout.DEFAULT_SIZE;
    protected int max = GroupLayout.DEFAULT_SIZE;
    // group
    protected int horizontalSizeLinkId = UNSETTED;
    protected int verticalSizeLinkId = UNSETTED;
    protected boolean parallel;
    protected String alignment;// Individual alignment of this group (real group or component or gap group).
    protected String childrenAlignment;// Alignment of whole general children. May be overriden by individual child's alignment.
    protected boolean resizeable;
    protected List<GroupLayoutGroupDesignInfo> children = new ArrayList<>();

    public GroupLayoutGroupDesignInfo() {
        super();
    }
    /*
    public boolean isBaselineGroup()
    {
    boolean res = isGroup() && GroupLayout.Alignment.BASELINE.name().equals(childrenAlignment);
    if(res)
    {
    assert isParallel();
    }
    return res;
    }
     */

    public boolean isGap() {
        boolean res = component == null && (children == null || children.isEmpty());
        if (res) {
            assert !isComponent();
            assert !isGroup();
        }
        return res;
    }

    public boolean isPreferredGap() {
        return isGap() && componentPlacement != null;
    }

    public boolean isComponent() {
        boolean res = component != null;
        if (res) {
            assert !isGap();
            assert !isGroup();
        }
        return res;
    }

    public boolean isGroup() {
        boolean res = children != null && !children.isEmpty();
        if (res) {
            assert !isGap();
            assert !isComponent();
        }
        return res;
    }

    @Serial
    public String getComponentPlacement() {
        return componentPlacement;
    }

    @Serial
    public void setComponentPlacement(String aValue) {
        String oldValue = componentPlacement;
        componentPlacement = aValue;
        firePropertyChange("componentPlacement", oldValue, componentPlacement);
    }

    @Serial
    public String getComponent() {
        return component;
    }

    @Serial
    public void setComponent(String aValue) {
        String oldValue = component;
        component = aValue;
        firePropertyChange("component", oldValue, component);
    }

    @Serial
    public int getMin() {
        return min;
    }

    @Serial
    public void setMin(int aValue) {
        int oldValue = min;
        min = aValue;
        firePropertyChange("min", oldValue, min);
    }

    @Serial
    public int getPref() {
        return pref;
    }

    @Serial
    public void setPref(int aValue) {
        int oldValue = pref;
        pref = aValue;
        firePropertyChange("pref", oldValue, pref);
    }

    @Serial
    public int getMax() {
        return max;
    }

    @Serial
    public void setMax(int aValue) {
        int oldValue = max;
        max = aValue;
        firePropertyChange("max", oldValue, max);
    }

    @Serial
    public boolean isParallel() {
        return parallel;
    }

    @Serial
    public void setParallel(boolean aValue) {
        boolean oldValue = parallel;
        parallel = aValue;
        firePropertyChange("parallel", oldValue, parallel);
    }

    @Serial
    public String getAlignment() {
        return alignment;
    }

    @Serial
    public void setAlignment(String aValue) {
        String oldValue = alignment;
        alignment = aValue;
        firePropertyChange("alignment", oldValue, alignment);
    }

    @Serial
    public String getChildrenAlignment() {
        return childrenAlignment;
    }

    @Serial
    public void setChildrenAlignment(String aValue) {
        String oldValue = childrenAlignment;
        childrenAlignment = aValue;
        firePropertyChange("childrenAlignment", oldValue, childrenAlignment);
    }

    @Serial
    public boolean isResizeable() {
        return resizeable;
    }

    @Serial
    public void setResizeable(boolean aValue) {
        boolean oldValue = resizeable;
        resizeable = aValue;
        firePropertyChange("resizeable", oldValue, resizeable);
    }

    @Serial
    public int getHorizontalSizeLinkId() {
        return horizontalSizeLinkId;
    }

    @Serial
    public void setHorizontalSizeLinkId(int aValue) {
        int oldValue = horizontalSizeLinkId;
        horizontalSizeLinkId = aValue;
        firePropertyChange("horizontalSizeLinkId", oldValue, horizontalSizeLinkId);
    }

    @Serial
    public int getVerticalSizeLinkId() {
        return verticalSizeLinkId;
    }

    @Serial
    public void setVerticalSizeLinkId(int aValue) {
        int oldValue = verticalSizeLinkId;
        verticalSizeLinkId = aValue;
        firePropertyChange("verticalSizeLinkId", oldValue, verticalSizeLinkId);
    }

    @SerialCollection(deserializeAs = ArrayList.class, elementTagName = "subgroup", elementType = GroupLayoutGroupDesignInfo.class)
    public List<GroupLayoutGroupDesignInfo> getChildren() {
        return children;
    }

    @SerialCollection(deserializeAs = ArrayList.class, elementTagName = "subgroup", elementType = GroupLayoutGroupDesignInfo.class)
    public void setChildren(List<GroupLayoutGroupDesignInfo> aValue) {
        List<GroupLayoutGroupDesignInfo> oldValue = children;
        children = aValue;
        firePropertyChange("children", oldValue, children);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GroupLayoutGroupDesignInfo other = (GroupLayoutGroupDesignInfo) obj;
        if ((this.component == null) ? (other.component != null) : !this.component.equals(other.component)) {
            return false;
        }
        if ((this.componentPlacement == null) ? (other.componentPlacement != null) : !this.componentPlacement.equals(other.componentPlacement)) {
            return false;
        }
        if (this.alignment == null ? other.alignment != null : !this.alignment.equals(other.alignment)) {
            return false;
        }
        if (this.childrenAlignment == null ? other.childrenAlignment != null : !this.childrenAlignment.equals(other.childrenAlignment)) {
            return false;
        }
        if (this.min != other.min) {
            return false;
        }
        if (this.pref != other.pref) {
            return false;
        }
        if (this.max != other.max) {
            return false;
        }
        if (this.parallel != other.parallel) {
            return false;
        }
        if (this.resizeable != other.resizeable) {
            return false;
        }
        if (this.horizontalSizeLinkId != other.horizontalSizeLinkId) {
            return false;
        }
        if (this.verticalSizeLinkId != other.verticalSizeLinkId) {
            return false;
        }
        if (this.children != other.children && (this.children == null || !listsEquals(this.children, other.children))) {
            return false;
        }
        return true;
    }

    @Override
    public void accept(LayoutDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof GroupLayoutGroupDesignInfo) {
            GroupLayoutGroupDesignInfo source = (GroupLayoutGroupDesignInfo) aSource;
            // component
            component = source.component != null ? new String(source.component.toCharArray()) : null;
            componentPlacement = source.componentPlacement != null ? new String(source.componentPlacement.toCharArray()) : null;
            alignment = source.alignment != null ? new String(source.alignment.toCharArray()) : null;
            childrenAlignment = source.childrenAlignment != null ? new String(source.childrenAlignment.toCharArray()) : null;
            min = source.min;
            pref = source.pref;
            max = source.max;
            // group
            horizontalSizeLinkId = source.horizontalSizeLinkId;
            verticalSizeLinkId = source.verticalSizeLinkId;
            parallel = source.parallel;
            resizeable = source.resizeable;
            if (source.children != null) {
                children = new ArrayList<>();
                for (int i = 0; i < source.children.size(); i++) {
                    children.add(i, (GroupLayoutGroupDesignInfo) source.children.get(i).copy());
                }
            } else {
                children = null;
            }
        }
    }

    private boolean listsEquals(List<GroupLayoutGroupDesignInfo> left, List<GroupLayoutGroupDesignInfo> right) {
        assert left != null;
        if (right != null) {
            if (left.size() != right.size()) {
                return false;
            }
            for (int i = 0; i < left.size(); i++) {
                if (!left.get(i).isEqual(right.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
