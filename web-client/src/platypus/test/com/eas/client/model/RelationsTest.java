/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.sorting.RowsComparator;
import com.bearsoft.rowset.sorting.SortingCriterion;
import com.eas.client.application.AppClient;
import com.eas.client.application.Application;
import com.eas.client.application.Loader;
import com.eas.client.form.api.JSContainers;
import com.eas.client.form.api.JSControls;
import com.eas.client.form.api.ModelJSControls;
import com.eas.client.CancellableCallback;
import com.eas.client.Utils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;

/**
 * 
 * @author mg
 */
public class RelationsTest extends ModelBaseTest {

	public static final class PublishedModule extends JavaScriptObject {
		protected PublishedModule() {
		}

		public native int getRowsetsCounter() throws Exception/*-{
			return rowsetsCounter;
		}-*/;
	}

	protected native static PublishedModule publish(RelationsTest aTest) throws Exception/*-{
		var publishedModule = {
			rowsetsCounter : 0,
			entity11_Requeried : function() {
				publishedModule.rowsetsCounter++;
				publishedModule.requeried();
			},
			entity21_Requeried : function() {
				publishedModule.rowsetsCounter++;
				publishedModule.requeried();
			},
			entity22_Requeried : function() {
				publishedModule.rowsetsCounter++;
				publishedModule.requeried();
			},
			entity31_Requeried : function() {
				publishedModule.rowsetsCounter++;
				publishedModule.requeried();
			},
			entity32_Requeried : function() {
				publishedModule.rowsetsCounter++;
				publishedModule.requeried();
			},
			entity33_Requeried : function() {
				publishedModule.rowsetsCounter++;
				publishedModule.requeried();
			},
			entity41_Requeried : function() {
				publishedModule.rowsetsCounter++;
				publishedModule.requeried();
			},

			requeried : function() {
				if (publishedModule.rowsetsCounter == 7) {
					aTest.@com.eas.client.model.RelationsTest::checkPks()();
					aTest.@com.eas.client.model.RelationsTest::touchData()();
				} else if (publishedModule.rowsetsCounter == 7 + 6) {
					aTest.@com.eas.client.model.RelationsTest::scrolledBeforeFirst()();
				} else if (publishedModule.rowsetsCounter == 7 + 6 + 6) {
					aTest.@com.eas.client.model.RelationsTest::scrolledNext()();
				} else if (publishedModule.rowsetsCounter == 7 + 6 + 6 + 6) {
					aTest.@com.eas.client.model.RelationsTest::scrolledNext()();
				} else if (publishedModule.rowsetsCounter == 7 + 6 + 6 + 6 + 6) {
					aTest.@com.eas.client.model.RelationsTest::scrolledNext()();
				} else if (publishedModule.rowsetsCounter == 7 + 6 + 6 + 6 + 6 + 6) {
					aTest.@com.eas.client.model.RelationsTest::scrolledAfterLast()();
				} else if (publishedModule.rowsetsCounter == 7 + 6 + 6 + 6 + 6 + 6 + 6) {
					aTest.@com.eas.client.model.RelationsTest::scrolledFirst()();
				}
			}
		}
		return publishedModule;
	}-*/;

	@Override
	public void validate() throws Exception {
		assertEquals(8 , callCounter);
	}

	@Override
	public Collection<String> queries() {
		return Arrays.asList(new String[] { "133818273961796", "124349292311931632", });
	}

	protected Model model;
	protected Entity entity11;
	protected Entity entity21;
	protected Entity entity22;
	protected Entity entity31;
	protected Entity entity32;
	protected Entity entity33;
	protected Entity entity41;
	protected int callCounter;

