/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.interacting.mixed;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
public abstract class MixedTest extends ModelBaseTest {

	public static final String DATAMODEL_MIXED_RELATIONS = ""
	        + "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
	        + "<datamodel>"
	        + "    <parametersEntity Title=\"Параметры\">"
	        + "    </parametersEntity>"
	        + "    <entity Name=\"dsEdIzmPoVel1\" Title=\"Единицы измерения  по величине 1\" entityId=\"128073231281282\" queryId=\"128049734162523\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsNaimSi\" Title=\"Наименования СИ\" entityId=\"128049764412588\" queryId=\"128049565893763\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsMarki\" Title=\"Марки объектов ремонта\" entityId=\"128049574970367\" queryId=\"128049551290614\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsNaimSiPoVel1\" Title=\"Наименования СИ по величине 1\" entityId=\"128073233404649\" queryId=\"128049732520388\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsEdIzmPoVel\" Title=\"Единицы измерения  по величине\" entityId=\"128049746332840\" queryId=\"128049734162523\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsGroup\" Title=\"Группа объекта ремонта\" entityId=\"128049573928131\" queryId=\"128049508301511\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsNaimSiPoVel\" Title=\"Наименования СИ по величине\" entityId=\"128049750556261\" queryId=\"128049732520388\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsIzmVel\" Title=\"Измеряемые величины\" entityId=\"128049576695369\" queryId=\"128049562734356\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsEdObor\" Title=\"Единицы оборудования\" entityId=\"128049574495320\" queryId=\"128049547464084\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsVidObjRem\" Title=\"Вид объекта ремонта\" entityId=\"128049576096827\" queryId=\"128049558485943\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>" + "    <relation leftEntityFieldName=\"VALUE\" leftEntityId=\"128049764412588\" rightEntityFieldName=\"VALUE\" rightEntityId=\"128049750556261\"/>"
	        + "    <relation leftEntityFieldName=\"ID\" leftEntityId=\"128049574970367\" rightEntityFieldName=\"MARK\" rightEntityId=\"128049574495320\"/>"
	        + "    <relation leftEntityFieldName=\"VALUE\" leftEntityId=\"128049750556261\" rightEntityId=\"128073233404649\" rightEntityParameterName=\"VALUE\"/>"
	        + "    <relation leftEntityFieldName=\"MEASURAND\" leftEntityId=\"128049746332840\" rightEntityId=\"128073231281282\" rightEntityParameterName=\"VALUE\"/>"
	        + "    <relation leftEntityFieldName=\"ID\" leftEntityId=\"128049576695369\" rightEntityId=\"128049746332840\" rightEntityParameterName=\"VALUE\"/>"
	        + "    <relation leftEntityFieldName=\"ID\" leftEntityId=\"128049576695369\" rightEntityId=\"128049750556261\" rightEntityParameterName=\"VALUE\"/>" + "</datamodel>";

	// 1st layer
	public static String ENTITY_GRUPPA_OBJECTA_REMONTA_ID = "128049573928131";
	public static String ENTITY_VID_OBJECTA_REMONTA_ID = "128049576096827";
	// 2nd layer
	public static String ENTITY_IZMERJAEMIE_VELICHINI_ID = "128049576695369";
	public static String ENTITY_NAIMENOVANIE_SI_ID = "128049764412588";
	public static String ENTITY_MARKI_OBJECTOV_REMONTA_ID = "128049574970367";
	
	// 3rd layer
	public static String ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_ID = "128049746332840";
	public static String ENTITY_NAIMENOVANIA_SI_PO_VELICHINE_ID = "128049750556261";
	public static String ENTITY_EDINICI_OBORUDOVANIJA_ID = "128049574495320";
	// 4th layer
	public static String ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_1_ID = "128073231281282";
	public static String ENTITY_NAIMENOVANIA_SI_PO_VELICHINE_1_ID = "128073233404649";

	protected class ModelState {

