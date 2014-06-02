/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.bearsoft.rowset.Utils.JsObject;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.sorting.RowsComparator;
import com.bearsoft.rowset.sorting.SortingCriterion;
import com.eas.client.RunnableAdapter;
import com.eas.client.application.AppClient;
import com.eas.client.application.Application;
import com.eas.client.application.Loader;
import com.eas.client.form.js.JsContainers;
import com.eas.client.form.js.JsModelWidgets;
import com.eas.client.form.js.JsWidgets;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
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
			return this.rowsetsCounter;
		}-*/;

		public native void setRowsetsCounter(int aValue) throws Exception/*-{
			this.rowsetsCounter = aValue;
		}-*/;
	}

	protected native static PublishedModule publish(RelationsTest aTest) throws Exception/*-{
		var publishedModule = {
			rowsetsCounter : 0,
			entity11_Requeried : function() {
				publishedModule.rowsetsCounter++;
			},
			entity21_Requeried : function() {
				publishedModule.rowsetsCounter++;
			},
			entity22_Requeried : function() {
				publishedModule.rowsetsCounter++;
			},
			entity31_Requeried : function() {
				publishedModule.rowsetsCounter++;
			},
			entity31_Filtered : function() {
				publishedModule.rowsetsCounter++;
			},
			entity32_Requeried : function() {
				publishedModule.rowsetsCounter++;
			},
			entity33_Requeried : function() {
				publishedModule.rowsetsCounter++;
			},
			entity33_Filtered : function() {
				publishedModule.rowsetsCounter++;
				publishedModule.requeried();
			},
			entity41_Requeried : function() {
				publishedModule.rowsetsCounter++;
			},
			entity41_Filtered : function() {
				publishedModule.rowsetsCounter++;
			}
		}
		return publishedModule;
	}-*/;

	@Override
	public void validate() throws Exception {
		assertEquals(8, callCounter);
	}

	@Override
	public Collection<String> queries() {
		return Arrays.asList(new String[] { "133818273961796", "124349292311931632" });
	}

	protected PublishedModule module;
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
		JsWidgets.init();
		JsContainers.init();
		JsModelWidgets.init();
		AppClient client = initDevelopTestClient();
		Application.publish(client);
		AppClient.publishApi(client);
		Loader l = new Loader(client);
		l.loadQueries(queries(), new RunnableAdapter() {

			@Override
			protected void doWork() throws Exception {
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
			module = publish(this);
			AppClient client = initDevelopTestClient();
			model = new Model(client);
			entity11 = new Entity(model);
			entity11.setQueryId("133818273961796");
			entity11.setOnRequeried(module.<JsObject> cast().getJs("entity11_Requeried"));
			entity21 = new Entity(model);
			entity21.setQueryId("124349292311931632");
			entity21.setOnRequeried(module.<JsObject> cast().getJs("entity21_Requeried"));
			entity22 = new Entity(model);
			entity22.setQueryId("124349292311931632");
			entity22.setOnRequeried(module.<JsObject> cast().getJs("entity22_Requeried"));
			entity31 = new Entity(model);
			entity31.setQueryId("124349292311931632");
			entity31.setOnRequeried(module.<JsObject> cast().getJs("entity31_Requeried"));
			entity31.setOnFiltered(module.<JsObject> cast().getJs("entity31_Filtered"));
			entity32 = new Entity(model);
			entity32.setQueryId("124349292311931632");
			entity32.setOnRequeried(module.<JsObject> cast().getJs("entity32_Requeried"));
			entity33 = new Entity(model);
			entity33.setQueryId("124349292311931632");
			entity33.setOnRequeried(module.<JsObject> cast().getJs("entity33_Requeried"));
			entity33.setOnFiltered(module.<JsObject> cast().getJs("entity33_Filtered"));
			entity41 = new Entity(model);
			entity41.setQueryId("124349292311931632");
			entity41.setOnRequeried(module.<JsObject> cast().getJs("entity41_Requeried"));
			entity41.setOnFiltered(module.<JsObject> cast().getJs("entity41_Filtered"));

			assertEquals(model.getEntities().size(), 1);// Parameters entity is
			                                            // always present
			model.addEntity(entity11);
			model.addEntity(entity21);
			model.addEntity(entity22);
			model.addEntity(entity31);
			model.addEntity(entity32);
			model.addEntity(entity33);
			model.addEntity(entity41);

			assertEquals(model.getEntities().size(), 8);// 7 user entities and
			                                            // single
			                                            // parameters entity
			model.validateQueries();

			/*
			 * Relation rel11_21 = new Relation(entity11, true, "AMOUNT",
			 * entity21, false, "amount"); Relation rel11_22 = new
			 * Relation(entity11, true, "AMOUNT", entity22, false, "amount");
			 * 
			 * Relation rel21_31 = new Relation(entity21, false, "amount",
			 * entity31, false, "amount"); Relation rel21_33 = new
			 * Relation(entity21, true, "amount", entity33, false, "amount");
			 * Relation rel21_31_ = new Relation(entity21, true, "ORDER_ID",
			 * entity31, true, "ORDER_ID");
			 * 
			 * Relation rel22_32 = new Relation(entity22, false, "amount",
			 * entity32, false, "amount"); Relation rel22_33_ = new
			 * Relation(entity22, true, "ORDER_ID", entity33, true, "ORDER_ID");
			 * 
			 * Relation rel11_41 = new Relation(entity11, true, "AMOUNT",
			 * entity41, false, "amount"); Relation rel32_41 = new
			 * Relation(entity32, true, "AMOUNT", entity41, true, "amount");
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

			Model.publishTopLevelFacade(module, model);
			model.requery(null);
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

				@Override
				public boolean execute() {
					checkPks();
					try {
						touchData();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}

			}, 5);
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
		List<SortingCriterion> criteria = new ArrayList<>();
		criteria.add(new SortingCriterion(entity11.getRowset().getFields().find(keyCols.get(0).getName()), true));
		entity11.getRowset().sort(new RowsComparator(criteria));

		entity11.getRowset().beforeFirst();
		callCounter++;
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

			@Override
			public boolean execute() {
				try {
					scrolledBeforeFirst();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		}, 5);
	}

	public void scrolledBeforeFirst() throws Exception {
		assertTrue(entity11.getRowset().next());
		callCounter++;
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

			@Override
			public boolean execute() {
				try {
					scrolledNext();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		}, 5);
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

		// int c = module.getRowsetsCounter();

		assertTrue(entity31.getRowset().size() > 0);
		assertTrue(entity32.getRowset().size() > 0);
		assertTrue(entity33.getRowset().size() > 0);

		// 4 layer
		assertNotNull(entity41.getRowset());
		assertTrue(entity41.getRowset().size() > 0);
		entity11.getRowset().next();
		callCounter++;
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

			@Override
			public boolean execute() {
				try {
					if (entity11.getRowset().isAfterLast())
						scrolledAfterLast();
					else
						scrolledNext();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		}, 5);
	}

	public void scrolledAfterLast() throws Exception {
		entity11.getRowset().first();
		callCounter++;
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

			@Override
			public boolean execute() {
				try {
					scrolledFirst();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		}, 5);
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
