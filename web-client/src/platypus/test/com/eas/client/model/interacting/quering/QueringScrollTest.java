package com.eas.client.model.interacting.quering;

import java.util.Map;

import com.bearsoft.rowset.Rowset;
import com.eas.client.Utils;
import com.eas.client.model.interacting.filtering.FilteringTest;
import com.eas.client.model.store.XmlDom2Model;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.xml.client.XMLParser;

public class QueringScrollTest extends QueringTest {

	public native JavaScriptObject publish(QueringScrollTest aTest) throws Exception/*-{
		var publishedModule = {
			imRequeriedCounter : 0,
			imRequeried : function() {
				publishedModule.imRequeriedCounter++;
				if (publishedModule.imRequeriedCounter == 1) {
					aTest.@com.eas.client.model.interacting.quering.QueringScrollTest::validateQueringScroll()();
				} else if (publishedModule.imRequeriedCounter == 2) {
					aTest.@com.eas.client.model.interacting.quering.QueringScrollTest::izmRsBeforeFirstScrolled()();
				} else if (publishedModule.imRequeriedCounter >= 3 && publishedModule.imRequeriedCounter <= 16) {
					aTest.@com.eas.client.model.interacting.quering.QueringScrollTest::izmRsNextScrolled()();
				} else if (publishedModule.imRequeriedCounter == 17) {
					aTest.@com.eas.client.model.interacting.quering.QueringScrollTest::izmRsFirstScrolled()();
				}
			},
			grObRemRequeriedCounter : 0,
			grObRemRequeried : function() {
				publishedModule.grObRemRequeriedCounter++;
				if (publishedModule.grObRemRequeriedCounter == 2)
					aTest.@com.eas.client.model.interacting.quering.QueringScrollTest::grObRemParamChanged()();
			},
			edIzm1RequeriedCounter : 0,
			edIzm1Requeried : function() {
				publishedModule.edIzm1RequeriedCounter++;
				if (publishedModule.edIzm1RequeriedCounter == 3) {
					aTest.@com.eas.client.model.interacting.quering.QueringScrollTest::dlinaRowsetBeforeFirstScrolled()();
				} else if (publishedModule.edIzm1RequeriedCounter >= 4 && publishedModule.edIzm1RequeriedCounter <= 8) {
					aTest.@com.eas.client.model.interacting.quering.QueringScrollTest::dlinaRowsetNextScrolled()();
				}
			},
			edOborPoMarkeRequeriedCounter : 0,
			edOborPoMarkeRequeried : function() {
				publishedModule.edOborPoMarkeRequeriedCounter++;
				if (publishedModule.edOborPoMarkeRequeriedCounter == 2) {
					aTest.@com.eas.client.model.interacting.quering.QueringScrollTest::marksRsBeforeFirstScrolled()();
				} else if (publishedModule.edOborPoMarkeRequeriedCounter >= 3 && publishedModule.edOborPoMarkeRequeriedCounter <= 13) {
					aTest.@com.eas.client.model.interacting.quering.QueringScrollTest::marksRsNextScrolled()();
				}
			}
		}
		return publishedModule;
	}-*/;

	protected ModelState state;
	protected Map<String, Integer> counts;
	protected int callCounter;

	@Override
	public void validate() throws Exception {
		assertEquals(35, callCounter);
	}
	
	public void testQueringScroll() throws Exception {
	}

