/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.model.BaseTest;
import com.eas.client.resourcepool.GeneralResourceProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Simple changes apply test
 *
 * @author mg
 */
public class ApplyTest extends BaseTest {

    protected static final String tableName = "access_list";
    protected static final String selectClause = "select a.ACCESS_LIST_ID, a.ACCESS_NAME, a.KEY_WORD, a.FORM_ID from " + tableName + " a where a.KEY_WORD = ? or (a.KEY_WORD is null and ? is null)";
    protected static final String paramDataValue = "Passing";
    protected static final String paramNullValue = null;

    @Test
    public void applyCRUDTest() throws Exception {
        System.out.println("applyCRUDTest");
        try (DatabasesClientWithResource resource = BaseTest.initDevelopTestClient()) {
            DatabasesClient client = resource.getClient();
            Parameters params = new Parameters();
            Parameter param1 = params.createNewField();
            Parameter param2 = params.createNewField();
            params.add(param1);
            params.add(param2);

            Set<DataTypeInfo> paramTypes = new HashSet<>();
            paramTypes.add(DataTypeInfo.CHAR);
            paramTypes.add(DataTypeInfo.VARCHAR);
            paramTypes.add(DataTypeInfo.LONGVARCHAR);
            //paramTypes.add(DataTypeInfo.NCHAR);
            //paramTypes.add(DataTypeInfo.NVARCHAR);
            //paramTypes.add(DataTypeInfo.LONGNVARCHAR);

            FlowProvider flow = client.createFlowProvider(null, null, tableName, selectClause, null, Collections.<String>emptySet(), Collections.<String>emptySet());

            for (DataTypeInfo typeInfo : paramTypes) {
                param1.setTypeInfo(typeInfo);
                param2.setTypeInfo(typeInfo);

                param1.setValue(paramDataValue);
                param2.setValue(paramDataValue);

                Rowset rs = new Rowset(flow);
                rs.refresh(params);
                assertTrue(rs.size() >= 1);

                int pkColIndex = rs.getFields().find("ACCESS_LIST_ID");
                rs.getFields().get(pkColIndex).setPk(true);
                Integer newPkValue = 1;

                rs.insert();
                rs.updateObject(pkColIndex, newPkValue);
                rs.updateObject(rs.getFields().find("KEY_WORD"), paramDataValue);

                int oldCursorPos = rs.getCursorPos();
                client.commit(null);
                assertTrue(client.getChangeLog(null, null).isEmpty());
                assertEquals(oldCursorPos, rs.getCursorPos());

                Fields fields = rs.getFields();
                rs.refresh(params);
                assertTrue(rs.size() == 2);
                Fields fields1 = rs.getFields();
                assertSame(fields1, fields);

                rs.beforeFirst();
                while (rs.next()) {
                    Object oPkValue = rs.getObject(pkColIndex);
                    assertNotNull(oPkValue);
                    assertTrue(oPkValue instanceof Number);
                    if (RowsetUtils.number2BigDecimal((Number) oPkValue).equals(RowsetUtils.number2BigDecimal(newPkValue))) {
                        rs.updateObject(rs.getFields().find("KEY_WORD"), paramNullValue);
                        break;
                    }
                }
                oldCursorPos = rs.getCursorPos();
                client.commit(null);
                assertTrue(client.getChangeLog(null, null).isEmpty());
                assertEquals(oldCursorPos, rs.getCursorPos());

                rs.refresh(params);
                assertTrue(rs.size() == 1);

                param1.setValue(paramNullValue);
                param2.setValue(paramNullValue);

                rs.refresh(params);
                assertTrue(rs.size() == 2);
                rs.beforeFirst();
                while (rs.next()) {
                    Object oPkValue = rs.getObject(pkColIndex);
                    assertNotNull(oPkValue);
                    assertTrue(oPkValue instanceof Number);
                    if (RowsetUtils.number2BigDecimal((Number) oPkValue).equals(RowsetUtils.number2BigDecimal(newPkValue))) {
                        rs.delete();
                        break;
                    }
                }
                oldCursorPos = rs.getCursorPos();
                client.commit(null);
                assertTrue(client.getChangeLog(null, null).isEmpty());
                assertEquals(oldCursorPos, rs.getCursorPos());
                rs.refresh(params);
                assertTrue(rs.size() == 1);
                param1.setValue(paramDataValue);
                param2.setValue(paramDataValue);

                rs.refresh(params);
                assertTrue(rs.size() == 1);
            }
        }
    }

    @Test
    public void applyNullsTest() throws Exception {
        System.out.println("applyNullsTest");
        try (DatabasesClientWithResource resource = BaseTest.initDevelopTestClient()) {
            DatabasesClient client = resource.getClient();
            Parameters params = new Parameters();
            Parameter param1 = params.createNewField();
            Parameter param2 = params.createNewField();
            params.add(param1);
            params.add(param2);

            Set<DataTypeInfo> paramTypes = new HashSet<>();
            paramTypes.add(DataTypeInfo.CHAR);
            paramTypes.add(DataTypeInfo.VARCHAR);
            paramTypes.add(DataTypeInfo.LONGVARCHAR);
            //paramTypes.add(DataTypeInfo.NCHAR);
            //paramTypes.add(DataTypeInfo.NVARCHAR);
            //paramTypes.add(DataTypeInfo.LONGNVARCHAR);
            FlowProvider flow = client.createFlowProvider(null, null, tableName, selectClause, null, Collections.<String>emptySet(), Collections.<String>emptySet());

            for (DataTypeInfo typeInfo : paramTypes) {
                param1.setTypeInfo(typeInfo);
                param1.setValue(paramNullValue);
                param2.setTypeInfo(typeInfo);
                param2.setValue(paramNullValue);

                Rowset rs = new Rowset(flow);
                rs.refresh(params);
                assertTrue(rs.size() >= 1);

                int pkColIndex = rs.getFields().find("ACCESS_LIST_ID");
                rs.getFields().get(pkColIndex).setPk(true);
                Integer newPkValue = 1;

                rs.insert();
                rs.updateObject(pkColIndex, newPkValue);
                rs.updateObject(rs.getFields().find("KEY_WORD"), paramNullValue);

                client.commit(null);
                assertTrue(client.getChangeLog(null, null).isEmpty());

                Fields fields = rs.getFields();
                rs.refresh(params);
                assertTrue(rs.size() == 2);
                Fields fields1 = rs.getFields();
                assertSame(fields1, fields);

                rs.beforeFirst();
                while (rs.next()) {
                    Object oPkValue = rs.getObject(pkColIndex);
                    assertNotNull(oPkValue);
                    assertTrue(oPkValue instanceof Number);
                    if (RowsetUtils.number2BigDecimal((Number) oPkValue).equals(RowsetUtils.number2BigDecimal(newPkValue))) {
                        rs.delete();
                        break;
                    }
                }
                client.commit(null);
                assertTrue(client.getChangeLog(null, null).isEmpty());

                rs.refresh(params);
                assertTrue(rs.size() == 1);
            }
        }
    }
}
