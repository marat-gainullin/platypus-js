package com.eas.client.form.js;

public class JsMenus {

	public native static void init()/*-{
		
		function publishComponentProperties(aPublished) {
			@com.eas.client.form.js.JsWidgets::publishComponentProperties(Lcom/eas/client/form/published/PublishedComponent;)(aPublished);
		}
				
		// ***************************************************
		$wnd.MenuBar = function() {
			if (!(this instanceof $wnd.MenuBar)) {
				throw  ' use  "new MenuBar()" !';
			}
			var aComponent = arguments.length > 0 ? arguments[0] : null;

			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusMenuBar::new(Z)(false);
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishChildren(published);
		};

		// ***************************************************
		$wnd.Menu = function(aText) {
			if (!(this instanceof $wnd.Menu)) {
				throw  ' use  "new Menu()" !';
			}
			var aComponent = arguments.length > 1 ? arguments[1] : null;

			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusMenu::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishChildren(published);

			if (aText) {
				published.text = aText;
			}
		};

		// ***************************************************
		$wnd.PopupMenu = function() {
			if (!(this instanceof $wnd.PopupMenu)) {
				throw  ' use  "new PopupMenu()" !';
			}
			var aComponent = arguments.length > 0 ? arguments[0] : null;

			var published = this;
			aComponent = aComponent || @com.eas.client.form.published.menu.PlatypusPopupMenu::new()();
			published.unwrap = function() {
				return aComponent;
			};
			publishComponentProperties(published);
			publishChildren(published);
		};
		
		// **************************************************************************
		$wnd.MenuItem = function (aText, aIcon, aCallback) {
			var aComponent = arguments.length > 3 ? arguments[3] : null;
			
			if (!(this instanceof $wnd.MenuItem)) {
				throw  ' use  "new MenuItem()" !';
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
		$wnd.CheckMenuItem = function (aText, aSelected, aCallback) {
			var aComponent = arguments.length > 3 ? arguments[3] : null;
			
			if (!(this instanceof $wnd.CheckMenuItem)) {
				throw  ' use  "new CheckMenuItem()" !';
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
		$wnd.RadioMenuItem = function (aText, aSelected, aCallback) {
			var aComponent = arguments.length > 3 ? arguments[3] : null;
			
			if (!(this instanceof $wnd.RadioMenuItem)) {
				throw  ' use  "new RadioMenuItem()" !';
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
		$wnd.MenuSeparator = function () {
			var aComponent = arguments.length > 0 ? arguments[0] : null;
			
			if (!(this instanceof $wnd.MenuSeparator)) {
				throw  ' use  "new MenuSeparator()" !';
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
