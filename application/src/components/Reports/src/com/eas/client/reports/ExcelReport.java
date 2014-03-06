/*
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
import com.eas.client.model.script.ScriptableRowset;
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
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.beanutils.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author mg
 */
public class ExcelReport {

    public static final String FIXED_SIZE_COLLECTION_FLAG_NAME = "fixed";
    public static final String REPORT_DYNA_CLASS_PREFIX = "PlatypusReportClass_";
    protected ApplicationModel<?, ?, ?, ?> model;
    protected ScriptableObject so;
    protected CompactBlob template;
    protected boolean validTemplate = true;
    protected boolean templateModified;
    protected String templatePath;
    private String format;
    protected Map<String, ScriptableRowset<?>> fakeRowsetes = new HashMap<>();
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

    public ExcelReport(ApplicationModel<?, ?, ?, ?> aModel, ScriptableObject aSo) {
        super();
        model = aModel;
        so = aSo;
        if (so instanceof ReportRunner) {
            format = ((ReportRunner) so).format;
        }
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

    public void put(String aName, ScriptableRowset<?> aRowset) {
        fakeRowsetes.put(aName, aRowset);
    }

    public void remove(String aName) {
        fakeRowsetes.remove(aName);
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
                generateRowsetsNamedMap(transformer);
                return transformer.transformXLS(is, generated);
            }
        } else {
            throw new Exception("Report template is absent.");
        }
    }

    private Object wrapScriptableObject(Object aSubject) {
        if (aSubject instanceof List) {
            return wrapScriptableList((List<Object>) aSubject);
        } else if (aSubject instanceof Map) {
            return wrapScriptableMap((Map) aSubject);
        } else {
            return ScriptUtils.js2Java(aSubject);
        }
    }

    private DynaBean wrapScriptableMap(Map<?, ?> aSubject) {
        List<DynaProperty> props = new ArrayList<>();
        for (Object subjectKey : aSubject.keySet()) {
            if (subjectKey instanceof String) {
                DynaProperty dp = new DynaProperty((String) subjectKey);
                props.add(dp);
            }
        }
        DynaClass subjectClass = new BasicDynaClass(REPORT_DYNA_CLASS_PREFIX + String.valueOf(IDGenerator.genID()), null, props.toArray(new DynaProperty[]{}));
        BasicDynaBean bean = new BasicDynaBean(subjectClass);
        for (Object oe : aSubject.entrySet()) {
            bean.set((String) ((Entry) oe).getKey(), wrapScriptableObject(((Entry) oe).getValue()));
        }
        return bean;
    }

    private List<Object> wrapScriptableList(List<Object> aSubject) {
        List<Object> wrappedList = new ArrayList<>();
        for (Object le : aSubject) {
            if (le instanceof List<?>) {
                wrappedList.add(wrapScriptableList((List<Object>) le));
            } else if (le instanceof Map<?, ?>) {
                wrappedList.add(wrapScriptableMap((Map<?, ?>) le));
            } else {
                wrappedList.add(wrapScriptableObject(le));
            }
        }
        return wrappedList;
    }

    private void generateRowsetsNamedMap(XLSTransformer aTransformer) throws Exception {
        if (model != null && so != null) {
            generated = new HashMap<>();
            for (Object sid : so.getIds()) {
                if (sid instanceof String) {
                    Object subject = so.get(sid);
                    if (!(subject instanceof Function)) {
                        if (subject instanceof List<?>) {
                            List<?> wrappedList = wrapScriptableList((List<Object>) subject);
                            generated.put((String) sid, wrappedList);
                            if (subject instanceof Scriptable) {
                                Scriptable scrSubject = (Scriptable) subject;
                                Object oFixed = scrSubject.get(FIXED_SIZE_COLLECTION_FLAG_NAME, scrSubject);
                                if (Boolean.TRUE.equals(oFixed)) {
                                    aTransformer.markAsFixedSizeCollection((String) sid);
                                }
                            }
                        } else if (subject instanceof Map) {
                            DynaBean bean = wrapScriptableMap((Map) subject);
                            generated.put((String) sid, bean);
                        } else if(subject instanceof Number || subject instanceof Boolean ||
                                subject instanceof Date || subject instanceof String ||
                                subject == null) {// Atomic values
                            generated.put((String) sid, ScriptUtils.js2Java(subject));
                        }
                    }
                }
            }
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
            for (Entry<String, ScriptableRowset<?>> entry : fakeRowsetes.entrySet()) {
                if (entry != null) {
                    String lName = entry.getKey();
                    if (entry.getValue() != null
                            && entry.getValue().getEntity() != null
                            && entry.getValue().getEntity().getRowset() != null) {
                        Rowset rowset = entry.getValue().getEntity().getRowset();
                        rowset.beforeFirst();
                        RowSetDynaClass cls = new RowSetDynaClass(new ResultSetImpl(rowset, rowset.getConverter()), false);
                        generated.put(lName, cls.getRows());
                        if (!rowset.isEmpty()) {
                            rowset.first();
                        }
                    }
                }
            }
        } else {
            throw new Exception("Model is absent. Can't deal with null.");
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
    
    public void setTemplateModified(boolean aValue){
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
