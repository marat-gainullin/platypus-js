package com.eas.client.model.interacting.filtering;

import com.bearsoft.rowset.Rowset;

public class FilteringEmptyKeysSourceTest extends FilteringTest {

	@Override
	public void validate() throws Exception {
		System.out.println("filteringEmptyKeysSourceTest");
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
