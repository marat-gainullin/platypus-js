/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.client.forms.components.model.ModelCheckBox;
import com.eas.client.forms.components.model.ModelDate;
import com.eas.client.forms.components.model.ModelFormattedField;
import com.eas.client.forms.components.model.ModelSpin;
import com.eas.client.forms.components.model.ModelTextArea;
import com.eas.client.forms.components.model.ModelWidget;
import com.eas.script.HasPublished;
import com.eas.script.Scripts;
import java.util.ResourceBundle;
import javax.swing.JComponent;

/**
 *
 * @author mg
 */
public class Forms {

    protected static ResourceBundle rb = ResourceBundle.getBundle(Form.class.getPackage().getName() + "/Bundle");

    public static String getLocalizedString(String aKey) {
        if (rb.containsKey(aKey)) {
            return rb.getString(aKey);
        }
        return aKey;
    }

    public static ModelWidget chooseWidgetByType(String aType) {
        switch (aType) {
            case Scripts.NUMBER_TYPE_NAME:
                return new ModelSpin();
            // booleans
            case Scripts.BOOLEAN_TYPE_NAME:
                return new ModelCheckBox();
            // strings
            case Scripts.STRING_TYPE_NAME:
                return new ModelFormattedField();
            // dates
            case Scripts.DATE_TYPE_NAME:
                return new ModelDate();
            default:
                return new ModelTextArea();
        }
    }

    public static Widget lookupPublishedParent(JComponent aWidget) {
        java.awt.Component cur = aWidget.getParent();
        while (cur != null && !(cur instanceof HasPublished)) {
            cur = cur.getParent();
        }
        return (Widget) cur;
    }
}
