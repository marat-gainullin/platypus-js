/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.threetier.requests;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.jdbc.JdbcReader;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.serial.BinaryRowsetWriter;
import com.eas.client.SQLUtils;
import com.eas.client.threetier.PlatypusRowsetWriter;
import com.eas.client.threetier.binary.PlatypusResponseReader;
import com.eas.client.threetier.binary.PlatypusResponseWriter;
import com.eas.client.threetier.binary.RequestsTags;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author pk
 */
public class RowsetResponseTest {
    
    private Rowset rowset;
    private Fields expectedFields;
    private byte[] rowsetData;

    public RowsetResponseTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp() throws ClassNotFoundException, SQLException, IOException, RowsetException {
        Class.forName("oracle.jdbc.OracleDriver");
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@asvr:1521/adb", "eas", "eas");
        final PreparedStatement stmt = conn.prepareStatement("select MDENT_ID, MDENT_NAME, MDENT_TYPE from MTD_ENTITIES");
        final ResultSet rs = stmt.executeQuery();
        JdbcReader rsReader = new JdbcReader();
        rowset = rsReader.readRowset(rs, -1);
        SQLUtils.processFieldsPreClient(rowset.getFields());
        expectedFields = rowset.getFields();
        ByteArrayOutputStream rowsetDataStream = new ByteArrayOutputStream();
        BinaryRowsetWriter rsWriter = new PlatypusRowsetWriter();
        rsWriter.write(rowset, rowsetDataStream);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ProtoWriter writer = new ProtoWriter(outStream);
        writer.put(RequestsTags.TAG_UPDATE_COUNT, 0);
        writer.put(RequestsTags.TAG_ROWSET);
        writer.put(CoreTags.TAG_STREAM, rowsetDataStream.toByteArray());
        writer.flush();
        rowsetData = outStream.toByteArray();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of writeData method, of class RowSetResponse.
     * @throws Exception
     */
    @Test
    public void testWriteData() throws Exception
    {
        System.out.println("writeData");
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        RowsetResponse instance = new RowsetResponse(1, rowset, 0);
        PlatypusResponseWriter bodyWriter = new PlatypusResponseWriter(outStream);
        instance.accept(bodyWriter);
        assertArrayEquals(rowsetData, outStream.toByteArray());
    }

    /**
     * Test of readData method, of class RowSetResponse.
     * @throws Exception
     */
    @Test
    public void testReadData() throws Exception
    {
        System.out.println("readData");
        RowsetResponse instance = new RowsetResponse(1L, null, 10, expectedFields);
        PlatypusResponseReader bodyReader = new PlatypusResponseReader(rowsetData);
        instance.accept(bodyReader);
        assertNotNull(instance.getRowset());
    }

    /**
     * Test of getRowset method, of class RowSetResponse.
     */
    @Test
    public void testGetRowset()
    {
        System.out.println("getRowset");
        RowsetResponse instance = new RowsetResponse(1, rowset, 0);
        assertEquals(rowset, instance.getRowset());
   }

}