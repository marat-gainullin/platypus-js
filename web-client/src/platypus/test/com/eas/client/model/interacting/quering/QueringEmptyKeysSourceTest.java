package com.eas.client.model.interacting.quering;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.Utils.JsObject;
import com.eas.client.model.interacting.filtering.FilteringTest;
import com.eas.client.model.store.XmlDom2Model;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.xml.client.XMLParser;

public class QueringEmptyKeysSourceTest extends QueringTest {

	public native JavaScriptObject publish(QueringEmptyKeysSourceTest aTest) throws Exception/*-{
		var publishedModule = {
			imRequeriedCounter : 0,
			imRequeried : function() {
				publishedModule.imRequeriedCounter++;
//				if (publishedModule.imRequeriedCounter == 1)
//					aTest.@com.eas.client.model.interacting.quering.QueringEmptyKeysSourceTest::validateQueringEmptyKeysSource()();
//				else if (publishedModule.imRequeriedCounter == 2)
//					aTest.@com.eas.client.model.interacting.quering.QueringEmptyKeysSourceTest::beforeFirstScrolled()();
//				else if (publishedModule.imRequeriedCounter >= 3)
//					aTest.@com.eas.client.model.interacting.quering.QueringEmptyKeysSourceTest::nextScrolled()();
			}
		}
		return publishedModule;
	}-*/;

	protected ModelState state;
	protected Rowset rowset;
	protected int pkColIndex;
	protected int callCounter;

	public void testQueringEmptyKeysSource() throws Exception {
	}

	@Override
	public void validate() throws Exception {
		assertEquals(16, callCounter);
	}

	@Override
	protected void setupModel() throws Exception {
		try {
			JavaScriptObject module = publish(this);
			model = XmlDom2Model.transform(XMLParser.parse(DATAMODEL_QUERING_RELATIONS), module);
			model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_ID).setOnRequeried(module.<JsObject>cast().getJs("imRequeried"));
			model.publish(module);
			model.setRuntime(true);
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand(){

				@Override
                public boolean execute() {
					try {
						validateQueringEmptyKeysSource();						
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

	public void validateQueringEmptyKeysSource() throws Exception {
		System.out.println("queringEmptyKeysSourceTest");
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
			Object oPk = rowset.getObject(pkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(FilteringTest.DLINA) || lPk.equals(FilteringTest.SILA_EL) || lPk.equals(FilteringTest.VOLUME)) {
					assertTrue(state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size() > 0);
				} else {
					assertNotNull(state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset());
					assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
				}
			} else {
				fail("PrimaryKeys must be numbers");
			}
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
		callCounter++;
	}
}
