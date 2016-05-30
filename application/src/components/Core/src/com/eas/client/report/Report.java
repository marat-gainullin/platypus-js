/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.report;

import com.eas.client.ClientConstants;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.settings.SettingsConstants;
import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.LpcTransient;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import com.eas.script.Scripts;
import com.eas.util.IdGenerator;
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
public class Report implements HasPublished, LpcTransient {

    private final byte[] body;
    protected JSObject scriptData;
    private final String format;
    private final String name;
    protected JSObject published;

    @ScriptFunction(jsDoc = ""
            + "/**\n"
            + " * Creates report, generated with template.\n"
            + " * @param body The report binary body (array of byte).\n"
            + " * @param format Format of the report (xls, xlsx).\n"
            + " * @param name Name of the report. May be used as output file name.\n"
            + " */"
            + "", params = {"body", "format", "name"})
    public Report(byte[] aBody, String aFormat, String aName) {
        super();
        body = aBody;
        format = aFormat;
        name = aName;
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            JSObject publisher = Scripts.getSpace().getPublisher(this.getClass().getName());
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject) publisher.call(null, new Object[]{this});
        }
        return published;
    }

    @Override
    public void setPublished(JSObject aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
    }

    public JSObject getScriptData() {
        return scriptData;
    }

    public void setScriptData(JSObject aValue) {
        scriptData = aValue;
    }

    private static final String SHOW_JSDOC = ""
            + "/**\n"
            + " * Shows report as Excel application.\n"
            + " */";

    @ScriptFunction(jsDoc = SHOW_JSDOC)
    public void show() throws Exception {
        if (getBody() != null) {
            shellShowReport(save());
        }
    }
    private static final String PRINT_JSDOC = ""
            + "/**\n"
            + " * Runs printing.\n"
            + " */";

    @ScriptFunction(jsDoc = PRINT_JSDOC)
    public void print() throws Exception {
        if (getBody() != null) {
            shellPrintReport(save());
        }
    }

    private static final String SAVE_JSDOC = ""
            + "/**\n"
            + " * Saves the report at a specified location.\n"
            + " * @param aFileName Name of a file, the generated report should be save in.\n"
            + " */";

    @ScriptFunction(jsDoc = SAVE_JSDOC, params = {"aFileName"})
    public void save(String aFileName) throws Exception {
        if (getBody() != null) {
            saveReport(getBody(), aFileName);
        }
    }

    private String save() throws IOException {
        String path = generateReportPath(name, format);
        saveReport(getBody(), path);
        return path;
    }

    protected static String generateReportPath(String aFileName, String aFormat) {
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
        String suffix = "." + (aFormat != null ? aFormat : PlatypusFiles.REPORT_LAYOUT_EXTENSION_X);
        String reportName = aFileName;
        if (reportName.toLowerCase().endsWith(suffix.toLowerCase())) {
            reportName = reportName.substring(0, reportName.length() - suffix.length());
        }
        reportName += "-" + IdGenerator.genId() + suffix;
        reportPath += File.separator + reportName;
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

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the body
     */
    public byte[] getBody() {
        return body;
    }
}
