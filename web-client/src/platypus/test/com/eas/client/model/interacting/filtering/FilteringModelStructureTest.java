package com.eas.client.model.interacting.filtering;

import com.bearsoft.rowset.Rowset;
import com.eas.client.model.EntityDataListener;

public class FilteringModelStructureTest extends FilteringTest {

	@Override
	public void validate() throws Exception {
		System.out.println("filteringModelStructureTest");
		ModelState state = new ModelState(model);

		EntityDataListener dataListener = new EntityDataListener();
		state.EDINICI_IZMERENIJA.getRowset().addRowsetListener(dataListener);

		EntityDataListener dataListener1 = new EntityDataListener();
		state.EDINICI_IZMERENIJA_1.getRowset().addRowsetListener(dataListener1);

		EntityDataListener dataListener2 = new EntityDataListener();
		state.NAIMENOVANIA_SI.getRowset().addRowsetListener(dataListener2);

		Rowset rowset = state.IZMERJAEMIE_VELICHINI.getRowset();

		int pkColIndex = rowset.getFields().find("ID");
		rowset.beforeFirst();
		while (rowset.next()) {
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(DLINA)) {
					Rowset edRowset = state.EDINICI_IZMERENIJA.getRowset();
					assertEquals(4, edRowset.size());
				}
			}
		}
		rowset.first();
		// /////////////////////////////////////////////

		state.EDINICI_IZMERENIJA.getRowset().removeRowsetListener(dataListener);

		while (rowset.next()) {
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(DLINA)) {
					Rowset edRowset = state.EDINICI_IZMERENIJA.getRowset();
					assertEquals(4, edRowset.size());
					edRowset.beforeFirst();
					while (edRowset.next()) {
						assertEquals(1, state.EDINICI_IZMERENIJA_1.getRowset()
								.size());
						assertEquals(0, state.NAIMENOVANIA_SI.getRowset()
								.size());
					}
					edRowset.first();
				}
			}
		}
		rowset.first();
	}
}
