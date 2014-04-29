/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.eas.client.DatabasesClientWithResource;
import com.eas.client.DbClient;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.store.XmlDom2ApplicationModel;
import com.eas.client.settings.DbConnectionSettings;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author mg
 */
public class BaseTest {

    public static final String RESOURCES_PREFIX = "/com/eas/client/model/resources/";

    public static DatabasesClientWithResource initDevelopTestClient() throws Exception {
        DbConnectionSettings settings = new DbConnectionSettings();
        settings.setUrl("jdbc:oracle:thin:@asvr/adb");
        settings.setSchema("eas");
        settings.setUser("eas");
        settings.setPassword("eas");
        settings.setMaxStatements(1);
        return new DatabasesClientWithResource(settings);
    }

    public static Document documentFromStream(InputStream is) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        return documentBuilder.parse(is);
    }

    public static Document documentFromString(String aData) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(aData));
        return documentBuilder.parse(is);
    }

    public static ApplicationDbModel modelFromStream(DbClient aClient, InputStream is) throws Exception {
        Document modelDoc = BaseTest.documentFromStream(is);
        ApplicationDbModel model = new ApplicationDbModel(aClient);
        model.accept(new XmlDom2ApplicationModel(modelDoc));
        return model;
    }

    public static ApplicationDbModel modelFromString(DbClient aClient, String sData) throws Exception {
        Document modelDoc = BaseTest.documentFromString(sData);
        ApplicationDbModel model = new ApplicationDbModel(aClient);
        model.accept(new XmlDom2ApplicationModel(modelDoc));
        return model;
    }

    @Test
    public void dummyTest() {
    }
}
