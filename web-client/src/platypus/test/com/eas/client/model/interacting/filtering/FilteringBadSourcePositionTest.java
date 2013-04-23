package com.eas.client.model.interacting.filtering;

import com.bearsoft.rowset.Rowset;

public class FilteringBadSourcePositionTest extends FilteringTest {
	
	@Override
	public void validate() throws Exception {
		System.out.println("filteringBadSourcePositionTest");
		ModelState state = new ModelState(model);

		Rowset rowset = state.IZMERJAEMIE_VELICHINI.getRowset();
		int pkColIndex = rowset.getFields().find("ID");
		rowset.beforeFirst();
		while (rowset.next()) {
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(DLINA) || lPk.equals(SILA_EL)
						|| lPk.equals(VOLUME)) {
					// ensure state
					assertNotNull(state.EDINICI_IZMERENIJA_1.getRowset());
					assertEquals(1, state.EDINICI_IZMERENIJA_1.getRowset()
							.size());
					// before first
					state.EDINICI_IZMERENIJA.getRowset().beforeFirst();
					assertEquals(0, state.EDINICI_IZMERENIJA_1.getRowset()
							.size());
					state.EDINICI_IZMERENIJA.getRowset().first();
					assertEquals(1, state.EDINICI_IZMERENIJA_1.getRowset()
							.size());
					// after last
					state.EDINICI_IZMERENIJA.getRowset().afterLast();
					assertEquals(0, state.EDINICI_IZMERENIJA_1.getRowset()
							.size());
					state.EDINICI_IZMERENIJA.getRowset().last();
					assertEquals(1, state.EDINICI_IZMERENIJA_1.getRowset()
							.size());
				} else {
					assertNotNull(state.EDINICI_IZMERENIJA_1.getRowset());
					assertEquals(0, state.EDINICI_IZMERENIJA_1.getRowset()
							.size());
				}
			} else {
				assertTrue(false);
			}
		}
		rowset.first();
	}
}
