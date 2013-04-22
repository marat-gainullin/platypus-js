/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.controls.plain;

import com.eas.controls.ControlsDesignInfoVisitor;
import com.eas.controls.DesignInfo;

/**
 *
 * @author mg
 */
public class TextPaneDesignInfo extends TextFieldDesignInfo {

    public TextPaneDesignInfo() {
        super();
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        TextPaneDesignInfo other = (TextPaneDesignInfo) obj;
        return true;
    }

    @Override
    public void accept(ControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }

    @Override
    public void assign(DesignInfo aValue) {
        super.assign(aValue);
        if (aValue instanceof TextPaneDesignInfo) {
            TextPaneDesignInfo source = (TextPaneDesignInfo) aValue;
        }
    }
}
