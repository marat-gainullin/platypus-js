/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.date;

import com.eas.dbcontrols.DbControl;
import com.eas.dbcontrols.InitializingMethod;
import com.eas.dbcontrols.date.rt.RendererOptimisticDateFormatter;
import de.wannawork.jcalendar.JCalendarComboBox;
import de.wannawork.jcalendar.JCalendarInvokerButton;
import de.wannawork.jcalendar.JCalendarPanel;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

/**
 *
 * @author mg
 */
public class DbDate extends DbControlPanel implements DbControl {

    private Object checkTime(Object aValue) {
        if (aValue instanceof Date) {
            Date dt = (Date) aValue;
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            if (isOnlyDateFormat()) {
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
            }
            return cal.getTime();
        }
        return aValue;
    }

    protected class DateChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            try {
                updateRowset();
            } catch (Exception ex) {
                Logger.getLogger(DbDate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void updateRowset() throws Exception {
            if (!DbDate.this.isUpdating()) {
                Object lValue = null;
                if (isExpanded()) {
                    lValue = datePanelRendererEditor.getCalendar().getTime();
                } else {
                    lValue = dateEditor.getModel().getValue();
                }
                lValue = checkTime(lValue);
                if (standalone) {
                    setValue(lValue);
                } else {
                    // If the change will be rejected by model, then editingValue will be changed back by grid's
                    // inner mechanisms.
                    setEditingValue(lValue);
                    fireCellEditingCompleted();
                }
            }
        }
    }
    public static final String DD = "DD";
    public static final String DD_MM = "DD_MM";
    public static final String DD_MM_YYYY = "DD_MM_YYYY";
    public static final String MM = "MM";
    public static final String MM_YYYY = "MM_YYYY";
    public static final String YYYY = "YYYY";
    public static final String MM_SS = "MM_SS";
    public static final String HH_MM = "HH_MM";
    public static final String HH_MM_SS = "HH_MM_SS";
    public static final String DD_MM_YYYY_HH_MM_SS = "DD_MM_YYYY_HH_MM_SS";
    public static final Date NULL_DATE_VALUE = new Date(-62135780400000L);
    public static final Pattern TIME_PATTERN = Pattern.compile("[aHkKhmsSzZX]");
    public static final Pattern NO_TIME_PATTERN = Pattern.compile("[GyYMwWDdFEu]");

    public DbDate() {
        super();
    }

    @Override
    protected void initializeDesign() {
        if (kind == InitializingMethod.UNDEFINED
                && getComponentCount() == 0) {
            super.initializeDesign();
            JCalendarComboBox ccb = new JCalendarComboBox();
            ccb.setOpaque(false);
            add(ccb, BorderLayout.CENTER);
        }
    }
    protected String dateFormat;
    protected boolean expanded;
    protected JCalendarPanel datePanelRendererEditor;
    protected Calendar datePanelCalendar;
    protected JFormattedTextField dateRenderer;
    protected JCalendarComboBox dateEditor;

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String aValue) {
        if (dateFormat == null ? aValue != null : !dateFormat.equals(aValue)) {
            dateFormat = convertString2DateFormat(aValue.trim());
            /*
             if (dateRenderer != null) {
             DateFormatter df = new RendererOptimisticDateFormatter(convertString2DateFormat(dateFormat));
             dateRenderer.setFormatterFactory(new DefaultFormatterFactory(df));
             }
             if (dateEditor != null) {
             dateEditor.setDateFormat(convertString2DateFormat(dateFormat));
             }
             */
        }
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean aValue) throws Exception {
        if (expanded != aValue) {
            expanded = aValue;
        }
    }

    public String getRendererText() {
        if (dateRenderer != null) {
            return dateRenderer.getText();
        }
        return null;
    }

    @Override
    protected void initializeRenderer() {
        if (kind != InitializingMethod.RENDERER) {
            kind = InitializingMethod.RENDERER;
            removeAll();
            setLayout(new BorderLayout());
            addIconLabel();
            if (isExpanded()) {
                datePanelRendererEditor = new JCalendarPanel();
                datePanelRendererEditor.setBorder(null);
                datePanelCalendar = Calendar.getInstance();
                datePanelRendererEditor.setCalendar(datePanelCalendar);
                add(datePanelRendererEditor, BorderLayout.CENTER);
            } else {
                DateFormatter df = new RendererOptimisticDateFormatter(new SimpleDateFormat(getDateFormat()));
                dateRenderer = new JFormattedTextField(new DefaultFormatterFactory(df));
                dateRenderer.setBorder(null);
                add(dateRenderer, BorderLayout.CENTER);
            }
            applyAlign();
            applyFont();
        }
    }

