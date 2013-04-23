package com.eas.client.model.interacting.filtering;

import com.bearsoft.rowset.Rowset;

public class FilteringUserFilterTest extends FilteringTest {

	@Override
	public void validate() throws Exception {
		System.out.println("Enable and disable user custom filtering");
		ModelState state = new ModelState(model);
		Rowset rowset = state.IZMERJAEMIE_VELICHINI.getRowset();
		int pkColIndex = rowset.getFields().find("ID");
		rowset.beforeFirst();
		while (rowset.next()) {
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(DLINA)) {
					assertEquals(0, state.NAIMENOVANIA_SI.getRowset().size());
				}
			}
		}

		// let's apply user filtering
		state.NAIMENOVANIA_SI.setUserFiltering(true);
		try {
			rowset.beforeFirst();
			while (rowset.next()) {
				assertEquals(18, state.NAIMENOVANIA_SI.getRowset().size());
			}
		} finally {
			// let's cancel user filtering
			state.NAIMENOVANIA_SI.setUserFiltering(false);
		}

		rowset.beforeFirst();
		while (rowset.next()) {
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(DLINA)) {
					assertEquals(0, state.NAIMENOVANIA_SI.getRowset().size());
				}
			}
		}
	}

}
