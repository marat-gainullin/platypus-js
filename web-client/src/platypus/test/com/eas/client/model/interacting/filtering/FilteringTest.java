/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.interacting.filtering;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.eas.client.RunnableAdapter;
import com.eas.client.application.AppClient;
import com.eas.client.application.Loader;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ModelBaseTest;
import com.eas.client.model.store.XmlDom2Model;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import com.google.gwt.xml.client.XMLParser;

/**
 * 
 * @author mg
 */
public abstract class FilteringTest extends ModelBaseTest {

	public static final String DATAMODEL_FILTERING_RELATIONS = "" + "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<datamodel>" + "    <parametersEntity Title=\"Параметры\">"
	        + "    </parametersEntity>"
	        + "    <entity Name=\"dsEdIzm\" Title=\"Единицы измерения\" entityId=\"128049575554676\" queryId=\"128049555364003\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsSrIzmNames\" Title=\"Наименования СИ\" entityId=\"128049577162567\" queryId=\"128049565893763\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsMarki\" Title=\"Марки объектов ремонта\" entityId=\"128049574970367\" queryId=\"128049551290614\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsGroup\" Title=\"Группа объекта ремонта\" entityId=\"128049573928131\" queryId=\"128049508301511\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsIzmVel\" Title=\"Измеряемые величины\" entityId=\"128049576695369\" queryId=\"128049562734356\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsEdObor\" Title=\"Единицы оборудования\" entityId=\"128049574495320\" queryId=\"128049547464084\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsRemVid\" Title=\"Вид объекта ремонта\" entityId=\"128049576096827\" queryId=\"128049558485943\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>"
	        + "    <entity Name=\"dsEdIzm1\" Title=\"Единицы измерения 1\" entityId=\"128072901201589\" queryId=\"128049555364003\" tableDbId=\"null\" tableName=\"\" tableSchemaName=\"\">"
	        + "    </entity>" + "    <relation leftEntityFieldName=\"ID\" leftEntityId=\"128049575554676\" rightEntityFieldName=\"ID\" rightEntityId=\"128072901201589\"/>"
	        + "    <relation leftEntityFieldName=\"ID\" leftEntityId=\"128049576695369\" rightEntityFieldName=\"VALUE\" rightEntityId=\"128049577162567\"/>"
	        + "    <relation leftEntityFieldName=\"ID\" leftEntityId=\"128049574970367\" rightEntityFieldName=\"MARK\" rightEntityId=\"128049574495320\"/>"
	        + "    <relation leftEntityFieldName=\"ID\" leftEntityId=\"128049576695369\" rightEntityFieldName=\"MEASURAND\" rightEntityId=\"128049575554676\"/>" + "</datamodel>";

	// 1st layer
	public static String ENTITY_GRUPPA_OBJECTA_REMONTA_ID = "128049573928131";
	public static String ENTITY_VID_OBJECTA_REMONTA_ID = "128049576096827";
	// 2nd layer
	public static String ENTITY_IZMERJAEMIE_VELICHINI_ID = "128049576695369";
	public static String ENTITY_MARKI_OBJECTOV_REMONTA_ID = "128049574970367";
	// 3rd layer
	public static String ENTITY_EDINICI_IZMERENIJA_ID = "128049575554676";
	public static String ENTITY_EDINICI_IZMERENIJA_1_ID = "128072901201589";
	public static String ENTITY_NAIMENOVANIA_SI_ID = "128049577162567";
	public static String ENTITY_EDINICI_OBORUDOVANIJA_ID = "128049574495320";

	protected class ModelState {

		private Model model = null;
		public Entity GRUPPA_OBJECTA_REMONTA = null;
		public Entity VID_OBJECTA_REMONTA = null;
		public Entity IZMERJAEMIE_VELICHINI = null;
		public Entity MARKI_OBJECTOV_REMONTA = null;
		public Entity EDINICI_IZMERENIJA = null;
		public Entity EDINICI_IZMERENIJA_1 = null;
		public Entity NAIMENOVANIA_SI = null;
		public Entity EDINICI_OBORUDOVANIJA = null;

