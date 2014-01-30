/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.script;

import com.bearsoft.rowset.metadata.*;
import com.eas.client.DbClient;
import com.eas.client.model.BaseTest;
import com.eas.client.model.Model;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.script.ScriptUtils;
import static org.junit.Assert.*;
import org.junit.Test;
import org.mozilla.javascript.*;

/**
 *
 * @author mg
 */
public class ScriptableRowsetSyntaxTest extends BaseTest {

    private static final String SCRIPT_TABLE_PATTERN_TEST_SOURCE
            = "var p = param1;\n"
            + "p = params.param1;\n"
            + "var m = md;\n"
            + "m = params.md;\n"
            + "var mp = md.param1;\n"
            + "mp = params.md.param1;\n"
            + "var d = entity11.NAME;\n"
            + "m = entity11.md.NAME;\n"
            + "mp = entity11.params;\n"
            + "m = entity11.params.md;\n"
            + "entity11.first();\n"
            + "var rowsetData1 = entity11.ID;\n"
            + "var rowsetData2 = entity11.NAME;\n"
            + "var rowsetData3 = entity11.FIELD3;\n"
            + "var rowsetData4 = entity11.FIELD4;\n"
            + "var rowsetData5 = entity11.FIELD5;\n"
            + "var rowsetData6 = entity11.FIELD6;\n"
            + "var row = entity11.getRow(1);\n"
            + "var rowData1 = row.ID;\n"
            + "var rowData2 = row.NAME;\n"
            + "var rowData3 = row.FIELD3;\n"
            + "var rowData4 = row.FIELD4;\n"
            + "var rowData5 = row.FIELD5;\n"
            + "var rowData6 = row.FIELD6;\n"
            + "row.FIELD6 = 0.56;\n"
            + "param1 = row.FIELD6;\n";
    private static final String SCRIPT_ORM_TEST_SOURCE
            = "var p = param1;\n"
            + "p = params.param1;\n"
            + "var m = md;\n"
            + "m = params.md;\n"
            + "var mp = md.param1;\n"
            + "var mp = params.md.param1;\n"
            + "var d = entity11.length;\n"
            + "entity11.push({ID:150, NAME:'sample7'});"
            + "entity11.push({ID:520, NAME:'sample6'});"
            + "entity11.push({ID:350, NAME:'sample1'});"
            + "entity11.push({ID:110, NAME:'sample2', badNamedProperty:'badValue'});"
            + "entity11.push({ID:210, NAME:'sample3'});"
            + "entity11.push({ID:120, NAME:'sample4'});"
            + "entity11.push({ID:420, NAME:'sample5'});"
            + "entity11[entity11.length] = {ID:490, NAME:'sample9'};"
            + "entity11[5] = {ID:820, NAME:'sample8'};"
            + "entity11.pop();"
            + "m = entity11.md.length;\n"
            + "mp = entity11.params.length;\n"
            + "var f0 = entity11.md[0];\n"
            + "var f5 = entity11.md[5];\n"
            + "var firstRow = entity11[0];\n"
            + "var rowData1_1 = firstRow.ID;\n"
            + "var rowData2_1 = firstRow.NAME;\n"
            + "var rowData3_1 = firstRow.FIELD3;\n"
            + "var rowData4_1 = firstRow.FIELD4;\n"
            + "var rowData5_1 = firstRow.FIELD5;\n"
            + "var rowData6_1 = firstRow.FIELD6;\n"
            + "var secondRow = entity11[1];\n"
            + "var rowData1_2 = secondRow.ID;\n"
            + "var rowData2_2 = secondRow.NAME;\n"
            + "var rowData3_2 = secondRow.FIELD3;\n"
            + "var rowData4_2 = secondRow.FIELD4;\n"
            + "var rowData5_2 = secondRow.FIELD5;\n"
            + "var rowData6_2 = secondRow.FIELD6;\n"
            + "secondRow.FIELD6 = 0.56;\n"
            + "param1 = secondRow.FIELD6;\n"
            + "entity11.sort((function(a, b){\n"
            + "    return a.ID > b.ID?1:-1;\n"
            + "}));\n"
            + "var f1 = entity11.md[0];\n"
            + "var entity11Md = entity11.md;\n"
            + "var fieldsLength = entity11Md.length;\n"
            + "var lastField = entity11Md[entity11Md.length-1];\n"
            + "for(var pi in entity11)\n"
            + "    if(param2 == null)\n"
            + "        param2 = pi;\n"
            + "    else"
            + "        param2 += \"\\n\"+pi;\n"
            + "for(var fi in entity11.md)\n"
            + "    if(param3 == null)\n"
            + "        param3 = fi;\n"
            + "    else"
            + "        param3 += \"\\n\"+fi;\n"
            + "var rf = entity11.afterLast;\n"
            + "rf.apply(entity11)";

    @Test
    public void tablePatternSyntaxicTest() throws Exception {
        System.out.println("Testing rowset's script syntax. Cases like a params.md.param1 = ...");
        scopeSyntaxicTest(SCRIPT_TABLE_PATTERN_TEST_SOURCE);
    }

    @Test
    public void ormSyntaxicTest() throws Exception {
        System.out.println("Testing rowset's script syntax. Cases like a 'var r = ds[10];' ...");
        scopeSyntaxicTest(SCRIPT_ORM_TEST_SOURCE);
    }

