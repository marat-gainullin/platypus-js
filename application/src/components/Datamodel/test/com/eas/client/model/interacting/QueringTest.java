/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.interacting;

import com.bearsoft.rowset.Rowset;
import com.eas.client.DbClient;
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
public class QueringTest extends BaseTest {

    private static String MODEL_TEST_PATH = BaseTest.RESOURCES_PREFIX + "datamodelQueringRelations.xml";
    // 1st layer
    private static Long ENTITY_GRUPPA_OBJECTA_REMONTA_PO_RODITELU_ID = 128049787114001L;
    private static Long ENTITY_VID_OBJECTA_REMONTA_ID = 128049576096827L;
    // 2nd layer
    private static Long ENTITY_IZMERJAEMIE_VELICHINI_ID = 128049576695369L;
    private static Long ENTITY_MARKI_OBJECTOV_REMONTA_ID = 128049574970367L;
    // 3rd layer
    private static Long ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_ID = 128049746332840L;
    private static Long ENTITY_NAIMENOVANIA_SI_PO_VELICHINE_ID = 128049750556261L;
    private static Long ENTITY_EDINICI_OBORUDOVANIJA_PO_MARKE_ID = 128049775173425L;
    // 4th layer
    private static Long ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_1_ID = 128073170857902L;

    private class ModelState {

        private ApplicationDbModel model = null;
        public ApplicationDbEntity GRUPPA_OBJECTA_REMONTA_PO_RODITELU = null;
        public ApplicationDbEntity VID_OBJECTA_REMONTA = null;
        public ApplicationDbEntity IZMERJAEMIE_VELICHINI = null;
        public ApplicationDbEntity MARKI_OBJECTOV_REMONTA = null;
        public ApplicationDbEntity EDINICI_IZMERENIJA_PO_VELICHINE = null;
        public ApplicationDbEntity NAIMENOVANIA_SI_PO_VELICHINE = null;
        public ApplicationDbEntity EDINICI_OBORUDOVANIJA_PO_MARKE = null;
        public ApplicationDbEntity EDINICI_IZMERENIJA_PO_VELICHINE_1 = null;

