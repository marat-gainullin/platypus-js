package com.eas.client.form.js;

public class JsModelWidgets {

	public native static void init()/*-{
		
		function publishComponentProperties(aPublished) {
			@com.eas.client.form.js.JsWidgets::publishComponentProperties(Lcom/eas/client/form/published/PublishedComponent;)(aPublished);
		}
		
		function publishModelComponentProperties(aPublished) {
		}
		
		// **************************************************************************
		$wnd.ModelGrid = function () {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			
			if (!(this instanceof $wnd.ModelGrid)) {
				throw  ' use  "new ModelGrid()" !';
			}

			var published = this;
			var injected = aComponent != null;
			aComponent = injected ? aComponent : @com.eas.client.form.published.widgets.model.ModelGrid::new()(); 
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);

			if(!injected){
				aComponent.@com.eas.client.form.published.widgets.model.ModelGrid::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return published;
		};	
		
		// **************************************************************************
		$wnd.ModelCheckBox = function (aText) {
			var aComponent = arguments.length>1?arguments[1]:null;
			
			if (!(this instanceof $wnd.ModelCheckBox)) {
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
				aComponent.@com.eas.client.form.published.widgets.model.ModelCheck::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
				published.text = aText;
			}
			return published;
		};	

		// **************************************************************************
		$wnd.ModelFormattedField = function () {
			var aComponent = arguments.length>0?arguments[0]:null;
			
			if (!(this instanceof $wnd.ModelFormattedField)) {
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

			if(!injected) {
				aComponent.@com.eas.client.form.published.widgets.model.ModelFormattedField::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return published;
		};	

		// **************************************************************************
		$wnd.ModelTextArea = function () {
			var aComponent = arguments.length>0?arguments[0]:null;
			
			if (!(this instanceof $wnd.ModelTextArea)) {
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

			if(!injected) {
				aComponent.@com.eas.client.form.published.widgets.model.ModelTextArea::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return published;
		};	

		// **************************************************************************
		$wnd.ModelDate = function () {
			var aComponent = arguments.length>0?arguments[0]:null;
			
			if (!(this instanceof $wnd.ModelDate)) {
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
			if(!injected) {
				aComponent.@com.eas.client.form.published.widgets.model.ModelDate::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return published;
		};	

		// **************************************************************************
		$wnd.ModelSpin = function () {
			var aComponent = arguments.length>0?arguments[0]:null;
			
			if (!(this instanceof $wnd.ModelSpin)) {
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

			if(!injected) {
				aComponent.@com.eas.client.form.published.widgets.model.ModelDate::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return published;
		};	

		// **************************************************************************
		$wnd.ModelCombo = function () {
			var aComponent = arguments.length>0?arguments[0]:null;
			
			if (!(this instanceof $wnd.ModelCombo)) {
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

			if(!injected) {
				aComponent.@com.eas.client.form.published.widgets.model.ModelCombo::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return published;
		};	

	}-*/;

}