    private static void scopeSyntaxicTest(String aSource) throws Exception {
        String entityName = "entity11";
        Double paramValue = new Double(98.597878f);
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            ApplicationDbModel dm = new ApplicationDbModel(client);
            Parameters params = dm.getParameters();
            Parameter param = (Parameter) params.createNewField("param1");
            param.setTypeInfo(DataTypeInfo.DOUBLE);
            param.setValue(paramValue);
            params.add(param);
            Parameter param2 = (Parameter) params.createNewField("param2");
            param2.setTypeInfo(DataTypeInfo.VARCHAR);
            params.add(param2);
            Parameter param3 = (Parameter) params.createNewField("param3");
            param3.setTypeInfo(DataTypeInfo.VARCHAR);
            params.add(param3);
            assertEquals(params.getParametersCount(), 3);
            final ApplicationDbEntity entity11 = dm.newGenericEntity();
            entity11.setQueryId("128015347915605");
            dm.addEntity(entity11);
            entity11.setName(entityName);

            ContextFactory cf = ContextFactory.getGlobal();
            Context cx = cf.enterContext();
            try {
                Scriptable scope = cx.newObject(ScriptUtils.getScope());
                dm.setRuntime(true);
                dm.setScriptThis(scope);
                // let's compile test script
                Script script = cx.compileString(aSource, "rowsetSyntaxTest", 0, null);
                script.exec(cx, scope);
                assertEquals(0.56, param.getValue());
            } finally {
                Context.exit();
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Test
    public void scopeTechnicalTest() throws Exception {
        System.out.println("Testing rowset's script internals.");

        String paramsName = Model.PARAMETERS_SCRIPT_NAME;

        String entityName = "entity11";
        String paramName = "param1";
        Double paramValue = new Double(98.597878f);
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            ApplicationDbModel dm = new ApplicationDbModel(client);
            Parameters params = dm.getParameters();
            Parameter param = (Parameter) params.createNewField(paramName);
            param.setTypeInfo(DataTypeInfo.DOUBLE);
            param.setValue(paramValue);
            params.add(param);

            assertEquals(params.getParametersCount(), 1);
            final ApplicationDbEntity entity11 = dm.newGenericEntity();
            entity11.setQueryId("128015347915605");
            dm.addEntity(entity11);
            entity11.setName(entityName);

            ContextFactory cf = ContextFactory.getGlobal();
            Context cx = cf.enterContext();
            try {
                Scriptable scope = cx.newObject(ScriptUtils.getScope());
                dm.setRuntime(true);
                dm.setScriptThis(scope);

                RowsetHostObject srEntity = (RowsetHostObject) scope.get(entityName, scope);
                assertNotNull(srEntity);
                assertTrue(srEntity.unwrap() instanceof ScriptableRowset);

                String lName = (String) srEntity.get("NAME", srEntity);
                assertNotNull(lName);

                FieldsHostObject srMdEntity = (FieldsHostObject) srEntity.get(Model.DATASOURCE_METADATA_SCRIPT_NAME, srEntity);
                assertNotNull(srMdEntity);
                assertTrue(srMdEntity.unwrap() instanceof Fields);
                assertSame(srMdEntity.unwrap(), entity11.getFields());

                ParametersHostObject srPEntity = (ParametersHostObject) srEntity.get(paramsName, srEntity);
                assertNotNull(srPEntity);
                //assertTrue(srPEntity.unwrap() instanceof Parameters);
                assertSame(srPEntity.unwrap(), entity11.getQuery().getParameters());

                Field lField = (Field) ((NativeJavaObject) srMdEntity.get("NAME", srMdEntity)).unwrap();
                assertNotNull(lField);
                assertEquals(lField, entity11.getFields().get("NAME"));

                RowsetHostObject srParams = (RowsetHostObject) scope.get(paramsName, scope);
                assertNotNull(srParams);
                assertTrue(srParams.unwrap() instanceof ScriptableRowset);

                Double vParam = (Double) scope.get(paramName, scope); // var pv = <paramName>;
                assertNotNull(vParam);
                assertEquals(vParam, paramValue);

                Double vParam1 = (Double) srParams.get(paramName, srParams); // var pv = params.<paramName>;
                assertNotNull(vParam1);
                assertEquals(vParam1, paramValue);

                /*
                 * NativeJavaObject srMdParams = (NativeJavaObject)
                 * scope.get(Model.DATASOURCE_METADATA_SCRIPT_NAME, scope);
                 * assertTrue(srMdParams.unwrap() instanceof Parameters);
                 * assertEquals(srMdParams.unwrap(), params);
                 *
                 * Parameter mdParam = (Parameter) ((NativeJavaObject)
                 * srMdParams.get(paramName, srMdParams)).unwrap(); // var mdP =
                 * md.param1; assertEquals(mdParam, param);
                 */
                FieldsHostObject srMdParams1 = (FieldsHostObject) srParams.get(Model.DATASOURCE_METADATA_SCRIPT_NAME, scope); // var p = params.md
                assertTrue(srMdParams1.unwrap() instanceof Parameters);
                assertEquals(srMdParams1.unwrap(), params);
                assertSame(srMdParams1.unwrap(), params);

                Parameter mdParam1 = (Parameter) ((NativeJavaObject) srMdParams1.get(paramName, srMdParams1)).unwrap(); // var p = params.md.param1;
                assertEquals(mdParam1, param);
                assertSame(mdParam1, param);
            } finally {
                Context.exit();
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }
}
