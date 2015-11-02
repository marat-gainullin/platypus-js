/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.designer.explorer.project;

import org.netbeans.spi.project.ui.PrivilegedTemplates;

/**
 *
 * @author mg
 */
public class PlatypusPrivilegedTemplates implements PrivilegedTemplates{

    protected static final String TEMPLATES_PREFIX = "Templates/Platypus/";
    protected static String[] TEMPLATES = new String[]{
        TEMPLATES_PREFIX+"PlatypusFolderTemplate",
        TEMPLATES_PREFIX+"PlatypusDbDiagramTemplate.pd",
        TEMPLATES_PREFIX+"PlatypusQueryTemplate.sql",
        TEMPLATES_PREFIX+"Resources/PlatypusReportTemplate.xlsx"};

    @Override
    public String[] getPrivilegedTemplates() {
        return TEMPLATES;
    }

}
