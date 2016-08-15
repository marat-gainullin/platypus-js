/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.eas.client.Application;
import com.eas.client.ModuleStructure;
import com.eas.client.ModulesProxy;
import com.eas.client.ServerModulesProxy;
import com.eas.client.TestConstants;
import com.eas.client.cache.FormsDocuments;
import com.eas.client.cache.ModelsDocuments;
import com.eas.client.cache.ReportsConfigs;
import com.eas.client.cache.ScriptsConfigs;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.scripts.ScriptedResource;
import com.eas.script.Scripts;
import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;
import net.sf.jxls.transformer.XLSTransformer;
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
    public static void setUpClass() throws Exception {
        Path platypusJsPath = ScriptedResource.lookupPlatypusJs();
        Scripts.init(platypusJsPath, false);
        Scripts.setOnlySpace(Scripts.createSpace());
        ScriptedResource.init(new Application() {
            @Override
            public Application.Type getType() {
                return Application.Type.CLIENT;
            }

            @Override
            public QueriesProxy getQueries() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ModulesProxy getModules() {
                return new ModulesProxy() {
                    @Override
                    public ModuleStructure getModule(String string, Scripts.Space space, Consumer<ModuleStructure> cnsmr, Consumer<Exception> cnsmr1) throws Exception {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public File getResource(String string, Scripts.Space space, Consumer<File> cnsmr, Consumer<Exception> cnsmr1) throws Exception {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public Path getLocalPath() {
                        return platypusJsPath;
                    }

                    @Override
                    public File nameToFile(String string) throws Exception {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public String getDefaultModuleName(File file) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                };
            }

            @Override
            public ServerModulesProxy getServerModules() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ModelsDocuments getModels() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public FormsDocuments getForms() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ReportsConfigs getReports() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ScriptsConfigs getScriptsConfigs() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }, platypusJsPath, false);
        Scripts.getSpace().initSpaceGlobal();
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
        double generatedNamedTimestamp = (Double)template.generated.get("time");
        assertTrue(Math.abs(expectedNamedTimestamp - generatedNamedTimestamp) < 1e-10d);
        JSDynaList list = (JSDynaList) template.generated.get("elems");
        assertEquals(list.get(0), 1);
        assertEquals(list.get(1), "Hi");
        assertEquals(list.get(2), Boolean.TRUE);
        assertEquals(((JSDynaBean) list.get(3)).get("text"), "Hello!");
        double expectedIndexedTimestamp = 1403461342000d / 86400000d + 25569;
        double generatedIndexedTimestamp = (Double)list.get(4);
        assertTrue(Math.abs(expectedIndexedTimestamp - generatedIndexedTimestamp) < 1e-10);
    }

}
