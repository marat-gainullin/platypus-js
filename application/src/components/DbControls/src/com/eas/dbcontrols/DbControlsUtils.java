/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.dbcontrols.check.DbCheck;
import com.eas.dbcontrols.check.DbCheckDesignInfo;
import com.eas.dbcontrols.combo.DbCombo;
import com.eas.dbcontrols.combo.DbComboDesignInfo;
import com.eas.dbcontrols.date.DbDate;
import com.eas.dbcontrols.date.DbDateDesignInfo;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import com.eas.dbcontrols.image.DbImage;
import com.eas.dbcontrols.image.DbImageDesignInfo;
import com.eas.dbcontrols.label.DbLabel;
import com.eas.dbcontrols.label.DbLabelDesignInfo;
import com.eas.dbcontrols.spin.DbSpin;
import com.eas.dbcontrols.spin.DbSpinDesignInfo;
import com.eas.dbcontrols.text.DbText;
import com.eas.dbcontrols.text.DbTextDesignInfo;
import com.eas.gui.CascadedStyle;
import com.eas.gui.Font;
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

/**
 *
 * @author mg
 */
public class DbControlsUtils {

    protected static ResourceBundle rb = ResourceBundle.getBundle(DbControlsUtils.class.getPackage().getName() + "/Bundle");
    protected static final Map<Integer, Class<?>[]> typesControls = new HashMap<>();
    protected static final Map<Class<?>, Class<?>> controlsDesignClasses = new HashMap<>();
    protected static final String DESIGN_INFO_KEY = "CustomContentRead";
    protected static final Color DB_CONTROLS_BORDER_COLOR = controlsBorderColor();
    public static final int DB_CONTROLS_DEFAULT_WIDTH = 100;
    public static final int DB_CONTROLS_DEFAULT_HEIGHT = 100;

    private static Color controlsBorderColor() {
        Color lafGridColor = UIManager.getColor("Table.gridColor");
        if (lafGridColor != null) {
            return lafGridColor;
        } else {
            return new Color(103, 144, 185);
        }
    }

