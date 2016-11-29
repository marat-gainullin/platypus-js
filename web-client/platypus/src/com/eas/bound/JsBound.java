package com.eas.bound;

public class JsBound {
	
	public native static void init()/*-{
		
		function publishComponentProperties(aPublished){
			@com.eas.widgets.JsWidgets::publishComponentProperties(Lcom/eas/ui/PublishedComponent;)(aPublished);
		}
		
		function predefine(aDeps, aName, aDefiner){
			@com.eas.core.Predefine::predefine(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDeps, aName, aDefiner);
		}
		
		predefine([], 'forms/model-check-box', function(){
			function ModelCheckBox(aText) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				
				if (!(this instanceof ModelCheckBox)) {
					throw  ' use  "new ModelCheckBox()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.bound.ModelCheck::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
	
				if(!injected){
					published.text = aText;
				}
				return published;
			}
			@com.eas.bound.BoundPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ModelCheckBox', ModelCheckBox);
			return ModelCheckBox;	
		});
		
		predefine([], 'forms/model-formatted-field', function(){
			function ModelFormattedField() {
				var aComponent = arguments.length>0?arguments[0]:null;
				
				if (!(this instanceof ModelFormattedField)) {
					throw  ' use  "new ModelFormattedField()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.bound.ModelFormattedField::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			@com.eas.bound.BoundPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ModelFormattedField', ModelFormattedField);
			return ModelFormattedField;	
		});
		
		predefine([], 'forms/model-text-area', function(){
			function ModelTextArea() {
				var aComponent = arguments.length>0?arguments[0]:null;
				
				if (!(this instanceof ModelTextArea)) {
					throw  ' use  "new ModelTextArea()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.bound.ModelTextArea::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			@com.eas.bound.BoundPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ModelTextArea', ModelTextArea);
			return ModelTextArea;
		});	

		predefine([], 'forms/model-date', function(){
			function ModelDate() {
				var aComponent = arguments.length>0?arguments[0]:null;
				
				if (!(this instanceof ModelDate)) {
					throw  ' use  "new ModelDate()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.bound.ModelDate::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			@com.eas.bound.BoundPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ModelDate', ModelDate);
			return ModelDate;
		});	

		predefine([], 'forms/model-spin', function(){
			function ModelSpin() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof ModelSpin)) {
					throw  ' use  "new ModelSpin()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.bound.ModelSpin::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			@com.eas.bound.BoundPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ModelSpin', ModelSpin);
			return ModelSpin;
		});	

		predefine([], 'forms/model-combo', function() {
			function ModelCombo() {
				var aComponent = arguments.length>0?arguments[0]:null;
				
				if (!(this instanceof ModelCombo)) {
					throw  ' use  "new ModelCombo()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.bound.ModelCombo::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			@com.eas.bound.BoundPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ModelCombo', ModelCombo);
			return ModelCombo;
		});	
	}-*/;
}
