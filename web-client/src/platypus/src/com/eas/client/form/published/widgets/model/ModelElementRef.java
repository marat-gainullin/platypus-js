package com.eas.client.form.published.widgets.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.bearsoft.rowset.Utils;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ParametersEntity;
import com.google.gwt.xml.client.Element;

public class ModelElementRef {
	protected Model model;
	public String entityId;
	public Entity entity;
	protected String fieldName;
	public boolean isField = true;
	public Field field;
	// runtime
	protected int colIndex;

	public ModelElementRef(Element aTag, Model aModel) throws Exception {
		super();
		model = aModel;
		if (aTag != null) {
			entityId = aTag.getAttribute("entityId");
			entity = aModel.getEntityById(entityId);
			fieldName = aTag.getAttribute("fieldName");
			isField = Utils.getBooleanAttribute(aTag, "field", true);
			resolveField();
		}
	}

	public ModelElementRef(Model aModel, String aEntityId, String aFieldName, boolean aIsField) throws Exception {
		super();
		model = aModel;
		entityId = aEntityId;
		entity = aModel.getEntityById(entityId);
		fieldName = aFieldName;
		isField = aIsField;
		resolveField();
	}

	public void resolveField() throws Exception {
		if (entity != null) {
			if (entity instanceof ParametersEntity) {
				assert isField : "Parameter must be refereced as a field only! (Parameters entity has no own parameters)";
				field = entity.getFields().get(fieldName);
			} else if (entity.getQuery() != null) {
				if (isField) {
					field = entity.getFields().get(fieldName);
				} else {
					field = entity.getQuery().getParameters().get(fieldName);
				}
			}
		} else {
			Logger.getLogger(ModelElementRef.class.getName()).log(Level.SEVERE,
			        "Model's entity missing while controls binding. Entity name: " + entityId + "; " + (isField ? "field" : "parameter") + " name: " + fieldName);
		}
	}

	public boolean isCorrect() {
		return entity != null && (fieldName == null || (field != null && (field instanceof Parameter || isField)));
	}

	public int getColIndex() {
		if (field != null && entity != null && entity.getRowset() != null) {
			if (colIndex == 0) {
				colIndex = entity.getRowset().getFields().find(field.getName());
			}
			return colIndex;
		} else
			return 0;
	}
}
