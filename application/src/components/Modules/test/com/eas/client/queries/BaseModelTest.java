/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

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
public class BaseModelTest {

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
/*
    public static ApplicationDbModel modelFromStream(DatabasesClient aClient, InputStream is) throws Exception {
        Document modelDoc = BaseModelTest.documentFromStream(is);
        ApplicationDbModel model = new ApplicationDbModel(aClient);
        model.accept(new XmlDom2ApplicationModel(modelDoc));
        return model;
    }

    public static ApplicationDbModel modelFromString(DatabasesClient aClient, String sData) throws Exception {
        Document modelDoc = BaseModelTest.documentFromString(sData);
        ApplicationDbModel model = new ApplicationDbModel(aClient);
        model.accept(new XmlDom2ApplicationModel(modelDoc));
        return model;
    }
*/
    @Test
    public void dummyTest() {
    }
}
