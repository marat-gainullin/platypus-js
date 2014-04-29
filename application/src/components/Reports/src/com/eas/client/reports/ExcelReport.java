/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.compacts.CompactBlob;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.utils.IDGenerator;
import com.bearsoft.rowset.wrappers.jdbc.ResultSetImpl;
import com.eas.client.ClientConstants;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.application.ApplicationParametersEntity;
import com.eas.client.settings.SettingsConstants;
import com.eas.script.ScriptUtils;
import com.eas.util.BinaryUtils;
import java.awt.Desktop;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import jdk.nashorn.api.scripting.JSObject;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.beanutils.*;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author mg
 */
public class ExcelReport {

    public static final String FIXED_SIZE_COLLECTION_FLAG_NAME = "fixed";
    public static final String REPORT_DYNA_CLASS_PREFIX = "PlatypusReportClass_";
    protected ApplicationModel<?, ?, ?, ?> model;
    protected JSObject scriptData;
    protected CompactBlob template;
    protected boolean validTemplate = true;
    protected boolean templateModified;
    protected String templatePath;
    protected String format;
    protected Map<String, Object> generated;

    /**
     * Constructor for such use case: create instacne to edit the template.
     */
    public ExcelReport() {
        super();
    }

    public ExcelReport(String aFormat) {
        super();
        format = aFormat;
    }

    public ExcelReport(ApplicationModel<?, ?, ?, ?> aModel, JSObject aScriptData, String aFormat) {
        super();
        model = aModel;
        format = aFormat;
        scriptData = aScriptData;
    }

    public JSObject getScriptData() {
        return scriptData;
    }

    public void setScriptData(JSObject aValue) {
        scriptData = aValue;
    }

    public CompactBlob getTemplate() throws Exception {
        validateTemplate();
        return template;
    }

    public void setTemplate(CompactBlob aTemplate) {
        if (template != aTemplate) {
            template = aTemplate;
            validTemplate = true;
            templatePath = null;
            templateModified = true;
        }
    }

    public void execute(String aPath2Save) throws Exception {
        if (aPath2Save != null) {
            Workbook workbook = executeReport();
            saveReport(workbook, aPath2Save);
        } else {
            throw new Exception("Path is absent. Can't execute a report");
        }
    }

    public void show() throws Exception {
        Workbook workbook = executeReport();
        String lPath2Save = generateReportPath(format);
        saveReport(workbook, lPath2Save);
        File f = new File(lPath2Save);
        f.deleteOnExit();
        shellShowReport(lPath2Save);
    }

    public void save(String aFileName) throws Exception {
        if (aFileName != null && !aFileName.isEmpty()) {
            Workbook workbook = executeReport();
            saveReport(workbook, aFileName);
        } else {
            throw new Exception("Report filename could not be empty.");
        }
    }

    public void print() throws Exception {
        Workbook workbook = executeReport();
        String lPath2Save = generateReportPath(format);
        saveReport(workbook, lPath2Save);
        File f = new File(lPath2Save);
        f.deleteOnExit();
        shellPrintReport(lPath2Save);
    }

    public void edit() throws Exception {
        CompactBlob template2Edit = template;
        if (template2Edit != null) {
            if (templatePath == null) {
                templatePath = generateReportPath(format);
            }
            if (templatePath != null && !templatePath.isEmpty()) {
                try {
                    File f = new File(templatePath);
                    if (canCreateTemplateFile()) {
                        if (f.exists()) {
                            f.delete();
                        }
                        f.createNewFile();
                        f.deleteOnExit();
                        if (template2Edit.length() > 0) {
                            try (OutputStream out = new FileOutputStream(f)) {
                                out.write(template2Edit.getBytes(1, (int) template2Edit.length()));
                                out.flush();
                            }
                        }
                        shellShowReport(templatePath);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ExcelReport.class.getName()).log(Level.SEVERE, "It seems that file is already opened. May be editing?");
                }
            }
            invalidateTemplate();
        }
    }

