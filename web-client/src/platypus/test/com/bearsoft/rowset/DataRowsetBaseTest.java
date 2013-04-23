/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset;

import com.bearsoft.rowset.events.*;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.bearsoft.rowset.exceptions.InvalidCursorPositionException;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 
 * @author mg
 */
public class DataRowsetBaseTest extends RowsetBaseTest {

	protected static final long millis = System.currentTimeMillis();
	protected static Object[][] testData = new Object[][] { { 1L, true, "sample string data20", new Date(millis), null, BigDecimal.valueOf(345677.9898d), new BigInteger("345677"), "true string1" },
	        { 2L, true, "sample string data17", null, null, BigDecimal.valueOf(345677.9898d), new BigInteger("345677"), "true string2" },
	        { 3L, true, "sample string data15", null, null, BigDecimal.valueOf(345677.9898d), new BigInteger("345677"), "true string3" },
	        { 4L, true, "sample string data16", new Date(millis + 1), null, BigDecimal.valueOf(345677.9898d), new BigInteger("345677"), "true string4" },
	        { 5L, true, "sample string data21", new Date(millis + 2), null, new BigDecimal(54367f), new BigInteger("345677"), "true string5" },
	        { 6L, true, "sample string data1", new Date(millis + 3), null, new BigDecimal(54367f), new BigInteger("345677"), "true string6" },
	        { 7L, true, "sample string data2", new Date(millis + 4), null, new BigDecimal(54367f), new BigInteger("345677"), "true string7" },
	        { 8L, true, "sample string data3", new Date(millis + 5), null, new BigDecimal(54367f), new BigInteger("345677"), "true string8" },
	        { 9L, true, "sample string data4", new Date(millis + 6), null, new BigDecimal(54367f), new BigInteger("345677"), "true string9" },
	        { 10L, true, "sample string data5", new Date(millis + 7), null, new BigDecimal(54367f), new BigInteger("345677"), "true string10" },
	        { 11L, true, "sample string data6", new Date(millis + 8), null, new BigDecimal(54367f), new BigInteger("745677"), "true string11" },
	        { 12L, false, "sample string data7", new Date(millis + 9), null, new BigDecimal(54367f), new BigInteger("745677"), "true string12" },
	        { 13L, false, "sample string data8", new Date(millis + 10), null, new BigDecimal(54367f), new BigInteger("748677"), "false string13" },
	        { 14L, false, "sample string data9", new Date(millis + 11), null, new BigDecimal(54367f), new BigInteger("745677"), "true string14" },
	        { 15L, false, "sample string data10", new Date(millis + 12), null, BigDecimal.valueOf(345677.9898d), new BigInteger("745677"), "true string15" },
	        { 16L, false, "sample string data11", new Date(millis + 13), null, BigDecimal.valueOf(345677.9898d), new BigInteger("745677"), "true string16" },
	        { 17L, false, "sample string data12", new Date(millis + 14), null, BigDecimal.valueOf(345677.9898d), new BigInteger("745677"), "true string17" },
	        { 18L, false, "sample string data13", new Date(millis + 15), null, BigDecimal.valueOf(345677.9898d), new BigInteger("745677"), "true string18" },
	        { 19L, false, "sample string data14", new Date(millis + 16), null, BigDecimal.valueOf(345677.9898d), new BigInteger("745677"), "true string19" },
	        { 20L, false, "sample string data22", new Date(millis + 17), null, BigDecimal.valueOf(345677.9898d), new BigInteger("745677"), "true string21" },
	        { 21L, false, "sample string data22", new Date(millis + 18), null, BigDecimal.valueOf(345677.9898d), new BigInteger("345677"), "true string22" },
	        { 22L, false, "sample string data22", new Date(millis + 19), null, BigDecimal.valueOf(345677.9898d), new BigInteger("345677"), "true string23" } };
	protected static Fields fields;
	protected static String TEST_TABLE_NAME = "TEST_TABLE";
	protected static String TEST_TABLE_DESCRIPTION = "Тестовая таблица";
	protected static String TEST_FIELD_PREFIX = "TT_";
	static {
		fields = new Fields();
		fields.setTableDescription(TEST_TABLE_DESCRIPTION);
		Field f1 = new Field(TEST_FIELD_PREFIX + "ID", "Идентификатор");

		f1.getTypeInfo().setType(java.sql.Types.NUMERIC);
		f1.setPk(true);
		f1.setNullable(false);
		fields.add(f1);
		Field f2 = new Field(TEST_FIELD_PREFIX + "CONFIRMED", "Подтверждено");
		f2.getTypeInfo().setType(java.sql.Types.BOOLEAN);
		fields.add(f2);
		Field f3 = new Field(TEST_FIELD_PREFIX + "CAPTION", "Заголовок");
		f3.getTypeInfo().setType(java.sql.Types.VARCHAR);
		fields.add(f3);
		Field f4 = new Field(TEST_FIELD_PREFIX + "TIME", "Время создания");
		f4.getTypeInfo().setType(java.sql.Types.DATE);
		fields.add(f4);
		Field f5 = new Field(TEST_FIELD_PREFIX + "BLOCK", "Кусок данных");
		f5.getTypeInfo().setType(java.sql.Types.BLOB);
		fields.add(f5);
		Field f6 = new Field(TEST_FIELD_PREFIX + "ORDER", "Порядок");
		f6.getTypeInfo().setType(java.sql.Types.DECIMAL);
		fields.add(f6);
		Field f7 = new Field(TEST_FIELD_PREFIX + "AMOUNT", "Количество");
		f7.getTypeInfo().setType(java.sql.Types.NUMERIC);
		f7.setNullable(false);
		fields.add(f7);
		Field f8 = new Field(TEST_FIELD_PREFIX + "DESCRIPTION", "Описание");
		f8.getTypeInfo().setType(java.sql.Types.NVARCHAR);
		f8.setNullable(false);
		fields.add(f8);
	}

