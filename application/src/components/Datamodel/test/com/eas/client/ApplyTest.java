/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.DelegatingFlowProvider;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.utils.RowsetUtils;
import com.eas.client.model.application.BaseTest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
            final List<Change> commonLog = new ArrayList<>();
            Map<String, List<Change>> changeLogs = new HashMap<>();
            changeLogs.put(null, commonLog);

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

            FlowProvider flow = client.createFlowProvider(null, tableName, selectClause, null, Collections.<String>emptySet(), Collections.<String>emptySet());

            for (DataTypeInfo typeInfo : paramTypes) {
                param1.setTypeInfo(typeInfo);
                param2.setTypeInfo(typeInfo);

                param1.setValue(paramDataValue);
                param2.setValue(paramDataValue);

                Rowset rowset = new Rowset(flow);
                rowset.setFlowProvider(new DelegatingFlowProvider(rowset.getFlowProvider()) {

                    @Override
                    public List<Change> getChangeLog() {
                        return commonLog;
                    }

                });
                rowset.refresh(params);
                assertTrue(rowset.size() >= 1);

                int pkColIndex = rowset.getFields().find("ACCESS_LIST_ID");
                rowset.getFields().get(pkColIndex).setPk(true);
                Integer newPkValue = 1;

                rowset.insert();
                rowset.updateObject(pkColIndex, newPkValue);
                rowset.updateObject(rowset.getFields().find("KEY_WORD"), paramDataValue);

                int oldCursorPos = rowset.getCursorPos();
                client.commit(changeLogs);
                assertTrue(commonLog.isEmpty());
                assertEquals(oldCursorPos, rowset.getCursorPos());

                Fields fields = rowset.getFields();
                rowset.refresh(params);
                assertTrue(rowset.size() == 2);
                Fields fields1 = rowset.getFields();
                assertSame(fields1, fields);

                rowset.beforeFirst();
                while (rowset.next()) {
                    Object oPkValue = rowset.getObject(pkColIndex);
                    assertNotNull(oPkValue);
                    assertTrue(oPkValue instanceof Number);
                    if (RowsetUtils.number2BigDecimal((Number) oPkValue).equals(RowsetUtils.number2BigDecimal(newPkValue))) {
                        rowset.updateObject(rowset.getFields().find("KEY_WORD"), paramNullValue);
                        break;
                    }
                }
                oldCursorPos = rowset.getCursorPos();
                client.commit(changeLogs);
                assertTrue(commonLog.isEmpty());
                assertEquals(oldCursorPos, rowset.getCursorPos());

                rowset.refresh(params);
                assertTrue(rowset.size() == 1);

                param1.setValue(paramNullValue);
                param2.setValue(paramNullValue);

                rowset.refresh(params);
                assertTrue(rowset.size() == 2);
                rowset.beforeFirst();
                while (rowset.next()) {
                    Object oPkValue = rowset.getObject(pkColIndex);
                    assertNotNull(oPkValue);
                    assertTrue(oPkValue instanceof Number);
                    if (RowsetUtils.number2BigDecimal((Number) oPkValue).equals(RowsetUtils.number2BigDecimal(newPkValue))) {
                        rowset.delete();
                        break;
                    }
                }
                oldCursorPos = rowset.getCursorPos();
                client.commit(changeLogs);
                assertTrue(commonLog.isEmpty());
                assertEquals(oldCursorPos, rowset.getCursorPos());
                rowset.refresh(params);
                assertTrue(rowset.size() == 1);
                param1.setValue(paramDataValue);
                param2.setValue(paramDataValue);

                rowset.refresh(params);
                assertTrue(rowset.size() == 1);
            }
        }
    }

    @Test
    public void applyNullsTest() throws Exception {
        System.out.println("applyNullsTest");
        try (DatabasesClientWithResource resource = BaseTest.initDevelopTestClient()) {
            final List<Change> commonLog = new ArrayList<>();
            Map<String, List<Change>> changeLogs = new HashMap<>();
            changeLogs.put(null, commonLog);

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
            FlowProvider flow = client.createFlowProvider(null, tableName, selectClause, null, Collections.<String>emptySet(), Collections.<String>emptySet());

            for (DataTypeInfo typeInfo : paramTypes) {
                param1.setTypeInfo(typeInfo);
                param1.setValue(paramNullValue);
                param2.setTypeInfo(typeInfo);
                param2.setValue(paramNullValue);

                Rowset rowset = new Rowset(flow);
                rowset.setFlowProvider(new DelegatingFlowProvider(rowset.getFlowProvider()) {

                    @Override
                    public List<Change> getChangeLog() {
                        return commonLog;
                    }

                });
                rowset.refresh(params);
                assertTrue(rowset.size() >= 1);

                int pkColIndex = rowset.getFields().find("ACCESS_LIST_ID");
                rowset.getFields().get(pkColIndex).setPk(true);
                Integer newPkValue = 1;

                rowset.insert();
                rowset.updateObject(pkColIndex, newPkValue);
                rowset.updateObject(rowset.getFields().find("KEY_WORD"), paramNullValue);

                client.commit(changeLogs);
                assertTrue(commonLog.isEmpty());

                Fields fields = rowset.getFields();
                rowset.refresh(params);
                assertTrue(rowset.size() == 2);
                Fields fields1 = rowset.getFields();
                assertSame(fields1, fields);

                rowset.beforeFirst();
                while (rowset.next()) {
                    Object oPkValue = rowset.getObject(pkColIndex);
                    assertNotNull(oPkValue);
                    assertTrue(oPkValue instanceof Number);
                    if (RowsetUtils.number2BigDecimal((Number) oPkValue).equals(RowsetUtils.number2BigDecimal(newPkValue))) {
                        rowset.delete();
                        break;
                    }
                }
                client.commit(changeLogs);
                assertTrue(commonLog.isEmpty());

                rowset.refresh(params);
                assertTrue(rowset.size() == 1);
            }
        }
    }
}