    static {
        // Numbers
        typesControls.put(java.sql.Types.BIGINT, new Class<?>[]{
            DbSpin.class, DbCheck.class, DbLabel.class, DbText.class, DbCombo.class
        });
        typesControls.put(java.sql.Types.DECIMAL, new Class<?>[]{
            DbSpin.class, DbCheck.class, DbLabel.class, DbText.class, DbCombo.class
        });
        typesControls.put(java.sql.Types.DOUBLE, new Class<?>[]{
            DbSpin.class, DbCheck.class, DbLabel.class, DbText.class, DbCombo.class
        });
        typesControls.put(java.sql.Types.FLOAT, new Class<?>[]{
            DbSpin.class, DbCheck.class, DbLabel.class, DbText.class, DbCombo.class
        });
        typesControls.put(java.sql.Types.INTEGER, new Class<?>[]{
            DbSpin.class, DbCheck.class, DbLabel.class, DbText.class, DbCombo.class
        });
        typesControls.put(java.sql.Types.NUMERIC, new Class<?>[]{
            DbSpin.class, DbCheck.class, DbLabel.class, DbText.class, DbCombo.class
        });
        typesControls.put(java.sql.Types.REAL, new Class<?>[]{
            DbSpin.class, DbCheck.class, DbLabel.class, DbText.class, DbCombo.class
        });
        typesControls.put(java.sql.Types.SMALLINT, new Class<?>[]{
            DbSpin.class, DbCheck.class, DbLabel.class, DbText.class, DbCombo.class
        });
        // Logical
        typesControls.put(java.sql.Types.BOOLEAN, new Class<?>[]{
            DbCheck.class
        });
        typesControls.put(java.sql.Types.BIT, new Class<?>[]{
            DbCheck.class
        });
        // Binaries
        typesControls.put(java.sql.Types.VARBINARY, new Class<?>[]{
            DbImage.class
        });
        typesControls.put(java.sql.Types.BINARY, new Class<?>[]{
            DbImage.class
        });
        typesControls.put(java.sql.Types.BLOB, new Class<?>[]{
            DbImage.class
        });
        typesControls.put(java.sql.Types.LONGVARBINARY, new Class<?>[]{
            DbImage.class
        });
        // Strings
        typesControls.put(java.sql.Types.CHAR, new Class<?>[]{
            DbLabel.class, DbText.class, DbCombo.class
        });
        typesControls.put(java.sql.Types.CLOB, new Class<?>[]{
            DbLabel.class, DbText.class, //, DbCombo.class
        });
        typesControls.put(java.sql.Types.LONGNVARCHAR, new Class<?>[]{
            DbLabel.class, DbText.class, DbCombo.class
        });
        typesControls.put(java.sql.Types.LONGVARCHAR, new Class<?>[]{
            DbLabel.class, DbText.class, DbCombo.class
        });
        typesControls.put(java.sql.Types.NCHAR, new Class<?>[]{
            DbLabel.class, DbText.class, DbCombo.class
        });
        typesControls.put(java.sql.Types.NCLOB, new Class<?>[]{
            DbLabel.class, DbText.class//, DbCombo.class
        });
        typesControls.put(java.sql.Types.NVARCHAR, new Class<?>[]{
            DbLabel.class, DbText.class, DbCombo.class
        });
        typesControls.put(java.sql.Types.VARCHAR, new Class<?>[]{
            DbLabel.class, DbText.class, DbCombo.class, DbCheck.class
        });
        typesControls.put(java.sql.Types.SQLXML, new Class<?>[]{
            DbLabel.class, DbText.class//, DbCombo.class
        });
        // Dates, times
        typesControls.put(java.sql.Types.DATE, new Class<?>[]{
            DbDate.class
        });
        typesControls.put(java.sql.Types.TIME, new Class<?>[]{
            DbDate.class
        });
        typesControls.put(java.sql.Types.TIMESTAMP, new Class<?>[]{
            DbDate.class
        });
        // Others
        typesControls.put(java.sql.Types.STRUCT, new Class<?>[]{
            DbLabel.class
        });
        typesControls.put(java.sql.Types.OTHER, new Class<?>[]{
            DbLabel.class
        });

        controlsDesignClasses.put(DbCheck.class, DbCheckDesignInfo.class);
        controlsDesignClasses.put(DbCombo.class, DbComboDesignInfo.class);
        controlsDesignClasses.put(DbDate.class, DbDateDesignInfo.class);
        controlsDesignClasses.put(DbGrid.class, DbGridDesignInfo.class);
        controlsDesignClasses.put(DbImage.class, DbImageDesignInfo.class);
        controlsDesignClasses.put(DbLabel.class, DbLabelDesignInfo.class);
        controlsDesignClasses.put(DbSpin.class, DbSpinDesignInfo.class);
        controlsDesignClasses.put(DbText.class, DbTextDesignInfo.class);
    }

