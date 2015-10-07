package com.eas.form;

public class JsForm {
	
	public native static void init()/*-{
		
		function predefine(aDeps, aName, aDefiner){
			@com.eas.core.Predefine::predefine(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDeps, aName, aDefiner);
		}
		
		predefine([], 'forms/form', function(Form){
			function Form(aView, aFormKey){
				var aComponent = arguments.length > 2 ? arguments[2] : null;
	
				var published = this;
				if(!aComponent){ 
					if(aView)
						aComponent = @com.eas.form.PlatypusWindow::new(Lcom/google/gwt/user/client/ui/Widget;)(aView.unwrap());
					else
						aComponent = @com.eas.form.PlatypusWindow::new()();
				} 	
				published.unwrap = function() {
					return aComponent;
				};
				if(aFormKey){
					aComponent.@com.eas.form.PlatypusWindow::setFormKey(Ljava/lang/String;)(aFormKey);
				}
				aComponent.@com.eas.core.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			Form.getShownForm = function(aFormKey){
				return @com.eas.form.PlatypusWindow::getShownForm(Ljava/lang/String;)(aFormKey);
			};
			
			Object.defineProperty(Form, "shown", {
				get : function() {
					return @com.eas.form.PlatypusWindow::getShownForms()();
				}
			});
			Object.defineProperty(Form, "onChange", {
				get : function() {
					return @com.eas.form.PlatypusWindow::getOnChange()();
				},
				set : function(aValue) {
					@com.eas.form.PlatypusWindow::setOnChange(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				}
			});
			return Form;
		});		
		
		predefine(['boxing', 'forms/form', 'forms/index', 'grid/index', 'ui'], 'forms', function(B, Form, FormsIndex, GridIndex, Ui){
			function readFormDocument(aDocumnet, aModel, aTarget){
				var factory = @com.eas.form.store.XmlDom2Form::transform(Lcom/google/gwt/xml/client/Document;Lcom/google/gwt/core/client/JavaScriptObject;)(aDocumnet, aModel);
				var nativeForm = factory.@com.eas.form.FormFactory::getForm()(); 
				if(aTarget)
					Form.call(aTarget, null, null, nativeForm);
				else
					aTarget = new Form(null, null, nativeForm);
				var nwList = factory.@com.eas.form.FormFactory::getWidgetsList()();
				for(var i = 0; i < nwList.@java.util.List::size()(); i++){
					var nWidget = nwList.@java.util.List::get(I)(i);
					var pWidget = nWidget.@com.eas.core.HasPublished::getPublished()();
					if(pWidget.name)
						aTarget[pWidget.name] = pWidget;
				}
				return aTarget;
			}
			function loadForm(appElementName, aModel, aTarget) {
				var appElementDoc = aClient.@com.eas.client.AppClient::getFormDocument(Ljava/lang/String;)(appElementName);
				var form = readFormDocument(appElementDoc, aModel, aTarget);
	            if (!form.title)
	                form.title = appElementName;
	            form.formKey = appElementName;
	            return form;
			}
			function readForm(aFormContent, aModel, aTarget){
				var doc = @com.google.gwt.xml.client.XMLParser::parse(Ljava/lang/String;)(aFormContent + "");
				return readFormDocument(doc, aModel, aTarget);
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
		    for (var f in FormsIndex) {
		        Object.defineProperty(module, f, {
		            enumerable: true,
		            value: FormsIndex[f]
		        });
		    }
		    for (var g in GridIndex) {
		        Object.defineProperty(module, g, {
		            enumerable: true,
		            value: GridIndex[g]
		        });
		    }
		    for (var u in Ui) {
		        Object.defineProperty(module, u, {
		            enumerable: true,
		            value: Ui[u]
		        });
		    }
			return module;
		});
	}-*/;
}
