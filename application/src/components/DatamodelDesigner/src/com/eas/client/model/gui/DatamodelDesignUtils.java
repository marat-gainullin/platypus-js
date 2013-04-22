/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui;

import com.eas.client.model.Entity;
import com.eas.client.model.gui.view.entities.EntityView;
import java.awt.Component;
import java.awt.Font;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

/**
 *
 * @author mg
 */
public class DatamodelDesignUtils {

    private static final ResourceBundle messages = java.util.ResourceBundle.getBundle("com/eas/client/model/gui/messages");
    public static Font fieldsFont = new Font("Arial", 0, 12);
    public static Font bindedFieldsFont = new Font("Arial", Font.BOLD, 12);
    public static String SWITCH_2_DATASOURCES_ACTION_NAME = "SwitchToDatasources";

    public static String getLocalizedString(String aKey) {
        return localizeString(aKey);
    }

    public static String localizeString(String aKey) {
        if (messages.containsKey(aKey)) {
            return messages.getString(aKey);
        } else {
            return aKey;
        }
    }

    public static Font getFieldsFont() {
        return fieldsFont;
    }

    public static Font getBindedFieldsFont() {
        return bindedFieldsFont;
    }

    public static <T extends Entity<?, ?, T>> EntityView<T> lookupEntityView(Component aComp) {
        Component lParent = aComp;
        while (lParent != null && !(lParent instanceof EntityView<?>)) {
            lParent = lParent.getParent();
        }
        if (lParent != null && lParent instanceof EntityView<?>) {
            return (EntityView<T>) lParent;
        }
        return null;
    }

    public static JFrame getRootFrame(Component aComp) {
        Component lParent = aComp;
        while (lParent != null && !(lParent instanceof JFrame)) {
            lParent = lParent.getParent();
        }
        if (lParent != null && lParent instanceof JFrame) {
            return (JFrame) lParent;
        }
        return null;
    }

    public static JDialog getRootDialog(Component aComp) {
        Component lParent = aComp;
        while (lParent != null && !(lParent instanceof JDialog)) {
            lParent = lParent.getParent();
        }
        if (lParent != null && lParent instanceof JDialog) {
            return (JDialog) lParent;
        }
        return null;
    }

    public static void checkActions(JComponent aComponent) {
        ActionMap am = aComponent.getActionMap();
        for (Object key : am.allKeys()) {
            Action action = am.get(key);
            if (action != null) {
                action.setEnabled(action.isEnabled());
            }
        }
    }

    public static boolean isLegalFieldName(String aName) {
        if (aName != null && !aName.isEmpty()) {
            aName = aName.trim();
            Pattern pat = Pattern.compile("[A-Z[А-Я]_]+[A-Z[А-Я[0-9]]_]*+", Pattern.CASE_INSENSITIVE);
            Matcher mat = pat.matcher(aName);
            return (aName != null && !aName.isEmpty()
                    && mat.matches()
                    //&& !jsSyntax.isKeyWord(aName.toLowerCase())
                    //&& !org.hsqldb.Token.isKeyword(aName.toLowerCase())
                    //&& !jsSyntax.isKeyWord(aName.toUpperCase())
                    //&& !org.hsqldb.Token.isKeyword(aName.toUpperCase())
                    );
        }
        return false;
    }

    public static InputMap fillInputMap(ActionMap am) {
        InputMap keysMap = new InputMap();
        if (am != null) {
            Object[] oNames = am.allKeys();
            if (oNames != null) {
                for (int i = 0; i < oNames.length; i++) {
                    Action action = am.get(oNames[i]);
                    Object oKey = action.getValue(Action.ACCELERATOR_KEY);
                    if (oKey != null && oKey instanceof KeyStroke) {
                        keysMap.put((KeyStroke) oKey, oNames[i]);
                    }
                }
            }
        }
        return keysMap;
    }
}
