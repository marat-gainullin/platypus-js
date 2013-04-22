/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.plain;

import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.DesignInfo;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class EditorPaneDesignInfo extends TextFieldDesignInfo {

    protected String page;

    public EditorPaneDesignInfo()
    {
        super();
    }

    @Serial
    public String getPage() {
        return page;
    }

    @Serial
    public void setPage(String aValue) {
        String oldValue = page;
        page = aValue;
        firePropertyChange("page", oldValue, page);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        EditorPaneDesignInfo other = (EditorPaneDesignInfo) obj;
        if ((this.page == null) ? (other.page != null) : !this.page.equals(other.page)) {
            return false;
        }
        return true;
    }

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public void assign(DesignInfo aValue) {
        super.assign(aValue);
        if (aValue instanceof EditorPaneDesignInfo) {
            EditorPaneDesignInfo source = (EditorPaneDesignInfo) aValue;
            page = source.page != null ? new String(source.page.toCharArray()) : null;
        }
    }
}
