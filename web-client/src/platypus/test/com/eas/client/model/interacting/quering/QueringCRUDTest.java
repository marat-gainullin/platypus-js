package com.eas.client.model.interacting.quering;

import com.bearsoft.rowset.Rowset;
import com.eas.client.Utils;
import com.eas.client.model.interacting.filtering.FilteringTest;
import com.eas.client.model.store.XmlDom2Model;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.xml.client.XMLParser;

/**
 * Edit rows from dlina to sila sveta and vice versa for several times.
 * 
 * @author mg
 * 
 */
public class QueringCRUDTest extends QueringTest {

	protected ModelState state;
	protected Rowset izmVelRowset;
	protected int izmVelPKColIndex;
	protected int callCounter;

	public native JavaScriptObject publish(QueringCRUDTest aTest) throws Exception/*-{
		var publishedModule = {
			izmVelRequeriedCounter : 0,
			izmVelRequeried : function() {
				publishedModule.izmVelRequeriedCounter++;
//				if (publishedModule.izmVelRequeriedCounter == 1)
//					aTest.@com.eas.client.model.interacting.quering.QueringCRUDTest::validateQueringCRUD()();
			},
			edIzmPoVelRequeriedCounter : 0,
			edIzmPoVelRequeried : function() {
				publishedModule.edIzmPoVelRequeriedCounter++;
//				if (publishedModule.edIzmPoVelRequeriedCounter == 2)
//					aTest.@com.eas.client.model.interacting.quering.QueringCRUDTest::izmVelBeforeFirstScrolled()();
//				else if (publishedModule.edIzmPoVelRequeriedCounter >= 3 && publishedModule.edIzmPoVelRequeriedCounter <= 16)
//					aTest.@com.eas.client.model.interacting.quering.QueringCRUDTest::izmVelNextScrolled()();
//				else if (publishedModule.edIzmPoVelRequeriedCounter >= 17 && publishedModule.edIzmPoVelRequeriedCounter <= 30)
//					aTest.@com.eas.client.model.interacting.quering.QueringCRUDTest::izmVelNextScrolled1()();
//				else if (publishedModule.edIzmPoVelRequeriedCounter >= 31 && publishedModule.edIzmPoVelRequeriedCounter <= 44)
//					aTest.@com.eas.client.model.interacting.quering.QueringCRUDTest::izmVelNextScrolled2()();
//				else if (publishedModule.edIzmPoVelRequeriedCounter >= 45 && publishedModule.edIzmPoVelRequeriedCounter <= 58)
//					aTest.@com.eas.client.model.interacting.quering.QueringCRUDTest::izmVelNextScrolled3()();
//				else if (publishedModule.edIzmPoVelRequeriedCounter >= 59 && publishedModule.edIzmPoVelRequeriedCounter <= 72)
//					aTest.@com.eas.client.model.interacting.quering.QueringCRUDTest::izmVelNextScrolled4()();
			}
		};
		return publishedModule;
	}-*/;

	@Override
	public void validate() throws Exception {
		assertEquals(72, callCounter);
	}

