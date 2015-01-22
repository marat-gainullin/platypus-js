/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.serial;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.RowsetBaseTest;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.serial.custom.CompactLobsSerializer;
import com.eas.proto.ProtoReaderException;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mg
 */
public class BinarySerializationTest extends RowsetBaseTest {

    public Object[] writeSamples(Rowset rowset) throws IOException, RowsetException {
        int testCount = 190;
        Object[] writtenData = new Object[testCount];
        for (int i = 0; i < writtenData.length; i++) {
            BinaryRowsetWriter writer = new BinaryRowsetWriter();
            byte[] rowsetData = writer.write(rowset);
            writtenData[i] = rowsetData;
        }
        return writtenData;
    }

    @Test
    public void stabilityTest() throws RowsetException, IOException, ProtoReaderException {
        System.out.println("stabilityTest");
        Rowset rowset = initRowset();
        Object[] writtenData = writeSamples(rowset);
        Object[] anotherWrittenData = writeSamples(rowset);
        for (int i = 0; i < anotherWrittenData.length; i++) {
            assertArrayEquals((byte[]) writtenData[i], (byte[]) anotherWrittenData[i]);
        }
    }

    @Test
    public void readAfterWriteTest() throws RowsetException, IOException, ProtoReaderException {
        System.out.println("readAfterWriteTest");
        Rowset rowset = initRowset();
        rowset.setCursorPos(0);
        Object[] writtenData = writeSamples(rowset);
        Object[] anotherWrittenData = writeSamples(rowset);
        for (int i = 0; i < anotherWrittenData.length; i++) {
            BinaryRowsetReader reader = new BinaryRowsetReader();
            Rowset rs = reader.read((byte[]) anotherWrittenData[i]);
            checkRowsetCorrespondToTestData(rs);
            rs.setCursorPos(0);
            BinaryRowsetWriter writer = new BinaryRowsetWriter();
            byte[] rowsetData = writer.write(rs);
            reader = new BinaryRowsetReader();
            rs = reader.read((byte[]) rowsetData);
            checkRowsetCorrespondToTestData(rs);
            assertArrayEquals(rowsetData, (byte[]) anotherWrittenData[i]);
            assertArrayEquals(rowsetData, (byte[]) writtenData[i]);
        }
    }

    public Object[] writeCustomSamples(Rowset rowset) throws IOException, RowsetException {
        int testCount = 190;
        Object[] writtenData = new Object[testCount];
        boolean sameSerializer = true;
        for (int i = 0; i < writtenData.length; i++) {
            BinaryRowsetWriter writer = new BinaryRowsetWriter();
            CompactLobsSerializer ser = new CompactLobsSerializer();
            writer.addSerializer(DataTypeInfo.CLOB, ser);
            if (!sameSerializer) {
                ser = new CompactLobsSerializer();
            }
            writer.addSerializer(DataTypeInfo.BLOB, ser);
            byte[] rowsetData = writer.write(rowset);
            writtenData[i] = rowsetData;
            sameSerializer = !sameSerializer;
        }
        return writtenData;
    }

    @Test
    public void customSerializationTest1() throws RowsetException, IOException, ProtoReaderException {
        System.out.println("customSerializationTest1");
        Rowset rowset = initRowset();
        rowset.setCursorPos(0);
        Object[] writtenData = writeCustomSamples(rowset);
        Object[] anotherWrittenData = writeCustomSamples(rowset);
        boolean sameSerializer = true;
        for (int i = 0; i < anotherWrittenData.length; i++) {
            BinaryRowsetReader reader = new BinaryRowsetReader();
            CompactLobsSerializer ser = new CompactLobsSerializer();
            reader.addSerializer(DataTypeInfo.CLOB, ser);
            if (!sameSerializer) {
                ser = new CompactLobsSerializer();
            }
            reader.addSerializer(DataTypeInfo.BLOB, ser);
            Rowset rs = reader.read((byte[]) anotherWrittenData[i]);
            rs.setCursorPos(0);
            BinaryRowsetWriter writer = new BinaryRowsetWriter();
            writer.addSerializer(DataTypeInfo.CLOB, ser);
            if (sameSerializer) {
                ser = new CompactLobsSerializer();
            }
            writer.addSerializer(DataTypeInfo.BLOB, ser);
            byte[] rowsetData = writer.write(rs);
            assertArrayEquals(rowsetData, (byte[]) anotherWrittenData[i]);
            assertArrayEquals(rowsetData, (byte[]) writtenData[i]);
            sameSerializer = !sameSerializer;
        }
    }

    @Test
    public void customSerializationTest2() throws RowsetException, IOException, ProtoReaderException {
        System.out.println("customSerializationTest2");
        CompactLobsSerializer ser = new CompactLobsSerializer();
        Rowset rowset = initRowset();
        rowset.setCursorPos(0);
        Object[] writtenData = writeCustomSamples(rowset);
        Object[] anotherWrittenData = writeCustomSamples(rowset);
        for (int i = 0; i < anotherWrittenData.length; i++) {
            BinaryRowsetReader reader = new BinaryRowsetReader();
            reader.addSerializer(DataTypeInfo.CLOB, ser);
            reader.addSerializer(DataTypeInfo.BLOB, ser);
            Rowset rs = reader.read((byte[]) anotherWrittenData[i]);
            rs.setCursorPos(0);
            BinaryRowsetWriter writer = new BinaryRowsetWriter();
            writer.addSerializer(DataTypeInfo.CLOB, ser);
            writer.addSerializer(DataTypeInfo.BLOB, ser);
            byte[] rowsetData = writer.write(rs);
            assertArrayEquals(rowsetData, (byte[]) anotherWrittenData[i]);
            assertArrayEquals(rowsetData, (byte[]) writtenData[i]);
        }
    }
}
