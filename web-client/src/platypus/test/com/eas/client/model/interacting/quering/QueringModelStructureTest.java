package com.eas.client.model.interacting.quering;

import com.bearsoft.rowset.Rowset;
import com.eas.client.Utils;
import com.eas.client.model.EntityDataListener;
import com.eas.client.model.interacting.filtering.FilteringTest;
import com.eas.client.model.store.XmlDom2Model;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.xml.client.XMLParser;

public class QueringModelStructureTest extends QueringTest {

	protected ModelState state;
	protected EntityDataListener dataListener;
	protected EntityDataListener dataListener1;
	protected EntityDataListener dataListener2;
	protected Rowset izmVelRowset;
	protected int izmVelPkColIndex;
	protected int callCounter;
	protected PublishedModule module;

	protected static final class PublishedModule extends JavaScriptObject {

		protected PublishedModule() {
			super();
		}

		public native void setEdIzmPoVel1RequeriedCounter(int aValue) throws Exception/*-{
			this.edIzmPoVel1RequeriedCounter = aValue;
		}-*/;

	}

	public native PublishedModule publish(QueringModelStructureTest aTest) throws Exception/*-{
		var publishedModule = {
			izmVelRequeriedCounter : 0,
			izmVelRequeried : function() {
				publishedModule.izmVelRequeriedCounter++;
				if (publishedModule.izmVelRequeriedCounter == 1) {
					aTest.@com.eas.client.model.interacting.quering.QueringModelStructureTest::validateQueringModelStructure()();
				}
			},
			edIzmPoVelRequeriedCounter : 0,
			edIzmPoVelRequeried : function() {
				publishedModule.edIzmPoVelRequeriedCounter++;
				if (publishedModule.edIzmPoVelRequeriedCounter == 2) {
					aTest.@com.eas.client.model.interacting.quering.QueringModelStructureTest::izmVelBeforeFirstScrolled()();
				} else if (publishedModule.edIzmPoVelRequeriedCounter >= 3 && publishedModule.edIzmPoVelRequeriedCounter <= 16) {
					aTest.@com.eas.client.model.interacting.quering.QueringModelStructureTest::izmVelNextScrolled()();
				} else if (publishedModule.edIzmPoVelRequeriedCounter == 17) {
					aTest.@com.eas.client.model.interacting.quering.QueringModelStructureTest::izmVelFirstScrolled()();
				} else if (publishedModule.edIzmPoVelRequeriedCounter == 18) {
					aTest.@com.eas.client.model.interacting.quering.QueringModelStructureTest::izmVelBeforeFirstScrolled1()();
				} else if (publishedModule.edIzmPoVelRequeriedCounter >= 19 && publishedModule.edIzmPoVelRequeriedCounter <= 32) {
					aTest.@com.eas.client.model.interacting.quering.QueringModelStructureTest::izmVelNextScrolled1()();
				}
			},
			edIzmPoVel1RequeriedCounter : -10000,
			edIzmPoVel1Requeried : function() {
				publishedModule.edIzmPoVel1RequeriedCounter++;
				if (publishedModule.edIzmPoVel1RequeriedCounter == 2) {
					aTest.@com.eas.client.model.interacting.quering.QueringModelStructureTest::edIzmPoVelBeforeFirstScrolled()();
				} else if (publishedModule.edIzmPoVel1RequeriedCounter >= 3) {
					aTest.@com.eas.client.model.interacting.quering.QueringModelStructureTest::edIzmPoVelNextScrolled()();
				}
			}
		};
		return publishedModule;
	}-*/;

	@Override
	public void validate() throws Exception {
		assertEquals(37, callCounter);
	}

	public void testQueringModelStructure() throws Exception {
	}