	@Override
	protected void setupModel() throws Exception {
		try {
			JavaScriptObject module = publish(this);
			model = XmlDom2Model.transform(XMLParser.parse(DATAMODEL_QUERING_RELATIONS), module);
			model.getEntityById(ENTITY_IZMERJAEMIE_VELICHINI_ID).setOnRequeried(Utils.lookupProperty(module, "izmVelRequeried"));
			model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_ID).setOnRequeried(Utils.lookupProperty(module, "edIzmPoVelRequeried"));
			model.publish(module);
			model.setRuntime(true);
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

				@Override
                public boolean execute() {
					try {
						validateQueringCRUD();
                    } catch (Exception e) {
	                    e.printStackTrace();
                    }
	                return false;
                }
				
			}, 500);
		} catch (Exception ex) {
			fail(ex.getMessage());
			throw ex;
		}
	}

	public void testQueringCRUD() throws Exception {
	}

	public void validateQueringCRUD() throws Exception {
		System.out.println("queringCrudTest");
		state = new ModelState(model);
		izmVelRowset = state.IZMERJAEMIE_VELICHINI.getRowset();
		izmVelPKColIndex = izmVelRowset.getFields().find("ID");
		// ensure pre-state
		izmVelRowset.beforeFirst();
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
	}

	public void izmVelBeforeFirstScrolled() throws Exception {
		izmVelRowset.next();
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
		if (!izmVelRowset.isAfterLast()) {
			Object oPk = izmVelRowset.getObject(izmVelPKColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(FilteringTest.DLINA)) {
					assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
				} else if (lPk.equals(FilteringTest.SILA_LIGHT)) {
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
				}
			}
			izmVelRowset.next();
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
		} else {
			izmVelRowset.beforeFirst();
			izmVelRowset.next();
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

				@Override
	            public boolean execute() {
					try {
						izmVelNextScrolled1();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                return false;
	            }
				
			}, 5);
		}
		callCounter++;
	}

	public void izmVelNextScrolled1() throws Exception {
		if (!izmVelRowset.isAfterLast()) {
			// let's edit query-key field's value.
			Object oPk = izmVelRowset.getObject(izmVelPKColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(FilteringTest.DLINA)) {
					Rowset edRowset = state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset();
					int velColIndex = edRowset.getFields().find("MEASURAND");
					int updated = 0;
					edRowset.beforeFirst();
					while (edRowset.next()) {
						edRowset.updateObject(velColIndex, FilteringTest.SILA_LIGHT);
						updated++;
						assertTrue(updated <= 4);
					}
					edRowset.first();
				}
			}
			izmVelRowset.next();
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

				@Override
	            public boolean execute() {
					try {
						izmVelNextScrolled1();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                return false;
	            }
				
			}, 5);
		} else {
			izmVelRowset.beforeFirst();
			izmVelRowset.next();
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

				@Override
	            public boolean execute() {
					try {
						izmVelNextScrolled2();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                return false;
	            }
				
			}, 5);
		}
		callCounter++;
	}

	public void izmVelNextScrolled2() throws Exception {
		if (!izmVelRowset.isAfterLast()) {
			// ensure results
			Object oPk = izmVelRowset.getObject(izmVelPKColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(FilteringTest.DLINA)) {
					assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
				} else if (lPk.equals(FilteringTest.SILA_LIGHT)) {
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
				}
			}
			izmVelRowset.next();
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

				@Override
	            public boolean execute() {
					try {
						izmVelNextScrolled2();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                return false;
	            }
				
			}, 5);
		} else {
			izmVelRowset.beforeFirst();
			izmVelRowset.next();
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

				@Override
	            public boolean execute() {
					try {
						izmVelNextScrolled3();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                return false;
	            }
				
			}, 5);
		}
		callCounter++;
	}

	public void izmVelNextScrolled3() throws Exception {
		if (!izmVelRowset.isAfterLast()) {
			// let's edit filter-key field's value.
			Object oPk = izmVelRowset.getObject(izmVelPKColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(FilteringTest.SILA_LIGHT)) {
					Rowset edRowset = state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset();
					int velColIndex = edRowset.getFields().find("MEASURAND");
					int updated = 0;
					while (edRowset.size() > 0) {
						edRowset.first();
						edRowset.updateObject(velColIndex, FilteringTest.DLINA);
						updated++;
						assertTrue(updated <= 4);
					}
				}
			}
			izmVelRowset.next();
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

				@Override
	            public boolean execute() {
					try {
						izmVelNextScrolled3();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                return false;
	            }
				
			}, 5);
		} else {
			izmVelRowset.beforeFirst();
			izmVelRowset.next();
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

				@Override
	            public boolean execute() {
					try {
						izmVelNextScrolled4();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                return false;
	            }
				
			}, 5);
		}
		callCounter++;
	}

	public void izmVelNextScrolled4() throws Exception {
		if (!izmVelRowset.isAfterLast()) {
			// ensure results
			Object oPk = izmVelRowset.getObject(izmVelPKColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(FilteringTest.DLINA)) {
					assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
				} else if (lPk.equals(FilteringTest.SILA_LIGHT)) {
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
				}
			}
			izmVelRowset.next();
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

				@Override
	            public boolean execute() {
					try {
						izmVelNextScrolled4();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                return false;
	            }
				
			}, 5);
		}
		callCounter++;
	}

}
