package com.eas.client.model.interacting.filtering;

import java.util.Map;

import com.bearsoft.rowset.Rowset;

public class FilteringScrollTest extends FilteringTest {

	@Override
	public void validate() throws Exception {
		System.out.println("filteringScrollTest, filteringPointsOfIntererstTest");
		ModelState state = new ModelState(model);
		Map<String, Integer> counts = state.gatherRowCounts();

		// let's move some rowset's la la la la...
		Rowset rowset = state.GRUPPA_OBJECTA_REMONTA.getRowset();
		rowset.beforeFirst();
		while (rowset.next()) {
			state.ensureRowCounts(counts, state.GRUPPA_OBJECTA_REMONTA.getEntityId());
		}
		rowset.first();

		rowset = state.VID_OBJECTA_REMONTA.getRowset();
		rowset.beforeFirst();
		while (rowset.next()) {
			state.ensureRowCounts(counts, state.VID_OBJECTA_REMONTA.getEntityId());
		}
		rowset.first();

		rowset = state.IZMERJAEMIE_VELICHINI.getRowset();
		int pkColIndex = rowset.getFields().find("ID");
		rowset.beforeFirst();
		while (rowset.next()) {
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(DLINA)) {
					assertEquals(4, state.EDINICI_IZMERENIJA.getRowset().size());
					assertEquals(1, state.EDINICI_IZMERENIJA_1.getRowset().size());
					assertEquals(0, state.NAIMENOVANIA_SI.getRowset().size());
					Rowset dlinaRowset = state.EDINICI_IZMERENIJA.getRowset();
					dlinaRowset.beforeFirst();
					while (dlinaRowset.next()) {
						assertEquals(1, state.EDINICI_IZMERENIJA_1.getRowset().size());
					}
					dlinaRowset.first();
				} else if (lPk.equals(SILA_EL)) {
					assertEquals(1, state.EDINICI_IZMERENIJA.getRowset().size());
					assertEquals(1, state.EDINICI_IZMERENIJA_1.getRowset().size());
					assertEquals(1, state.NAIMENOVANIA_SI.getRowset().size());
				} else if (lPk.equals(DAVL)) {
					assertEquals(0, state.EDINICI_IZMERENIJA.getRowset().size());
					assertEquals(0, state.EDINICI_IZMERENIJA_1.getRowset().size());
					assertEquals(1, state.NAIMENOVANIA_SI.getRowset().size());
				} else if (lPk.equals(MOSHN)) {
					assertEquals(0, state.EDINICI_IZMERENIJA.getRowset().size());
					assertEquals(0, state.EDINICI_IZMERENIJA_1.getRowset().size());
					assertEquals(1, state.NAIMENOVANIA_SI.getRowset().size());
				} else if (lPk.equals(NAPRJAZH)) {
					assertEquals(0, state.EDINICI_IZMERENIJA.getRowset().size());
					assertEquals(0, state.EDINICI_IZMERENIJA_1.getRowset().size());
					assertEquals(1, state.NAIMENOVANIA_SI.getRowset().size());
				} else if (lPk.equals(VOLUME)) {
					assertEquals(1, state.EDINICI_IZMERENIJA.getRowset().size());
					assertEquals(1, state.EDINICI_IZMERENIJA_1.getRowset().size());
					assertEquals(0, state.NAIMENOVANIA_SI.getRowset().size());
				} else {
					assertEquals(0, state.EDINICI_IZMERENIJA.getRowset().size());
					assertEquals(0, state.EDINICI_IZMERENIJA_1.getRowset().size());
					assertEquals(0, state.NAIMENOVANIA_SI.getRowset().size());
				}
			} else {
				assertTrue(false);
			}
		}
		rowset.first();

		rowset = state.MARKI_OBJECTOV_REMONTA.getRowset();
		pkColIndex = rowset.getFields().find("ID");
		rowset.beforeFirst();
		while (rowset.next()) {
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(128049594110963L)) {
					assertEquals(2, state.EDINICI_OBORUDOVANIJA.getRowset().size());
				} else if (lPk.equals(128049595046828L)) {
					assertEquals(2, state.EDINICI_OBORUDOVANIJA.getRowset().size());
				} else if (lPk.equals(128049595600024L)) {
					assertEquals(2, state.EDINICI_OBORUDOVANIJA.getRowset().size());
				} else if (lPk.equals(128049596076572L)) {
					assertEquals(3, state.EDINICI_OBORUDOVANIJA.getRowset().size());
				} else if (lPk.equals(128049596964037L)) {
					assertEquals(2, state.EDINICI_OBORUDOVANIJA.getRowset().size());
				} else if (lPk.equals(128049597468768L)) {
					assertEquals(2, state.EDINICI_OBORUDOVANIJA.getRowset().size());
				} else if (lPk.equals(128049597975084L)) {
					assertEquals(4, state.EDINICI_OBORUDOVANIJA.getRowset().size());
				} else if (lPk.equals(128049598748403L)) {
					assertEquals(2, state.EDINICI_OBORUDOVANIJA.getRowset().size());
				} else if (lPk.equals(128049599817169L)) {
					assertEquals(0, state.EDINICI_OBORUDOVANIJA.getRowset().size());
				} else if (lPk.equals(128049601170306L)) {
					assertEquals(1, state.EDINICI_OBORUDOVANIJA.getRowset().size());
				}
			} else {
				assertTrue(false);
			}
		}
		rowset.first();
	}
}