    protected void shellShowReport(String savedPath) throws IOException {
        File f = new File(savedPath);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            Desktop desk = Desktop.getDesktop();
            f.deleteOnExit();
            desk.open(f);
        } else {
            final String pathReport = Preferences.userRoot().node(SettingsConstants.CLIENT_SETTINGS_NODE).get(SettingsConstants.REPORT_RUN_COMMAND, "");
            Runtime.getRuntime().exec(String.format(pathReport, savedPath));
        }
    }

    protected void shellPrintReport(String savedPath) throws IOException {
        File f = new File(savedPath);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.PRINT)) {
            Desktop desk = Desktop.getDesktop();
            f.deleteOnExit();
            desk.print(f);
        } else {
            final String pathReport = Preferences.userRoot().node(SettingsConstants.CLIENT_SETTINGS_NODE).get(SettingsConstants.REPORT_PRINT_COMMAND, "");
            Runtime.getRuntime().exec(String.format(pathReport, savedPath));
        }
    }

    protected void saveReport(Workbook workbook, String aPath2Save) throws IOException {
        if (aPath2Save != null) {
            if (workbook != null) {
                File f = new File(aPath2Save);
                if (!f.exists()) {
                    f.createNewFile();
                }
                try (FileOutputStream osf = new FileOutputStream(f)) {
                    workbook.write(osf);
                    osf.flush();
                }
            }
        } else {
            throw new IOException("Path is absent.");
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

    public byte[] create() throws Exception {
        Workbook workbook = executeReport();
        ByteArrayOutputStream st = new ByteArrayOutputStream();
        workbook.write(st);
        return st.toByteArray();
    }

    protected Workbook executeReport() throws Exception {
        if (template != null) {
            try (InputStream is = template.getBinaryStream()) {
                XLSTransformer transformer = new XLSTransformer();
                transformer.registerRowProcessor(new ExcelRowProcessor());
                generateDataNamedMap(transformer);
                return transformer.transformXLS(is, generated);
            }
        } else {
            throw new Exception("Report template is absent.");
        }
    }

    private static boolean isPrimitive(Object aValue) {
        aValue = ScriptUtils.toJava(aValue);
        return aValue instanceof Number || aValue instanceof Boolean
                || aValue instanceof Date || aValue instanceof String
                || aValue == null;
    }

    private Object wrapScriptableObject(Object aSubject) {
        if (isPrimitive(aSubject)) {
            return ScriptUtils.toJava(aSubject);
        } else if (aSubject instanceof JSObject) {
            JSObject jsSubject = (JSObject) aSubject;
            if (jsSubject.isArray()) {
                return wrapScriptArray(jsSubject);
            } else {
                List<DynaProperty> props = new ArrayList<>();
                for (String subjectKey : jsSubject.keySet()) {
                    DynaProperty dp = new DynaProperty(subjectKey);
                    props.add(dp);
                }
                DynaClass subjectClass = new BasicDynaClass(REPORT_DYNA_CLASS_PREFIX + String.valueOf(IDGenerator.genID()), null, props.toArray(new DynaProperty[]{}));
                BasicDynaBean bean = new BasicDynaBean(subjectClass);
                for (DynaProperty prop : props) {
                    bean.set(prop.getName(), wrapScriptableObject(jsSubject.getMember(prop.getName())));
                }
                return bean;
            }
        } else {
            return null;
        }
    }

    private List<Object> wrapScriptArray(JSObject aSubject) {
        List<Object> wrappedList = new ArrayList<>();
        Number length = (Number) aSubject.getMember("length");
        for (int i = 0; i < length.intValue(); i++) {
            Object le = aSubject.getSlot(i);
            wrappedList.add(wrapScriptableObject(le));
        }
        return wrappedList;
    }

    private void generateDataNamedMap(XLSTransformer aTransformer) throws Exception {
        generated = new HashMap<>();
        if (scriptData != null) {
            for (String sid : scriptData.keySet()) {
                Object subject = scriptData.getMember(sid);
                if (isPrimitive(subject)) {// Atomic values
                    generated.put(sid, ScriptUtils.toJava(subject));
                } else if (subject instanceof JSObject) {// Non-atomic values
                    JSObject jsSubject = (JSObject) subject;
                    if (jsSubject.isArray()) {
                        List<?> wrappedList = wrapScriptArray(jsSubject);
                        generated.put((String) sid, wrappedList);
                        Object oFixed = ScriptUtils.toJava(jsSubject.hasMember(FIXED_SIZE_COLLECTION_FLAG_NAME) ? jsSubject.getMember(FIXED_SIZE_COLLECTION_FLAG_NAME) : null);
                        if (Boolean.TRUE.equals(oFixed)) {
                            aTransformer.markAsFixedSizeCollection((String) sid);
                        }
                    } else {
                        Object wrapped = wrapScriptableObject(jsSubject);
                        generated.put((String) sid, wrapped);
                    }
                }
            }
        }
        if (model != null) {
            for (ApplicationEntity<?, ?, ?> entity : model.getAllEntities().values()) {
                if (entity != null) {
                    String ldsName = entity.getName();
                    if (entity instanceof ApplicationParametersEntity) {
                        ldsName = ApplicationModel.PARAMETERS_SCRIPT_NAME;
                    }
                    if (ldsName != null && !ldsName.isEmpty()) {
                        Rowset rowset = entity.getRowset();
                        if (rowset != null) {
                            try {
                                rowset.beforeFirst();
                                RowSetDynaClass cls = new RowSetDynaClass(new ResultSetImpl(rowset, rowset.getConverter()), false);
                                generated.put(ldsName, cls.getRows());
                                if (!rowset.isEmpty()) {
                                    rowset.first();
                                }
                            } catch (InvalidCursorPositionException | SQLException ex) {
                                Logger.getLogger(ExcelReport.class.getName()).severe(ex.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }

    public String generateReportPath(String aFormat) {
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

    private boolean updateTemplateFromFile() throws Exception {
        if (templatePath != null && !templatePath.isEmpty()) {
            File f = new File(templatePath);
            if (f.exists() && canCreateTemplateFile()) {
                try (InputStream is = new FileInputStream(f)) {
                    byte[] bytes = BinaryUtils.readStream(is, -1);
                    template = new CompactBlob(bytes);
                    templateModified = true;
                }
                return true;
            }
        }
        return false;
    }

    private void invalidateTemplate() {
        validTemplate = false;
    }

    public void validateTemplate() throws Exception {
        if (!validTemplate && updateTemplateFromFile()) {
            validTemplate = true;
            templatePath = null;
        }
    }

    public boolean isTemplateValid() {
        return validTemplate;
    }

    public boolean isTemplateModified() {
        return templateModified;
    }

    public void setTemplateModified(boolean aValue) {
        templateModified = aValue;
    }

    protected boolean canCreateTemplateFile() {
        assert templatePath != null;
        File file = new File(templatePath);
        if (file.exists()) {
            String path = file.getPath();
            String fileName = file.getName();
            path = path.substring(0, path.length() - fileName.length());
            File fCandidate1 = new File(path + ".~lock." + fileName + "#");// open office
            File fCandidate2 = new File(path + "~$" + fileName); // microsoft office
            return !fCandidate1.exists() && !fCandidate2.exists() && checkWriteness(templatePath);
        } else {
            return true;
        }
    }

    protected boolean checkWriteness(String aFileName) {
        File file = new File(aFileName);
        File file1 = new File(aFileName + "_test");
        boolean renamed = file.renameTo(file1);
        if (renamed) {
            file1.renameTo(file);
        }
        return renamed;
    }
}
