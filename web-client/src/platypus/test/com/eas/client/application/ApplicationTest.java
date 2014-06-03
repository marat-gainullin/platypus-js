package com.eas.client.application;

import com.google.gwt.junit.client.GWTTestCase;

public class ApplicationTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "com.eas.client.application.Main";
	}

	/*
	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();
		delayTestFinish(60 * 60 * 1000);
		Timer timer = new Timer() {
			public void run() {
				// check test's internal counters
				// tell the test system the test is now done
				finishTest();
			}
		};
		// Schedule the event and return control to the test system.
		timer.schedule(60 * 10 * 1000);
	}

	public void testScriptableRowsetSyntaxTest() throws Exception {
		final AppClient client = ModelBaseTest.initDevelopTestClient(getModuleName());
		Map<String, Element> appElements = new HashMap();
		appElements.put("DatamodelAPI", null);
		Application.run(client, appElements);// Scriptable rowset syntax
		                                     // test platypus module.
	}
	*/
	
	public void testScriptableRowsetSyntaxTest() throws Exception {
		System.out.println("This test will be resumed after migragion to gwt2.5");
		
	}
}
