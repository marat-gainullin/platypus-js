/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.script;

import com.eas.client.model.script.ScriptableRowset;
import com.eas.client.DbClient;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.BaseTest;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.script.ScriptUtils;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author mg
 */
public class SubstitutingTest extends BaseTest {

    @Test
    public void dataSubstitutingTest() throws Exception {
        System.out.println("dataSubstitutingTest");
        DbClient client = BaseTest.initDevelopTestClient();
        try {
            ApplicationDbModel dm = new ApplicationDbModel(client);
            ApplicationDbEntity entity1 = dm.newGenericEntity();
            entity1.setQueryId("128015347915605");
            entity1.setName("entity1");

            ApplicationDbEntity entity2 = dm.newGenericEntity();
            entity2.setQueryId("128015347915605");
            entity2.setName("entity2");

            dm.addEntity(entity1);
            dm.addEntity(entity2);

            ContextFactory cf = ContextFactory.getGlobal();
            Context cx = cf.enterContext();
            try {
                ScriptableObject scope = ScriptUtils.getScope();
                dm.setScriptThis(scope);
                dm.setRuntime(true);

                Scriptable jsSr1 = (Scriptable) scope.get("entity1", scope);
                Scriptable jsSr2 = (Scriptable) scope.get("entity2", scope);

                ScriptableRowset sr1 = (ScriptableRowset) ScriptUtils.js2Java(jsSr1);
                ScriptableRowset sr2 = (ScriptableRowset) ScriptUtils.js2Java(jsSr2);

                jsSr1.put("substitute", scope, jsSr2);
                sr1.first();
                sr2.first();

                String substitutedField = "NAME";

                Object value = jsSr1.get(substitutedField, scope);
                assertNotNull(value);
                jsSr1.put(substitutedField, scope, null);
                value = jsSr1.get(substitutedField, scope);
                assertNotNull(value);
                jsSr2.put(substitutedField, scope, null);
                value = jsSr1.get(substitutedField, scope);
                assertNull(value);
            } finally {
                Context.exit();
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }
}
