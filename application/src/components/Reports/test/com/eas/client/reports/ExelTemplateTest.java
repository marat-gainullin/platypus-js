/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.eas.client.DatabasesClientWithResource;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.script.Scripts;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import jdk.nashorn.api.scripting.JSObject;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.beanutils.BasicDynaBean;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Andrew
 */
public class ExelTemplateTest {

    public ExelTemplateTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private DatabasesClientWithResource getDBClient() throws Exception {
        DbConnectionSettings settings = new DbConnectionSettings();
        settings.setUrl("jdbc:oracle:thin:@asvr/adb");
        settings.setSchema("eas");
        settings.setUser("eas");
        settings.setPassword("eas");
        settings.setMaxStatements(1);
        return new DatabasesClientWithResource(settings);
    }

    /**
     * Test of generateDataNamedMap method, of class ExelTemplate.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGenerateDataNamedMap() throws Exception {
        System.out.println("GenerateDataNamedMap jsObject");
        //Scripts.init();
        Scripts.Space space = Scripts.createSpace();
        JSObject data = (JSObject) space.exec("({name : 'test', count : 5, time : new Date(2014, 05, 11, 11, 11, 11, 0), elems : [1, 'Hi', true, {text:'Hello!'}, new Date(2014, 05, 22, 22, 22, 22, 0)]})");
        ExelTemplate template = new ExelTemplate();
        template.setScriptData(data);
        XLSTransformer transformer = new XLSTransformer();
        transformer.registerRowProcessor(new ExcelRowProcessor());
        template.generateDataNamedMap(transformer);
        assertEquals(template.generated.size(), 4);
        assertEquals(template.generated.get("count"), 5);
        assertEquals(template.generated.get("name"), "test");
        assertEquals(((Date) template.generated.get("time")).getTime(), (new Date(114, 5, 11, 11, 11, 11)).getTime());
        ArrayList list = (ArrayList) template.generated.get("elems");
        assertEquals(list.get(0), 1);
        assertEquals(list.get(1), "Hi");
        assertEquals(list.get(2), Boolean.TRUE);
        assertEquals(((BasicDynaBean) list.get(3)).get("text"), "Hello!");
        assertEquals(list.get(4), new Date(114, 5, 22, 22, 22, 22));
        
        System.out.println("GenerateDataNamedMap DataModel");
        try (DatabasesClientWithResource resource = getDBClient()) {
            ApplicationDbModel model = new ApplicationDbModel(resource.getClient(), null);
            ApplicationDbEntity entity41 = model.loadEntity("reportQuery");
            model.addEntity(entity41);
            ((JSObject)model.getPublished()).setMember("dsEnt", entity41.getPublished());
            model.requery();
            ExelTemplate templateWithModel = new ExelTemplate();
            templateWithModel.setScriptData((JSObject)model.getPublished());
            templateWithModel.generateDataNamedMap(transformer);
            assertEquals(templateWithModel.generated.size(), 1);
            ArrayList rows = (ArrayList)templateWithModel.generated.get("dsEnt");
            assertEquals(rows.size(), 13);
            assertEquals(((BasicDynaBean)rows.get(2)).get("name"), "Сила света");
            assertEquals(((BasicDynaBean)rows.get(2)).get("id"), new BigDecimal(124772784578140l));
        }
    }
        
}
