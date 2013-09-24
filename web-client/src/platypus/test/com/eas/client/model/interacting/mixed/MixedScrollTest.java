package com.eas.client.model.interacting.mixed;

import java.util.Map;

import com.bearsoft.rowset.Rowset;
import com.eas.client.Utils.JsObject;
import com.eas.client.model.interacting.filtering.FilteringTest;
import com.eas.client.model.store.XmlDom2Model;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.xml.client.XMLParser;

public class MixedScrollTest extends MixedTest {

	public native JavaScriptObject publish(MixedScrollTest aTest) throws Exception/*-{
		var publishedModule = {
			edIzmRequeriedCounter : 0,
			edIzmRequeried : function() {
				publishedModule.edIzmRequeriedCounter++;
//				if (publishedModule.edIzmRequeriedCounter == 1) {
//					aTest.@com.eas.client.model.interacting.mixed.MixedScrollTest::validateMixedScroll()();
//				} else if (publishedModule.edIzmRequeriedCounter == 2) {
//					aTest.@com.eas.client.model.interacting.mixed.MixedScrollTest::izmVelBeforeFirstScrolled()();
//				} else if (publishedModule.edIzmRequeriedCounter >= 3) {
//					aTest.@com.eas.client.model.interacting.mixed.MixedScrollTest::izmVelNextScrolled()();
//				}
			},
			naimSiPoVel1Requeried : function() {
				aTest.@com.eas.client.model.interacting.mixed.MixedScrollTest::naimSiPoVel1Requeried()();
			},
			edOborRequeriedCounter : 0,
			edOborRequeried : function() {
				publishedModule.edOborRequeriedCounter++;
//				if (publishedModule.edOborRequeriedCounter == 2) {
//					aTest.@com.eas.client.model.interacting.mixed.MixedScrollTest::markiObjRemBeforeFirstScrolled()();
//				} else if (publishedModule.edOborRequeriedCounter >= 3) {
//					aTest.@com.eas.client.model.interacting.mixed.MixedScrollTest::markiObjRemNextScrolled()();
//				}
			}
		}
		return publishedModule;
	}-*/;

	protected ModelState state;
	protected Map<String, Integer> grObRemCounts;
	protected int callCounter;

	@Override
	protected int getTimeout() {
	    return super.getTimeout();// * 60 * 60;
	}
	
	@Override
	public void validate() throws Exception {
		assertEquals(30, callCounter);
		assertEquals(5, siCounter);
	}

	public void testMixedScroll() throws Exception {
	}

	protected void setupModel() throws Exception {
		JavaScriptObject module = publish(this);

		model = XmlDom2Model.transform(XMLParser.parse(DATAMODEL_MIXED_RELATIONS), module);
		model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_ID).setOnRequeried(module.<JsObject>cast().getJs("edIzmRequeried"));
		model.getEntityById(ENTITY_NAIMENOVANIA_SI_PO_VELICHINE_1_ID).setOnRequeried(module.<JsObject>cast().getJs("naimSiPoVel1Requeried"));
		model.getEntityById(ENTITY_EDINICI_OBORUDOVANIJA_ID).setOnRequeried(module.<JsObject>cast().getJs("edOborRequeried"));
		model.publish(module);
		model.setRuntime(true);
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

