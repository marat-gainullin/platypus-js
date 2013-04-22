/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.eas.client.DatabasesClient;
import com.eas.client.DbClient;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.client.model.store.XmlDom2ApplicationModel;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.script.ScriptUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author mg
 */
public class BaseTest {

    public static final Set<String> eventsBeforeNames = new HashSet<>();
    public static final Set<String> eventsAfterNames = new HashSet<>();

    static {
        eventsBeforeNames.add(Model.DATASOURCE_BEFORE_SCROLL_EVENT_TAG_NAME);
        eventsBeforeNames.add(Model.DATASOURCE_BEFORE_CHANGE_EVENT_TAG_NAME);
        eventsBeforeNames.add(Model.DATASOURCE_BEFORE_INSERT_EVENT_TAG_NAME);
        eventsBeforeNames.add(Model.DATASOURCE_BEFORE_DELETE_EVENT_TAG_NAME);

        eventsAfterNames.add(Model.DATASOURCE_AFTER_SCROLL_EVENT_TAG_NAME);
        eventsAfterNames.add(Model.DATASOURCE_AFTER_CHANGE_EVENT_TAG_NAME);
        eventsAfterNames.add(Model.DATASOURCE_AFTER_INSERT_EVENT_TAG_NAME);
        eventsAfterNames.add(Model.DATASOURCE_AFTER_DELETE_EVENT_TAG_NAME);
        eventsAfterNames.add(Model.DATASOURCE_AFTER_FILTER_EVENT_TAG_NAME);
        eventsAfterNames.add(Model.DATASOURCE_AFTER_REQUERY_EVENT_TAG_NAME);
    }
    public static final String RESOURCES_PREFIX = "/com/eas/client/model/resources/";
    public static final String DUMMY_HANDLER_NAME = "dummyHandler";
    public static final String MODEL_SCRIPT_SOURCE = "var t = 0; function " + DUMMY_HANDLER_NAME + "(){var yu = 75;}\n"
            + "function "+Model.DATASOURCE_BEFORE_SCROLL_EVENT_TAG_NAME+"(){}\n"
            + "function "+Model.DATASOURCE_BEFORE_CHANGE_EVENT_TAG_NAME+"(){}\n"
            + "function "+Model.DATASOURCE_BEFORE_INSERT_EVENT_TAG_NAME+"(){}\n"
            + "function "+Model.DATASOURCE_BEFORE_DELETE_EVENT_TAG_NAME+"(){}\n"
            + "function "+Model.DATASOURCE_AFTER_SCROLL_EVENT_TAG_NAME+"(){}\n"
            + "function "+Model.DATASOURCE_AFTER_CHANGE_EVENT_TAG_NAME+"(){}\n"
            + "function "+Model.DATASOURCE_AFTER_INSERT_EVENT_TAG_NAME+"(){}\n"
            + "function "+Model.DATASOURCE_AFTER_DELETE_EVENT_TAG_NAME+"(){}\n"
            + "function "+Model.DATASOURCE_AFTER_FILTER_EVENT_TAG_NAME+"(){}\n"
            + "function "+Model.DATASOURCE_AFTER_REQUERY_EVENT_TAG_NAME+"(){}\n";
    protected static ScriptableObject dummyScriptableObject = null;

    public static DbClient initDevelopTestClient() throws Exception {
        DbConnectionSettings settings = new DbConnectionSettings();
        settings.setUrl("jdbc:oracle:thin:@asvr/adb");
        Properties info = new Properties();
        info.put("schema", "eas");
        info.put("user", "eas");
        info.put("password", "eas");
        settings.setInfo(info);
        settings.setMaxStatements(1);
        return new DatabasesClient(settings);
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

    public static synchronized Function getDummyHandler(String aHandlerName) {
        return (Function) getDummyScriptableObject().get(aHandlerName, getDummyScriptableObject());
    }

    public static synchronized ScriptableObject getDummyScriptableObject() {
        if (dummyScriptableObject == null) {
            dummyScriptableObject = new ScriptableObject(ScriptUtils.getScope(), ScriptUtils.getScope()) {
                @Override
                public String getClassName() {
                    return "DummyTestScriptableObject";
                }
            };
            Context cx = ScriptUtils.enterContext();
            try {
                cx.setOptimizationLevel(-1);
                Script lScript = cx.compileString(MODEL_SCRIPT_SOURCE, "dummyTestScript", 0, null);
                lScript.exec(cx, dummyScriptableObject);
            } finally {
                Context.exit();
            }
        }
        return dummyScriptableObject;
    }

    @Test
    public void dummyTest() {
    }
}
