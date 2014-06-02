package com.eas.client.form.js;

public class JsMenus {

	public native static void init()/*-{
		
		function publishComponentProperties(aPublished) {
			@com.eas.client.form.js.JsWidgets::publishComponentProperties(Lcom/eas/client/form/published/PublishedComponent;)(aPublished);
		}
				
		// ***************************************************
		$wnd.P.MenuBar = function() {
			if (!(this instanceof $wnd.P.MenuBar)) {
				throw  ' use  "new P.MenuBar()" !';
			}
			var aComponent = arguments.length > 0 ? arguments[0] : null;

			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusMenuBar::new(Z)(false);
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
		};

		// ***************************************************
		$wnd.P.Menu = function(aText) {
			if (!(this instanceof $wnd.P.Menu)) {
				throw  ' use  "new P.Menu()" !';
			}
			var aComponent = arguments.length > 1 ? arguments[1] : null;

			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusMenu::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);

			if (aText) {
				published.text = aText;
			}
		};

		// ***************************************************
		$wnd.P.PopupMenu = function() {
			if (!(this instanceof $wnd.P.PopupMenu)) {
				throw  ' use  "new P.PopupMenu()" !';
			}
			var aComponent = arguments.length > 0 ? arguments[0] : null;

			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusPopupMenu::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
		};
		
		// **************************************************************************
		$wnd.P.MenuItem = function (aText, aIcon, aCallback) {
			var aComponent = arguments.length > 3 ? arguments[3] : null;
			
			if (!(this instanceof $wnd.P.MenuItem)) {
				throw  ' use  "new P.MenuItem()" !';
			}

			var published = this;
			
			aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusMenuItemImageText::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);

			if (aText) {
				published.text = aText;
			} 	
			if (aIcon) {
				published.icon = aIcon;
			}
			if (aCallback) {
				published.onActionPerformed = aCallback;
			}	 	
			return published;
		};	
		
		// **************************************************************************
		$wnd.P.CheckMenuItem = function (aText, aSelected, aCallback) {
			var aComponent = arguments.length > 3 ? arguments[3] : null;
			
			if (!(this instanceof $wnd.P.CheckMenuItem)) {
				throw  ' use  "new P.CheckMenuItem()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusMenuItemCheckBox::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);

			if (aText) {
				published.text = aText;
			} 	
			if (aSelected) {
				published.selected = aSelected;
			}
			if(aCallback){
				published.onActionPerformed = aCallback; 
			}
			return published;
		}

		// **************************************************************************
		$wnd.P.RadioMenuItem = function (aText, aSelected, aCallback) {
			var aComponent = arguments.length > 3 ? arguments[3] : null;
			
			if (!(this instanceof $wnd.P.RadioMenuItem)) {
				throw  ' use  "new P.RadioMenuItem()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusMenuItemRadioButton::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			
			if (aText) {
				published.text = aText;
			} 	
			if (aSelected) {
				published.selected = aSelected;
			}
			if(aCallback){
				published.onActionPerformed = aCallback; 
			}
			return published;
		}
		
		// **************************************************************************
		$wnd.P.MenuSeparator = function () {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			
			if (!(this instanceof $wnd.P.MenuSeparator)) {
				throw  ' use  "new P.MenuSeparator()" !';
			}

			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusMenuItemSeparator::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			return published;
		}
	}-*/;
	
}
