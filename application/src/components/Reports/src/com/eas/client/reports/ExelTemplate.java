/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.eas.client.report.Report;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.compacts.CompactBlob;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.utils.IDGenerator;
import com.bearsoft.rowset.wrappers.jdbc.ResultSetImpl;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.client.model.application.ApplicationModel;
import com.eas.client.model.application.ApplicationParametersEntity;
import com.eas.script.ScriptUtils;
import com.eas.util.BinaryUtils;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.beanutils.*;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author mg
 */
public class ExelTemplate {

    public static final String FIXED_SIZE_COLLECTION_FLAG_NAME = "fixed";
    public static final String REPORT_DYNA_CLASS_PREFIX = "PlatypusReportClass_";
    protected ApplicationModel<?, ?, ?, ?> model;
    protected JSObject scriptData;
    protected CompactBlob template;
    protected String templatePath;
    protected String format;
    protected Map<String, Object> generated;

    /**
     * Constructor for such use case: create instacne to edit the template.
     */
    public ExelTemplate() {
        super();
    }

    public ExelTemplate(String aFormat) {
        super();
        format = aFormat;
    }

    public ExelTemplate(ApplicationModel<?, ?, ?, ?> aModel, JSObject aScriptData, String aFormat) {
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
        return template;
    }

    public void setTemplate(CompactBlob aTemplate) {
        if (template != aTemplate) {
            template = aTemplate;
            templatePath = null;
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
                jsSubject.keySet().stream().map((subjectKey) -> new DynaProperty(subjectKey)).forEach((dp) -> {
                    props.add(dp);
                });
                DynaClass subjectClass = new BasicDynaClass(REPORT_DYNA_CLASS_PREFIX + String.valueOf(IDGenerator.genID()), null, props.toArray(new DynaProperty[]{}));
                BasicDynaBean bean = new BasicDynaBean(subjectClass);
                props.stream().forEach((prop) -> {
                    bean.set(prop.getName(), wrapScriptableObject(jsSubject.getMember(prop.getName())));
                });
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
            scriptData.keySet().stream().forEach((sid) -> {
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
            });
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
                                Logger.getLogger(ExelTemplate.class.getName()).severe(ex.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }
}
