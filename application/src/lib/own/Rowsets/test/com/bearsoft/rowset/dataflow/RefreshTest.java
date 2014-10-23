/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.dataflow;

import java.util.Set;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.RowsetConverter;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import java.util.HashSet;
import java.util.Properties;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class RefreshTest extends FlowBaseTest {

    @Test
    public void fieldsFromDatabaseTest() throws Exception {
        System.out.println("fieldsFromDatabaseTest");

        Properties props = new Properties();
        String url = makeTestConnectionDescription(props);

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

        ObservingResourcesProvider resCounter = new ObservingResourcesProvider(url, props, paramTypes.size() * 2, paramTypes.size() * 2, 0);
        JdbcFlowProvider flow = new JdbcFlowProviderAdapter(null, resCounter, new RowsetConverter(), selectClause);

        for (DataTypeInfo type : paramTypes) {
            param1.setTypeInfo(type);
            param1.setValue(paramDataValue);
            param2.setTypeInfo(type);
            param2.setValue(paramDataValue);

            Rowset rs = new Rowset(flow);
            rs.refresh(params, null, null);
            assertTrue(rs.size() >= 1);
            Fields fields = rs.getFields();
            rs.refresh(params, null, null);
            assertTrue(rs.size() >= 1);
            Fields fields1 = rs.getFields();
            assertSame(fields1, fields);
        }
        resCounter.testResources();
    }

    @Test
    public void fieldsFromDatabaseWithNullParameterTest() throws Exception {
        System.out.println("fieldsFromDatabaseWithNullParameterTest");

        Properties props = new Properties();
        String url = makeTestConnectionDescription(props);

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

        ObservingResourcesProvider resCounter = new ObservingResourcesProvider(url, props, paramTypes.size() * 2, paramTypes.size() * 2, 0);
        JdbcFlowProvider flow = new JdbcFlowProviderAdapter(null, resCounter, new RowsetConverter(), selectClause);
        // let's testResources null parameter values
        for (DataTypeInfo typeInfo : paramTypes) {
            param1.setTypeInfo(typeInfo);
            param1.setValue(paramNullValue);
            param2.setTypeInfo(typeInfo);
            param2.setValue(paramNullValue);

            Rowset rs = new Rowset(flow);
            rs.refresh(params, null, null);
            assertTrue(rs.size() >= 1);
            Fields fields = rs.getFields();
            rs.refresh(params, null, null);
            assertTrue(rs.size() >= 1);
            Fields fields1 = rs.getFields();
            assertSame(fields1, fields);
        }
        resCounter.testResources();
    }

    @Test
    public void predefinedFieldsTest() throws Exception {
        System.out.println("predefinedFieldsTest");

        Properties props = new Properties();
        String url = makeTestConnectionDescription(props);

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

        ObservingResourcesProvider resCounter = new ObservingResourcesProvider(url, props, paramTypes.size() * 2, paramTypes.size() * 2, 0);
        JdbcFlowProvider flow = new JdbcFlowProviderAdapter(null, resCounter, new RowsetConverter(), selectClause);
        for (DataTypeInfo typeInfo : paramTypes) {
            param1.setTypeInfo(typeInfo);
            param1.setValue(paramDataValue);
            param2.setTypeInfo(typeInfo);
            param2.setValue(paramDataValue);

            Rowset rs = new Rowset(flow);
            rs.refresh(params, null, null);
            assertTrue(rs.size() >= 1);
            Fields fields = rs.getFields();

            Rowset rs1 = new Rowset(fields);
            rs1.setFlowProvider(flow);
            rs1.refresh(params, null, null);
            assertTrue(rs1.size() >= 1);
            Fields fields1 = rs1.getFields();
            assertSame(fields1, fields);
        }
        resCounter.testResources();
    }

    @Test
    public void predefinedFieldsWithNullParameterTest() throws Exception {
        System.out.println("predefinedFieldsWithNullParameterTest");

        Properties props = new Properties();
        String url = makeTestConnectionDescription(props);

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

        ObservingResourcesProvider resCounter = new ObservingResourcesProvider(url, props, paramTypes.size() * 2, paramTypes.size() * 2, 0);
        JdbcFlowProvider flow = new JdbcFlowProviderAdapter(null, resCounter, new RowsetConverter(), selectClause);
        // let's testResources null parameter values
        for (DataTypeInfo typeInfo : paramTypes) {
            param1.setTypeInfo(typeInfo);
            param1.setValue(paramNullValue);
            param2.setTypeInfo(typeInfo);
            param2.setValue(paramNullValue);

            Rowset rs = new Rowset(flow);
            rs.refresh(params, null, null);
            assertTrue(rs.size() >= 1);
            Fields fields = rs.getFields();

            Rowset rs1 = new Rowset(fields);
            rs1.setFlowProvider(flow);
            rs1.refresh(params, null, null);
            assertTrue(rs1.size() >= 1);
            Fields fields1 = rs1.getFields();
            assertSame(fields1, fields);
        }
        resCounter.testResources();
    }
}
