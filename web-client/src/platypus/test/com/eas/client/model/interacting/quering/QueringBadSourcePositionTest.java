package com.eas.client.model.interacting.quering;

import com.bearsoft.rowset.Rowset;
import com.eas.client.Utils;
import com.eas.client.model.interacting.filtering.FilteringTest;
import com.eas.client.model.store.XmlDom2Model;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.xml.client.XMLParser;

public class QueringBadSourcePositionTest extends QueringTest {

	public final static class PublishedModule extends JavaScriptObject {
		protected PublishedModule() {
			super();
		}
	}

	public native PublishedModule publish(QueringBadSourcePositionTest aTest) throws Exception/*-{
		var publishedModule = {
			imRequeriedCounter : 0,
			imRequeried : function() {
				publishedModule.imRequeriedCounter++;
//				if (publishedModule.imRequeriedCounter == 1)
//					aTest.@com.eas.client.model.interacting.quering.QueringBadSourcePositionTest::validateQueringBadSourcePosition()();
//				else if (publishedModule.imRequeriedCounter == 2)
//					aTest.@com.eas.client.model.interacting.quering.QueringBadSourcePositionTest::beforeFirstScrolled()();
//				else if (publishedModule.imRequeriedCounter >= 3)
//					aTest.@com.eas.client.model.interacting.quering.QueringBadSourcePositionTest::nextScrolled()();
			},
			im1RequeriedCounter : 0, // -10
			im1Requeried : function() {
				publishedModule.im1RequeriedCounter++;
//				if (publishedModule.im1RequeriedCounter == 2)
//					aTest.@com.eas.client.model.interacting.quering.QueringBadSourcePositionTest::im1BeforeFirstScrolled()();
//				else if (publishedModule.im1RequeriedCounter == 3)
//					aTest.@com.eas.client.model.interacting.quering.QueringBadSourcePositionTest::im1FirstScrolled()();
//				else if (publishedModule.im1RequeriedCounter == 4)
//					aTest.@com.eas.client.model.interacting.quering.QueringBadSourcePositionTest::im1AfterLastScrolled()();
//				else if (publishedModule.im1RequeriedCounter == 5)
//					aTest.@com.eas.client.model.interacting.quering.QueringBadSourcePositionTest::im1LastScrolled()();
			}
		};
		return publishedModule;
	}-*/;

	protected ModelState state;
	protected Rowset rowset;
	protected int pkColIndex;
	protected PublishedModule module;
	protected int callCounter;

	@Override
	protected void setupModel() throws Exception {
		try {
			model = XmlDom2Model.transform(XMLParser.parse(DATAMODEL_QUERING_RELATIONS), module = publish(this));
			model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_ID).setOnRequeried(Utils.lookupProperty(module, "imRequeried"));
			model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_1_ID).setOnRequeried(Utils.lookupProperty(module, "im1Requeried"));
			model.publish(module);
			model.setRuntime(true);
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

				@Override
                public boolean execute() {
					try {
	                    validateQueringBadSourcePosition();
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

	public void testQueringBadSourcePosition() throws Exception {
	}

	@Override
	public void validate() throws Exception {
		assertEquals(28, callCounter);
	}

	public void validateQueringBadSourcePosition() throws Exception {
		System.out.println("queringBadSourcePositionTest");
		state = new ModelState(model);
		rowset = state.IZMERJAEMIE_VELICHINI.getRowset();
		pkColIndex = rowset.getFields().find("ID");
		rowset.beforeFirst();
		callCounter++;
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

			@Override
            public boolean execute() {
				try {
					beforeFirstScrolled();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
			
		}, 5);
	}

	public void beforeFirstScrolled() throws Exception {
		rowset.next();
		callCounter++;
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

			@Override
            public boolean execute() {
				try {
					nextScrolled();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
			
		}, 5);
	}

	public void nextScrolled() throws Exception {
		if (!rowset.isAfterLast()) {
			//module.clearIm1RequeriedCounter();
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(FilteringTest.DLINA) || lPk.equals(FilteringTest.SILA_EL) || lPk.equals(FilteringTest.VOLUME)) {
					// ensure state
					assertNotNull(state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset());
					assertTrue(state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size() > 0);
					// before first
					state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().beforeFirst();
					Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

						@Override
			            public boolean execute() {
							try {
								im1BeforeFirstScrolled();
			                } catch (Exception e) {
			                    e.printStackTrace();
			                }
			                return false;
			            }
						
					}, 5);
				} else {
					assertNotNull(state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset());
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
					rowset.next();
					Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

						@Override
			            public boolean execute() {
							try {
								nextScrolled();
			                } catch (Exception e) {
			                    e.printStackTrace();
			                }
			                return false;
			            }
						
					}, 5);
				}
			} else {
				assertTrue(false);
			}

		}
		callCounter++;
	}

	public void im1BeforeFirstScrolled() throws Exception {
		assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
		state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().first();
		callCounter++;
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

			@Override
            public boolean execute() {
				try {
					im1FirstScrolled();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
			
		}, 5);
	}

	public void im1FirstScrolled() throws Exception {
		assertTrue(state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size() > 0);
		// after last
		state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().afterLast();
		callCounter++;
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

			@Override
            public boolean execute() {
				try {
					im1AfterLastScrolled();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
			
		}, 5);
	}

	public void im1AfterLastScrolled() throws Exception {
		assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
		state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().last();
		callCounter++;
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

			@Override
            public boolean execute() {
				try {
					im1LastScrolled();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
			
		}, 5);
	}

	public void im1LastScrolled() throws Exception {
		assertTrue(state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size() > 0);
		rowset.next();
		callCounter++;
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

			@Override
            public boolean execute() {
				try {
					nextScrolled();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
			
		}, 5);
	}
}
