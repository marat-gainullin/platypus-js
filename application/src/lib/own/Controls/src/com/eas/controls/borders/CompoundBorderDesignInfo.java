/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.borders;

import com.eas.controls.DesignInfo;
import com.eas.store.ClassedSerial;
import com.eas.store.PropertiesSimpleFactory;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class CompoundBorderDesignInfo extends BorderDesignInfo implements PropertiesSimpleFactory {

    protected BorderDesignInfo outsideBorder;
    protected BorderDesignInfo insideBorder;

    @ClassedSerial(propertyClassHint = "type")
    public BorderDesignInfo getOutsideBorder() {
        return outsideBorder;
    }

    @ClassedSerial(propertyClassHint = "type")
    public void setOutsideBorder(BorderDesignInfo aValue) {
        BorderDesignInfo oldValue = outsideBorder;
        outsideBorder = aValue;
        firePropertyChange("outsideBorder", oldValue, outsideBorder);
    }

    @ClassedSerial(propertyClassHint = "type")
    public BorderDesignInfo getInsideBorder() {
        return insideBorder;
    }

    @ClassedSerial(propertyClassHint = "type")
    public void setInsideBorder(BorderDesignInfo aValue) {
        BorderDesignInfo oldValue = insideBorder;
        insideBorder = aValue;
        firePropertyChange("insideBorder", oldValue, insideBorder);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final CompoundBorderDesignInfo other = (CompoundBorderDesignInfo) obj;
        if (!outsideBorder.isEqual(other.outsideBorder)) {
            return false;
        }
        if (!insideBorder.isEqual(other.insideBorder)) {
            return false;
        }
        return true;
    }

    protected void assign(CompoundBorderDesignInfo aSource) {
        outsideBorder = aSource.outsideBorder != null ? (BorderDesignInfo) aSource.outsideBorder.copy() : null;
        insideBorder = aSource.insideBorder != null ? (BorderDesignInfo) aSource.insideBorder.copy() : null;
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource instanceof CompoundBorderDesignInfo) {
            assign((CompoundBorderDesignInfo) aSource);
        }
    }

    @Override
    public void accept(BorderDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
    
    @Override
    public Object createPropertyObjectInstance(String aSimpleClassName) {
        Object maybeBorder = (new BorderPropertyFactory()).createPropertyObjectInstance(aSimpleClassName);
        if (maybeBorder != null) {
            return maybeBorder;
        } else {
            Logger.getLogger(TitledBorderDesignInfo.class.getName()).severe(String.format("Unknown border type occured: %s", aSimpleClassName));
            return null;
        }
    }
}
