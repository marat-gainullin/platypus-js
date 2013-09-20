/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.eas.client.application.AppClient;
import com.eas.client.application.Base64;
import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * 
 * @author mg
 */
public abstract class ModelBaseTest extends GWTTestCase {

	public static final Set<String> eventsBeforeNames = new HashSet<String>();
	public static final Set<String> eventsAfterNames = new HashSet<String>();

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
	public static final String GWT_MODULE_NAME = "com.eas.client.model.Model";
	public static final String TEST_URI = "application";
	public static final String RESOURCES_PREFIX = "/com/eas/client/datamodel/resources/";
	public static final String DUMMY_HANDLER_NAME = "dummyHandler";
	public static final String MODEL_SCRIPT_SOURCE = "var t = 0; function " + DUMMY_HANDLER_NAME + "(){var yu = 75;}";

	public static AppClient initDevelopTestClient(String aModuleName) throws Exception {
		AppClient client = new AppClient(GWT.getModuleBaseURL().replace(aModuleName + ".JUnit/", TEST_URI)) {
			@Override
			protected void interceptRequest(XMLHttpRequest req) {
				String requestAuthSting = "Basic " + Base64.encode("testuser1".concat(":").concat("test"));
				req.setRequestHeader("authorization", requestAuthSting);
			}
		};
		AppClient.setInstance(client);
		return client;
	}

	public AppClient initDevelopTestClient() throws Exception {
		return initDevelopTestClient(getModuleName());
	}

	@Override
	public String getModuleName() {
		return GWT_MODULE_NAME;
	}

	public abstract void validate() throws Exception;

	public abstract Collection<String> queries();

}
