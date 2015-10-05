package com.eas.form;

public class JsForm {
	
	public native static void init()/*-{
		
		function predefine(aDeps, aName, aDefiner){
			@com.eas.predefine.Predefine::predefine(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDeps, aName, aDefiner);
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
				aComponent.@com.eas.predefine.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
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
		
		predefine(['common-utils/color', 'common-utils/cursor', 'common-utils/font', 'forms/form'], 'ui', function(Color, Cursor, Font, Form){
			var Orientation = @com.eas.ui.JsUi::Orientation;
			var VerticalPosition = @com.eas.ui.JsUi::VerticalPosition;
			var HorizontalPosition = @com.eas.ui.JsUi::HorizontalPosition;
			var FontStyle = @com.eas.ui.JsUi::FontStyle;
			var ScrollBarPolicy = @com.eas.ui.JsUi::ScrollBarPolicy;
			
			function selectFile(aCallback, aFileFilter) {
				@com.eas.widgets.WidgetsUtils::jsSelectFile(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(aCallback, aFileFilter);
			}
			
			function selectColor(aCallback, aOldValue) {
				@com.eas.widgets.WidgetsUtils::jsSelectColor(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aOldValue != null ? aOldValue + '' : '', aCallback);
			}
			
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
					var pWidget = nWidget.@com.eas.predefine.HasPublished::getPublished()();
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
			
			function Icon() {
			}
			Object.defineProperty(Icon, "load", { 
				value: function(aIconName, aOnSuccess, aOnFailure) {
					@com.eas.ui.PlatypusImageResource::jsLoad(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(aIconName != null ? '' + aIconName : null, aOnSuccess, aOnFailure);
				} 
			});
			
		    Object.defineProperty(module, 'Colors', {
		        enumerable: true,
		        value: Color
		    });
		    Object.defineProperty(module, 'Color', {
		        enumerable: true,
		        value: Color
		    });
		    Object.defineProperty(module, 'Cursor', {
		        enumerable: true,
		        value: Cursor
		    });
		    Object.defineProperty(module, 'Icon', {
		        enumerable: true,
		        value: Icon
		    });
		    Object.defineProperty(module, 'Icons', {
		        enumerable: true,
		        value: Icon
		    });
		    Object.defineProperty(module, 'Font', {
		        enumerable: true,
		        value: Font
		    });
			Object.defineProperty(module, 'VK_ALT', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_ALT 
			});
			Object.defineProperty(module, 'VK_BACKSPACE', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_BACKSPACE 
			});
			Object.defineProperty(module, 'VK_BACKSPACE', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_BACKSPACE 
			});
			Object.defineProperty(module, 'VK_DELETE', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_DELETE 
			});
			Object.defineProperty(module, 'VK_DOWN', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_DOWN 
			});
			Object.defineProperty(module, 'VK_END', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_END 
			});
			Object.defineProperty(module, 'VK_ENTER', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_ENTER 
			});
			Object.defineProperty(module, 'VK_ESCAPE', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_ESCAPE 
			});
			Object.defineProperty(module, 'VK_HOME', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_HOME 
			});
			Object.defineProperty(module, 'VK_LEFT', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_LEFT
			});
			Object.defineProperty(module, 'VK_PAGEDOWN', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_PAGEDOWN 
			});
			Object.defineProperty(module, 'VK_PAGEUP', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_PAGEUP 
			});
			Object.defineProperty(module, 'VK_RIGHT', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_RIGHT 
			});
			Object.defineProperty(module, 'VK_SHIFT', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_SHIFT 
			});
			Object.defineProperty(module, 'VK_TAB', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_TAB 
			});
			Object.defineProperty(module, 'VK_UP', {
				enumerable: true,
				value : @com.google.gwt.event.dom.client.KeyCodes::KEY_UP 
			});
			Object.defineProperty(module, 'selectFile', {
				enumerable: true,
				value : selectFile
			});
			Object.defineProperty(module, 'selectColor', {
				enumerable: true,
				value : selectColor
			});
		    Object.defineProperty(module, 'msgBox', {
		        enumerable: true,
		        value: $wnd.alert
		    });
		    Object.defineProperty(module, 'error', {
		        enumerable: true,
		        value: $wnd.alert
		    });
		    Object.defineProperty(module, 'warn', {
		        enumerable: true,
		        value: $wnd.alert
		    });
		    Object.defineProperty(module, 'HorizontalPosition', {
		        enumerable: true,
		        value: HorizontalPosition
		    });
		    Object.defineProperty(module, 'VerticalPosition', {
		        enumerable: true,
		        value: VerticalPosition
		    });
		    Object.defineProperty(module, 'Orientation', {
		        enumerable: true,
		        value: Orientation
		    });
		    Object.defineProperty(module, 'ScrollBarPolicy', {
		        enumerable: true,
		        value: ScrollBarPolicy
		    });
		    Object.defineProperty(module, 'FontStyle', {
		        enumerable: true,
		        value: FontStyle
		    });
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