	protected void checkRowsetCorrespondToTestData(Rowset rowset) throws InvalidColIndexException, InvalidCursorPositionException {
		for (int i = 1; i <= rowset.size(); i++) {
			rowset.absolute(i);
			for (int colIndex = 1; colIndex <= rowset.getFields().getFieldsCount(); colIndex++) {
				Object tstData = testData[i - 1][colIndex - 1];
				// check of converter work
				if ((rowset.getFields().get(colIndex).getTypeInfo().getType() == java.sql.Types.NUMERIC || rowset.getFields().get(colIndex).getTypeInfo().getType() == java.sql.Types.DECIMAL)
				        && (tstData instanceof Number)) {
					tstData = new Double(((Number)tstData).doubleValue());
				}
				//
				assertEquals(rowset.getObject(colIndex), tstData);
			}
		}
	}

	protected void fillInRowset(Rowset rowset) throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
		for (int i = 0; i < testData.length; i++) {
			int oldSize = rowset.size();
			rowset.insert();
			if (oldSize < rowset.size()) {
				assertTrue(rowset.getRow(rowset.getCursorPos()) == rowset.getCurrentRow());
				assertTrue(rowset.getCurrentRow().isInserted());
				for (int colIndex = 0; colIndex < fields.getFieldsCount(); colIndex++) {
					rowset.updateObject(colIndex + 1, testData[i][colIndex]);
				}
			}
		}
	}

	protected Rowset initRowset() throws InvalidCursorPositionException, InvalidColIndexException, RowsetException {
		Rowset rowset = new Rowset(fields);
		// initial filling tests
		assertTrue(rowset.isEmpty());
		fillInRowset(rowset);
		assertFalse(rowset.isEmpty());
		rowset.originalToCurrent();
		assertTrue(rowset.isEmpty());
		fillInRowset(rowset);
		assertFalse(rowset.isEmpty());
		rowset.currentToOriginal();
		assertFalse(rowset.isEmpty());
		return rowset;
	}

	public void testDummy() {
	}

	public static class EventsReciver implements RowsetListener {

		public boolean allowScroll = true;
		public boolean allowSort = true;
		public boolean allowDelete = true;
		public boolean allowInsert = true;
		public boolean allowChange = true;
		public boolean allowFilter = true;
		public boolean allowRequery = true;
		public boolean allowPaging = true;
		public boolean allowSave = true;
		public int willScroll;
		public int willSort;
		public int willDelete;
		public int willInsert;
		public int willChange;
		public int willFilter;
		public int willRequery;
		public int beforeRequery;
		public int willNextPageFetch;
		public int scrolled;
		public int sorted;
		public int deleted;
		public int inserted;
		public int changed;
		public int filtered;
		public int requeried;
		public int netError;
		public int nextPageFetched;
		public int saved;
		public int rolledback;

		@Override
		public boolean willSort(RowsetSortEvent event) {
			++willSort;
			return allowSort;
		}

		@Override
		public boolean willScroll(RowsetScrollEvent event) {
			++willScroll;
			return allowScroll;
		}

		@Override
		public boolean willFilter(RowsetFilterEvent event) {
			++willFilter;
			return allowFilter;
		}

		@Override
		public boolean willRequery(RowsetRequeryEvent event) {
			++willRequery;
			return allowRequery;
		}

		@Override
		public void beforeRequery(RowsetRequeryEvent event) {
			++beforeRequery;
		}

		@Override
		public boolean willInsertRow(RowsetInsertEvent event) {
			++willInsert;
			return allowInsert;
		}

		@Override
		public boolean willChangeRow(RowChangeEvent event) {
			++willChange;
			return allowChange;
		}

		@Override
		public boolean willDeleteRow(RowsetDeleteEvent event) {
			++willDelete;
			return allowDelete;
		}

		@Override
		public void rowsetNetError(RowsetNetErrorEvent event) {
			++netError;
		}

		@Override
		public void rowsetFiltered(RowsetFilterEvent event) {
			++filtered;
		}

		@Override
		public void rowsetRequeried(RowsetRequeryEvent event) {
			++requeried;
		}

		@Override
		public void rowsetSaved(RowsetSaveEvent event) {
			++saved;
		}

		@Override
		public void rowsetRolledback(RowsetRollbackEvent event) {
			++rolledback;
		}

		@Override
		public void rowsetScrolled(RowsetScrollEvent event) {
			++scrolled;
		}

		@Override
		public void rowInserted(RowsetInsertEvent event) {
			assertNotNull(event.getRowset());
			assertNotNull(event.getRow());
			++inserted;
		}

		@Override
		public void rowChanged(RowChangeEvent event) {
			++changed;
		}

		@Override
		public void rowDeleted(RowsetDeleteEvent event) {
			++deleted;
		}

		@Override
		public void rowsetSorted(RowsetSortEvent event) {
			++sorted;
		}
	}
}
