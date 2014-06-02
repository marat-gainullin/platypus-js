package com.eas.client.model.js;

import com.eas.client.model.Entity;
import com.google.gwt.core.client.JavaScriptObject;

public class JsModel {

	public native static void init()/*-{
		
		$wnd.Entity = function(aModel){
			var delegate = arguments.length > 1 ? arguments[1] : @com.eas.client.model.Entity::new(Lcom/eas/client/model/Model;)(aModel.unwrap());
			delegate.@com.eas.client.model.Entity::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(this);
		};
		
	}-*/;
	
	public native static JavaScriptObject publish(Entity toBePublished)/*-{
		return new $wnd.Entity(null, toBePublished);
	}-*/;
}
