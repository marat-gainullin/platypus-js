package com.eas.client.model.js;

import com.eas.client.model.Entity;
import com.google.gwt.core.client.JavaScriptObject;

public class JsModel {

	public native static void init()/*-{
		
		(function(){		
			function Entity(aModel){
				var delegate = arguments.length > 1 ? arguments[1] : @com.eas.client.model.Entity::new(Lcom/eas/client/model/Model;)(aModel.unwrap());
				Entity.superclass.constructor.call(this);
				delegate.@com.eas.client.model.Entity::setPublished(Lcom/google/gwt/core/client/JavaScriptObject;)(this);
			}
			
			Object.defineProperty($wnd.P, "Entity", {
				value : Entity 
			}); 
			
		})();
		
	}-*/;
	
	public native static JavaScriptObject publish(Entity toBePublished)/*-{
		return new $wnd.P.Entity(null, toBePublished);
	}-*/;
}
