package com.eas.client.form.js;

public class JsModelWidgets {

	public native static void init()/*-{
		
		function publishComponentProperties(aPublished) {
			@com.eas.client.form.js.JsWidgets::publishComponentProperties(Lcom/eas/client/form/published/PublishedComponent;)(aPublished);
		}
		
		function publishModelComponentProperties(aPublished) {
		}
		
		// **************************************************************************
		$wnd.P.ModelGrid = function () {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			
			if (!(this instanceof $wnd.P.ModelGrid)) {
				throw  ' use  "new P.ModelGrid()" !';
			}

			var published = this;
			var injected = aComponent != null;
			aComponent = injected ? aComponent : @com.eas.client.form.published.widgets.model.ModelGrid::new()(); 
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			return published;
		};	
		
		// **************************************************************************
		$wnd.P.CheckGridColumn = function () {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			
			if (!(this instanceof $wnd.P.CheckGridColumn)) {
				throw  ' use  "new P.CheckGridColumn()" !';
			}

			var published = this;
			var injected = aComponent != null;
			aComponent = injected ? aComponent : @com.eas.client.form.grid.columns.header.CheckHeaderNode::new()(); 
			published.unwrap = function() {
				return aComponent;
			};
        	aComponent.@com.eas.client.form.published.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			return published;
		};	
		
		// **************************************************************************
		$wnd.P.RadioGridColumn = function () {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			
			if (!(this instanceof $wnd.P.RadioGridColumn)) {
				throw  ' use  "new P.RadioGridColumn()" !';
			}

			var published = this;
			var injected = aComponent != null;
			aComponent = injected ? aComponent : @com.eas.client.form.grid.columns.header.RadioHeaderNode::new()(); 
			published.unwrap = function() {
				return aComponent;
			};
        	aComponent.@com.eas.client.form.published.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			return published;
		};	
		
		// **************************************************************************
		$wnd.P.ServiceGridColumn = function () {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			
			if (!(this instanceof $wnd.P.ServiceGridColumn)) {
				throw  ' use  "new P.ServiceGridColumn()" !';
			}

			var published = this;
			var injected = aComponent != null;
			aComponent = injected ? aComponent : @com.eas.client.form.grid.columns.header.ServiceHeaderNode::new()(); 
			published.unwrap = function() {
				return aComponent;
			};
        	aComponent.@com.eas.client.form.published.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			return published;
		};	
		
		// **************************************************************************
		$wnd.P.ModelGridColumn = function () {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			
			if (!(this instanceof $wnd.P.ModelGridColumn)) {
				throw  ' use  "new P.ModelGridColumn()" !';
			}

			var published = this;
			var injected = aComponent != null;
			aComponent = injected ? aComponent : @com.eas.client.form.grid.columns.header.ModelHeaderNode::new()(); 
			published.unwrap = function() {
				return aComponent;
			};
        	aComponent.@com.eas.client.form.published.HasPublished::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			return published;
		};	
		
		// **************************************************************************
		$wnd.P.ModelImage = function () {
			throw 'P.ModelImage is unsupported in Html5 client'; 
		}
		// **************************************************************************
		$wnd.P.ModelCheckBox = function (aText) {
			var aComponent = arguments.length > 1 ? arguments[1] : null;
			
			if (!(this instanceof $wnd.P.ModelCheckBox)) {
				throw  ' use  "new P.ModelCheckBox()" !';
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
		};	

		// **************************************************************************
		$wnd.P.ModelFormattedField = function () {
			var aComponent = arguments.length>0?arguments[0]:null;
			
			if (!(this instanceof $wnd.P.ModelFormattedField)) {
				throw  ' use  "new P.ModelFormattedField()" !';
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
		};	

		// **************************************************************************
		$wnd.P.ModelTextArea = function () {
			var aComponent = arguments.length>0?arguments[0]:null;
			
			if (!(this instanceof $wnd.P.ModelTextArea)) {
				throw  ' use  "new P.ModelTextArea()" !';
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
		};	

		// **************************************************************************
		$wnd.P.ModelDate = function () {
			var aComponent = arguments.length>0?arguments[0]:null;
			
			if (!(this instanceof $wnd.P.ModelDate)) {
				throw  ' use  "new P.ModelDate()" !';
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
		};	

		// **************************************************************************
		$wnd.P.ModelSpin = function () {
			var aComponent = arguments.length>0?arguments[0]:null;
			
			if (!(this instanceof $wnd.P.ModelSpin)) {
				throw  ' use  "new P.ModelSpin()" !';
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
		};	

		// **************************************************************************
		$wnd.P.ModelCombo = function () {
			var aComponent = arguments.length>0?arguments[0]:null;
			
			if (!(this instanceof $wnd.P.ModelCombo)) {
				throw  ' use  "new P.ModelCombo()" !';
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
		};	

	}-*/;

}
