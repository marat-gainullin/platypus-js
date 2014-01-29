/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.store;

import com.eas.client.DbClient;
import com.eas.client.model.BaseTest;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.application.ApplicationParametersEntity;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.xml.dom.XmlDom2String;
import java.io.InputStream;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class SerialTest extends BaseTest {

    @Test
    public void logicalStabilityTest() throws Exception {
        System.out.println("serialization logical stability test");
        DbClient client = initDevelopTestClient();
        try {
            ApplicationDbModel model = modelFromResource(client);
            verifyModel(model);
            for (int i = 0; i < 100; i++) {
                String writtenString = model2String(model);
                model = modelFromString(client, writtenString);
                verifyModel(model);
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    @Test
    public void binaryStabilityTest() throws Exception {
        System.out.println("serialization binary stability test");
        DbClient client = initDevelopTestClient();
        try {
            ApplicationDbModel model = modelFromResource(client);
            verifyModel(model);
            for (int i = 0; i < 100; i++) {
                String writtenString = model2String(model);
                model = modelFromString(client, writtenString);
                verifyModel(model);
                String writtenString1 = model2String(model);
                assertEquals(writtenString.length(), writtenString1.length());
            }
        } finally {
            client.shutdown();
            GeneralResourceProvider.getInstance().unregisterDatasource("testDb");
        }
    }

    protected ApplicationDbModel modelFromResource(DbClient aClient) throws Exception {
        InputStream is = SerialTest.class.getResourceAsStream(BaseTest.RESOURCES_PREFIX + "formsDatamodel1.xml");
        return BaseTest.modelFromStream(aClient, is);
    }

    protected String model2String(ApplicationDbModel model) {
        return XmlDom2String.transform(model.toXML());
    }

    protected void verifyModel(ApplicationDbModel aModel) {
        ApplicationParametersEntity paramsEntity = aModel.getParametersEntity();
        assertNotNull(paramsEntity);// Parameters entity allways present
        int genericEntities = 0;
        for (ApplicationDbEntity entity : aModel.getEntities().values()) {
            if (entity instanceof ApplicationParametersEntity) {
                fail("Parameters entity shouldn't be in generic entities collection!");
            } else {
                genericEntities++;
            }
        }
        assertEquals(genericEntities, aModel.getEntities().size());
        assertEquals(aModel.getEntities().size(), 5);
        assertEquals(aModel.getRelations().size(), 4);
    }
}