		private Model model = null;
		public Entity GRUPPA_OBJECTA_REMONTA = null;
		public Entity VID_OBJECTA_REMONTA = null;
		public Entity IZMERJAEMIE_VELICHINI = null;
		public Entity NAIMENOVANIE_SI = null;
		public Entity MARKI_OBJECTOV_REMONTA = null;
		public Entity EDINICI_IZMERENIJA_PO_VELICHINE = null;
		public Entity NAIMENOVANIA_SI_PO_VELICHINE = null;
		public Entity EDINICI_OBORUDOVANIJA = null;
		public Entity EDINICI_IZMERENIJA_PO_VELICHINE_1 = null;
		public Entity NAIMENOVANIA_SI_PO_VELICHINE_1 = null;

		public ModelState(Model aModel) throws Exception {
			model = aModel;
			// 1st layer
			GRUPPA_OBJECTA_REMONTA = model.getEntityById(ENTITY_GRUPPA_OBJECTA_REMONTA_ID);
			assertNotNull(GRUPPA_OBJECTA_REMONTA.getRowset());
			VID_OBJECTA_REMONTA = model.getEntityById(ENTITY_VID_OBJECTA_REMONTA_ID);
			assertNotNull(VID_OBJECTA_REMONTA.getRowset());
			// 2nd layer
			IZMERJAEMIE_VELICHINI = model.getEntityById(ENTITY_IZMERJAEMIE_VELICHINI_ID);
			assertNotNull(IZMERJAEMIE_VELICHINI.getRowset());
			NAIMENOVANIE_SI = model.getEntityById(ENTITY_NAIMENOVANIE_SI_ID);
			assertNotNull(NAIMENOVANIE_SI.getRowset());
			MARKI_OBJECTOV_REMONTA = model.getEntityById(ENTITY_MARKI_OBJECTOV_REMONTA_ID);
			assertNotNull(MARKI_OBJECTOV_REMONTA.getRowset());
			// 3rd layer
			EDINICI_IZMERENIJA_PO_VELICHINE = model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_ID);
			assertNotNull(EDINICI_IZMERENIJA_PO_VELICHINE.getRowset());
			NAIMENOVANIA_SI_PO_VELICHINE = model.getEntityById(ENTITY_NAIMENOVANIA_SI_PO_VELICHINE_ID);
			assertNotNull(NAIMENOVANIA_SI_PO_VELICHINE.getRowset());
			EDINICI_OBORUDOVANIJA = model.getEntityById(ENTITY_EDINICI_OBORUDOVANIJA_ID);
			assertNotNull(EDINICI_OBORUDOVANIJA.getRowset());
			// 4th layer
			EDINICI_IZMERENIJA_PO_VELICHINE_1 = model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_1_ID);
			assertNotNull(EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset());
			NAIMENOVANIA_SI_PO_VELICHINE_1 = model.getEntityById(ENTITY_NAIMENOVANIA_SI_PO_VELICHINE_1_ID);
			assertNotNull(NAIMENOVANIA_SI_PO_VELICHINE_1.getRowset());
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

	protected static final Long AMPERMETR = 124764458743797L;
	protected static final Long MANOMETR = 124764470035963L;
	protected static final Long WATTMETR = 124764468034337L;
	protected static final Long VOLTMETR = 124764457876596L;

	protected Model model;

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
		l.loadQueries(queries(), new Runnable() {
			
			@Override
			public void run() {
				try {
					setupModel();
				} catch (Exception ex) {
					fail(ex.getMessage());
				}
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
		timer.schedule(getTimeout());
	}

	protected int getTimeout() {
		return 20 * 1000;
	}

	@Override
	public Collection<String> queries() {
		return Arrays.asList(new String[] { "128049734162523", "128049565893763", "128049551290614", "128049732520388", "128049734162523", "128049508301511", "128049732520388", "128049562734356",
		        "128049547464084", "128049558485943" });
	}

	protected abstract void setupModel() throws Exception;

}
