package com.eas.client.reports;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author mg
 */
public class ExelTemplate {

    protected JSObject scriptData;
    protected ReportTemplate template;
    protected String templatePath;
    protected String format;
    protected Map<String, Object> generated;

    /**
     * Constructor for such use case: create instacne to edit the template.
     */
    public ExelTemplate() {
        super();
    }

    public ExelTemplate(JSObject aScriptData, String aFormat, ReportTemplate aTemplate) {
        super();
        format = aFormat;
        scriptData = aScriptData;
        template = aTemplate;
    }

    public JSObject getScriptData() {
        return scriptData;
    }

    public void setScriptData(JSObject aValue) {
        scriptData = aValue;
    }

    public byte[] getTemplate() throws Exception {
        return template.getContent();
    }

    public byte[] create() throws Exception {
        Workbook workbook = executeReport();
        ByteArrayOutputStream st = new ByteArrayOutputStream();
        workbook.write(st);
        return st.toByteArray();
    }

    protected Workbook executeReport() throws Exception {
        if (template.getContent() != null) {
            try (InputStream is = new ByteArrayInputStream(template.getContent())) {
                XLSTransformer transformer = new XLSTransformer();
                transformer.registerRowProcessor(new ExcelRowProcessor());
                generateDataNamedMap(transformer);
                if (template.getFixed() != null) {
                    int length = JSType.toInteger(template.getFixed().getMember("length"));
                    for (int i = 0; i < length; i++) {
                        transformer.markAsFixedSizeCollection(JSType.toString(template.getFixed().getSlot(i)));
                    }
                }
                return transformer.transformXLS(is, generated);
            }
        } else {
            throw new Exception("Report template is absent.");
        }
    }

    protected void generateDataNamedMap(XLSTransformer aTransformer) throws Exception {
        generated = new HashMap<>();
        if (scriptData != null) {
            scriptData.keySet().stream().forEach((sid) -> {
                Object subject = scriptData.getMember(sid);
                generated.put(sid, JSDynaBean.wrap(subject, template.getTimezoneOffset()));
            });
        }
    }
}
