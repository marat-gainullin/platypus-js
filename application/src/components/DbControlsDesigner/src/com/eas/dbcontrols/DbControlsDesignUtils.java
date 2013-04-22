/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.Client;
import com.eas.client.SQLUtils;
import com.eas.client.datamodel.ApplicationEntity;
import com.eas.client.datamodel.ApplicationModel;
import com.eas.client.datamodel.ApplicationParametersEntity;
import com.eas.client.datamodel.ModelElementRef;
import com.eas.client.datamodel.gui.DatamodelDesignUtils;
import com.eas.client.datamodel.gui.view.FieldsTypeIconsCache;
import com.eas.client.queries.Query;
import com.eas.client.utils.syntax.JsHighlighting;
import com.eas.dbcontrols.check.DbCheck;
import com.eas.dbcontrols.check.DbCheckBeanInfo;
import com.eas.dbcontrols.combo.DbCombo;
import com.eas.dbcontrols.combo.DbComboBeanInfo;
import com.eas.dbcontrols.date.DbDate;
import com.eas.dbcontrols.date.DbDateBeanInfo;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.dbcontrols.grid.DbGridBeanInfo;
import com.eas.dbcontrols.image.DbImage;
import com.eas.dbcontrols.image.DbImageBeanInfo;
import com.eas.dbcontrols.label.DbLabel;
import com.eas.dbcontrols.label.DbLabelBeanInfo;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.DbMapBeanInfo;
import com.eas.dbcontrols.scheme.DbScheme;
import com.eas.dbcontrols.scheme.DbSchemeBeanInfo;
import com.eas.dbcontrols.spin.DbSpin;
import com.eas.dbcontrols.spin.DbSpinBeanInfo;
import com.eas.dbcontrols.text.DbText;
import com.eas.dbcontrols.text.DbTextBeanInfo;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.beans.BeanInfo;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 *
 * @author mg
 */
public class DbControlsDesignUtils {

    public static final String CONTAINER_ATTRIBUTE_NAME = "isContainer";
    protected static ResourceBundle rb = ResourceBundle.getBundle(DbControlsDesignUtils.class.getPackage().getName() + "/design18n");
    protected static final Map<Class<?>, BeanInfo> controlsBeanInfo = new HashMap<>();

    static {
        controlsBeanInfo.put(DbCheck.class, new DbCheckBeanInfo());
        controlsBeanInfo.put(DbCombo.class, new DbComboBeanInfo());
        controlsBeanInfo.put(DbDate.class, new DbDateBeanInfo());
        controlsBeanInfo.put(DbGrid.class, new DbGridBeanInfo());
        controlsBeanInfo.put(DbImage.class, new DbImageBeanInfo());
        controlsBeanInfo.put(DbLabel.class, new DbLabelBeanInfo());
        controlsBeanInfo.put(DbScheme.class, new DbSchemeBeanInfo());
        controlsBeanInfo.put(DbSpin.class, new DbSpinBeanInfo());
        controlsBeanInfo.put(DbText.class, new DbTextBeanInfo());
        controlsBeanInfo.put(DbMap.class, new DbMapBeanInfo());
    }

    private static void resolveFieldMd(ApplicationModel dm, ModelElementRef dmElement) throws Exception {
        if (dm != null && dmElement != null && dmElement.getField() != null) {
            Long entId = dmElement.getEntityId();
            Map<Long, ApplicationEntity> lentities = dm.getEntities();
            if (lentities != null && entId != null) {
                ApplicationEntity entity = lentities.get(entId);
                if (entity != null) {
                    if (dmElement.isField()) {
                        dmElement.setField(entity.getFields().get(dmElement.getFieldName()));
                    } else {
                        Query query = entity.getQuery();
                        if (query != null) {
                            dmElement.setField(query.getParameters().get(dmElement.getFieldName()));
                        }
                    }
                }
            }
        }
    }

