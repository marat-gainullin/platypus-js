/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.interacting;

import com.bearsoft.rowset.Rowset;
import com.eas.client.DatabasesClient;
import com.eas.client.DatabasesClientWithResource;
import com.eas.client.model.BaseTest;
import com.eas.client.model.DataScriptEventsListener;
import com.eas.client.model.EntityDataListener;
import com.eas.client.model.EntityRefreshFilterDataListener;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.script.ScriptUtils;
import com.eas.script.ScriptUtils.ScriptAction;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;
import org.mozilla.javascript.Context;

/**
 *
 * @author mg
 */
public class MixedTest extends BaseTest {

    public static String MODEL_TEST_PATH = BaseTest.RESOURCES_PREFIX + "datamodelMixedRelations.xml";
    // 1st layer
    public static Long ENTITY_GRUPPA_OBJECTA_REMONTA_ID = 128049573928131L;
    public static Long ENTITY_VID_OBJECTA_REMONTA_ID = 128049576096827L;
    // 2nd layer
    public static Long ENTITY_IZMERJAEMIE_VELICHINI_ID = 128049576695369L;
    public static Long ENTITY_NAIMENOVANIE_SI_ID = 128049764412588L;
    public static Long ENTITY_MARKI_OBJECTOV_REMONTA_ID = 128049574970367L;
    // 3rd layer
    public static Long ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_ID = 128049746332840L;
    public static Long ENTITY_NAIMENOVANIA_SI_PO_VELICHINE_ID = 128049750556261L;
    public static Long ENTITY_EDINICI_OBORUDOVANIJA_ID = 128049574495320L;
    // 4th layer
    public static Long ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_1_ID = 128073231281282L;
    public static Long ENTITY_NAIMENOVANIA_SI_PO_VELICHINE_1_ID = 128073233404649L;

    private class ModelState {

        private ApplicationDbModel model = null;
        public ApplicationDbEntity GRUPPA_OBJECTA_REMONTA = null;
        public ApplicationDbEntity VID_OBJECTA_REMONTA = null;
        public ApplicationDbEntity IZMERJAEMIE_VELICHINI = null;
        public ApplicationDbEntity NAIMENOVANIE_SI = null;
        public ApplicationDbEntity MARKI_OBJECTOV_REMONTA = null;
        public ApplicationDbEntity EDINICI_IZMERENIJA_PO_VELICHINE = null;
        public ApplicationDbEntity NAIMENOVANIA_SI_PO_VELICHINE = null;
        public ApplicationDbEntity EDINICI_OBORUDOVANIJA = null;
        public ApplicationDbEntity EDINICI_IZMERENIJA_PO_VELICHINE_1 = null;
        public ApplicationDbEntity NAIMENOVANIA_SI_PO_VELICHINE_1 = null;

