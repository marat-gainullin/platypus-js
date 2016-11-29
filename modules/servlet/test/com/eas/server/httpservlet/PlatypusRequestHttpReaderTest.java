/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.client.Application;
import com.eas.client.ModuleStructure;
import com.eas.client.ModulesProxy;
import com.eas.client.ServerModulesProxy;
import com.eas.client.cache.FormsDocuments;
import com.eas.client.cache.ModelsDocuments;
import com.eas.client.cache.ReportsConfigs;
import com.eas.client.cache.ScriptsConfigs;
import com.eas.client.changes.Change;
import com.eas.client.changes.ChangeValue;
import com.eas.client.changes.Command;
import com.eas.client.changes.Delete;
import com.eas.client.changes.Insert;
import com.eas.client.changes.Update;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.scripts.ScriptedResource;
import com.eas.client.threetier.json.ChangesJSONReader;
import com.eas.script.Scripts;
import com.eas.util.RowsetJsonConstants;
import java.io.File;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Consumer;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class PlatypusRequestHttpReaderTest {

    protected static final String WRITTEN_CHANGES = "[{\"kind\":\"insert\", \"entity\":\"testEntity\", \"data\":{\"data\\\"\\\"1\":56, \"data2\":\"data2Value\", \"da\\\"ta3\":true, \"data4\":false, \"data5\":\"2012-08-27T11:42:15.514Z\"}},{\"kind\":\"update\", \"entity\":\"testEntity\", \"data\":{\"data\\\"\\\"1\":56, \"data2\":\"data2Value\", \"da\\\"ta3\":true, \"data4\":false, \"data5\":\"2012-08-27T11:42:15.514Z\"}, \"keys\":{\"key1\":78.9000015258789, \"key2\":\"key2Value\"}},{\"kind\":\"delete\", \"entity\":\"testEntity\", \"keys\":{\"key1\":78.9000015258789, \"key2\":\"key2Value\"}},{\"kind\":\"command\", \"entity\":\"testEntity\", \"parameters\":{\"key1\":78.9000015258789, \"key2\":\"key2Value\"}}]";

    @BeforeClass
    public static void init() throws Exception{
        Path platypusJsPath = ScriptedResource.lookupPlatypusJs();
        Scripts.init(platypusJsPath, false);
        Scripts.setOnlySpace(Scripts.createSpace());
        ScriptedResource.init(new Application(){
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
                return new ModulesProxy(){
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
    
    @Test
    public void timeStampReadTest() throws ParseException {
        System.out.println("timeStampReadTest with millis");
        SimpleDateFormat sdf = new SimpleDateFormat(RowsetJsonConstants.DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dt = sdf.parse("2012-03-05T23:45:02.305Z");
        assertEquals(1330991102305L, dt.getTime());
    }

    @Test
    public void changesJsonReadTest() throws Exception {
        System.out.println("changesJsonReadTest");
        List<Change> changes = ChangesJSONReader.read(WRITTEN_CHANGES, Scripts.getSpace());

        ChangeValue key1 = new ChangeValue("key1", 78.9000015258789D);
        ChangeValue key2 = new ChangeValue("key2", "key2Value");
        ChangeValue[] keys = new ChangeValue[]{key1, key2};

        Date date = new Date(1346067735514L);
        ChangeValue data1 = new ChangeValue("data\"\"1", 56);
        ChangeValue data2 = new ChangeValue("data2", "data2Value");
        ChangeValue data3 = new ChangeValue("da\"ta3", true);
        ChangeValue data4 = new ChangeValue("data4", false);
        ChangeValue data5 = new ChangeValue("data5", date);
        ChangeValue[] data = new ChangeValue[]{data1, data2, data3, data4, data5};

        assertNotNull(changes);
        assertEquals(4, changes.size());
        assertTrue(changes.get(0) instanceof Insert);
        assertTrue(changes.get(1) instanceof Update);
        assertTrue(changes.get(2) instanceof Delete);
        assertTrue(changes.get(3) instanceof Command);
        Insert i = (Insert) changes.get(0);
        Update u = (Update) changes.get(1);
        Delete d = (Delete) changes.get(2);
        Command c = (Command) changes.get(3);
        assertEquals("testEntity", i.entityName);
        assertEquals("testEntity", u.entityName);
        assertEquals("testEntity", d.entityName);
        assertEquals("testEntity", c.entityName);
        assertNull(c.command);
        assertNotNull(i.getData());
        assertEquals(5, i.getData().size());
        assertNotNull(u.getData());
        assertEquals(5, u.getData().size());
        for (int j = 0; j < i.getData().size(); j++) {
            assertNotSame(i.getData().get(j), u.getData().get(j));
            compareValues(i.getData().get(j), u.getData().get(j));
            compareValues(i.getData().get(j), data[j]);
        }
        assertNotNull(u.getKeys());
        assertEquals(2, u.getKeys().size());
        assertNotNull(d.getKeys());
        assertEquals(2, d.getKeys().size());
        assertNotNull(c.getParameters());
        assertEquals(2, c.getParameters().size());
        for (int j = 0; j < u.getKeys().size(); j++) {
            assertNotSame(u.getKeys().get(j), d.getKeys().get(j));
            compareValues(u.getKeys().get(j), d.getKeys().get(j));
            assertNotSame(u.getKeys().get(j), c.getParameters().get(j));
            compareValues(u.getKeys().get(j), c.getParameters().get(j));
            compareValues(u.getKeys().get(j), keys[j]);
        }
    }

    protected static void compareValues(ChangeValue v1, ChangeValue v2) {
        assertEquals(v1.name, v2.name);
        if(v1.value != null && !v1.value.equals(v2.value)){
            int h = 0;
            h++;
        }
        assertEquals(v1.value, v2.value);
    }
}
