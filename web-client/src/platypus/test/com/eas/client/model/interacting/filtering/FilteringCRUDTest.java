package com.eas.client.model.interacting.filtering;

import com.bearsoft.rowset.Rowset;

public class FilteringCRUDTest extends FilteringTest {

	@Override
	public void validate() throws Exception {
		System.out.println("filteringCRUDTest");
		ModelState state = new ModelState(model);
		Rowset rowset = state.IZMERJAEMIE_VELICHINI.getRowset();
		int pkColIndex = rowset.getFields().find("ID");

		// edit rows from "dlina" to "sila sveta" and vice versa for several times
		for (int i = 0; i < 56; i++) {
			// ensure prestate
			rowset.beforeFirst();
			while (rowset.next()) {
				Object oPk = rowset.getObject(pkColIndex);
				assertNotNull(oPk);
				if (oPk instanceof Number) {
					Long lPk = ((Number) oPk).longValue();
					if (lPk.equals(DLINA)) {
						assertEquals(4, state.EDINICI_IZMERENIJA.getRowset()
								.size());
					} else if (lPk.equals(SILA_LIGHT)) {
						assertEquals(0, state.EDINICI_IZMERENIJA.getRowset()
								.size());
					}
				}
			}
			// let's edit filter-key field's value.
			rowset.beforeFirst();
			while (rowset.next()) {
				Object oPk = rowset.getObject(pkColIndex);
				assertNotNull(oPk);
				if (oPk instanceof Number) {
					Long lPk = ((Number) oPk).longValue();
					if (lPk.equals(DLINA)) {
						Rowset edRowset = state.EDINICI_IZMERENIJA.getRowset();
						int velColIndex = edRowset.getFields()
								.find("MEASURAND");
						int updated = 0;
						while (edRowset.size() > 0) {
							edRowset.first();
							edRowset.updateObject(velColIndex, SILA_LIGHT);
							updated++;
							assertTrue(updated <= 4);
						}
					}
				}
			}
			// ensure results
			rowset.beforeFirst();
			while (rowset.next()) {
				Object oPk = rowset.getObject(pkColIndex);
				assertNotNull(oPk);
				if (oPk instanceof Number) {
					Long lPk = ((Number) oPk).longValue();
					if (lPk.equals(DLINA)) {
						assertEquals(0, state.EDINICI_IZMERENIJA.getRowset()
								.size());
					} else if (lPk.equals(SILA_LIGHT)) {
						assertEquals(4, state.EDINICI_IZMERENIJA.getRowset()
								.size());
					}
				}
			}
			// let's edit filter-key field's value.
			rowset.beforeFirst();
			while (rowset.next()) {
				Object oPk = rowset.getObject(pkColIndex);
				assertNotNull(oPk);
				if (oPk instanceof Number) {
					Long lPk = ((Number) oPk).longValue();
					if (lPk.equals(SILA_LIGHT)) {
						Rowset edRowset = state.EDINICI_IZMERENIJA.getRowset();
						int velColIndex = edRowset.getFields()
								.find("MEASURAND");
						int updated = 0;
						while (edRowset.size() > 0) {
							edRowset.first();
							edRowset.updateObject(velColIndex, DLINA);
							updated++;
							assertTrue(updated <= 4);
						}
					}
				}
			}
			// ensure results
			rowset.beforeFirst();
			while (rowset.next()) {
				Object oPk = rowset.getObject(pkColIndex);
				assertNotNull(oPk);
				if (oPk instanceof Number) {
					Long lPk = ((Number) oPk).longValue();
					if (lPk.equals(DLINA)) {
						assertEquals(4, state.EDINICI_IZMERENIJA.getRowset()
								.size());
					} else if (lPk.equals(SILA_LIGHT)) {
						assertEquals(0, state.EDINICI_IZMERENIJA.getRowset()
								.size());
					}
				}
			}
		}
	}

}
