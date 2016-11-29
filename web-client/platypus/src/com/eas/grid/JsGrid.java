package com.eas.grid;

public class JsGrid {
	
	public native static void init()/*-{
		
		function publishComponentProperties(aPublished){
			@com.eas.widgets.JsWidgets::publishComponentProperties(Lcom/eas/ui/PublishedComponent;)(aPublished);
		}
		
		function predefine(aDeps, aName, aDefiner){
			@com.eas.core.Predefine::predefine(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDeps, aName, aDefiner);
		}
		
		predefine([], 'forms/model-grid', function(){
			function ModelGrid() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof ModelGrid)) {
					throw  ' use  "new ModelGrid()" !';
				}
	
				var published = this;
				var injected = aComponent != null;
				aComponent = injected ? aComponent : @com.eas.grid.ModelGrid::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			@com.eas.grid.GridPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ModelGrid', ModelGrid);
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
				aComponent = injected ? aComponent : @com.eas.grid.columns.header.CheckHeaderNode::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
	        	aComponent.@com.eas.core.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
				return published;
			}
			@com.eas.grid.GridPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('CheckGridColumn', CheckGridColumn);
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
				aComponent = injected ? aComponent : @com.eas.grid.columns.header.RadioHeaderNode::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
	        	aComponent.@com.eas.core.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
				return published;
			}
			@com.eas.grid.GridPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('RadioGridColumn', RadioGridColumn);
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
				aComponent = injected ? aComponent : @com.eas.grid.columns.header.ServiceHeaderNode::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
	        	aComponent.@com.eas.core.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
				return published;
			}
			@com.eas.grid.GridPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ServiceGridColumn', ServiceGridColumn);
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
				aComponent = injected ? aComponent : @com.eas.grid.columns.header.ModelHeaderNode::new()(); 
				published.unwrap = function() {
					return aComponent;
				};
	        	aComponent.@com.eas.core.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
				return published;
			}
			@com.eas.grid.GridPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('ModelGridColumn', ModelGridColumn);
			return ModelGridColumn;
		});	
	}-*/;
}
