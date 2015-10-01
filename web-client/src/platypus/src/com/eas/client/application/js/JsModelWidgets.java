package com.eas.client.application.js;

public class JsModelWidgets {

	public native static void init()/*-{
		
		function publishComponentProperties(aPublished) {
			@com.eas.client.application.js.JsWidgets::publishComponentProperties(Lcom/eas/client/form/published/PublishedComponent;)(aPublished);
		}
		
		function publishModelComponentProperties(aPublished) {
		}
		
		function predefine(aDeps, aName, aDefiner){
			var resolved = [];
			for(var d = 0; d < aDeps.length; d++){
				var module = @com.eas.client.application.Application::prerequire(Ljava/lang/String;)(aDeps[d]);
				resolved.push(module);
			}
			@com.eas.client.application.Application::predefine(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aName, aDefiner(resolved));
		}
		predefine([], 'forms/model-grid', function(){
			function ModelGrid() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof ModelGrid)) {
					throw  ' use  "new ModelGrid()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.client.form.published.widgets.model.ModelGrid::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			return ModelGrid;
		});		

		predefine([], 'forms/check-grid-column', function(){
			function CheckGridColumn() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof CheckGridColumn)) {
					throw  ' use  "new CheckGridColumn()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.client.form.grid.columns.header.CheckHeaderNode::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
	        	aComponent.@com.eas.client.form.published.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
				return published;
			}
			return CheckGridColumn;
		});	
		
		predefine([], 'forms/radio-grid-column', function(){
			function RadioGridColumn() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof RadioGridColumn)) {
					throw  ' use  "new RadioGridColumn()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.client.form.grid.columns.header.RadioHeaderNode::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
	        	aComponent.@com.eas.client.form.published.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
				return published;
			}
			return RadioGridColumn;	
		});
				
		predefine([], 'forms/service-grid-column', function(){
			function ServiceGridColumn() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof ServiceGridColumn)) {
					throw  ' use  "new ServiceGridColumn()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.client.form.grid.columns.header.ServiceHeaderNode::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
	        	aComponent.@com.eas.client.form.published.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
				return published;
			}
			return ServiceGridColumn;
		});	
		
		predefine([], 'forms/model-grid-column', function(){
			function ModelGridColumn() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof ModelGridColumn)) {
					throw  ' use  "new ModelGridColumn()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.client.form.grid.columns.header.ModelHeaderNode::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
	        	aComponent.@com.eas.client.form.published.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
				return published;
			}
			return ModelGridColumn;
		});	
		
		predefine([], 'forms/model-check-box', function(){
			function ModelCheckBox(aText) {
				var aComponent = arguments.length > 1 ? arguments[1] : null;
				
				if (!(this instanceof ModelCheckBox)) {
					throw  ' use  "new ModelCheckBox()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.client.form.published.widgets.model.ModelCheck::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
	
				if(!injected){
					published.text = aText;
				}
				return published;
			}
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
				aComponent = injected ? aComponent : @com.eas.client.form.published.widgets.model.ModelFormattedField::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishModelComponentProperties(published);
				return published;
			}
			return ModelFormattedField;	
		});
		
		predefine([], 'forms/model-text-aArea', function(){
			function ModelTextArea() {
				var aComponent = arguments.length>0?arguments[0]:null;
				
				if (!(this instanceof ModelTextArea)) {
					throw  ' use  "new ModelTextArea()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.client.form.published.widgets.model.ModelTextArea::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishModelComponentProperties(published);
				return published;
			}
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
				aComponent = injected ? aComponent : @com.eas.client.form.published.widgets.model.ModelDate::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishModelComponentProperties(published);
				return published;
			}
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
				aComponent = injected ? aComponent : @com.eas.client.form.published.widgets.model.ModelSpin::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishModelComponentProperties(published);
				return published;
			}
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
				aComponent = injected ? aComponent : @com.eas.client.form.published.widgets.model.ModelCombo::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				publishModelComponentProperties(published);
				return published;
			}
			return ModelCombo;
		});	

	}-*/;

}
