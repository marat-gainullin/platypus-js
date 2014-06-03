/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import java.util.Collection;
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