    public static void updateDmElement(ApplicationModel aModel, ModelElementRef dmElement, JPanel aPnlDmField, FieldRefRenderer aFieldRenderer, JTextField aTxtDmElementField, Font aFieldsFont) throws Exception {
        if (aPnlDmField != null && aTxtDmElementField != null && aPnlDmField.isVisible()) {
            aPnlDmField.remove(aFieldRenderer);
            aPnlDmField.add(aTxtDmElementField, BorderLayout.CENTER);
            if (dmElement != null && dmElement.entityId != null) {
                if (dmElement.getFieldName() != null && !dmElement.getFieldName().isEmpty()
                        && dmElement.getField() != null && dmElement.getField().getTypeInfo().getSqlType() == RowsetUtils.INOPERABLE_TYPE_MARKER) {
                    resolveFieldMd(aModel, dmElement);
                }
                setupDatamodelElementRenderer(aModel, dmElement, aFieldRenderer, aFieldsFont);
                aPnlDmField.remove(aTxtDmElementField);
                aPnlDmField.add(aFieldRenderer, BorderLayout.CENTER);
            }
            aPnlDmField.doLayout();
            aPnlDmField.repaint();
        }
    }

    public static void updateFont(Font aFont, JPanel aPnlFont, JTextField aTxtFont, Font aFieldsFont) {
        if (aPnlFont.isVisible()) {
            aTxtFont.setText(null);
            if (aFont != null) {
                aTxtFont.setText(generateFontDescription(aFont));
                aTxtFont.setFont(aFont);
            } else {
                aTxtFont.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
                aTxtFont.setFont(aFieldsFont);
            }
        }
    }

    public static String generateFontDescription(Font font) {
        String fontString = font.getFamily();
        fontString += ", " + String.valueOf(font.getSize()) + ", ";
        if (font.isBold()) {
            fontString += font.isItalic() ? DbControlsDesignUtils.getLocalizedString("bolditalic") : DbControlsDesignUtils.getLocalizedString("bold");
        } else {
            fontString += font.isItalic() ? DbControlsDesignUtils.getLocalizedString("italic") : DbControlsDesignUtils.getLocalizedString("plain");
        }
        return fontString;
    }

    public static void setupDatamodelElementRenderer(ApplicationModel<Client> aModel, ModelElementRef dmElement, FieldRefRenderer aRenderer, Font aFieldsFont) {
        if (aModel != null && dmElement != null && dmElement.getEntityId() != null) {
            String rendTitle = null;
            String fieldTitle = null;
            int liconTextGap = 4;
            if (dmElement.getField() != null) {
                Field fmd = dmElement.getField();
                String fieldName = fmd.getName();
                String ldesc = null;
                if (fmd instanceof Parameter) {
                    Parameter pmd = (Parameter) fmd;
                    ldesc = pmd.getDescription();
                } else {
                    ldesc = fmd.getDescription();
                }
                if (ldesc == null || ldesc.isEmpty()) {
                    ldesc = fieldName;
                }

                String lTypeName = SQLUtils.getLocalizedTypeName(fmd.getTypeInfo().getSqlType());

                Icon pkIcon = null;
                Icon fkIcon = null;
                Icon typeIcon = FieldsTypeIconsCache.getIcon16(fmd.getTypeInfo().getSqlType());

                boolean lisPk = fmd.isPk();
                boolean lisFk = fmd.isFk();
                if (lisPk) {
                    lTypeName = SQLUtils.getLocalizedPkName() + "." + lTypeName;
                    pkIcon = FieldsTypeIconsCache.getPkIcon16();
                    //liconTextGap += pkIcon.getIconWidth() + 2;
                }
                if (lisFk) {
                    lTypeName = SQLUtils.getLocalizedFkName() + "." + lTypeName;
                    fkIcon = FieldsTypeIconsCache.getFkIcon16();
                    //liconTextGap += fkIcon.getIconWidth() + 2;
                }
                if (pkIcon != null && fkIcon == null) {
                    aRenderer.setIcon(typeIcon);
                    aRenderer.setSecondIcon(pkIcon);
                    aRenderer.setThirdIcon(null);
                } else if (pkIcon == null && fkIcon != null) {
                    aRenderer.setIcon(typeIcon);
                    aRenderer.setSecondIcon(fkIcon);
                    aRenderer.setThirdIcon(null);
                } else if (pkIcon != null && fkIcon != null) {
                    aRenderer.setIcon(typeIcon);
                    aRenderer.setSecondIcon(pkIcon);
                    aRenderer.setThirdIcon(fkIcon);
                } else {
                    aRenderer.setIcon(typeIcon);
                    aRenderer.setSecondIcon(null);
                    aRenderer.setThirdIcon(null);
                }
                fieldTitle = (ldesc != null && !ldesc.isEmpty()) ? ldesc : fieldName;
                if (fieldTitle == null) {
                    fieldTitle = "";
                }
                if (lTypeName != null && !lTypeName.isEmpty()) {
                    if (!fieldTitle.isEmpty()) {
                        fieldTitle += " : ";
                    }
                    fieldTitle += lTypeName;
                }
            } else {
                aRenderer.setIcon(DesignIconCache.getIcon("16x16/rowset.png"));
            }
            ApplicationEntity ent = aModel.getEntities().get(dmElement.getEntityId());
            String entTitle = ent != null ? ((ent instanceof ApplicationParametersEntity) ? DatamodelDesignUtils.getLocalizedString("Parameters") : ent.getTitle()) : "";
            rendTitle = entTitle;
            if (rendTitle == null) {
                rendTitle = "";
            }
            if (fieldTitle != null && !fieldTitle.isEmpty()) {
                if (!rendTitle.isEmpty()) {
                    rendTitle += ".";
                }
                rendTitle += fieldTitle;
            }

            aRenderer.setText(rendTitle);
            aRenderer.setIconTextGap(liconTextGap);
            aRenderer.setFont(aFieldsFont);
        }
    }

