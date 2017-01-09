package com.eas.menu;

public class JsMenu {
	
	public native static void init()/*-{
		
		function publishComponentProperties(aPublished){
			@com.eas.widgets.JsWidgets::publishComponentProperties(Lcom/eas/ui/PublishedComponent;)(aPublished);
		}
		
		function predefine(aDeps, aName, aDefiner){
			@com.eas.core.Predefine::predefine(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(aDeps, aName, aDefiner);
		}
		
		predefine([], 'forms/menu-bar', function(){
			function MenuBar() {
				if (!(this instanceof MenuBar)) {
					throw  ' use  "new MenuBar()" !';
				}
				var aComponent = arguments.length > 0 ? arguments[0] : null;
	
				var published = this;
				aComponent = aComponent || @com.eas.menu.PlatypusMenuBar::new(Z)(false);
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
			}
			@com.eas.menu.MenuPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('MenuBar', MenuBar);
			return MenuBar;
		});

		predefine([], 'forms/menu', function(){
			function Menu(aText) {
				if (!(this instanceof Menu)) {
					throw  ' use  "new Menu()" !';
				}
				var aComponent = arguments.length > 1 ? arguments[1] : null;
	
				var published = this;
				aComponent = aComponent || @com.eas.menu.PlatypusMenu::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
	
				if (aText) {
					published.text = aText;
				}
			}
			@com.eas.menu.MenuPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('Menu', Menu);
			return Menu;
		});

		predefine([], 'forms/popup-menu', function(){
			function PopupMenu() {
				if (!(this instanceof PopupMenu)) {
					throw  ' use  "new PopupMenu()" !';
				}
				var aComponent = arguments.length > 0 ? arguments[0] : null;
	
				var published = this;
				aComponent = aComponent || @com.eas.menu.PlatypusPopupMenu::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
			}
			@com.eas.menu.MenuPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('PopupMenu', PopupMenu);
			return PopupMenu;
		});
		
		predefine([], 'forms/menu-item', function(){
			function MenuItem(aText, aIcon, aCallback) {
				var aComponent = arguments.length > 3 ? arguments[3] : null;
				
				if (!(this instanceof MenuItem)) {
					throw  ' use  "new MenuItem()" !';
				}
	
				var published = this;
				
				aComponent = aComponent || @com.eas.menu.PlatypusMenuItemImageText::new()();
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
			}
			@com.eas.menu.MenuPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('MenuItem', MenuItem);
			return MenuItem;
		});	
		
		predefine([], 'forms/check-menu-item', function(){
			function CheckMenuItem(aText, aSelected, aCallback) {
				var aComponent = arguments.length > 3 ? arguments[3] : null;
				
				if (!(this instanceof CheckMenuItem)) {
					throw  ' use  "new CheckMenuItem()" !';
				}
	
				var published = this;
				aComponent = aComponent || @com.eas.menu.PlatypusMenuItemCheckBox::new()();
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
			@com.eas.menu.MenuPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('CheckMenuItem', CheckMenuItem);
			return CheckMenuItem;
		});

		predefine([], 'forms/radio-menu-item', function(){
			function RadioMenuItem(aText, aSelected, aCallback) {
				var aComponent = arguments.length > 3 ? arguments[3] : null;
				
				if (!(this instanceof RadioMenuItem)) {
					throw  ' use  "new RadioMenuItem()" !';
				}
	
				var published = this;
				aComponent = aComponent || @com.eas.menu.PlatypusMenuItemRadioButton::new()();
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
			@com.eas.menu.MenuPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('RadioMenuItem', RadioMenuItem);
			return RadioMenuItem;
		});
		
		predefine([], 'forms/menu-separator', function(){
			function MenuSeparator() {
				var aComponent = arguments.length > 0 ? arguments[0] : null;
				
				if (!(this instanceof MenuSeparator)) {
					throw  ' use  "new MenuSeparator()" !';
				}
	
				var published = this;
				aComponent = aComponent || @com.eas.menu.PlatypusMenuItemSeparator::new()();
				published.unwrap = function() {
					return aComponent;
				};
				publishComponentProperties(published);
				return published;
			}
			@com.eas.menu.MenuPublisher::putPublisher(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)('MenuSeparator', MenuSeparator);
			return MenuSeparator;
		});
		
	}-*/;
}
