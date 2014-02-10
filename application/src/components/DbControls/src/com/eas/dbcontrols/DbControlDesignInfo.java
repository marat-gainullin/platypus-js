/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.eas.client.model.ModelElementRef;
import com.eas.controls.ControlDesignInfo;
import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.DesignInfo;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public abstract class DbControlDesignInfo extends ControlDesignInfo {

    public static final String DATAMODELELEMENT = "datamodelElement";
    public static final String SELECTFUNCTION = "selectFunction";
    public static final String HANDLEFUNCTION = "handleFunction";
    public static final String SELECTONLY = "selectOnly";
    public static final String EDITABLE = "editable";
    protected ModelElementRef datamodelElement = null;
    protected String selectFunction = "";
    protected String handleFunction = "";
    protected boolean selectOnly = false;
    protected boolean editable = true;

    public DbControlDesignInfo()
    {
        super();
        opaque = true;
    }
    
    protected abstract void accept(DbControlsDesignInfoVisitor aVisitor);

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        if(aVisitor instanceof DbControlsDesignInfoVisitor)
        {
            accept((DbControlsDesignInfoVisitor)aVisitor);
        }
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final DbControlDesignInfo other = (DbControlDesignInfo) obj;
        if (this.datamodelElement != other.datamodelElement && (this.datamodelElement == null || !this.datamodelElement.equals(other.datamodelElement))) {
            return false;
        }        
        if (this.selectFunction != other.selectFunction && (this.selectFunction == null || !this.selectFunction.equals(other.selectFunction))) {
            return false;
        }
        if (this.handleFunction != other.handleFunction && (this.handleFunction == null || !this.handleFunction.equals(other.handleFunction))) {
            return false;
        }
        if (this.selectOnly != other.selectOnly) {
            return false;
        }
        if (this.editable != other.editable) {
            return false;
        }
        return true;
    }

    @Serial
    public ModelElementRef getDatamodelElement() {
        return datamodelElement;
    }

    @Serial
    public void setDatamodelElement(ModelElementRef aValue) {
        ModelElementRef old = datamodelElement;
        datamodelElement = aValue;
        firePropertyChange(DATAMODELELEMENT, old, aValue);
    }

    @Serial
    public String getSelectFunction() {
        return selectFunction;
    }

    @Serial
    public void setSelectFunction(String aValue) {
        String old = selectFunction;
        selectFunction = aValue;
        firePropertyChange(SELECTFUNCTION, old, aValue);
    }

    @Serial
    public String getHandleFunction() {
        return handleFunction;
    }

    @Serial
    public void setHandleFunction(String aValue) {
        String old = handleFunction;
        handleFunction = aValue;
        firePropertyChange(HANDLEFUNCTION, old, aValue);
    }

    public String getOnSelect() {
        return selectFunction;
    }

    public void setOnSelect(String aValue) {
        selectFunction = aValue;
    }

    public String getOnRender() {
        return handleFunction;
    }

    public void setOnRender(String aValue) {
        handleFunction = aValue;
    }
    
    @Serial
    public boolean isSelectOnly() {
        return selectOnly;
    }

    @Serial
    public void setSelectOnly(boolean aValue) {
        boolean old = selectOnly;
        selectOnly = aValue;
        firePropertyChange(SELECTONLY, old, aValue);
    }

    @Serial
    public boolean isEditable() {
        return editable;
    }

    @Serial
    public void setEditable(boolean aValue) {
        boolean old = editable;
        editable = aValue;
        firePropertyChange(EDITABLE, old, aValue);
    }

    protected void assign(DbControlDesignInfo aSource) {
        if (aSource != null) {
            setDatamodelElement(aSource.getDatamodelElement() != null ? aSource.getDatamodelElement().copy() : null);
            setSelectFunction(aSource.getSelectFunction());
            setHandleFunction(aSource.getHandleFunction());
            setSelectOnly(aSource.isSelectOnly());
            setEditable(aSource.isEditable());
        }
    }

    @Override
    public void assign(DesignInfo aValue) {
        super.assign(aValue);
        if (aValue instanceof DbControlDesignInfo) {
            assign((DbControlDesignInfo) aValue);
        }
    }
}