	public void testAttainability() throws Exception {
	}

	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();
		delayTestFinish(60 * 60 * 1000);
		JSControls.initControls();
		JSContainers.initContainers();
		ModelJSControls.initModelControls();
		AppClient client = initDevelopTestClient();
		Application.publish(client);
		AppClient.publishApi(client);
		Loader l = new Loader(client);
		l.loadQueries(queries(), new CancellableCallback() {

			@Override
			public void cancel() {
			}

			@Override
			public void run() throws Exception {
				setupModel();
			}

		});
		Timer timer = new Timer() {
			public void run() {
				// check test's internal counters
				try {
					validate();
					finishTest();
				} catch (Exception ex) {
					fail(ex.getMessage());
				}
			}
		};
		// Schedule the event and return control to the test system.
		timer.schedule(20 * 1000);
	}

	protected void setupModel() {
		// Set a delay period significantly longer than the
		// test is expected to take.
		try {
			PublishedModule module = publish(this);
			AppClient client = initDevelopTestClient();
			model = new Model(client);
			entity11 = new Entity(model);
			entity11.setQueryId("133818273961796");
			entity11.setOnRequeried(Utils.lookupProperty(module, "entity11_Requeried"));
			entity21 = new Entity(model);
			entity21.setQueryId("124349292311931632");
			entity21.setOnRequeried(Utils.lookupProperty(module, "entity21_Requeried"));
			entity22 = new Entity(model);
			entity22.setQueryId("124349292311931632");
			entity22.setOnRequeried(Utils.lookupProperty(module, "entity22_Requeried"));
			entity31 = new Entity(model);
			entity31.setQueryId("124349292311931632");
			entity31.setOnRequeried(Utils.lookupProperty(module, "entity31_Requeried"));
			entity32 = new Entity(model);
			entity32.setQueryId("124349292311931632");
			entity32.setOnRequeried(Utils.lookupProperty(module, "entity32_Requeried"));
			entity33 = new Entity(model);
			entity33.setQueryId("124349292311931632");
			entity33.setOnRequeried(Utils.lookupProperty(module, "entity33_Requeried"));
			entity41 = new Entity(model);
			entity41.setQueryId("124349292311931632");
			entity41.setOnRequeried(Utils.lookupProperty(module, "entity41_Requeried"));

			assertEquals(model.getEntities().size(), 1);// Parameters entity is
			                                            // always present

			/*
			Relation rel11_21 = new Relation(entity11, true, "AMOUNT", entity21, false, "amount");
			Relation rel11_22 = new Relation(entity11, true, "AMOUNT", entity22, false, "amount");

			Relation rel21_31 = new Relation(entity21, false, "amount", entity31, false, "amount");
			Relation rel21_33 = new Relation(entity21, true, "amount", entity33, false, "amount");
			Relation rel21_31_ = new Relation(entity21, true, "ORDER_ID", entity31, true, "ORDER_ID");

			Relation rel22_32 = new Relation(entity22, false, "amount", entity32, false, "amount");
			Relation rel22_33_ = new Relation(entity22, true, "ORDER_ID", entity33, true, "ORDER_ID");

			Relation rel11_41 = new Relation(entity11, true, "AMOUNT", entity41, false, "amount");
			Relation rel32_41 = new Relation(entity32, true, "AMOUNT", entity41, true, "amount");
			*/
	        Relation rel11_21 = new Relation(entity11, entity11.getFields().get("AMOUNT"), entity21, entity21.getQuery().getParameters().get("amount"));
	        Relation rel11_22 = new Relation(entity11, entity11.getFields().get("AMOUNT"), entity22, entity22.getQuery().getParameters().get("amount"));

	        Relation rel21_31 = new Relation(entity21, entity21.getQuery().getParameters().get("amount"), entity31, entity31.getQuery().getParameters().get("amount"));
	        Relation rel21_33 = new Relation(entity21, entity21.getFields().get("amount"), entity33, entity33.getQuery().getParameters().get("amount"));
	        Relation rel21_31_ = new Relation(entity21, entity21.getFields().get("ORDER_ID"), entity31, entity31.getFields().get("ORDER_ID"));

	        Relation rel22_32 = new Relation(entity22, entity22.getQuery().getParameters().get("amount"), entity32, entity32.getQuery().getParameters().get("amount"));
	        Relation rel22_33_ = new Relation(entity22, entity22.getFields().get("ORDER_ID"), entity33, entity33.getFields().get("ORDER_ID"));

	        Relation rel11_41 = new Relation(entity11, entity11.getFields().get("AMOUNT"), entity41, entity41.getQuery().getParameters().get("amount"));
	        Relation rel32_41 = new Relation(entity32, entity32.getFields().get("AMOUNT"), entity41, entity41.getFields().get("amount"));

			model.addEntity(entity11);
			model.addEntity(entity21);
			model.addEntity(entity22);
			model.addEntity(entity31);
			model.addEntity(entity32);
			model.addEntity(entity33);
			model.addEntity(entity41);

			assertEquals(model.getEntities().size(), 8);// 7 user entities and
			                                            // parameters entity

			model.addRelation(rel11_21);
			model.addRelation(rel11_22);
			model.addRelation(rel21_31);
			model.addRelation(rel21_33);
			model.addRelation(rel21_31_);
			model.addRelation(rel22_32);
			model.addRelation(rel22_33_);
			model.addRelation(rel11_41);
			model.addRelation(rel32_41);

			assertEquals(model.getRelations().size(), 9);

			model.publish(module);
			model.setRuntime(true);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	public void checkPks() {
		System.out.println("attainabilityTest. Field to parameter, parameter to parameter and field to parameter relations test");
		// pks control section
		assertEquals(1, entity11.getFields().getPrimaryKeys().size());
		assertEquals(1, entity21.getFields().getPrimaryKeys().size());
		assertEquals(1, entity22.getFields().getPrimaryKeys().size());
		assertEquals(1, entity31.getFields().getPrimaryKeys().size());
		assertEquals(1, entity32.getFields().getPrimaryKeys().size());
		assertEquals(1, entity33.getFields().getPrimaryKeys().size());
		assertEquals(1, entity41.getFields().getPrimaryKeys().size());

		assertFalse(entity11.getRowset().isEmpty());
		assertFalse(entity21.getRowset().isEmpty());
		assertFalse(entity22.getRowset().isEmpty());
		assertFalse(entity31.getRowset().isEmpty());
		assertFalse(entity32.getRowset().isEmpty());
		assertFalse(entity33.getRowset().isEmpty());
		assertFalse(entity41.getRowset().isEmpty());
		//
		callCounter++;
	}

	public void touchData() throws Exception {
		// 1 layer
		assertNotNull(entity11.getRowset());
		assertTrue(entity11.getRowset().size() > 0);
		List<Field> keyCols = entity11.getRowset().getFields().getPrimaryKeys();
		assertNotNull(keyCols);
		assertEquals(keyCols.size(), 1);
		List<SortingCriterion> criteria = new ArrayList();
		criteria.add(new SortingCriterion(entity11.getRowset().getFields().find(keyCols.get(0).getName()), true));
		entity11.getRowset().sort(new RowsComparator(criteria));

		entity11.getRowset().beforeFirst();
		callCounter++;
	}

	public void scrolledBeforeFirst() throws Exception {
		assertTrue(entity11.getRowset().next());
		callCounter++;
	}

	public void scrolledNext() throws Exception {
		// 2 layer
		assertNotNull(entity21.getRowset());
		assertNotNull(entity22.getRowset());

		assertTrue(entity21.getRowset().size() > 0);
		assertTrue(entity22.getRowset().size() > 0);

		// 3 layer
		assertNotNull(entity31.getRowset());
		assertNotNull(entity32.getRowset());
		assertNotNull(entity33.getRowset());

		assertTrue(entity31.getRowset().size() > 0);
		assertTrue(entity32.getRowset().size() > 0);
		assertTrue(entity33.getRowset().size() > 0);

		// 4 layer
		assertNotNull(entity41.getRowset());
		assertTrue(entity41.getRowset().size() > 0);
		entity11.getRowset().next();
		callCounter++;
	}

	public void scrolledAfterLast() throws Exception {
		entity11.getRowset().first();
		callCounter++;
	}

	public void scrolledFirst() throws Exception {
		assertTrue(entity21.getRowset().size() > entity31.getRowset().size());

		assertEquals(entity22.getRowset().size(), entity32.getRowset().size());

		assertTrue(entity22.getRowset().size() > entity31.getRowset().size());
		assertTrue(entity32.getRowset().size() > entity31.getRowset().size());

		assertEquals(entity11.getRowset().getObject(entity11.getRowset().getFields().find("amount")), entity41.getQuery().getParameters().get("amount").getValue());

		entity32.getRowset().beforeFirst();
		while (entity32.getRowset().next()) {
			Double bdAmount = entity32.getRowset().getDouble(entity32.getRowset().getFields().find("amount"));
			double amount = bdAmount.doubleValue();
			entity41.getRowset().beforeFirst();
			while (entity41.getRowset().next()) {
				Double _bdAmount = entity41.getRowset().getDouble(entity41.getRowset().getFields().find("amount"));
				double _amount = _bdAmount.doubleValue();
				assertEquals((Double) amount, (Double) _amount);
			}
		}
		callCounter++;
	}
}
