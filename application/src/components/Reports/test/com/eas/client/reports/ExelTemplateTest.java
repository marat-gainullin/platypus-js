/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import com.eas.client.Application;
import com.eas.client.DatabasesClientWithResource;
import com.eas.client.ModuleStructure;
import com.eas.client.ModulesProxy;
import com.eas.client.ServerModulesProxy;
import com.eas.client.cache.FormsDocuments;
import com.eas.client.cache.ModelsDocuments;
import com.eas.client.cache.ReportsConfigs;
import com.eas.client.cache.ScriptsConfigs;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.scripts.ScriptedResource;
import com.eas.client.settings.DbConnectionSettings;
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
        JSObject data = (JSObject) Scripts.getSpace().exec("({name : 'test', count : 5, time : new Date(2014, 05, 11, 11, 11, 11, 0), elems : [1, 'Hi', true, {text:'Hello!'}, new Date(2014, 05, 22, 22, 22, 22, 0)]})");
        ExelTemplate template = new ExelTemplate(data, "xlsx", new ReportTemplate(null, null, null, data));
        XLSTransformer transformer = new XLSTransformer();
        transformer.registerRowProcessor(new ExcelRowProcessor());
        template.generateDataNamedMap(transformer);
        assertEquals(template.generated.size(), 4);
        assertEquals(template.generated.get("count"), 5);
        assertEquals(template.generated.get("name"), "test");
        assertEquals(template.generated.get("time"), 1402474271000d / 86400000 + 25569);
        JSDynaList list = (JSDynaList) template.generated.get("elems");
        assertEquals(list.get(0), 1);
        assertEquals(list.get(1), "Hi");
        assertEquals(list.get(2), Boolean.TRUE);
        assertEquals(((JSDynaBean) list.get(3)).get("text"), "Hello!");
        assertEquals(list.get(4), 1403464942000d / 86400000 + 25569);
    }

}
