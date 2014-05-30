/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.ClientConstants;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.settings.SettingsConstants;
import com.eas.script.HasPublished;
import com.eas.script.ScriptFunction;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.Preferences;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author Andrew
 */
public class Report implements HasPublished {

    private final byte[] report;
    private final String format;
    private Object published;
    private static JSObject publisher;

    public Report(byte[] aReport, String aFormat) {
        super();
        report = aReport;
        format = aFormat;
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(Object aPublished) {
        if (published == null) {
            published = aPublished;
        } else {
            //throw new Exception
        }
    }

    private static final String SHOW_JSDOC = ""
            + "/**\n"
            + " * Shows report as Excel application.\n"
            + " */";

    @ScriptFunction(jsDoc = SHOW_JSDOC)
    public void show() throws Exception {
        if (report != null) {
            shellShowReport(save());
        }
    }
    private static final String PRINT_JSDOC = ""
            + "/**\n"
            + " * Runs printing.\n"
            + " */";

    @ScriptFunction(jsDoc = PRINT_JSDOC)
    public void print() throws Exception {
        if (report != null) {
            shellPrintReport(save());
        }
    }

    private static final String SAVE_JSDOC = ""
            + "/**\n"
            + " * Saves the report at a specified location.\n"
            + " * @param aFileName Name of a file, the generated report should be save in."
            + " */";

    @ScriptFunction(jsDoc = SAVE_JSDOC, params = {"aFileName"})
    public void save(String aFileName) throws Exception {
        if (report != null) {
            saveReport(report, aFileName);
        }
    }

    private String save() throws IOException {
        String path = generateReportPath(format);
        saveReport(report, path);
        return path;
    }

    protected static String generateReportPath(String aFormat) {
        String reportPath = System.getProperty(ClientConstants.USER_HOME_PROP_NAME);
        if (!reportPath.endsWith(File.separator)) {
            reportPath += File.separator;
        }
        reportPath += ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME;
        File newDir = new File(reportPath);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
        reportPath += File.separator + "reports";
        newDir = new File(reportPath);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
        reportPath += File.separator + String.valueOf(IDGenerator.genID()) + "." + (aFormat != null ? aFormat : PlatypusFiles.REPORT_LAYOUT_EXTENSION_X);
        return reportPath;
    }

    protected static void shellShowReport(String savedPath) throws IOException {
        File f = new File(savedPath);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            Desktop desk = Desktop.getDesktop();
            desk.open(f);
        } else {
            final String pathReport = Preferences.userRoot().node(SettingsConstants.CLIENT_SETTINGS_NODE).get(SettingsConstants.REPORT_RUN_COMMAND, "");
            Runtime.getRuntime().exec(String.format(pathReport, savedPath));
        }
    }

    protected static void shellPrintReport(String savedPath) throws IOException {
        File f = new File(savedPath);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.PRINT)) {
            Desktop desk = Desktop.getDesktop();
            desk.print(f);
        } else {
            final String pathReport = Preferences.userRoot().node(SettingsConstants.CLIENT_SETTINGS_NODE).get(SettingsConstants.REPORT_PRINT_COMMAND, "");
            Runtime.getRuntime().exec(String.format(pathReport, savedPath));
        }
    }

    protected void saveReport(byte[] workbook, String aPath2Save) throws IOException {
        if (aPath2Save != null) {
            if (workbook != null) {
                File f = new File(aPath2Save);
                if (!f.exists()) {
                    f.createNewFile();
                }
                try (FileOutputStream osf = new FileOutputStream(f)) {
                    osf.write(workbook);
                    osf.flush();
                }
            }
        } else {
            throw new IOException("Path is absent.");
        }
    }
}
