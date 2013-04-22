/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.layouts;

import com.eas.store.Serial;
import com.eas.controls.DesignInfo;

/**
 *
 * @author mg
 */
public class GroupLayoutDesignInfo extends LayoutDesignInfo {

    protected boolean autoCreateContainerGaps;
    protected boolean autoCreateGaps;
    protected boolean honorsVisibility;
    protected GroupLayoutGroupDesignInfo horizontalGroup;
    protected GroupLayoutGroupDesignInfo verticalGroup;

    public GroupLayoutDesignInfo()
    {
        super();
    }

    @Serial
    public boolean isAutoCreateContainerGaps() {
        return autoCreateContainerGaps;
    }

    @Serial
    public void setAutoCreateContainerGaps(boolean aValue) {
        boolean oldValue = autoCreateContainerGaps;
        autoCreateContainerGaps = aValue;
        firePropertyChange("autoCreateContainerGaps", oldValue, autoCreateContainerGaps);
    }

    @Serial
    public boolean isAutoCreateGaps() {
        return autoCreateGaps;
    }

    @Serial
    public void setAutoCreateGaps(boolean aValue) {
        boolean oldValue = autoCreateGaps;
        autoCreateGaps = aValue;
        firePropertyChange("autoCreateGaps", oldValue, autoCreateGaps);
    }

    @Serial
    public GroupLayoutGroupDesignInfo getHorizontalGroup() {
        return horizontalGroup;
    }

    @Serial
    public void setHorizontalGroup(GroupLayoutGroupDesignInfo aValue) {
        GroupLayoutGroupDesignInfo oldValue = horizontalGroup;
        horizontalGroup = aValue;
        firePropertyChange("horizontalGroup", oldValue, horizontalGroup);
    }

    @Serial
    public GroupLayoutGroupDesignInfo getVerticalGroup() {
        return verticalGroup;
    }

    @Serial
    public void setVerticalGroup(GroupLayoutGroupDesignInfo aValue) {
        GroupLayoutGroupDesignInfo oldValue = verticalGroup;
        verticalGroup = aValue;
        firePropertyChange("verticalGroup", oldValue, verticalGroup);
    }

    @Serial
    public boolean isHonorsVisibility() {
        return honorsVisibility;
    }

    @Serial
    public void setHonorsVisibility(boolean aValue) {
        boolean oldValue = honorsVisibility;
        honorsVisibility = aValue;
        firePropertyChange("honorsVisibility", oldValue, honorsVisibility);
    }

    @Override
    public void accept(LayoutDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final GroupLayoutDesignInfo other = (GroupLayoutDesignInfo) obj;
        if (this.autoCreateContainerGaps != other.autoCreateContainerGaps) {
            return false;
        }
        if (this.autoCreateGaps != other.autoCreateGaps) {
            return false;
        }
        if (this.honorsVisibility != other.honorsVisibility) {
            return false;
        }
        if (this.horizontalGroup != other.horizontalGroup && (this.horizontalGroup == null || !this.horizontalGroup.isEqual(other.horizontalGroup))) {
            return false;
        }
        if (this.verticalGroup != other.verticalGroup && (this.verticalGroup == null || !this.verticalGroup.isEqual(other.verticalGroup))) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof GroupLayoutDesignInfo) {
            GroupLayoutDesignInfo source = (GroupLayoutDesignInfo) aSource;
            autoCreateContainerGaps = source.autoCreateContainerGaps;
            autoCreateGaps = source.autoCreateGaps;
            honorsVisibility = source.honorsVisibility;
            horizontalGroup = source.horizontalGroup != null ? (GroupLayoutGroupDesignInfo) source.horizontalGroup.copy() : null;
            verticalGroup = source.verticalGroup != null ? (GroupLayoutGroupDesignInfo) source.verticalGroup.copy() : null;
        }
    }
}