			@Override
            public boolean execute() {
				try {
					validateMixedScroll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
			
		}, 500);
	}

	public void validateMixedScroll() throws Exception {
		System.out.println("mixedScrollTest,\nmixedPointOfInterestTest");
		state = new ModelState(model);
		grObRemCounts = state.gatherRowCounts();

		// let's move some unrelated rowset's la la la la...
		// It should be no any related events on other entities
		Rowset rowset = state.GRUPPA_OBJECTA_REMONTA.getRowset();
		rowset.beforeFirst();
		while (rowset.next()) {
			state.ensureRowCounts(grObRemCounts, state.GRUPPA_OBJECTA_REMONTA.getEntityId());
		}
		rowset = state.VID_OBJECTA_REMONTA.getRowset();
		rowset.beforeFirst();
		while (rowset.next()) {
			state.ensureRowCounts(grObRemCounts, state.VID_OBJECTA_REMONTA.getEntityId());
		}
		// Let's start some tests for related entities
		Rowset izmVel = state.IZMERJAEMIE_VELICHINI.getRowset();
		Rowset naimSi = state.NAIMENOVANIE_SI.getRowset();
		Rowset markiObRem = state.MARKI_OBJECTOV_REMONTA.getRowset();
		assertNotNull(izmVel);
		assertNotNull(naimSi);
		assertNotNull(markiObRem);

		izmVel.beforeFirst();
		markiObRem.beforeFirst();
		callCounter++;
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

			@Override
            public boolean execute() {
				try {
					izmVelBeforeFirstScrolled();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
			
		}, 5);
		markiObjRemBeforeFirstScrolled();
	}

	public void izmVelBeforeFirstScrolled() throws Exception {
		Rowset izmVel = state.IZMERJAEMIE_VELICHINI.getRowset();
		izmVel.next();
		callCounter++;
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

			@Override
            public boolean execute() {
				try {
					izmVelNextScrolled();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
			
		}, 5);
	}

	public void izmVelNextScrolled() throws Exception {
		Rowset izmVel = state.IZMERJAEMIE_VELICHINI.getRowset();
		if (!izmVel.isAfterLast()) {
			Rowset naimSi = state.NAIMENOVANIE_SI.getRowset();
			int velPkColIndex = izmVel.getFields().find("ID");
			int siPkColIndex = naimSi.getFields().find("ID");
			Object oVelPk = izmVel.getObject(velPkColIndex);
			assertNotNull(oVelPk);
			if (oVelPk instanceof Number) {
				Long velPk = ((Number) oVelPk).longValue();
				if (velPk.equals(FilteringTest.DLINA)) {
					assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
					assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
				} else if (velPk.equals(FilteringTest.SILA_EL)) {
					assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
					assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
					naimSi.beforeFirst();
					while (naimSi.next()) {
						Object oSiPk = naimSi.getObject(siPkColIndex);
						assertNotNull(oSiPk);
						assertTrue(oSiPk instanceof Number);
						Long siPk = ((Number) oSiPk).longValue();
						if (siPk.equals(AMPERMETR)) {
							assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
							return;
						}
					}
				} else if (velPk.equals(FilteringTest.DAVL)) {
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
					naimSi.beforeFirst();
					while (naimSi.next()) {
						Object oSiPk = naimSi.getObject(siPkColIndex);
						assertNotNull(oSiPk);
						assertTrue(oSiPk instanceof Number);
						Long siPk = ((Number) oSiPk).longValue();
						if (siPk.equals(MANOMETR)) {
							assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
							return;
						}
					}
				} else if (velPk.equals(FilteringTest.MOSHN)) {
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
					naimSi.beforeFirst();
					while (naimSi.next()) {
						Object oSiPk = naimSi.getObject(siPkColIndex);
						assertNotNull(oSiPk);
						assertTrue(oSiPk instanceof Number);
						Long siPk = ((Number) oSiPk).longValue();
						if (siPk.equals(WATTMETR)) {
							assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
							return;
						}
					}
				} else if (velPk.equals(FilteringTest.NAPRJAZH)) {
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
					naimSi.beforeFirst();
					while (naimSi.next()) {
						Object oSiPk = naimSi.getObject(siPkColIndex);
						assertNotNull(oSiPk);
						assertTrue(oSiPk instanceof Number);
						Long siPk = ((Number) oSiPk).longValue();
						if (siPk.equals(VOLTMETR)) {
							assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
							return;
						}
					}
				} else if (velPk.equals(FilteringTest.VOLUME)) {
					assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
					assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
					assertEquals(0, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
					siCounter++;
					return;
				}
			} else {
				fail("Primary keys must be numbers.");
			}
			izmVel.next();
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

				@Override
	            public boolean execute() {
					try {
						izmVelNextScrolled();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                return false;
	            }
				
			}, 5);
		}
		callCounter++;
	}

	protected int siCounter;

	public void naimSiPoVel1Requeried() throws Exception {
		Rowset naimSiPoVel1 = state.NAIMENOVANIA_SI_PO_VELICHINE_1.getRowset();
		Rowset izmVel = state.IZMERJAEMIE_VELICHINI.getRowset();
		if (!izmVel.isAfterLast()) {
			Rowset naimSi = state.NAIMENOVANIE_SI.getRowset();
			int velPkColIndex = izmVel.getFields().find("ID");
			int siPkColIndex = naimSi.getFields().find("ID");
			int velFkColIndex = naimSiPoVel1.getFields().find("VALUE");
			Object oVelPk = izmVel.getObject(velPkColIndex);
			Object oSiPk = naimSi.getObject(siPkColIndex);
			if (oVelPk instanceof Number && oSiPk instanceof Number) {
				Long velPk = ((Number) oVelPk).longValue();
				Long siPk = ((Number) oSiPk).longValue();
				if (velPk.equals(FilteringTest.DLINA)) {
					// all checks are in izmVelNextScrolled
				} else if (velPk.equals(FilteringTest.SILA_EL)) {
					if (siPk.equals(AMPERMETR) && !naimSiPoVel1.isEmpty() && FilteringTest.SILA_EL.equals(naimSiPoVel1.getLong(velFkColIndex))) {
						if (1 == naimSiPoVel1.size()) {
							siCounter++;
							izmVel.next();
							Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

								@Override
					            public boolean execute() {
									try {
										izmVelNextScrolled();
					                } catch (Exception e) {
					                    e.printStackTrace();
					                }
					                return false;
					            }
								
							}, 5);
						}
					}
				} else if (velPk.equals(FilteringTest.DAVL)) {
					if (siPk.equals(MANOMETR) && !naimSiPoVel1.isEmpty() && FilteringTest.DAVL.equals(naimSiPoVel1.getLong(velFkColIndex))) {
						if (1 == naimSiPoVel1.size()) {
							siCounter++;
							izmVel.next();
							Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

								@Override
					            public boolean execute() {
									try {
										izmVelNextScrolled();
					                } catch (Exception e) {
					                    e.printStackTrace();
					                }
					                return false;
					            }
								
							}, 5);
						}
					}
				} else if (velPk.equals(FilteringTest.MOSHN)) {
					if (siPk.equals(WATTMETR) && !naimSiPoVel1.isEmpty() && FilteringTest.MOSHN.equals(naimSiPoVel1.getLong(velFkColIndex))) {
						if (1 == naimSiPoVel1.size()) {
							siCounter++;
							izmVel.next();
							Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

								@Override
					            public boolean execute() {
									try {
										izmVelNextScrolled();
					                } catch (Exception e) {
					                    e.printStackTrace();
					                }
					                return false;
					            }
								
							}, 5);
						}
					}
				} else if (velPk.equals(FilteringTest.NAPRJAZH)) {
					if (siPk.equals(VOLTMETR) && !naimSiPoVel1.isEmpty() && FilteringTest.NAPRJAZH.equals(naimSiPoVel1.getLong(velFkColIndex))) {
						if (1 == naimSiPoVel1.size()) {
							siCounter++;
							izmVel.next();
							Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

								@Override
					            public boolean execute() {
									try {
										izmVelNextScrolled();
					                } catch (Exception e) {
					                    e.printStackTrace();
					                }
					                return false;
					            }
								
							}, 5);
						}
					}
				}
			} else
				fail("Primary keys must be numbers.");
		}
		callCounter++;
	}

	public void markiObjRemBeforeFirstScrolled() throws Exception {
		Rowset rowset = state.MARKI_OBJECTOV_REMONTA.getRowset();
		rowset.next();
		callCounter++;
		markiObjRemNextScrolled();
	}

	public void markiObjRemNextScrolled() throws Exception {
		Rowset rowset = state.MARKI_OBJECTOV_REMONTA.getRowset();
		if (!rowset.isAfterLast()) {
			int pkColIndex = rowset.getFields().find("ID");
			int oborSize = state.EDINICI_OBORUDOVANIJA.getRowset().size();
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				// Obor by marks...
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(128049594110963L)) {
					assertEquals(2, oborSize);
				} else if (lPk.equals(128049595046828L)) {
					assertEquals(2, oborSize);
				} else if (lPk.equals(128049595600024L)) {
					assertEquals(2, oborSize);
				} else if (lPk.equals(128049596076572L)) {
					assertEquals(3, oborSize);
				} else if (lPk.equals(128049596964037L)) {
					assertEquals(2, oborSize);
				} else if (lPk.equals(128049597468768L)) {
					assertEquals(2, oborSize);
				} else if (lPk.equals(128049597975084L)) {
					assertEquals(4, oborSize);
				} else if (lPk.equals(128049598748403L)) {
					assertEquals(2, oborSize);
				} else if (lPk.equals(128049599817169L)) {
					assertEquals(0, oborSize);
				} else if (lPk.equals(128049601170306L)) {
					assertEquals(1, oborSize);
				}
			} else {
				fail("Primary keys must be a numbers.");
			}
			rowset.next();
			markiObjRemNextScrolled();
		} else {
			rowset.first();
		}
		callCounter++;
	}

}
