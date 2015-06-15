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
import com.eas.client.metadata.DataTypeInfo;
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
    protected static Scripts.LocalContext context;

    public static void initContext(Scripts.LocalContext aValue) {
        context = aValue;
    }
    
    public static Scripts.LocalContext getContext(){
        return context;
    }

    public static String getLocalizedString(String aKey) {
        if (rb.containsKey(aKey)) {
            return rb.getString(aKey);
        }
        return aKey;
    }

    public static ModelWidget chooseWidgetByType(DataTypeInfo aType) {
        switch (aType.getSqlType()) {
            // numbers
            case java.sql.Types.BIGINT:
            case java.sql.Types.DECIMAL:
            case java.sql.Types.DOUBLE:
            case java.sql.Types.FLOAT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.NUMERIC:
            case java.sql.Types.SMALLINT:
                return new ModelSpin();
            // booleans
            case java.sql.Types.BOOLEAN:
            case java.sql.Types.BIT:
                return new ModelCheckBox();
            // strings
            case java.sql.Types.CHAR:
            case java.sql.Types.CLOB:
            case java.sql.Types.LONGNVARCHAR:
            case java.sql.Types.LONGVARCHAR:
            case java.sql.Types.NCHAR:
            case java.sql.Types.NCLOB:
            case java.sql.Types.NVARCHAR:
            case java.sql.Types.VARCHAR:
                return new ModelFormattedField();
            // dates
            case java.sql.Types.DATE:
            case java.sql.Types.TIME:
            case java.sql.Types.TIMESTAMP:
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
