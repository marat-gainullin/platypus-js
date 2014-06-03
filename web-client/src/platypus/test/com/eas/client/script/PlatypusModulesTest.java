package com.eas.client.script;

import java.util.Collections;

import com.eas.client.RunnableAdapter;
import com.eas.client.application.AppClient;
import com.eas.client.application.Application;
import com.eas.client.application.Loader;
import com.eas.client.model.ModelBaseTest;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

public abstract class PlatypusModulesTest extends GWTTestCase {
	public static final String GWT_MODULE_NAME = "com.eas.client.application.Main";

	protected boolean onSuccessCalled;

	@Override
	public String getModuleName() {
		return GWT_MODULE_NAME;
	}

	public void onSuccess(int aValue) {
		onSuccessCalled = true;
	}

	protected abstract String testsModuleName();
	
	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();
		delayTestFinish(60 * 60 * 1000);
		final AppClient client = ModelBaseTest.initDevelopTestClient(getModuleName());
		Application.publish(client);
		Loader loader = new Loader(client);
		loader.load(Collections.singleton(testsModuleName()), new RunnableAdapter() {

			protected native JavaScriptObject bind(PlatypusModulesTest aRunner, String aModuleName)/*-{
				window.Logger = {info:function(aMessage){}, severe : function(aMessage){}, warning : function(aMessage){},
				fine:function(aMessage){}, finer : function(aMessage){}, finest : function(aMessage){}};
				$wnd.P.Logger = window.Logger;
				var constr = $wnd[aModuleName];
				var instance = new constr();
				instance.onSuccess = function(aValue) {
					aRunner.@com.eas.client.script.PlatypusModulesTest::onSuccess(I)(aValue);
				}
				return instance;
			}-*/;

			@Override
			protected void doWork() throws Exception {
				JavaScriptObject instance = bind(PlatypusModulesTest.this, testsModuleName());
				instance = null;
			}

		});
		Timer timer = new Timer() {
			public void run() {
				// check test's internal counters
				try {
					validate();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// tell the test system the test is now done
				finishTest();
			}
		};
		// Schedule the event and return control to the test system.
		timer.schedule(60 * 1000);
	}

	protected void validate() {
		assertTrue(onSuccessCalled);
	}

	public void testStart() {
	}
}
