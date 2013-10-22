package com.eas.client.gxtcontrols.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eas.client.model.Model;
import com.eas.client.model.ParametersEntity;
import com.google.gwt.xml.client.Element;

public class LazyModelElementRef extends ModelElementRef {

	public LazyModelElementRef(Element aTag, Model aModel) throws Exception {
		super(aTag, aModel);
	}

	public LazyModelElementRef(Model aModel, String aEntityId, String aFieldName, boolean aIsField) throws Exception {
		super(aModel, aEntityId, aFieldName, aIsField);
	}

	@Override
	protected void tryResolveField() throws Exception {
		if (entity instanceof ParametersEntity) {
			assert isField : "Parameter must be refereced as a field only! (Parameters entity has no own parameters)";
			field = entity.getFields().get(fieldName);
		} else {
			try {
				if (entity != null) {
					resolveField();
				}
			} catch (Exception e) {
				Logger.getLogger(LazyControlBounder.class.getName()).log(Level.SEVERE, e.getMessage());
			}
		}
	}
}