		public ModelState(Model aModel) throws Exception {
			super();
			model = aModel;
			// 1st layer
			GRUPPA_OBJECTA_REMONTA = model.getEntityById(ENTITY_GRUPPA_OBJECTA_REMONTA_ID);
			assertNotNull(GRUPPA_OBJECTA_REMONTA.getRowset());
			VID_OBJECTA_REMONTA = model.getEntityById(ENTITY_VID_OBJECTA_REMONTA_ID);
			assertNotNull(VID_OBJECTA_REMONTA.getRowset());
			// 2nd layer
			IZMERJAEMIE_VELICHINI = model.getEntityById(ENTITY_IZMERJAEMIE_VELICHINI_ID);
			assertNotNull(IZMERJAEMIE_VELICHINI.getRowset());
			MARKI_OBJECTOV_REMONTA = model.getEntityById(ENTITY_MARKI_OBJECTOV_REMONTA_ID);
			assertNotNull(MARKI_OBJECTOV_REMONTA.getRowset());
			// 3rd layer
			EDINICI_IZMERENIJA = model.getEntityById(ENTITY_EDINICI_IZMERENIJA_ID);
			assertNotNull(EDINICI_IZMERENIJA.getRowset());
			EDINICI_IZMERENIJA_1 = model.getEntityById(ENTITY_EDINICI_IZMERENIJA_1_ID);
			assertNotNull(EDINICI_IZMERENIJA_1.getRowset());
			NAIMENOVANIA_SI = model.getEntityById(ENTITY_NAIMENOVANIA_SI_ID);
			assertNotNull(NAIMENOVANIA_SI.getRowset());
			EDINICI_OBORUDOVANIJA = model.getEntityById(ENTITY_EDINICI_OBORUDOVANIJA_ID);
			assertNotNull(EDINICI_OBORUDOVANIJA.getRowset());
		}

		public Map<String, Integer> gatherRowCounts() throws Exception {
			Map<String, Integer> counts = new HashMap<>();
			for (Entity entity : model.getEntities().values()) {
				counts.put(entity.getEntityId(), entity.getRowset().size());
			}
			return counts;
		}

		public void ensureRowCounts(Map<String, Integer> counts, String toSkip) throws Exception {
			for (String entityID : counts.keySet()) {
				if (!entityID.equals(toSkip)) {
					assertEquals((Integer) counts.get(entityID), (Integer) model.getEntityById(entityID).getRowset().size());
				}
			}
		}
	}

	public static final Long DLINA = 124772604470311L;
	public static final Long SILA_EL = 124772776092170L;
	public static final Long SILA_LIGHT = 124772784578140L;
	public static final Long DAVL = 128049701701571L;
	public static final Long MOSHN = 128049695192184L;
	public static final Long NAPRJAZH = 128049696157809L;
	public static final Long VOLUME = 124772787892110L;

	protected Model model;

	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();
		delayTestFinish(60 * 60 * 1000);
		AppClient client = initDevelopTestClient();
		Loader l = new Loader(client);
		l.loadQueries(queries(), new RunnableAdapter() {

			@Override
			protected void doWork() throws Exception {
				JavaScriptObject module = publish(FilteringTest.this);
				model = XmlDom2Model.transform(XMLParser.parse(DATAMODEL_FILTERING_RELATIONS), module);
				Model.publishTopLevelFacade(module, model);
				model.requery(null);
			}

		});
		Timer timer = new Timer() {
			public void run() {
				// check test's internal counters
				try {
					validate();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// tell the test system the test is now done
				finishTest();
			}
		};
		// Schedule the event and return control to the test system.
		timer.schedule(20 * 1000);
	}

	@Override
	public Collection<String> queries() {
		return Arrays.asList(new String[] { "128049555364003", "128049565893763", "128049551290614", "128049508301511", "128049562734356", "128049547464084", "128049558485943", "128049555364003" });
	}

	public native JavaScriptObject publish(ModelBaseTest aTest) throws Exception/*-{
		return {
			imRequeried : function() {
			}
		};
	}-*/;

	public void testStart() {
	}
}
