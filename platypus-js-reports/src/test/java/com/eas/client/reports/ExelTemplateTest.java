package com.eas.client.reports;

import com.eas.client.scripts.ScriptedResource;
import com.eas.script.Scripts;
import java.nio.file.Path;
import jdk.nashorn.api.scripting.JSObject;
import net.sf.jxls.transformer.XLSTransformer;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Andrew
 */
public class ExelTemplateTest {

    @BeforeClass
    public static void init() throws Exception {
        Path platypusJsPath = ScriptedResource.lookupPlatypusJs();
        Scripts.init(platypusJsPath, false);
        Scripts.setOnlySpace(Scripts.createSpace());
        Scripts.getSpace().initSpaceGlobal();
    }

    /**
     * Test of generateDataNamedMap method, of class ExelTemplate.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGenerateDataNamedMap() throws Exception {
        System.out.println("GenerateDataNamedMap jsObject");
        JSObject data = (JSObject) Scripts.getSpace().exec("({name : 'test', count : 5, time : new Date(1402470671000), elems : [1, 'Hi', true, {text:'Hello!'}, new Date(1403461342000)]})");
        ExelTemplate template = new ExelTemplate(data, "xlsx", new ReportTemplate(null, null, null, data));
        XLSTransformer transformer = new XLSTransformer();
        transformer.registerRowProcessor(new ExcelRowProcessor());
        template.generateDataNamedMap(transformer);
        assertEquals(template.generated.size(), 4);
        assertEquals(template.generated.get("count"), 5);
        assertEquals(template.generated.get("name"), "test");
        double expectedNamedTimestamp = 1402470671000d / 86400000d + 25569;
        Object doubledDate = template.generated.get("time");
        assertTrue(doubledDate instanceof Double);
        double generatedNamedTimestamp = (Double) doubledDate;
        assertTrue(Math.abs(expectedNamedTimestamp - generatedNamedTimestamp) < 1e-10d);
        JSDynaList list = (JSDynaList) template.generated.get("elems");
        assertEquals(list.get(0), 1);
        assertEquals(list.get(1), "Hi");
        assertEquals(list.get(2), Boolean.TRUE);
        assertEquals(((JSDynaBean) list.get(3)).get("text"), "Hello!");
        double expectedIndexedTimestamp = 1403461342000d / 86400000d + 25569;
        double generatedIndexedTimestamp = (Double) list.get(4);
        assertTrue(Math.abs(expectedIndexedTimestamp - generatedIndexedTimestamp) < 1e-10);
    }

}
