/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import com.eas.client.settings.ConnectionSettings;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 *
 * @author pk, mg refactoring
 */
public class ConnectionsListModel extends AbstractListModel {

    private final List<ConnectionSettings> elements = new ArrayList<>();
    private final DbConnectionRenderer connectionRenderer = new DbConnectionRenderer();

    public ConnectionsListModel() throws Exception {
        fillElements();
    }

    @Override
    public Object getElementAt(int index) {
        return elements.get(index);
    }

    public void putElementAt(int index, ConnectionSettings element) {
        if (index == elements.size()) {
            elements.add(element);
        } else {
            elements.add(index, element);
        }
        fireIntervalAdded(this, index, index);
    }

    @Override
    public int getSize() {
        return elements.size();
    }

    public ListCellRenderer getCellRenderer() {
        return connectionRenderer;
    }

    public void removeElementAt(int index) {
        elements.remove(index);
        fireIntervalRemoved(this, index, index);
    }

    public void fireContentsChanged(int index) {
        super.fireContentsChanged(this, index, index);
    }

    private void fillElements() throws Exception {
        ConnectionsSelector.reset();
        ConnectionSettings[] settings = ConnectionsSelector.getSettings();
        if (settings != null) {
            elements.addAll(Arrays.asList(settings));
        }
    }
    private static final Color textInactiveTextcolor = UIManager.getColor("Label.disabledForeground") != null ? UIManager.getColor("Label.disabledForeground") : Color.gray;

    private class DbConnectionRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            DefaultListCellRenderer r = (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            ConnectionSettings settings = (ConnectionSettings) value;
            String displayName = settings.getName();
            if (displayName == null || displayName.isEmpty()) {
                displayName = settings.getUrl();
            }
            if (!settings.isEditable()) {
                displayName = displayName + " [" + ConnectionsSelector.bundle.getString("systemConnectionPrefix") + "]";
                r.setForeground(textInactiveTextcolor);
            }
            r.setText(displayName);
            return r;
        }
    }
}
