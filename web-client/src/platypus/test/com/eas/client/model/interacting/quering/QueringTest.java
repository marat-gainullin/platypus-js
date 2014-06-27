/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.interacting.quering;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.eas.client.RunnableAdapter;
import com.eas.client.application.AppClient;
import com.eas.client.application.Application;
import com.eas.client.application.Loader;
import com.eas.client.form.js.JsContainers;
import com.eas.client.form.js.JsEvents;
import com.eas.client.form.js.JsMenus;
import com.eas.client.form.js.JsModelWidgets;
import com.eas.client.form.js.JsWidgets;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ModelBaseTest;
import com.eas.client.model.js.JsModel;
import com.google.gwt.user.client.Timer;

/**
 * 
 * @author mg
 */
public abstract class QueringTest extends ModelBaseTest {

	public static final String DATAMODEL_QUERING_RELATIONS = ""
	        + "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
	        + "<datamodel>"
	        + "    <parameters>"
	        + "        <parameter description=\"Родитель\" name=\"P_ID\" nullable=\"true\" precision=\"10\" scale=\"0\" selectionForm=\"null\" signed=\"true\" size=\"22\" type=\"3\" typeName=\"NUMBER\">"
	        + "            <primaryKey field=\"ID\" name=\"PK_ASSET_GROUPS\" schema=\"EAS\" table=\"ASSET_GROUPS\"/>"
	        + "        </parameter>"
	        + "    </parameters>"
	        + "    <parametersEntity Title=\"Параметры\">"
	        + "    </parametersEntity>"
	        + "    <entity Name=\"dsMarki\" Title=\"Марки объектов ремонта\" entityId=\"128049574970367\" queryId=\"128049551290614\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsEdOborPoMarke\" Title=\"Единицы оборудования по марке\" entityId=\"128049775173425\" queryId=\"128049731084369\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsEdIzmPoVel\" Title=\"Единицы измерения  по величине\" entityId=\"128049746332840\" queryId=\"128049734162523\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsSrIzmPoVel\" Title=\"Наименования СИ по величине\" entityId=\"128049750556261\" queryId=\"128049732520388\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsIzmVel\" Title=\"Измеряемые величины\" entityId=\"128049576695369\" queryId=\"128049562734356\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsEdIzm1\" Title=\"Единицы измерения  по величине 1\" entityId=\"128073170857902\" queryId=\"128049734162523\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsGroup\" Title=\"Группа объекта ремонта по родителю\" entityId=\"128049787114001\" queryId=\"128049779804659\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsVidObj\" Title=\"Вид объекта ремонта\" entityId=\"128049576096827\" queryId=\"128049558485943\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>" + "    <relation leftEntityFieldName=\"P_ID\" leftEntityId=\"-1\" rightEntityId=\"128049787114001\" rightEntityParameterName=\"P_ID\"/>"
	        + "    <relation leftEntityFieldName=\"MEASURAND\" leftEntityId=\"128049746332840\" rightEntityId=\"128073170857902\" rightEntityParameterName=\"VALUE\"/>"
	        + "    <relation leftEntityFieldName=\"ID\" leftEntityId=\"128049576695369\" rightEntityId=\"128049750556261\" rightEntityParameterName=\"VALUE\"/>"
	        + "    <relation leftEntityFieldName=\"ID\" leftEntityId=\"128049574970367\" rightEntityId=\"128049775173425\" rightEntityParameterName=\"MARK\"/>"
	        + "    <relation leftEntityFieldName=\"ID\" leftEntityId=\"128049576695369\" rightEntityId=\"128049746332840\" rightEntityParameterName=\"VALUE\"/>" + "</datamodel>";

	// 1st layer
	protected static String ENTITY_GRUPPA_OBJECTA_REMONTA_PO_RODITELU_ID = "128049787114001";
	protected static String ENTITY_VID_OBJECTA_REMONTA_ID = "128049576096827";
	// 2nd layer
	protected static String ENTITY_IZMERJAEMIE_VELICHINI_ID = "128049576695369";
	protected static String ENTITY_MARKI_OBJECTOV_REMONTA_ID = "128049574970367";
	// 3rd layer
	protected static String ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_ID = "128049746332840";
	protected static String ENTITY_NAIMENOVANIA_SI_PO_VELICHINE_ID = "128049750556261";
	protected static String ENTITY_EDINICI_OBORUDOVANIJA_PO_MARKE_ID = "128049775173425";
	// 4th layer
	protected static String ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_1_ID = "128073170857902";