    public static Object createDesignInfoBySimpleClassName(String aSimpleClassName) {
        for (Class<?> cl : controlsDesignClasses.values()) {
            try {
                if (cl != null && cl.getSimpleName().equals(aSimpleClassName)) {
                    return cl.newInstance();
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(DbControlsUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static Set<Class<?>> getDbControlsClasses() {
        return controlsDesignClasses.keySet();
    }

    public static Class<?> getDesignInfoClass(Class<?> aControlClass) {
        if (aControlClass != null) {
            return controlsDesignClasses.get(aControlClass);
        }
        return null;
    }

    public static Class<?>[] getCompatibleControls(int type) {
        return typesControls.get(type);
    }

    public static boolean isControlCompatible2Type(Class<?> aControlClass, int aType) {
        if (aControlClass != null) {
            Class<?>[] controlClasses = typesControls.get(aType);
            if (controlClasses != null) {
                for (Class<?> controlClass : controlClasses) {
                    if (controlClass != null && controlClass.isAssignableFrom(aControlClass)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isDesignInfoCompatible2Type(Class<?> aDesignInfoClass, int aType) {
        if (aDesignInfoClass != null) {
            Class<?>[] controlClasses = typesControls.get(aType);
            if (controlClasses != null) {
                for (Class<?> controlClass : controlClasses) {
                    Class<?> designInfoClass = controlsDesignClasses.get(controlClass);
                    if (designInfoClass != null && designInfoClass.isAssignableFrom(aDesignInfoClass)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static ApplicationEntity<?, ?, ?> resolveEntity(ApplicationModel<?, ?> aModel, ModelElementRef aModelRef) throws Exception {
        if (aModel != null && aModelRef != null && aModelRef.getEntityId() != null) {
            return aModel.getEntityById(aModelRef.getEntityId());
        }
        return null;
    }

    public static Rowset resolveRowset(ApplicationModel<?, ?> aModel, ModelElementRef aModelRef) throws Exception {
        if (aModel != null && aModelRef != null && aModelRef.getEntityId() != null) {
            ApplicationEntity<?, ?, ?> appEntity = aModel.getEntityById(aModelRef.getEntityId());
            if (appEntity != null) {
                return appEntity.getRowset();
            }
        }
        return null;
    }

    public static int resolveFieldIndex(ApplicationModel<?, ?> aModel, ModelElementRef aModelRef) throws Exception {
        if (aModel != null && aModelRef != null && aModelRef.getEntityId() != null) {
            ApplicationEntity<?, ?, ?> appEntity = aModel.getEntityById(aModelRef.getEntityId());
            if (appEntity != null) {
                Rowset rs = appEntity.getRowset();
                if (rs != null) {
                    return rs.getFields().find(aModelRef.getFieldName());
                }
            }
        }
        return 0;
    }

    public static Field resolveField(ApplicationModel<?, ?> aModel, ModelElementRef aModelRef) throws Exception {
        if (aModel != null && aModelRef != null && aModelRef.getEntityId() != null) {
            ApplicationEntity<?, ?, ?> appEntity = aModel.getEntityById(aModelRef.getEntityId());
            if (appEntity != null) {
                Fields fields = appEntity.getFields();
                if (fields != null) {
                    return fields.get(aModelRef.getFieldName());
                }
            }
        }
        return null;
    }

    public static Parameter resolveParameter(ApplicationModel<?, ?> aModel, ModelElementRef aModelRef) throws Exception {
        if (aModel != null && aModelRef != null && aModelRef.getEntityId() != null) {
            ApplicationEntity<?, ?, ?> appEntity = aModel.getEntityById(aModelRef.getEntityId());
            if (!aModelRef.isField() && appEntity != null && appEntity.getQuery() != null) {
                Parameters params = appEntity.getQuery().getParameters();
                if (params != null) {
                    return params.get(aModelRef.getFieldName());
                }
            }
        }
        return null;
    }

    public static JTable getFirstTable(Component aComp) {
        Component lParent = aComp;
        while (lParent != null && !(lParent instanceof JTable)) {
            lParent = lParent.getParent();
        }
        if (lParent != null && lParent instanceof JTable) {
            return (JTable) lParent;
        }
        return null;
    }

    public static InputMap fillInputMap(ActionMap am) {
        if (am != null) {
            InputMap im = new InputMap();
            Object[] keys = am.keys();
            if (keys != null) {
                for (Object key : keys) {
                    Action action = am.get(key);
                    if (action != null) {
                        Object stroke = action.getValue(Action.ACCELERATOR_KEY);
                        if (stroke != null && stroke instanceof KeyStroke) {
                            im.put((KeyStroke) stroke, key);
                        }
                    }
                }
            }
            return im;
        }
        return null;
    }

    public static ListSelectionModel ajustSelection(ListSelectionModel aSelection, int dy) {
        DefaultListSelectionModel res = new DefaultListSelectionModel();
        for (int sel = aSelection.getMinSelectionIndex(); sel <= aSelection.getMaxSelectionIndex(); sel++) {
            if (aSelection.isSelectedIndex(sel)) {
                res.addSelectionInterval(sel + dy, sel + dy);
            }
        }
        return res;
    }

    public static java.awt.Font toNativeFont(Font aFont) {
        return aFont != null ? new java.awt.Font(aFont.getFamily(), CascadedStyle.fontStyleToNativeFontStyle(aFont.getStyle()), aFont.getSize()) : null;
    }

    public static Font toFont(java.awt.Font aNativeFont) {
        return aNativeFont != null ? new Font(aNativeFont.getFamily(), CascadedStyle.nativeFontStyleToFontStyle(aNativeFont), aNativeFont.getSize()) : null;
    }
}
