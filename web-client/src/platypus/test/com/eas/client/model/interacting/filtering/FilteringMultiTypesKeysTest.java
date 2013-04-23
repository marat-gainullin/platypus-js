package com.eas.client.model.interacting.filtering;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.bearsoft.rowset.Rowset;

public class FilteringMultiTypesKeysTest extends FilteringTest {

	@Override
	public void validate() throws Exception {
		System.out
		.println("Test of filtering process with key values of various types, but same values");
		boolean met = false;
		ModelState state = new ModelState(model);
		Rowset rowset = state.IZMERJAEMIE_VELICHINI.getRowset();
		int pkColIndex = rowset.getFields().find("ID");

		// BigDecimal test
		met = false;
		rowset.beforeFirst();
		while (rowset.next()) {
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(SILA_EL)) {
					// avoid converting to test pre filter converting capability
					// of our entities
					rowset.getCurrentRow().setColumnObject(pkColIndex,
							BigDecimal.valueOf(SILA_EL));
					met = true;
				}
			}
		}
		assertTrue(met);
		met = false;
		rowset.beforeFirst();
		while (rowset.next()) {
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(SILA_EL)) {
					assertEquals(1, state.NAIMENOVANIA_SI.getRowset().size());
					met = true;
				}
			}
		}
		assertTrue(met);

		// BigInteger test
		met = false;
		rowset.beforeFirst();
		while (rowset.next()) {
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(SILA_EL)) {
					// avoid converting to test pre filter converting capability
					// of our entities
					rowset.getCurrentRow().setColumnObject(pkColIndex,
							BigInteger.valueOf(SILA_EL));
					met = true;
				}
			}
		}
		assertTrue(met);
		met = false;
		rowset.beforeFirst();
		while (rowset.next()) {
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(SILA_EL)) {
					assertEquals(1, state.NAIMENOVANIA_SI.getRowset().size());
					met = true;
				}
			}
		}
		assertTrue(met);

		// String test
		met = false;
		rowset.beforeFirst();
		while (rowset.next()) {
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(SILA_EL)) {
					// avoid converting to test pre filter converting capability
					// of our entities
					rowset.getCurrentRow().setColumnObject(pkColIndex,
							String.valueOf(SILA_EL));
					met = true;
				}
			}
		}
		assertTrue(met);
		met = false;
		rowset.beforeFirst();
		while (rowset.next()) {
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(SILA_EL)) {
					assertEquals(1, state.NAIMENOVANIA_SI.getRowset().size());
					met = true;
				}
			}
		}
		assertTrue(met);
	}

}
