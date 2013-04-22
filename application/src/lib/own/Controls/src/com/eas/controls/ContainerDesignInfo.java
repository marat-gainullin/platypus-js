/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls;

import com.eas.controls.layouts.*;
import com.eas.store.ClassedSerial;
import com.eas.store.PropertiesSimpleFactory;
import com.eas.store.Serial;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public abstract class ContainerDesignInfo extends ControlDesignInfo implements PropertiesSimpleFactory {

    protected boolean focusCycleRoot = true;
    protected LayoutDesignInfo layout;

    public ContainerDesignInfo() {
        super();
    }

    @Serial
    public boolean isFocusCycleRoot() {
        return focusCycleRoot;
    }

    @Serial
    public void setFocusCycleRoot(boolean aValue) {
        boolean oldValue = focusCycleRoot;
        focusCycleRoot = aValue;
        firePropertyChange("focusCycleRoot", oldValue, focusCycleRoot);
    }

    @ClassedSerial(propertyClassHint="type")
    public LayoutDesignInfo getLayout() {
        return layout;
    }

    @ClassedSerial(propertyClassHint="type")
    public void setLayout(LayoutDesignInfo aValue) {
        LayoutDesignInfo oldValue = layout;
        layout = aValue;
        firePropertyChange("layout", oldValue, layout);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        ContainerDesignInfo other = (ContainerDesignInfo) obj;
        if (focusCycleRoot != other.focusCycleRoot) {
            return false;
        }
        if ((this.layout == null) ? (other.layout != null) : !this.layout.isEqual(other.layout)) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aValue) {
        super.assign(aValue);
        if (aValue instanceof ContainerDesignInfo) {
            ContainerDesignInfo source = (ContainerDesignInfo) aValue;
            layout = source.layout != null ? (LayoutDesignInfo) source.layout.copy() : null;
            focusCycleRoot = source.focusCycleRoot;
        }
    }

    @Override
    public Object createPropertyObjectInstance(String aSimpleClassName) {
        if (BorderLayoutDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new BorderLayoutDesignInfo();
        } else if (BoxLayoutDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new BoxLayoutDesignInfo();
        } else if (CardLayoutDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new CardLayoutDesignInfo();
        } else if (FlowLayoutDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new FlowLayoutDesignInfo();
        } else if (GridBagLayoutDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new GridBagLayoutDesignInfo();
        } else if (GridLayoutDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new GridLayoutDesignInfo();
        } else if (GroupLayoutDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new GroupLayoutDesignInfo();
        } else if (GroupLayoutGroupDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new GroupLayoutGroupDesignInfo();
        } else if (AbsoluteLayoutDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new AbsoluteLayoutDesignInfo();
        } else if (MarginLayoutDesignInfo.class.getSimpleName().equals(aSimpleClassName)) {
            return new MarginLayoutDesignInfo();    
        } else {
            Object res = super.createPropertyObjectInstance(aSimpleClassName);
            if (res == null) {
                Logger.getLogger(ControlDesignInfo.class.getName()).severe(String.format("Neither layout nor constraints found with such name:: %s", aSimpleClassName));
            }
            return res;
        }
    }
}