	protected static class ModelState {

		private Model model = null;
		public Entity GRUPPA_OBJECTA_REMONTA_PO_RODITELU = null;
		public Entity VID_OBJECTA_REMONTA = null;
		public Entity IZMERJAEMIE_VELICHINI = null;
		public Entity MARKI_OBJECTOV_REMONTA = null;
		public Entity EDINICI_IZMERENIJA_PO_VELICHINE = null;
		public Entity NAIMENOVANIA_SI_PO_VELICHINE = null;
		public Entity EDINICI_OBORUDOVANIJA_PO_MARKE = null;
		public Entity EDINICI_IZMERENIJA_PO_VELICHINE_1 = null;

		public ModelState(Model aModel) throws Exception {
			model = aModel;
			// 1st layer
			GRUPPA_OBJECTA_REMONTA_PO_RODITELU = model.getEntityById(ENTITY_GRUPPA_OBJECTA_REMONTA_PO_RODITELU_ID);
			assertNotNull(GRUPPA_OBJECTA_REMONTA_PO_RODITELU.getRowset());
			VID_OBJECTA_REMONTA = model.getEntityById(ENTITY_VID_OBJECTA_REMONTA_ID);
			assertNotNull(VID_OBJECTA_REMONTA.getRowset());
			// 2nd layer
			IZMERJAEMIE_VELICHINI = model.getEntityById(ENTITY_IZMERJAEMIE_VELICHINI_ID);
			assertNotNull(IZMERJAEMIE_VELICHINI.getRowset());
			MARKI_OBJECTOV_REMONTA = model.getEntityById(ENTITY_MARKI_OBJECTOV_REMONTA_ID);
			assertNotNull(MARKI_OBJECTOV_REMONTA.getRowset());
			// 3rd layer
			EDINICI_IZMERENIJA_PO_VELICHINE = model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_ID);
			assertNotNull(EDINICI_IZMERENIJA_PO_VELICHINE.getRowset());
			NAIMENOVANIA_SI_PO_VELICHINE = model.getEntityById(ENTITY_NAIMENOVANIA_SI_PO_VELICHINE_ID);
			assertNotNull(NAIMENOVANIA_SI_PO_VELICHINE.getRowset());
			EDINICI_OBORUDOVANIJA_PO_MARKE = model.getEntityById(ENTITY_EDINICI_OBORUDOVANIJA_PO_MARKE_ID);
			assertNotNull(EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset());
			// 4th layer
			EDINICI_IZMERENIJA_PO_VELICHINE_1 = model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_1_ID);
			assertNotNull(EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset());
		}

		public Map<String, Integer> gatherRowCounts() throws Exception {
			Map<String, Integer> counts = new HashMap<>();
			for (Entity entity : model.getEntities().values()) {
				counts.put(entity.getEntityId(), entity.getRowset().size());
			}
			return counts;
		}

		public void ensureRowCounts(Map<String, Integer> counts, String toSkip) throws Exception {
			for (String entityId : counts.keySet()) {
				if (!entityId.equals(toSkip)) {
					assertEquals((Integer) counts.get(entityId), (Integer) model.getEntityById(entityId).getRowset().size());
				}
			}
		}
	}

	protected static final Long PROIZVODSTVENNIE_OS = 1L;

	protected Model model = null;

	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();
		delayTestFinish(60 * 60 * 1000);
		AppClient client = initDevelopTestClient();
		JsModel.init();
		JsWidgets.init();
		JsMenus.init();
		JsContainers.init();
		JsModelWidgets.init();
		JsEvents.init();
		Application.publish(client);
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

	@Override
	public Collection<String> queries() {
		return Arrays.asList(new String[] { "128049551290614", "128049731084369", "128049734162523", "128049732520388", "128049562734356", "128049734162523", "128049779804659", "128049558485943" });
	}

	protected abstract void setupModel() throws Exception;

}
