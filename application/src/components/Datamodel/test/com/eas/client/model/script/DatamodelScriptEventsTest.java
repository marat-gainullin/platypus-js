/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.script;

import com.bearsoft.rowset.Rowset;
import com.eas.client.DbClient;
import com.eas.client.model.BaseTest;
import com.eas.client.model.Model;
import com.eas.client.model.ModelScriptEventsListener;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.application.ApplicationEntity.EntityInstanceChangeEvent;
import com.eas.client.resourcepool.GeneralResourceProvider;
import static org.junit.Assert.*;
import org.junit.Test;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/**
 *
 * @author mg
 */
public class DatamodelScriptEventsTest extends BaseTest {

    int beforeScrollEnqueuedCount = 0;
    int beforeChangeEnqueuedCount = 0;
    int beforeInsertEnqueuedCount = 0;
    int beforeDeleteEnqueuedCount = 0;
    int afterScrollCount = 0;
    int afterChangeCount = 0;
    int afterInsertCount = 0;
    int afterDeleteCount = 0;
    int afterFilterCount = 0;
    int afterRequeryCount = 0;

    @Test
    public void entityScriptEventsTest() throws Exception {
        System.out.println("Testing rowset's data editing and rowset scrolling to script events connection");
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            ApplicationDbModel model = new ApplicationDbModel(client);
            final ApplicationDbEntity entity11 = model.newGenericEntity();
            model.addEntity(entity11);
            entity11.setQueryId("128015347915605");

            final String stringValue2UpdateWith = "newString";
            final String string2Insert2 = "insertedString2";
            final String string2Insert3 = "insertedString3";

            beforeScrollEnqueuedCount = 0;
            beforeChangeEnqueuedCount = 0;
            beforeInsertEnqueuedCount = 0;
            beforeDeleteEnqueuedCount = 0;

            afterScrollCount = 0;
            afterChangeCount = 0;
            afterInsertCount = 0;
            afterDeleteCount = 0;
            afterFilterCount = 0;
            afterRequeryCount = 0;

            model.addScriptEventsListener(new ModelScriptEventsListener() {

                @Override
                public void eventEnqueueing(ScriptEvent anEvent) {
                    assertTrue(anEvent.getEntity() == entity11);
                    BaseFunction eventHandler = (BaseFunction) anEvent.getHandler();
                    assertFalse(BaseTest.eventsBeforeNames.contains(eventHandler.getFunctionName()));
                }

                @Override
                public void eventExecuting(ScriptEvent anEvent) {
                    assertTrue(anEvent.getEntity() == entity11);
                    BaseFunction eventHandler = (BaseFunction) anEvent.getHandler();
                    String eventName = eventHandler.getFunctionName();
                    assertTrue(BaseTest.eventsBeforeNames.contains(eventName) || BaseTest.eventsAfterNames.contains(eventName));
                    switch (eventName) {
                        case Model.DATASOURCE_BEFORE_SCROLL_EVENT_TAG_NAME:
                            beforeScrollEnqueuedCount++;
                            break;
                        case Model.DATASOURCE_BEFORE_CHANGE_EVENT_TAG_NAME:
                            beforeChangeEnqueuedCount++;
                            assertTrue(anEvent.getEvent() instanceof EntityInstanceChangeEvent);
                            EntityInstanceChangeEvent chEvent = (EntityInstanceChangeEvent) anEvent.getEvent();
                            assertNotNull(anEvent.getEvent());
                            assertNotNull(chEvent.getOldValue());
                            assertEquals(chEvent.getNewValue(), stringValue2UpdateWith);
                            break;
                        case Model.DATASOURCE_BEFORE_INSERT_EVENT_TAG_NAME:
                            beforeInsertEnqueuedCount++;
                            break;
                        case Model.DATASOURCE_BEFORE_DELETE_EVENT_TAG_NAME:
                            beforeDeleteEnqueuedCount++;
                            break;
                        case Model.DATASOURCE_AFTER_SCROLL_EVENT_TAG_NAME:
                            afterScrollCount++;
                            assertEquals(afterScrollCount, beforeScrollEnqueuedCount);
                            break;
                        case Model.DATASOURCE_AFTER_CHANGE_EVENT_TAG_NAME:
                            afterChangeCount++;
                            assertTrue(anEvent.getEvent() instanceof EntityInstanceChangeEvent);
                            EntityInstanceChangeEvent chEvent1 = (EntityInstanceChangeEvent) anEvent.getEvent();
                            assertNotNull(chEvent1.getOldValue());
                            assertEquals(chEvent1.getNewValue(), stringValue2UpdateWith);
                            assertEquals(afterChangeCount, beforeChangeEnqueuedCount);
                            break;
                        case Model.DATASOURCE_AFTER_INSERT_EVENT_TAG_NAME:
                            afterInsertCount++;
                            assertEquals(afterInsertCount, beforeInsertEnqueuedCount);
                            break;
                        case Model.DATASOURCE_AFTER_DELETE_EVENT_TAG_NAME:
                            afterDeleteCount++;
                            assertEquals(afterDeleteCount, beforeDeleteEnqueuedCount);
                            break;
                        case Model.DATASOURCE_AFTER_FILTER_EVENT_TAG_NAME:
                            afterFilterCount++;
                            break;
                        case Model.DATASOURCE_AFTER_REQUERY_EVENT_TAG_NAME:
                            afterRequeryCount++;
                            break;
                        default:
                            assertTrue(false);
                            break;
                    }
                }
            });

            entity11.setOnBeforeScroll(getDummyHandler(Model.DATASOURCE_BEFORE_SCROLL_EVENT_TAG_NAME));
            entity11.setOnBeforeChange(getDummyHandler(Model.DATASOURCE_BEFORE_CHANGE_EVENT_TAG_NAME));
            entity11.setOnBeforeInsert(getDummyHandler(Model.DATASOURCE_BEFORE_INSERT_EVENT_TAG_NAME));
            entity11.setOnBeforeDelete(getDummyHandler(Model.DATASOURCE_BEFORE_DELETE_EVENT_TAG_NAME));

            entity11.setOnAfterScroll(getDummyHandler(Model.DATASOURCE_AFTER_SCROLL_EVENT_TAG_NAME));
            entity11.setOnAfterChange(getDummyHandler(Model.DATASOURCE_AFTER_CHANGE_EVENT_TAG_NAME));
            entity11.setOnAfterInsert(getDummyHandler(Model.DATASOURCE_AFTER_INSERT_EVENT_TAG_NAME));
            entity11.setOnAfterDelete(getDummyHandler(Model.DATASOURCE_AFTER_DELETE_EVENT_TAG_NAME));

            entity11.setOnFiltered(getDummyHandler(Model.DATASOURCE_AFTER_FILTER_EVENT_TAG_NAME));
            entity11.setOnRequeried(getDummyHandler(Model.DATASOURCE_AFTER_REQUERY_EVENT_TAG_NAME));

            ContextFactory cf = ContextFactory.getGlobal();
            Context cx = cf.enterContext();
            try {
                model.setScriptThis(BaseTest.getDummyScriptableObject());
                model.setRuntime(true);
                Rowset rowset = entity11.getRowset();
                // scrolls and changes
                int rowCount = rowset.size();
                rowset.beforeFirst();
                while (rowset.next()) {
                    rowset.updateObject(2, stringValue2UpdateWith);
                }
                assertEquals(beforeScrollEnqueuedCount, afterScrollCount);
                assertEquals(beforeScrollEnqueuedCount, rowCount + 2);
                assertEquals(afterScrollCount, rowCount + 2);// There are two additional scrolls:
                // scroll to before position while beforeFirst() call
                // and scroll to after last position while last next() call

                int insertCount = 3;
                // insert 1
                rowset.insert();
                // insert 2
                rowset.insert(new Object[]{2, string2Insert2});
                // insert 3
                rowset.insert(new Object[]{2, string2Insert3});

                // deletes
                while (rowset.size() > 0) {
                    assertTrue(rowset.first());
                    rowset.delete();
                }

                entity11.refresh();

                assertEquals(beforeScrollEnqueuedCount, rowCount + 3);

                assertEquals(beforeChangeEnqueuedCount, rowCount);
                assertEquals(beforeInsertEnqueuedCount, insertCount);
                assertEquals(beforeDeleteEnqueuedCount, insertCount + rowCount);

                assertEquals(afterScrollCount, rowCount + 3);
                assertEquals(beforeScrollEnqueuedCount, afterScrollCount);

                assertEquals(afterChangeCount, rowCount);
                assertEquals(afterInsertCount, insertCount);
                assertEquals(afterDeleteCount, insertCount + rowCount);
                assertEquals(afterFilterCount, 0);
                // The initial query and refresh call
                assertEquals(afterRequeryCount, 2);
            } finally {
                Context.exit();
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }
}