    @Override
    protected void setupRenderer(JTable table, int row, int column, boolean isSelected) {
        if (isExpanded()) {
            datePanelCalendar.setTime((Date) editingValue);
            datePanelRendererEditor.setCalendar(datePanelCalendar);
        } else {
            if (table != null) {
                if (isSelected) {
                    dateRenderer.setBackground(table.getSelectionBackground());
                    dateRenderer.setForeground(table.getSelectionForeground());
                } else {
                    dateRenderer.setBackground(table.getBackground());
                    dateRenderer.setForeground(table.getForeground());
                }
            }
            dateRenderer.setValue(editingValue);
            dateRenderer.setFont(getFont());
        }
    }

    @Override
    protected void initializeEditor() {
        if (kind != InitializingMethod.EDITOR) {
            kind = InitializingMethod.EDITOR;
            removeAll();
            setLayout(new BorderLayout());
            addIconLabel();
            if (isExpanded()) {
                datePanelRendererEditor = new JCalendarPanel();
                datePanelRendererEditor.setBorder(null);
                datePanelCalendar = Calendar.getInstance();
                datePanelRendererEditor.setCalendar(datePanelCalendar);
                checkEvents(datePanelRendererEditor);
                datePanelRendererEditor.addChangeListener(new DateChangeListener());
                datePanelRendererEditor.setOpaque(standalone && isOpaque());
                datePanelRendererEditor.setInheritsPopupMenu(true);
                add(datePanelRendererEditor, BorderLayout.CENTER);
            } else {
                dateEditor = new JCalendarComboBox(Calendar.getInstance(), Locale.getDefault(), new SimpleDateFormat(getDateFormat()), SwingConstants.RIGHT, true, true);
                if (borderless) {
                    dateEditor.setBorder(null);
                    dateEditor.setBorderless(true);
                }
                assert dateEditor.getEditorComponent() instanceof JFormattedTextField;
                JFormattedTextField tf = (JFormattedTextField) dateEditor.getEditorComponent();
                checkEvents(tf);
                if (!standalone) {
                    Object actionKey = tf.getInputMap().get(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
                    if (actionKey != null) {
                        tf.getActionMap().put(actionKey, new TextFieldsCommitAction(tf, actionKey));
                    }
                }
                if (isOnlyTimeFormat()) {
                    JCalendarInvokerButton jb = dateEditor.getDropDownButton();
                    if (jb != null) {
                        jb.setVisible(false);
                    }
                }
                dateEditor.addChangeListener(new DateChangeListener());
                dateEditor.setEnabled(false);
                dateEditor.setOpaque(false);
                dateEditor.setInheritsPopupMenu(true);
                add(dateEditor, BorderLayout.CENTER);
            }
            super.initializeEditor();
        }
    }

    @Override
    public void applyEditable2Field() {
        if (dateEditor != null) {
            dateEditor.setEditable(editable);
        }
    }

    @Override
    protected void setupEditor(JTable table) {
        if (isExpanded()) {
            datePanelCalendar.setTime((Date) editingValue);
            datePanelRendererEditor.setCalendar(datePanelCalendar);
        } else {
            if (dateEditor != null && dateEditor.getModel() != null) {
                dateEditor.getModel().setValue(editingValue);
            }
        }
        super.setupEditor(table);
    }

    @Override
    protected void applyFont() {
        if (datePanelRendererEditor != null) {
            datePanelRendererEditor.setFont(getFont());
        }
        if (dateEditor != null) {
            dateEditor.setFont(getFont());
        }
        if (dateRenderer != null) {
            dateRenderer.setFont(getFont());
        }
    }

    @Override
    protected void applyAlign() {
        if (dateRenderer != null) {
            applySwingAlign2TextAlign(dateRenderer, align);
        }
        if (dateEditor != null) {
            JComponent jComp = dateEditor.getEditorComponent();
            if (jComp instanceof JTextField) {
                applySwingAlign2TextAlign((JTextField) jComp, align);
            }
        }
        super.applyAlign();
    }

    @Override
    protected void applyTooltip(String aText) {
        if (datePanelRendererEditor != null) {
            datePanelRendererEditor.setToolTipText(aText);
        }
        if (dateEditor != null) {
            dateEditor.setToolTipText(aText);
        }
        if (dateRenderer != null) {
            dateRenderer.setToolTipText(aText);
        }
    }

    @Override
    public JComponent getFocusTargetComponent() {
        if (datePanelRendererEditor != null) {
            return datePanelRendererEditor;
        }
        if (dateEditor != null) {
            return dateEditor.getEditorComponent();
        }
        if (dateRenderer != null) {
            return dateRenderer;
        }
        return null;
    }

    @Override
    protected void invokingLaterProcessControls() {
        super.invokingLaterProcessControls();
        if (dateEditor != null) {
            dateEditor.setEnabled(true);
        }
    }

    @Override
    protected void invokingLaterUnprocessControls() {
        super.invokingLaterUnprocessControls();
        if (dateEditor != null) {
            dateEditor.setEnabled(false);
        }
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (datePanelRendererEditor != null) {
            datePanelRendererEditor.setBorder(null);
        }
    }

    @Override
    public Object getCellEditorValue() {
        if (kind == InitializingMethod.EDITOR) {
            if (isExpanded()) {
                editingValue = datePanelRendererEditor.getCalendar().getTime();
            } else {
                if (editingValue != null) {
                    editingValue = dateEditor.getModel().getDate();
                }
            }
        }
        return checkTime(editingValue);
    }

    @Override
    protected void applyEnabled() {
        if (dateEditor != null) {
            dateEditor.setEnabled(isEnabled());
        }
        if (datePanelRendererEditor != null) {
            datePanelRendererEditor.setEnabled(isEnabled());
        }
        if (dateRenderer != null) {
            dateRenderer.setEnabled(isEnabled());
        }
        super.applyEnabled();
    }

    @Override
    public void applyForeground() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (isExpanded()) {
                if (datePanelRendererEditor != null) {
                    datePanelRendererEditor.setForeground(getForeground());
                }
            } else {
                if (kind == InitializingMethod.RENDERER) {
                    if (dateRenderer != null) {
                        dateRenderer.setForeground(getForeground());
                    }
                } else {
                    if (dateEditor != null) {
                        dateEditor.setForeground(getForeground());
                    }
                }
            }
        }
    }

    @Override
    public void applyBackground() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (isExpanded()) {
                if (datePanelRendererEditor != null) {
                    datePanelRendererEditor.setBackground(getBackground());
                }
            } else {
                if (kind == InitializingMethod.RENDERER) {
                    if (dateRenderer != null) {
                        dateRenderer.setBackground(getBackground());
                    }
                } else {
                    if (dateEditor != null) {
                        dateEditor.setBackground(getBackground());
                    }
                }
            }
        }
    }

    @Override
    public void applyCursor() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (isExpanded()) {
                if (datePanelRendererEditor != null) {
                    datePanelRendererEditor.setCursor(getCursor());
                }
            } else {
                if (kind == InitializingMethod.RENDERER) {
                    if (dateRenderer != null) {
                        dateRenderer.setCursor(getCursor());
                    }
                } else {
                    if (dateEditor != null) {
                        dateEditor.setCursor(getCursor());
                    }
                }
            }
        }
    }

    @Override
    public void applyOpaque() {
        if (kind != InitializingMethod.UNDEFINED) {
            if (isExpanded()) {
                if (datePanelRendererEditor != null) {
                    datePanelRendererEditor.setOpaque(isOpaque());
                }
            } else {
                if (kind == InitializingMethod.RENDERER) {
                    if (dateRenderer != null) {
                        dateRenderer.setOpaque(isOpaque());
                    }
                } else {
                    if (dateEditor != null) {
                        dateEditor.setOpaque(isOpaque());
                    }
                }
            }
        }
    }

    private boolean isOnlyTimeFormat() {
        Matcher timeMatcher = TIME_PATTERN.matcher(dateFormat);
        Matcher noTimeMatcher = NO_TIME_PATTERN.matcher(dateFormat);
        return timeMatcher.find() && !noTimeMatcher.find();
    }

    private boolean isOnlyDateFormat() {
        Matcher timeMatcher = TIME_PATTERN.matcher(dateFormat);
        Matcher noTimeMatcher = NO_TIME_PATTERN.matcher(dateFormat);
        return !timeMatcher.find() && noTimeMatcher.find();
    }

    protected String emptyText;

    public String getEmptyText() {
        return emptyText;
    }

    public void setEmptyText(String aValue) {
        String oldValue = emptyText;
        emptyText = aValue;
        firePropertyChange("emptyText", oldValue, emptyText);
    }

    private String convertString2DateFormat(String dateFormat) {
        if (dateFormat != null) {
            switch (dateFormat) {
                case DD:
                    return "dd";
                case MM:
                    return "MM";
                case MM_YYYY:
                    return "MM.yyyy";
                case YYYY:
                    return "yyyy";
                case DD_MM:
                    return "dd.MM";
                case DD_MM_YYYY:
                    return "dd.MM.yyyy";
                case MM_SS:
                    return "mm:ss";
                case HH_MM:
                    return "HH:mm";
                case HH_MM_SS:
                    return "HH:mm:ss";
                case DD_MM_YYYY_HH_MM_SS:
                    return "dd.MM.yyyy HH:mm:ss";
                default:
                    return dateFormat;
            }
        }
        return null;
    }
}
