/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui;

import com.bearsoft.routing.Connector;
import com.bearsoft.routing.QuadTree;
import com.eas.client.metadata.Field;
import com.eas.client.model.Entity;
import com.eas.client.model.Relation;
import com.eas.client.model.application.ReferenceRelation;
import com.eas.client.model.gui.view.entities.EntityView;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ResourceBundle;
import java.util.Set;
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

    public static String getString(String aKey) {
        return localizeString(aKey);
    }
    
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

    private interface RectanlgeCallback {

        public boolean run(Rectangle aValue);
    }

    private static <E extends Entity<?, ?, E>> void quadTreeOperation(Connector aConnector, RectanlgeCallback aPerformer) {
        if (aConnector != null) {
            for (int i = 1; i < aConnector.getSize(); i++) {
                int x1 = aConnector.getX()[i - 1];
                int y1 = aConnector.getY()[i - 1];
                int x2 = aConnector.getX()[i];
                int y2 = aConnector.getY()[i];
                Rectangle key = new Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1) + 1, Math.abs(y2 - y1) + 1);
                if (!aPerformer.run(key)) {
                    break;
                }
            }
        }
    }

    public static <E extends Entity<?, ?, E>> void addToQuadTree(final QuadTree<Relation<E>> aIndex, Connector aConnector, final Relation<E> aRelation) {
        quadTreeOperation(aConnector, new RectanlgeCallback() {
            @Override
            public boolean run(Rectangle aValue) {
                aIndex.insert(aValue, aRelation);
                return true;
            }
        });
    }

    public static <E extends Entity<?, ?, E>> void removeFromQuadTree(final QuadTree<Relation<E>> aIndex, Connector aConnector, final Relation<E> aRelation) {
        quadTreeOperation(aConnector, new RectanlgeCallback() {
            @Override
            public boolean run(Rectangle aValue) {
                aIndex.remove(aValue, aRelation);
                return true;
            }
        });
    }

    public static <E extends Entity<?, ?, E>> boolean hittestConnector(Connector aConnector, final Point aPoint, final int aEpsilon) {
        return hittestConnectorSegment(aConnector, aPoint, aEpsilon) > -1;
    }

    public static <E extends Entity<?, ?, E>> int hittestConnectorSegment(Connector aConnector, final Point aPoint, final int aEpsilon) {
        final Point resCounter = new Point();
        quadTreeOperation(aConnector, new RectanlgeCallback() {
            @Override
            public boolean run(Rectangle aValue) {
                resCounter.y++;
                aValue.grow(aEpsilon, aEpsilon);
                if (aValue.contains(aPoint)) {
                    resCounter.x++;
                    return false;
                }
                return true;
            }
        });
        return resCounter.x > 0 ? resCounter.y - 1 : -1;
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

    public static <E extends Entity<?, ?, E>> boolean isRelationAlreadyDefined(E leftEntity, Field leftField, E rightEntity, Field rightField) {
        if (leftEntity != null && rightEntity != null
                && leftField != null
                && rightField != null) {
            Set<Relation<E>> inRels = rightEntity.getInRelations();
            if (inRels != null) {
                for (Relation<E> rel : inRels) {
                    if (rel != null) {
                        assert !(rel instanceof ReferenceRelation<?>);
                        E lEntity = rel.getLeftEntity();
                        if (lEntity == leftEntity) {
                            if (leftField == rel.getLeftField()
                                    && rightField == rel.getRightField()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isLegalFieldName(String aName) {
        if (aName != null && !aName.isEmpty()) {
            aName = aName.trim();
            Pattern pat = Pattern.compile("[A-Z[А-Я]_]+[A-Z[А-Я[0-9]]_]*+", Pattern.CASE_INSENSITIVE);
            Matcher mat = pat.matcher(aName);
            return (aName != null && !aName.isEmpty()
                    && mat.matches() //&& !jsSyntax.isKeyWord(aName.toLowerCase())
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