	@Override
	protected void setupModel() throws Exception {
		try {
			JavaScriptObject module = publish(this);

			model = XmlDom2Model.transform(XMLParser.parse(DATAMODEL_QUERING_RELATIONS), module);
			model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_ID).setOnRequeried(Utils.lookupProperty(module, "imRequeried"));
			model.getEntityById(ENTITY_GRUPPA_OBJECTA_REMONTA_PO_RODITELU_ID).setOnRequeried(Utils.lookupProperty(module, "grObRemRequeried"));
			model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_1_ID).setOnRequeried(Utils.lookupProperty(module, "edIzm1Requeried"));
			model.getEntityById(ENTITY_EDINICI_OBORUDOVANIJA_PO_MARKE_ID).setOnRequeried(Utils.lookupProperty(module, "edOborPoMarkeRequeried"));
			model.publish(module);
			model.setRuntime(true);
		} catch (Exception ex) {
			fail(ex.getMessage());
			throw ex;
		}
	}

	public void validateQueringScroll() throws Exception {
		System.out.println("queringScrollTest, queringPointOfInterestTest");
		state = new ModelState(model);
		int parIndex = model.getParametersEntity().getRowset().getFields().find("P_ID");
		model.getParametersEntity().getRowset().updateObject(parIndex, PROIZVODSTVENNIE_OS);
		callCounter++;
	}

	public void grObRemParamChanged() throws Exception {
		Rowset groupByParentRs = state.GRUPPA_OBJECTA_REMONTA_PO_RODITELU.getRowset();
		assertNotNull(groupByParentRs);
		assertEquals(3, groupByParentRs.size());
		Map<String, Integer> counts = state.gatherRowCounts();
		groupByParentRs.beforeFirst();
		while (groupByParentRs.next()) {
			state.ensureRowCounts(counts, state.GRUPPA_OBJECTA_REMONTA_PO_RODITELU.getEntityId());
		}
		groupByParentRs.first();

		// Validating VID_OBJECTA_REMONTA entity.
		// Placed here because it have no any relations with other entities.
		Rowset repairKindRs = state.VID_OBJECTA_REMONTA.getRowset();
		assertNotNull(repairKindRs);
		assertEquals(3, repairKindRs.size());
		counts = state.gatherRowCounts();
		repairKindRs.beforeFirst();
		while (repairKindRs.next()) {
			state.ensureRowCounts(counts, state.VID_OBJECTA_REMONTA.getEntityId());
		}
		repairKindRs.first();

		Rowset izmRs = state.IZMERJAEMIE_VELICHINI.getRowset();
		assertNotNull(izmRs);
		izmRs.beforeFirst();
		callCounter++;
	}

	public void izmRsBeforeFirstScrolled() throws Exception {
		Rowset izmRs = state.IZMERJAEMIE_VELICHINI.getRowset();
		izmRs.next();
		callCounter++;
	}

	public void izmRsNextScrolled() throws Exception {
		Rowset izmRs = state.IZMERJAEMIE_VELICHINI.getRowset();
		if (!izmRs.isAfterLast()) {
			int pkColIndex = izmRs.getFields().find("ID");
			Object oPk = izmRs.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(FilteringTest.DLINA)) {
					assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
					assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
					assertEquals(0, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
					Rowset dlinaRowset = state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset();
					dlinaRowset.beforeFirst();
					return;
				} else if (lPk.equals(FilteringTest.SILA_EL)) {
					assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
					assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
					assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
				} else if (lPk.equals(FilteringTest.DAVL)) {
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
					assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
				} else if (lPk.equals(FilteringTest.MOSHN)) {
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
					assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
				} else if (lPk.equals(FilteringTest.NAPRJAZH)) {
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
					assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
				} else if (lPk.equals(FilteringTest.VOLUME)) {
					assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
					assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
					assertEquals(0, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
				} else {
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
					assertEquals(0, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
				}
			} else {
				fail("Primary keys fields for tests must be numbers.");
			}
			izmRs.next();
		} else
			izmRs.first();
		callCounter++;
	}

	public void dlinaRowsetBeforeFirstScrolled() throws Exception {
		Rowset dlinaRowset = state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset();
		dlinaRowset.next();
		callCounter++;
	}

	public void dlinaRowsetNextScrolled() throws Exception {
		Rowset dlinaRowset = state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset();
		if (!dlinaRowset.isAfterLast()) {
			assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
			dlinaRowset.next();
			// "Dlina" has several measurements and so, we have to force data
			// re-query.
			if (!dlinaRowset.isAfterLast()) {
				state.EDINICI_IZMERENIJA_PO_VELICHINE.refreshChildren();
			}
		} else {
			Rowset izmRs = state.IZMERJAEMIE_VELICHINI.getRowset();
			assertTrue(izmRs.next());
		}
		callCounter++;
	}

	public void izmRsFirstScrolled() throws Exception {
		Rowset marksRs = state.MARKI_OBJECTOV_REMONTA.getRowset();
		marksRs.beforeFirst();
		callCounter++;
	}

	public void marksRsBeforeFirstScrolled() throws Exception {
		Rowset marksRs = state.MARKI_OBJECTOV_REMONTA.getRowset();
		marksRs.next();
		callCounter++;
	}

	public void marksRsNextScrolled() throws Exception {
		Rowset marksRs = state.MARKI_OBJECTOV_REMONTA.getRowset();
		int pkColIndex = marksRs.getFields().find("ID");
		if (!marksRs.isAfterLast()) {
			Object oPk = marksRs.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(128049594110963L)) {
					assertEquals(2, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
				} else if (lPk.equals(128049595046828L)) {
					assertEquals(2, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
				} else if (lPk.equals(128049595600024L)) {
					assertEquals(2, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
				} else if (lPk.equals(128049596076572L)) {
					assertEquals(3, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
				} else if (lPk.equals(128049596964037L)) {
					assertEquals(2, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
				} else if (lPk.equals(128049597468768L)) {
					assertEquals(2, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
				} else if (lPk.equals(128049597975084L)) {
					assertEquals(4, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
				} else if (lPk.equals(128049598748403L)) {
					assertEquals(2, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
				} else if (lPk.equals(128049599817169L)) {
					assertEquals(0, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
				} else if (lPk.equals(128049601170306L)) {
					assertEquals(1, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
				}
			} else {
				assertTrue(false);
			}
			marksRs.next();
		} else
			marksRs.first();
		callCounter++;
	}
}