    public static void setSelectedItem(JComboBox aCombo, Object aItem) {
        Action action = aCombo.getAction();
        try {
            aCombo.setAction(null);
            aCombo.setSelectedItem(aItem);
        } finally {
            aCombo.setAction(action);
        }
    }

    public static void setSelectedIndex(JComboBox aCombo, int aIndex) {
        Action action = aCombo.getAction();
        try {
            aCombo.setAction(null);
            aCombo.setSelectedIndex(aIndex);
        } finally {
            aCombo.setAction(action);
        }
    }

    /*
     * public static void installScriptGenerator(JComboBox aCombo,
     * ScriptGeneratorListener aGenerator) { if (aCombo != null &&
     * aCombo.getEditor() != null && aCombo.getEditor().getEditorComponent() !=
     * null) { Component scEditor = aCombo.getEditor().getEditorComponent();
     * scEditor.removeMouseListener(aGenerator);
     * scEditor.addMouseListener(aGenerator); } }
     *
     */
    public static void updateScriptItem(JComboBox aCombo, String aScript) {
        setSelectedItem(aCombo, aScript);
        Component scriptEditor = aCombo.getEditor().getEditorComponent();
        if (scriptEditor instanceof JTextComponent) {
            JTextComponent txtEditor = (JTextComponent) scriptEditor;
            Action action = aCombo.getAction();
            try {
                aCombo.setAction(null);
                txtEditor.setText(aScript);
            } finally {
                aCombo.setAction(action);
            }
        }
    }
    private static JsHighlighting jsSyntax = new JsHighlighting();

    public static boolean isNameLegalColumnName(String sId) {
        if (sId != null && !sId.isEmpty()) {
            sId = sId.trim();
            return (sId != null && !sId.isEmpty()
                    && Pattern.matches("[A-Z[a-z[А-Я[а-я_]]]]+[A-Z[a-z[А-Я[а-я[0-9_]]]]]*+", sId)
                    && !jsSyntax.isKeyWord(sId.toLowerCase())
                    && !jsSyntax.isKeyWord(sId.toUpperCase()));
        }
        return false;
    }

    public static Collection<BeanInfo> getDbControlsBeanInfos() {
        return controlsBeanInfo.values();
    }

    public static BeanInfo getSampleBeanInfo(Class<?> aControlClass) {
        return controlsBeanInfo.get(aControlClass);
    }

    public static String getLocalizedString(String aKey) {
        if (rb.containsKey(aKey)) {
            return rb.getString(aKey);
        }
        return aKey;
    }
}
