package com.eas.client.application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.Command;
import com.bearsoft.rowset.changes.Delete;
import com.bearsoft.rowset.changes.Insert;
import com.bearsoft.rowset.changes.Update;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.eas.client.serial.ChangesWriter;
import com.google.gwt.junit.client.GWTTestCase;

public class ChangesWriterTest extends GWTTestCase {

	protected static final String WRITTEN_CHANGES = "[{\"kind\":\"insert\", \"entity\":\"testEntity\", \"data\":{\"data\\\"\\\"1\":56, \"data2\":\"data2Value\", \"da\\\"ta3\":true, \"data4\":false, \"data5\":1346067735514}},{\"kind\":\"update\", \"entity\":\"testEntity\", \"data\":{\"data\\\"\\\"1\":56, \"data2\":\"data2Value\", \"da\\\"ta3\":true, \"data4\":false, \"data5\":1346067735514}, \"keys\":{\"key1\":78.9000015258789, \"key2\":\"key2Value\"}},{\"kind\":\"delete\", \"entity\":\"testEntity\", \"keys\":{\"key1\":78.9000015258789, \"key2\":\"key2Value\"}},{\"kind\":\"command\", \"entity\":\"testEntity\", \"parameters\":{\"key1\":78.9000015258789, \"key2\":\"key2Value\"}}]";

	@Override
	public String getModuleName() {
		return "com.eas.client.application.Main";
	}

	public void testSerializeTest() throws Exception {
		String entityId = "testEntity";
		String command = "testCommand";
		Change.Value key1 = new Change.Value("key1", 78.9f, DataTypeInfo.FLOAT);
		Change.Value key2 = new Change.Value("key2", "key2Value", DataTypeInfo.CHAR);
		Change.Value[] keys = new Change.Value[] { key1, key2 };

		Date date = new Date(1346067735514L);
		Change.Value data1 = new Change.Value("data\"\"1", 56, DataTypeInfo.INTEGER);
		Change.Value data2 = new Change.Value("data2", "data2Value", DataTypeInfo.VARCHAR);
		Change.Value data3 = new Change.Value("da\"ta3", true, DataTypeInfo.BOOLEAN);
		Change.Value data4 = new Change.Value("data4", false, DataTypeInfo.BIT);
		Change.Value data5 = new Change.Value("data5", date, DataTypeInfo.TIMESTAMP);
		Change.Value[] data = new Change.Value[] { data1, data2, data3, data4, data5 };

		List<Change> changes = new ArrayList();
		Insert i = new Insert(entityId);
		i.data = data;
		Update u = new Update(entityId);
		u.data = data;
		u.keys = keys;
		Delete d = new Delete(entityId);
		d.keys = keys;
		Command c = new Command(entityId);
		c.command = command;
		c.parameters = keys;
		changes.add(i);
		changes.add(u);
		changes.add(d);
		changes.add(c);
		String result = ChangesWriter.writeLog(changes);
		assertEquals(WRITTEN_CHANGES, result);
	}
}
