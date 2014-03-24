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
			var aComponent = arguments.length>0?arguments[0]:null;
			
			if (!(this instanceof $wnd.ModelGrid)) {
				throw  ' use  "new ModelGrid()" !';
			}

			var published = this;
			var injected = aComponent != null;
			aComponent = injected ? aComponent : @com.eas.client.gxtcontrols.grid.ModelGrid::new()(); 
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);

			published.select = function(aRow) {
				if(aRow != null && aRow != undefined)
					aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::selectRow(Lcom/bearsoft/rowset/Row;)(aRow.unwrap());
			};
			published.unselect = function(aRow) {
				if(aRow != null && aRow != undefined)
					aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::unselectRow(Lcom/bearsoft/rowset/Row;)(aRow.unwrap());
			};
			published.clearSelection = function() {
				aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::clearSelection()();
			};
			published.find = function(){
				aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::find()();
			};
			published.findSomething = function() {
				published.find();
			};
			published.makeVisible = function(aRow, needToSelect) {
				var need2Select = true;
				if(needToSelect != undefined)
					need2Select = (false != needToSelect);
				if(aRow != null)
					return aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::makeVisible(Lcom/bearsoft/rowset/Row;Z)(aRow.unwrap(), need2Select);
				else
					return false;
			};
			
			Object.defineProperty(published, "onRender", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::getGeneralCellFunction()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::setGeneralCellFunction(Lcom/google/gwt/core/client/JavaScriptObject;)(aValue);
				}
			});
			Object.defineProperty(published, "selected", {
				get : function() {
					var selectionList = aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::getJsSelected()();
					var selectionArray = [];
					for(var i=0;i<selectionList.@java.util.List::size()();i++)
					{
						selectionArray[selectionArray.length] = selectionList.@java.util.List::get(I)(i);
					}
					return selectionArray;
				}
			});
			Object.defineProperty(published, "editable", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::isEditable()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::setEditable(Z)(aValue);
				}
			});
			Object.defineProperty(published, "deletable", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::isDeletable()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::setDeletable(Z)(aValue);
				}
			});
			Object.defineProperty(published, "insertable", {
				get : function() {
					return aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::isInsertable()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::setInsertable(Z)(aValue);
				}
			});
			if(!injected)
			{
				aComponent.@com.eas.client.gxtcontrols.grid.ModelGrid::setPublished(Lcom/eas/client/form/layout/published/PublishedComponent;)(published);
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
			aComponent = injected ? aComponent : @com.eas.client.form.layout.model.ModelCheck::new()(); 
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);

			if(!injected)
			{
				aComponent.@com.eas.client.form.layout.model.ModelCheck::setPublishedField(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
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
			aComponent = injected ? aComponent : @com.eas.client.form.layout.model.ModelFormattedField::new()(); 
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			Object.defineProperty(published, "emptyText", {
				get : function() {
					return aComponent.@com.eas.client.form.layout.model.ModelFormattedField::getEmptyText()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.form.layout.model.ModelFormattedField::setEmptyText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
				}
			});
			publishModelComponentProperties(published);

			if(!injected) {
				aComponent.@com.eas.client.form.layout.model.ModelFormattedField::setPublishedField(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
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
			aComponent = injected ? aComponent : @com.eas.client.form.layout.model.ModelTextArea::new()(); 
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			Object.defineProperty(published, "emptyText", {
				get : function() {
					return aComponent.@com.eas.client.form.layout.model.ModelTextArea::getEmptyText()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.form.layout.model.ModelTextArea::setEmptyText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
				}
			});
			publishModelComponentProperties(published);

			if(!injected) {
				aComponent.@com.eas.client.form.layout.model.ModelTextArea::setPublishedField(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
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
			aComponent = injected ? aComponent : @com.eas.client.form.layout.model.ModelDate::new()(); 
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			Object.defineProperty(published, "emptyText", {
				get : function() {
					return aComponent.@com.eas.client.form.layout.model.ModelDate::getEmptyText()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.form.layout.model.ModelDate::setEmptyText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
				}
			});
			publishModelComponentProperties(published);

			if(!injected) {
				aComponent.@com.eas.client.form.layout.model.ModelDate::setPublishedField(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
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
			aComponent = injected ? aComponent : @com.eas.client.form.layout.model.ModelSpin::new()(); 
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			Object.defineProperty(published, "emptyText", {
				get : function() {
					return aComponent.@com.eas.client.form.layout.model.ModelSpin::getEmptyText()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.form.layout.model.ModelSpin::setEmptyText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
				}
			});
			publishModelComponentProperties(published);

			if(!injected) {
				aComponent.@com.eas.client.form.layout.model.ModelDate::setPublishedField(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
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
			aComponent = injected ? aComponent : @com.eas.client.form.layout.model.ModelCombo::new()(); 
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			Object.defineProperty(published, "emptyText", {
				get : function() {
					return aComponent.@com.eas.client.form.layout.model.ModelCombo::getEmptyText()();
				},
				set : function(aValue) {
					aComponent.@com.eas.client.form.layout.model.ModelCombo::setEmptyText(Ljava/lang/String;)(aValue!=null?''+aValue:null);
				}
			});
			publishModelComponentProperties(published);

			if(!injected) {
				aComponent.@com.eas.client.form.layout.model.ModelCombo::setPublishedField(Lcom/google/gwt/core/client/JavaScriptObject;)(published);
			}
			return published;
		};	

	}-*/;

}
