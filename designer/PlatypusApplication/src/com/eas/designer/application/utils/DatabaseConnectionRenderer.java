/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.utils;

import com.eas.designer.application.project.PlatypusProject;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.netbeans.api.db.explorer.DatabaseConnection;

/**
 *
 * @author mg
 */
public class DatabaseConnectionRenderer extends DefaultListCellRenderer {

    protected PlatypusProject project;

    public DatabaseConnectionRenderer(PlatypusProject aProject) {
        super();
        project = aProject;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value == null) {
            if (project != null) {
                value = DatabaseConnections.lookup(project.getSettings().getAppSettings().getDefaultDatasource());
            }
            return super.getListCellRendererComponent(list, value instanceof DatabaseConnection ? ((DatabaseConnection) value).getDisplayName() : " ", index, isSelected, cellHasFocus);
        } else {
            return super.getListCellRendererComponent(list, value instanceof DatabaseConnection ? ((DatabaseConnection) value).getDisplayName() : value, index, isSelected, cellHasFocus);
        }
    }

}