        public ModelState(ApplicationDbModel aModel) throws Exception {
            model = aModel;
            // 1st layer
            GRUPPA_OBJECTA_REMONTA_PO_RODITELU = model.getEntityById(ENTITY_GRUPPA_OBJECTA_REMONTA_PO_RODITELU_ID);
            assertNotNull(GRUPPA_OBJECTA_REMONTA_PO_RODITELU.getRowset());
            installScriptEvents(GRUPPA_OBJECTA_REMONTA_PO_RODITELU);
            VID_OBJECTA_REMONTA = model.getEntityById(ENTITY_VID_OBJECTA_REMONTA_ID);
            assertNotNull(VID_OBJECTA_REMONTA.getRowset());
            installScriptEvents(VID_OBJECTA_REMONTA);
            // 2nd layer
            IZMERJAEMIE_VELICHINI = model.getEntityById(ENTITY_IZMERJAEMIE_VELICHINI_ID);
            assertNotNull(IZMERJAEMIE_VELICHINI.getRowset());
            installScriptEvents(IZMERJAEMIE_VELICHINI);
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
            EDINICI_OBORUDOVANIJA_PO_MARKE = model.getEntityById(ENTITY_EDINICI_OBORUDOVANIJA_PO_MARKE_ID);
            assertNotNull(EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset());
            installScriptEvents(EDINICI_OBORUDOVANIJA_PO_MARKE);
            // 4th layer
            EDINICI_IZMERENIJA_PO_VELICHINE_1 = model.getEntityById(ENTITY_EDINICI_IZMERENIJA_PO_VELICHINE_1_ID);
            assertNotNull(EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset());
            installScriptEvents(EDINICI_IZMERENIJA_PO_VELICHINE_1);
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
    private static final Long PROIZVODSTVENNIE_OS = 1L;

    @Test
    public void queringScrollTest() throws Exception {
        System.out.println("queringScrollTest, queringPointOfInterestTest");
        final DbClient client = initDevelopTestClient();
        ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Object run(Context cx) throws Exception {
                ApplicationDbModel model = BaseTest.modelFromStream(client, QueringTest.class.getResourceAsStream(MODEL_TEST_PATH));
                model.setScriptThis(BaseTest.getDummyScriptableObject());
                model.setRuntime(true);
                ModelState state = new ModelState(model);
                int parIndex = model.getParametersEntity().getRowset().getFields().find("P_ID");
                model.getParametersEntity().getRowset().updateObject(parIndex, PROIZVODSTVENNIE_OS);

                Rowset groupByParentRs = state.GRUPPA_OBJECTA_REMONTA_PO_RODITELU.getRowset();
                assertNotNull(groupByParentRs);
                assertEquals(3, groupByParentRs.size());
                Map<Long, Integer> counts = state.gatherRowCounts();
                groupByParentRs.beforeFirst();
                while (groupByParentRs.next()) {
                    state.ensureRowCounts(counts, state.GRUPPA_OBJECTA_REMONTA_PO_RODITELU.getEntityId());
                }
                groupByParentRs.first();

                Rowset repairKindRs = state.VID_OBJECTA_REMONTA.getRowset();
                assertNotNull(repairKindRs);
                assertEquals(3, repairKindRs.size());
                counts = state.gatherRowCounts();
                repairKindRs.beforeFirst();
                while (repairKindRs.next()) {
                    state.ensureRowCounts(counts, state.VID_OBJECTA_REMONTA.getEntityId());
                }
                repairKindRs.first();

                Rowset izmRs = state.IZMERJAEMIE_VELICHINI.getRowset();
                int pkColIndex = izmRs.getFields().find("ID");
                assertNotNull(izmRs);
                izmRs.beforeFirst();
                while (izmRs.next()) {
                    Object oPk = izmRs.getObject(pkColIndex);
                    assertNotNull(oPk);
                    if (oPk instanceof Number) {
                        Long lPk = ((Number) oPk).longValue();
                        if (lPk.equals(FilteringTest.DLINA)) {
                            assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                            assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                            assertEquals(0, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
                            Rowset dlinaRowset = state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset();
                            dlinaRowset.beforeFirst();
                            while (dlinaRowset.next()) {
                                assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                            }
                            dlinaRowset.first();
                        } else if (lPk.equals(FilteringTest.SILA_EL)) {
                            assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                            assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                            assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
                        } else if (lPk.equals(FilteringTest.DAVL)) {
                            assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                            assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                            assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
                        } else if (lPk.equals(FilteringTest.MOSHN)) {
                            assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                            assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                            assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
                        } else if (lPk.equals(FilteringTest.NAPRJAZH)) {
                            assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                            assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                            assertEquals(1, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
                        } else if (lPk.equals(FilteringTest.VOLUME)) {
                            assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                            assertEquals(1, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                            assertEquals(0, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
                        } else {
                            assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                            assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                            assertEquals(0, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());
                        }
                    } else {
                        assertTrue(false);
                    }
                }
                izmRs.first();

                Rowset marksRs = state.MARKI_OBJECTOV_REMONTA.getRowset();
                pkColIndex = marksRs.getFields().find("ID");
                marksRs.beforeFirst();
                while (marksRs.next()) {
                    Object oPk = marksRs.getObject(pkColIndex);
                    assertNotNull(oPk);
                    if (oPk instanceof Number) {
                        Long lPk = ((Number) oPk).longValue();
                        if (lPk.equals(128049594110963L)) {
                            assertEquals(2, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
                        } else if (lPk.equals(128049595046828L)) {
                            assertEquals(2, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
                        } else if (lPk.equals(128049595600024L)) {
                            assertEquals(2, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
                        } else if (lPk.equals(128049596076572L)) {
                            assertEquals(3, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
                        } else if (lPk.equals(128049596964037L)) {
                            assertEquals(2, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
                        } else if (lPk.equals(128049597468768L)) {
                            assertEquals(2, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
                        } else if (lPk.equals(128049597975084L)) {
                            assertEquals(4, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
                        } else if (lPk.equals(128049598748403L)) {
                            assertEquals(2, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
                        } else if (lPk.equals(128049599817169L)) {
                            assertEquals(0, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
                        } else if (lPk.equals(128049601170306L)) {
                            assertEquals(1, state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().size());
                        }
                    } else {
                        assertTrue(false);
                    }
                }
                marksRs.first();
                return null;
            }
        });
    }

    @Test
    public void queringEmptyKeysSourceTest() throws Exception {
        System.out.println("queringEmptyKeysSourceTest");
        final DbClient client = initDevelopTestClient();
        ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Object run(Context cx) throws Exception {
                ApplicationDbModel model = BaseTest.modelFromStream(client, FilteringTest.class.getResourceAsStream(MODEL_TEST_PATH));
                model.setScriptThis(BaseTest.getDummyScriptableObject());
                model.setRuntime(true);
                ModelState state = new ModelState(model);

                Rowset rowset = state.IZMERJAEMIE_VELICHINI.getRowset();
                int pkColIndex = rowset.getFields().find("ID");
                rowset.beforeFirst();
                while (rowset.next()) {
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
                        assertTrue(false);
                    }
                }
                rowset.first();
                return null;
            }
        });
    }

    @Test
    public void queringBadSourcePositionTest() throws Exception {
        System.out.println("queringBadSourcePositionTest");
        final DbClient client = initDevelopTestClient();
        ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Object run(Context cx) throws Exception {
                ApplicationDbModel model = BaseTest.modelFromStream(client, FilteringTest.class.getResourceAsStream(MODEL_TEST_PATH));
                model.setScriptThis(BaseTest.getDummyScriptableObject());
                model.setRuntime(true);
                ModelState state = new ModelState(model);

                Rowset rowset = state.IZMERJAEMIE_VELICHINI.getRowset();
                int pkColIndex = rowset.getFields().find("ID");
                rowset.beforeFirst();
                while (rowset.next()) {
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
                            assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                            state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().first();
                            assertTrue(state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size() > 0);
                            // after last
                            state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().afterLast();
                            assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                            state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().last();
                            assertTrue(state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size() > 0);
                        } else {
                            assertNotNull(state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset());
                            assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
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

    @Test
    public void queringCrudTest() throws Exception {
        System.out.println("queringCrudTest");
        final DbClient client = initDevelopTestClient();
        ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Object run(Context cx) throws Exception {
                ApplicationDbModel model = BaseTest.modelFromStream(client, QueringTest.class.getResourceAsStream(MODEL_TEST_PATH));
                model.setScriptThis(BaseTest.getDummyScriptableObject());
                model.setRuntime(true);
                ModelState state = new ModelState(model);
                Rowset rowset = state.IZMERJAEMIE_VELICHINI.getRowset();
                int pkColIndex = rowset.getFields().find("ID");

                // edit rows from dline to sila sveta and vice versa for several times
                for (int i = 0; i < 56; i++) {
                    // ensure prestate
                    rowset.beforeFirst();
                    while (rowset.next()) {
                        Object oPk = rowset.getObject(pkColIndex);
                        assertNotNull(oPk);
                        if (oPk instanceof Number) {
                            Long lPk = ((Number) oPk).longValue();
                            if (lPk.equals(FilteringTest.DLINA)) {
                                assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                            } else if (lPk.equals(FilteringTest.SILA_LIGHT)) {
                                assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                            }
                        }
                    }
                    // let's edit query-key field's value.
                    rowset.beforeFirst();
                    while (rowset.next()) {
                        Object oPk = rowset.getObject(pkColIndex);
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
                    }
                    // ensure results
                    rowset.beforeFirst();
                    while (rowset.next()) {
                        Object oPk = rowset.getObject(pkColIndex);
                        assertNotNull(oPk);
                        if (oPk instanceof Number) {
                            Long lPk = ((Number) oPk).longValue();
                            if (lPk.equals(FilteringTest.DLINA)) {
                                assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                            } else if (lPk.equals(FilteringTest.SILA_LIGHT)) {
                                assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                            }
                        }
                    }
                    // let's edit filter-key field's value.
                    rowset.beforeFirst();
                    while (rowset.next()) {
                        Object oPk = rowset.getObject(pkColIndex);
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
                    }
                    // ensure results
                    rowset.beforeFirst();
                    while (rowset.next()) {
                        Object oPk = rowset.getObject(pkColIndex);
                        assertNotNull(oPk);
                        if (oPk instanceof Number) {
                            Long lPk = ((Number) oPk).longValue();
                            if (lPk.equals(FilteringTest.DLINA)) {
                                assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                            } else if (lPk.equals(FilteringTest.SILA_LIGHT)) {
                                assertEquals(0, state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().size());
                            }
                        }
                    }
                }
                return null;
            }
        });
    }

    @Test
    public void queringScriptEventsVsDataTest() throws Exception {
        System.out.println("queringScriptEventsVsDataTest");
        final DbClient client = initDevelopTestClient();
        ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Object run(Context cx) throws Exception {
                ApplicationDbModel model = BaseTest.modelFromStream(client, QueringTest.class.getResourceAsStream(MODEL_TEST_PATH));
                model.setScriptThis(BaseTest.getDummyScriptableObject());
                model.setRuntime(true);

                ModelState state = new ModelState(model);

                DataScriptEventsListener scriptListener = new DataScriptEventsListener(state.EDINICI_IZMERENIJA_PO_VELICHINE);
                EntityDataListener dataListener = new EntityDataListener();
                state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().addRowsetListener(dataListener);
                model.addScriptEventsListener(scriptListener);

                DataScriptEventsListener scriptListener1 = new DataScriptEventsListener(state.EDINICI_IZMERENIJA_PO_VELICHINE_1);
                EntityDataListener dataListener1 = new EntityDataListener();
                state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().addRowsetListener(dataListener1);
                model.addScriptEventsListener(scriptListener1);

                DataScriptEventsListener scriptListener2 = new DataScriptEventsListener(state.NAIMENOVANIA_SI_PO_VELICHINE);
                EntityDataListener dataListener2 = new EntityDataListener();
                state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().addRowsetListener(dataListener2);
                model.addScriptEventsListener(scriptListener2);

                Rowset rowset = state.IZMERJAEMIE_VELICHINI.getRowset();

                int pkColIndex = rowset.getFields().find("ID");
                dataListener.reset();
                scriptListener.reset();
                dataListener1.reset();
                scriptListener1.reset();
                dataListener2.reset();
                scriptListener2.reset();
                rowset.beforeFirst();
                assertEquals(dataListener.getEvents(), scriptListener.getEvents());
                assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                assertTrue(dataListener.getScrollEvents() >= scriptListener.getScrollEvents());
                assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                while (rowset.next()) {
                    Object oPk = rowset.getObject(pkColIndex);
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
                    assertEquals(dataListener.getEvents(), scriptListener.getEvents());
                    assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                    assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                    assertTrue(dataListener.getScrollEvents() >= scriptListener.getScrollEvents());
                    assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                    assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                }
                assertEquals(dataListener.getEvents(), scriptListener.getEvents());
                assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                assertTrue(dataListener.getScrollEvents() >= scriptListener.getScrollEvents());
                assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                rowset.first();
                assertEquals(dataListener.getEvents(), scriptListener.getEvents());
                assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                assertTrue(dataListener.getScrollEvents() >= scriptListener.getScrollEvents());
                assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
/////////////////////////////////////////

                state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().removeRowsetListener(dataListener);
                model.removeScriptEventsListener(scriptListener);

                dataListener1.reset();
                scriptListener1.reset();
                dataListener2.reset();
                scriptListener2.reset();
                rowset.beforeFirst();
                assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                while (rowset.next()) {
                    Object oPk = rowset.getObject(pkColIndex);
                    assertNotNull(oPk);
                    if (oPk instanceof Number) {
                        Long lPk = ((Number) oPk).longValue();
                        if (lPk.equals(FilteringTest.DLINA)) {
                            Rowset edRowset = state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset();
                            assertEquals(4, edRowset.size());
                            assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                            assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                            assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                            assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                            edRowset.beforeFirst();
                            assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                            assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                            assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                            assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                            while (edRowset.next()) {
                                assertEquals(4, state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().size());
                                assertEquals(0, state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().size());

                                assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                                assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                                assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                                assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                            }
                            assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                            assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                            assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                            assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                            edRowset.first();
                            assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                            assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                            assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                            assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                        }
                    }
                    assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                    assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                    assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                    assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                }
                assertEquals(dataListener1.getEvents(), scriptListener1.getEvents());
                assertEquals(dataListener2.getEvents(), scriptListener2.getEvents());
                assertTrue(dataListener1.getScrollEvents() >= scriptListener1.getScrollEvents());
                assertTrue(dataListener2.getScrollEvents() >= scriptListener2.getScrollEvents());
                rowset.first();
                return null;
            }
        });
    }
    EntityRefreshFilterDataListener listenerOf1Layer = null;
    EntityRefreshFilterDataListener listenerOf2Layer = null;
    EntityRefreshFilterDataListener listenerOf3Layer = null;

    @Test
    public void queringExecutingOrderTest() throws Exception {
        System.out.println("queringExecutingOrderTest");
        final DbClient client = initDevelopTestClient();
        ScriptUtils.inContext(new ScriptAction() {
            @Override
            public Object run(Context cx) throws Exception {
                try {
                    ApplicationDbModel model = BaseTest.modelFromStream(client, QueringTest.class.getResourceAsStream(MODEL_TEST_PATH));
                    model.setScriptThis(BaseTest.getDummyScriptableObject());
                    model.setRuntime(true);

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
                    state.IZMERJAEMIE_VELICHINI.getRowset().addRowsetListener(listenerOf1Layer);
                    state.VID_OBJECTA_REMONTA.getRowset().addRowsetListener(listenerOf1Layer);
                    state.MARKI_OBJECTOV_REMONTA.getRowset().addRowsetListener(listenerOf1Layer);
                    state.GRUPPA_OBJECTA_REMONTA_PO_RODITELU.getRowset().addRowsetListener(listenerOf1Layer);
                    // 2nd layer
                    state.EDINICI_IZMERENIJA_PO_VELICHINE.getRowset().addRowsetListener(listenerOf2Layer);
                    state.NAIMENOVANIA_SI_PO_VELICHINE.getRowset().addRowsetListener(listenerOf2Layer);
                    state.EDINICI_OBORUDOVANIJA_PO_MARKE.getRowset().addRowsetListener(listenerOf2Layer);
                    // 3rd layer
                    state.EDINICI_IZMERENIJA_PO_VELICHINE_1.getRowset().addRowsetListener(listenerOf3Layer);

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