        public ModelState(ApplicationDbModel aModel) throws Exception {
            model = aModel;
            // 1st layer
            GRUPPA_OBJECTA_REMONTA = model.getEntityById(ENTITY_GRUPPA_OBJECTA_REMONTA_ID);
            assertNotNull(GRUPPA_OBJECTA_REMONTA.getRowset());
            installScriptEvents(GRUPPA_OBJECTA_REMONTA);
            VID_OBJECTA_REMONTA = model.getEntityById(ENTITY_VID_OBJECTA_REMONTA_ID);
            assertNotNull(VID_OBJECTA_REMONTA.getRowset());
            installScriptEvents(VID_OBJECTA_REMONTA);
            // 2nd layer
            IZMERJAEMIE_VELICHINI = model.getEntityById(ENTITY_IZMERJAEMIE_VELICHINI_ID);
            assertNotNull(IZMERJAEMIE_VELICHINI.getRowset());
            installScriptEvents(IZMERJAEMIE_VELICHINI);
            NAIMENOVANIE_SI = model.getEntityById(ENTITY_NAIMENOVANIE_SI_ID);
            assertNotNull(NAIMENOVANIE_SI.getRowset());
            installScriptEvents(NAIMENOVANIE_SI);
            MARKI_OBJECTOV_REMONTA = model.getEntityById(ENTITY_MARKI_OBJECTOV_REMONTA_ID);
            assertNotNull(MARKI_OBJECTOV_REMONTA.getRowset());
            installScriptEvents(MARKI_OBJECTOV_REMONTA);
            // 3rd layer
            EDINICI_IZMERENIJA_PO_VELICHINE = model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_ID);
            assertNotNull(EDINICI_IZMERENIJA_PO_VELICHINE.getRowset());
            installScriptEvents(EDINICI_IZMERENIJA_PO_VELICHINE);
            NAIMENOVANIA_SI_PO_VELICHINE = model.getEntityById(ENTITY_NAIMENOVANIA_SI_PO_VELICHINE_ID);
            assertNotNull(NAIMENOVANIA_SI_PO_VELICHINE.getRowset());
            installScriptEvents(NAIMENOVANIA_SI_PO_VELICHINE);
            EDINICI_OBORUDOVANIJA = model.getEntityById(ENTITY_EDINICI_OBORUDOVANIJA_ID);
            assertNotNull(EDINICI_OBORUDOVANIJA.getRowset());
            installScriptEvents(EDINICI_OBORUDOVANIJA);
            // 4th layer
            EDINICI_IZMERENIJA_PO_VELICHINE_1 = model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_1_ID);
            assertNotNull(EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset());
            installScriptEvents(EDINICI_IZMERENIJA_PO_VELICHINE_1);
            NAIMENOVANIA_SI_PO_VELICHINE_1 = model.getEntityById(ENTITY_NAIMENOVANIA_SI_PO_VELICHINE_1_ID);
            assertNotNull(NAIMENOVANIA_SI_PO_VELICHINE_1.getRowset());
            installScriptEvents(NAIMENOVANIA_SI_PO_VELICHINE_1);
        }

        protected final void installScriptEvents(ApplicationDbEntity entity) throws Exception {
            entity.setOnBeforeScroll(getDummyHandler(BaseTest.DUMMY_HANDLER_NAME));
            entity.setOnBeforeChange(getDummyHandler(BaseTest.DUMMY_HANDLER_NAME));
            entity.setOnBeforeDelete(getDummyHandler(BaseTest.DUMMY_HANDLER_NAME));
            entity.setOnBeforeInsert(getDummyHandler(BaseTest.DUMMY_HANDLER_NAME));

            entity.setOnAfterScroll(getDummyHandler(BaseTest.DUMMY_HANDLER_NAME));
            entity.setOnAfterChange(getDummyHandler(BaseTest.DUMMY_HANDLER_NAME));
            entity.setOnAfterDelete(getDummyHandler(BaseTest.DUMMY_HANDLER_NAME));
            entity.setOnAfterInsert(getDummyHandler(BaseTest.DUMMY_HANDLER_NAME));

            entity.setOnFiltered(getDummyHandler(BaseTest.DUMMY_HANDLER_NAME));
            entity.setOnRequeried(getDummyHandler(BaseTest.DUMMY_HANDLER_NAME));
        }

        public Map<Long, Integer> gatherRowCounts() throws Exception {
            Map<Long, Integer> counts = new HashMap<>();
            for (ApplicationDbEntity entity : model.getEntities().values()) {
                counts.put(entity.getEntityId(), entity.getRowset().size());
            }
            return counts;
        }

        public void ensureRowCounts(Map<Long, Integer> counts, Long toSkip) throws Exception {
            for (Long entityID : counts.keySet()) {
                if (!entityID.equals(toSkip)) {
                    assertEquals((Integer) counts.get(entityID), (Integer) model.getEntityById(entityID).getRowset().size());
                }
            }
        }
    }
    private static final Long AMPERMETR = 124764458743797L;
    private static final Long MANOMETR = 124764470035963L;
    private static final Long WATTMETR = 124764468034337L;
    private static final Long VOLTMETR = 124764457876596L;

    @Test
    public void mixedScrollTest() throws Exception {
        System.out.println("mixedScrollTest, mixedPointOfInterestTest");
        try (DatabasesClientWithResource resource = BaseTest.initDevelopTestClient()) {
            final DatabasesClient client = resource.getClient();
            ScriptUtils.inContext(new ScriptUtils.ScriptAction() {
                @Override
                public Object run(Context cx) throws Exception {
                    ApplicationDbModel model = BaseTest.modelFromStream(client, MixedTest.class.getResourceAsStream(MODEL_TEST_PATH));
                    model.setScriptThis(BaseTest.getDummyScriptableObject());
                    model.requery();
                    ModelState state = new ModelState(model);
                    Map<Long, Integer> counts = state.gatherRowCounts();

                    // let's move some rowset's la la la la...
                    Rowset rowset = state.GRUPPA_OBJECTA_REMONTA.getRowset();
                    rowset.beforeFirst();
                    while (rowset.next()) {
                        state.ensureRowCounts(counts, state.GRUPPA_OBJECTA_REMONTA.getEntityId());
                    }
                    rowset.first();

                    rowset = state.VID_OBJECTA_REMONTA.getRowset();
                    rowset.beforeFirst();
                    while (rowset.next()) {
                        state.ensureRowCounts(counts, state.VID_OBJECTA_REMONTA.getEntityId());
                    }
                    rowset.first();

                    Rowset izmVel = state.IZMERJAEMIE_VELICHINI.getRowset();
                    Rowset naimSi = state.NAIMENOVANIE_SI.getRowset();
                    assertNotNull(izmVel);
                    assertNotNull(naimSi);
                    int velPkColIndex = izmVel.getFields().find("ID");
                    int siPkColIndex = naimSi.getFields().find("ID");
                    izmVel.beforeFirst();
                    while (izmVel.next()) {
                        naimSi.beforeFirst();
                        while (naimSi.next()) {
                            Object oVelPk = izmVel.getObject(velPkColIndex);
                            Object oSiPk = naimSi.getObject(siPkColIndex);
                            assertNotNull(oVelPk);
                            assertNotNull(oSiPk);
                            if (oVelPk instanceof Number && oSiPk instanceof Number) {
                                Long velPk = ((Number) oVelPk).longValue();
                                Long siPk = ((Number) oSiPk).longValue();
                                if (velPk.equals(FilteringTest.DLINA)) {
                                    assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                                    assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                                } else if (velPk.equals(FilteringTest.SILA_EL)) {
                                    assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                                    assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                                    if (siPk.equals(AMPERMETR)) {
                                        naimSi.previous();
                                        naimSi.next();
                                        assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
                                        assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE_1.getRowset().size());
                                    }
                                } else if (velPk.equals(FilteringTest.DAVL)) {
                                    assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                                    assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                                    if (siPk.equals(MANOMETR)) {
                                        assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
                                        assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE_1.getRowset().size());
                                    }
                                } else if (velPk.equals(FilteringTest.MOSHN)) {
                                    assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                                    assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                                    if (siPk.equals(WATTMETR)) {
                                        assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
                                        assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE_1.getRowset().size());
                                    }
                                } else if (velPk.equals(FilteringTest.NAPRJAZH)) {
                                    assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                                    assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                                    if (siPk.equals(VOLTMETR)) {
                                        assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
                                        assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE_1.getRowset().size());
                                    }
                                } else if (velPk.equals(FilteringTest.VOLUME)) {
                                    assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                                    assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                                    assertEquals(0, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
                                    assertEquals(0, state.NAIMENOVANIA_SI_PO_VELICHINE_1.getRowset().size());
                                }

                            } else {
                                assertTrue(false);
                            }
                        }
                        naimSi.first();
                    }
                    izmVel.first();

                    rowset = state.MARKI_OBJECTOV_REMONTA.getRowset();
                    int pkColIndex = rowset.getFields().find("ID");
                    rowset.beforeFirst();
                    while (rowset.next()) {
                        Object oPk = rowset.getObject(pkColIndex);
                        assertNotNull(oPk);
                        if (oPk instanceof Number) {
                            Long lPk = ((Number) oPk).longValue();
                            if (lPk.equals(128049594110963L)) {
                                assertEquals(2, state.EDINICI_OBORUDOVANIJA.getRowset().size());
                            } else if (lPk.equals(128049595046828L)) {
                                assertEquals(2, state.EDINICI_OBORUDOVANIJA.getRowset().size());
                            } else if (lPk.equals(128049595600024L)) {
                                assertEquals(2, state.EDINICI_OBORUDOVANIJA.getRowset().size());
                            } else if (lPk.equals(128049596076572L)) {
                                assertEquals(3, state.EDINICI_OBORUDOVANIJA.getRowset().size());
                            } else if (lPk.equals(128049596964037L)) {
                                assertEquals(2, state.EDINICI_OBORUDOVANIJA.getRowset().size());
                            } else if (lPk.equals(128049597468768L)) {
                                assertEquals(2, state.EDINICI_OBORUDOVANIJA.getRowset().size());
                            } else if (lPk.equals(128049597975084L)) {
                                assertEquals(4, state.EDINICI_OBORUDOVANIJA.getRowset().size());
                            } else if (lPk.equals(128049598748403L)) {
                                assertEquals(2, state.EDINICI_OBORUDOVANIJA.getRowset().size());
                            } else if (lPk.equals(128049599817169L)) {
                                assertEquals(0, state.EDINICI_OBORUDOVANIJA.getRowset().size());
                            } else if (lPk.equals(128049601170306L)) {
                                assertEquals(1, state.EDINICI_OBORUDOVANIJA.getRowset().size());
                            }
                        } else {
                            assertTrue(false);
                        }
                    }
                    rowset.first();
                    return null;
                }
            });
        }
    }
    EntityRefreshFilterDataListener listenerOf1Layer = null;
    EntityRefreshFilterDataListener listenerOf2Layer = null;
    EntityRefreshFilterDataListener listenerOf3Layer = null;

    @Test
    public void mixedExecutingOrderTest() throws Exception {
        System.out.println("mixedExecutingOrderTest");
        try (DatabasesClientWithResource resource = BaseTest.initDevelopTestClient()) {
            final DatabasesClient client = resource.getClient();
            ScriptUtils.inContext(new ScriptAction() {
                @Override
                public Object run(Context cx) throws Exception {
                    try {
                        ApplicationDbModel model = BaseTest.modelFromStream(client, MixedTest.class.getResourceAsStream(MODEL_TEST_PATH));
                        model.setScriptThis(BaseTest.getDummyScriptableObject());
                        model.requery();
                        ModelState state = new ModelState(model);
                        listenerOf1Layer = null;
                        listenerOf2Layer = null;
                        listenerOf3Layer = null;

                        listenerOf1Layer = new EntityRefreshFilterDataListener() {
                            @Override
                            protected void incEvents() {
                                super.incEvents();
                                assertEquals(0, listenerOf2Layer.getEvents());
                                assertEquals(0, listenerOf3Layer.getEvents());
                            }
                        };
                        listenerOf2Layer = new EntityRefreshFilterDataListener() {
                            @Override
                            protected void incEvents() {
                                super.incEvents();
                                assertTrue(listenerOf1Layer.getEvents() > 0);
                                assertEquals(0, listenerOf3Layer.getEvents());
                            }
                        };
                        listenerOf3Layer = new EntityRefreshFilterDataListener() {
                            @Override
                            protected void incEvents() {
                                super.incEvents();
                                assertTrue(listenerOf1Layer.getEvents() > 0);
                                assertTrue(listenerOf2Layer.getEvents() > 0);
                            }
                        };
                        // 1st layer
                        state.GRUPPA_OBJECTA_REMONTA.getRowset().addRowsetListener(listenerOf1Layer);
                        state.VID_OBJECTA_REMONTA.getRowset().addRowsetListener(listenerOf1Layer);
                        state.IZMERJAEMIE_VELICHINI.getRowset().addRowsetListener(listenerOf1Layer);
                        state.NAIMENOVANIE_SI.getRowset().addRowsetListener(listenerOf1Layer);
                        state.GRUPPA_OBJECTA_REMONTA.getRowset().addRowsetListener(listenerOf1Layer);
                        // 2nd layer
                        state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().addRowsetListener(listenerOf2Layer);
                        state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().addRowsetListener(listenerOf2Layer);
                        state.EDINICI_OBORUDOVANIJA.getRowset().addRowsetListener(listenerOf2Layer);
                        // 3rd layer
                        state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().addRowsetListener(listenerOf3Layer);
                        state.NAIMENOVANIA_SI_PO_VELICHINE_1.getRowset().addRowsetListener(listenerOf3Layer);

                        model.requery();
                    } finally {
                        listenerOf1Layer = null;
                        listenerOf2Layer = null;
                        listenerOf3Layer = null;
                    }
                    return null;
                }
            });
        }
    }

    @Test
    public void mixedCrudTest() throws Exception {
        System.out.println("mixedCrudTest");
        try (DatabasesClientWithResource resource = BaseTest.initDevelopTestClient()) {
            final DatabasesClient client = resource.getClient();
            ScriptUtils.inContext(new ScriptAction() {
                @Override
                public Object run(Context cx) throws Exception {
                    ApplicationDbModel model = BaseTest.modelFromStream(client, MixedTest.class.getResourceAsStream(MODEL_TEST_PATH));
                    model.setScriptThis(BaseTest.getDummyScriptableObject());
                    model.requery();
                    ModelState state = new ModelState(model);

                    Rowset velRs = state.IZMERJAEMIE_VELICHINI.getRowset();
                    assertNotNull(velRs);
                    int velPkColIndex = velRs.getFields().find("ID");
                    velRs.beforeFirst();
                    while (velRs.next()) {
                        Object oVelPk = velRs.getObject(velPkColIndex);
                        assertNotNull(oVelPk);
                        if (oVelPk instanceof Number) {
                            Long velPk = ((Number) oVelPk).longValue();
                            if (velPk.equals(FilteringTest.SILA_EL)) {
                                Rowset siRs = state.NAIMENOVANIE_SI.getRowset();
                                assertNotNull(siRs);
                                int velColIndex = siRs.getFields().find("VALUE");
                                for (int i = 0; i < 53; i++) {
                                    siRs.insert();
                                    assertEquals(0, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
                                    assertEquals(0, state.NAIMENOVANIA_SI_PO_VELICHINE_1.getRowset().size());
                                    siRs.updateObject(velColIndex, FilteringTest.SILA_EL);
                                    assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
                                    assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE_1.getRowset().size());
                                    siRs.delete();
                                }
                            }
                        } else {
                            assertTrue(false);
                        }
                    }
                    return null;
                }
            });
        }
    }

    @Test
    public void mixedScriptEventsVsDataTest() throws Exception {
        System.out.println("mixedScriptEventsVsDataTest");
        try (DatabasesClientWithResource resource = BaseTest.initDevelopTestClient()) {
            final DatabasesClient client = resource.getClient();
            ScriptUtils.inContext(new ScriptAction() {
                @Override
                public Object run(Context cx) throws Exception {
                    ApplicationDbModel model = BaseTest.modelFromStream(client, MixedTest.class.getResourceAsStream(MODEL_TEST_PATH));
                    model.setScriptThis(BaseTest.getDummyScriptableObject());
                    model.requery();
                    ModelState state = new ModelState(model);

                    DataScriptEventsListener scriptListener1 = new DataScriptEventsListener(state.EDINICI_IZMERENIJA_PO_VELICHINE);
                    EntityDataListener dataListener1 = new EntityDataListener();
                    state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().addRowsetListener(dataListener1);
                    model.addScriptEventsListener(scriptListener1);

                    DataScriptEventsListener scriptListener2 = new DataScriptEventsListener(state.EDINICI_IZMERENIJA_PO_VELICHINE_1);
                    EntityDataListener dataListener2 = new EntityDataListener();
                    state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().addRowsetListener(dataListener2);
                    model.addScriptEventsListener(scriptListener2);

                    DataScriptEventsListener scriptListener3 = new DataScriptEventsListener(state.NAIMENOVANIA_SI_PO_VELICHINE);
                    EntityDataListener dataListener3 = new EntityDataListener();
                    state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().addRowsetListener(dataListener3);
                    model.addScriptEventsListener(scriptListener3);

                    DataScriptEventsListener scriptListener4 = new DataScriptEventsListener(state.NAIMENOVANIA_SI_PO_VELICHINE_1);
                    EntityDataListener dataListener4 = new EntityDataListener();
                    state.NAIMENOVANIA_SI_PO_VELICHINE_1.getRowset().addRowsetListener(dataListener4);
                    model.addScriptEventsListener(scriptListener4);

                    Rowset izmVel = state.IZMERJAEMIE_VELICHINI.getRowset();
                    Rowset naimSi = state.NAIMENOVANIE_SI.getRowset();
                    assertNotNull(izmVel);
                    assertNotNull(naimSi);
                    scriptListener1.reset();
                    dataListener1.reset();
                    scriptListener2.reset();
                    dataListener2.reset();
                    scriptListener3.reset();
                    dataListener3.reset();
                    scriptListener4.reset();
                    dataListener4.reset();
                    izmVel.beforeFirst();
                    assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                    assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                    assertEquals(dataListener3.getEvents(), scriptListener3.getEvents());
                    assertEquals(dataListener4.getEvents(), scriptListener4.getEvents());
                    assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                    assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                    assertTrue(dataListener3.getScrollEvents() >= scriptListener3.getScrollEvents());
                    assertTrue(dataListener4.getScrollEvents() >= scriptListener4.getScrollEvents());
                    while (izmVel.next()) {
                        assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                        assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                        assertEquals(dataListener3.getEvents(), scriptListener3.getEvents());
                        assertEquals(dataListener4.getEvents(), scriptListener4.getEvents());
                        assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                        assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                        assertTrue(dataListener3.getScrollEvents() >= scriptListener3.getScrollEvents());
                        assertTrue(dataListener4.getScrollEvents() >= scriptListener4.getScrollEvents());
                        naimSi.beforeFirst();
                        assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                        assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                        assertEquals(dataListener3.getEvents(), scriptListener3.getEvents());
                        assertEquals(dataListener4.getEvents(), scriptListener4.getEvents());
                        assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                        assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                        assertTrue(dataListener3.getScrollEvents() >= scriptListener3.getScrollEvents());
                        assertTrue(dataListener4.getScrollEvents() >= scriptListener4.getScrollEvents());
                        while (naimSi.next()) {
                            assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                            assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                            assertEquals(dataListener3.getEvents(), scriptListener3.getEvents());
                            assertEquals(dataListener4.getEvents(), scriptListener4.getEvents());
                            assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                            assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                            assertTrue(dataListener3.getScrollEvents() >= scriptListener3.getScrollEvents());
                            assertTrue(dataListener4.getScrollEvents() >= scriptListener4.getScrollEvents());
                        }
                        assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                        assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                        assertEquals(dataListener3.getEvents(), scriptListener3.getEvents());
                        assertEquals(dataListener4.getEvents(), scriptListener4.getEvents());
                        assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                        assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                        assertTrue(dataListener3.getScrollEvents() >= scriptListener3.getScrollEvents());
                        assertTrue(dataListener4.getScrollEvents() >= scriptListener4.getScrollEvents());
                    }
                    assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                    assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                    assertEquals(dataListener3.getEvents(), scriptListener3.getEvents());
                    assertEquals(dataListener4.getEvents(), scriptListener4.getEvents());
                    assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                    assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                    assertTrue(dataListener3.getScrollEvents() >= scriptListener3.getScrollEvents());
                    assertTrue(dataListener4.getScrollEvents() >= scriptListener4.getScrollEvents());
                    return null;
                }
            });
        }
    }
}
