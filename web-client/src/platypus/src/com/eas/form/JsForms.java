package com.eas.form;

public class JsForms {
	
	public native static void init()/*-{
		
		function predefine(aDeps, aName, aDefiner){
			@com.eas.core.Predefine::predefine(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDeps, aName, aDefiner);
		}
		
		predefine(['boxing', 'forms/form'], 'forms', function(B, Form){
			function readFormDocument(aDocumnet, aModuleName, aModel, aTarget){
				var factory = @com.eas.form.store.XmlDom2Form::transform(Lcom/google/gwt/xml/client/Document;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDocumnet, aModuleName, aModel);
				if(factory){
					var nativeForm = factory.@com.eas.form.FormFactory::getForm()(); 
					if(aTarget)
						Form.call(aTarget, null, null, nativeForm);
					else
						aTarget = new Form(null, null, nativeForm);
					var nwList = factory.@com.eas.form.FormFactory::getWidgetsList()();
					for(var i = 0; i < nwList.@java.util.List::size()(); i++){
						var nWidget = nwList.@java.util.List::get(I)(i);
						var pWidget = nWidget.@com.eas.core.HasPublished::getPublished()();
						if(pWidget.name){
							aTarget[pWidget.name] = pWidget;
						}
					}
					return aTarget;
				}else{
					return null;
				}
			}
			function loadForm(aModuleName, aModel, aTarget) {
				var aClient = @com.eas.client.AppClient::getInstance()();
				var appElementDoc = aClient.@com.eas.client.AppClient::getFormDocument(Ljava/lang/String;)(aModuleName);
				var form = readFormDocument(appElementDoc, aModuleName, aModel, aTarget);
	            if (!form.title)
	                form.title = appElementName;
	            form.formKey = appElementName;
	            return form;
			}
			function readForm(aFormContent, aModel, aTarget){
				var doc = @com.google.gwt.xml.client.XMLParser::parse(Ljava/lang/String;)(aFormContent + "");
				return readFormDocument(doc, null, aModel, aTarget);
			}
			
			var module = {};
			Object.defineProperty(module, 'loadForm', {
				enumerable: true,
				value : loadForm
			});
			Object.defineProperty(module, 'readForm', {
				enumerable: true,
				value : readForm
			});
			return module;
		});
	}-*/;
}