	@Override
	protected void setupModel() throws Exception {
		try {
			module = publish(this);
			
			model = XmlDom2Model.transform(XMLParser.parse(DATAMODEL_QUERING_RELATIONS), module);
			model.getEntityById(ENTITY_IZMERJAEMIE_VELICHINI_ID).setOnRequeried(Utils.lookupProperty(module, "izmVelRequeried"));
			model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_ID).setOnRequeried(Utils.lookupProperty(module, "edIzmPoVelRequeried"));
			model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_1_ID).setOnRequeried(Utils.lookupProperty(module, "edIzmPoVel1Requeried"));
			model.publish(module);
			model.setRuntime(true);
		} catch (Exception ex) {
			fail(ex.getMessage());
			throw ex;
		}
	}

	public void validateQueringModelStructure() throws Exception {
		System.out.println("queringModelStructure");
		state = new ModelState(model);

		dataListener = new EntityDataListener();
		state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().addRowsetListener(dataListener);

		dataListener1 = new EntityDataListener();
		state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().addRowsetListener(dataListener1);

		dataListener2 = new EntityDataListener();
		state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().addRowsetListener(dataListener2);

		izmVelRowset = state.IZMERJAEMIE_VELICHINI.getRowset();

		izmVelPkColIndex = izmVelRowset.getFields().find("ID");
		dataListener.reset();
		dataListener1.reset();
		dataListener2.reset();
		izmVelRowset.beforeFirst();
		callCounter++;
	}

	public void izmVelBeforeFirstScrolled() throws Exception {
		dataListener.reset();
		dataListener1.reset();
		dataListener2.reset();
		izmVelRowset.next();
		callCounter++;
	}

	public void izmVelNextScrolled() throws Exception {
		if (!izmVelRowset.isAfterLast()) {
			Object oPk = izmVelRowset.getObject(izmVelPkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(FilteringTest.DLINA)) {
					Rowset edRowset = state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset();
					assertEquals(4, edRowset.size());
					Rowset edRowset1 = state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset();
					assertEquals(4, edRowset1.size());
				}
			}
			dataListener.reset();
			dataListener1.reset();
			dataListener2.reset();
			izmVelRowset.next();
		} else {
			dataListener.reset();
			dataListener1.reset();
			dataListener2.reset();
			izmVelRowset.first();
		}
		callCounter++;
	}

	public void izmVelFirstScrolled() throws Exception {
		state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().removeRowsetListener(dataListener);

		dataListener1.reset();
		dataListener2.reset();
		izmVelRowset.beforeFirst();
		callCounter++;
	}

	public void izmVelBeforeFirstScrolled1() throws Exception {
		dataListener1.reset();
		dataListener2.reset();
		izmVelRowset.next();
		callCounter++;
	}

	public void izmVelNextScrolled1() throws Exception {
		if (!izmVelRowset.isAfterLast()) {
			Object oPk = izmVelRowset.getObject(izmVelPkColIndex);
			assertNotNull(oPk);
			if (oPk instanceof Number) {
				Long lPk = ((Number) oPk).longValue();
				if (lPk.equals(FilteringTest.DLINA)) {
					Rowset edRowset = state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset();
					assertEquals(4, edRowset.size());
					dataListener1.reset();
					dataListener2.reset();
					edRowset.beforeFirst();
					module.setEdIzmPoVel1RequeriedCounter(0);
					return;
				}
			}
			dataListener1.reset();
			dataListener2.reset();
			izmVelRowset.next();
		} else {
			dataListener1.reset();
			dataListener2.reset();
			izmVelRowset.first();
		}
		callCounter++;
	}

	public void edIzmPoVelBeforeFirstScrolled() throws Exception {
		Rowset edRowset = state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset();
		dataListener1.reset();
		dataListener2.reset();
		edRowset.next();
		callCounter++;
	}

	public void edIzmPoVelNextScrolled() throws Exception {
		Rowset edRowset = state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset();
		if (!edRowset.isAfterLast()) {
			// ensure context
			Object oPk = izmVelRowset.getObject(izmVelPkColIndex);
			assertNotNull(oPk);
			assertTrue("Primary keys should be numbers", oPk instanceof Number);
			Long lPk = ((Number) oPk).longValue();
			assertEquals(FilteringTest.DLINA, lPk);
			// do test's work
			assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
			assertEquals(0, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());

			dataListener1.reset();
			dataListener2.reset();
			edRowset.next();
			if (!edRowset.isAfterLast()) {
				// Dlina has several measurements and so, we have to force data
				// re-query.
				state.EDINICI_IZMERENIJA_PO_VELICHINE.refreshChildren();
			}
		} else {
			module.setEdIzmPoVel1RequeriedCounter(-10000);
			dataListener1.reset();
			dataListener2.reset();
			edRowset.first();
			dataListener1.reset();
			dataListener2.reset();
			izmVelRowset.next();
		}
		callCounter++;
	}
}
