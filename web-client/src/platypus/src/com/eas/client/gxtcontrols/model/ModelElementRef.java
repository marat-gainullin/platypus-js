package com.eas.client.gxtcontrols.model;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.eas.client.Utils;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.ParametersEntity;
import com.google.gwt.xml.client.Element;

public class ModelElementRef {
	protected Model model;
	public Entity entity;
	protected String fieldName;
	public boolean isField = true;
	public Field field = null;
	// runtime
	protected int colIndex;

	public ModelElementRef(Element aTag, Model aModel) throws Exception {
		super();
		model = aModel;
		if (aTag != null) {
			String entityId = aTag.getAttribute("entityId");
			entity = aModel.getEntityById(entityId);
			fieldName = aTag.getAttribute("fieldName");
			isField = Utils.getBooleanAttribute(aTag, "field", true);
			tryResolveField();
		}
	}

	public ModelElementRef(Model aModel, String aEntityId, String aFieldName, boolean aIsField) throws Exception {
		super();
		model = aModel;
		entity = aModel.getEntityById(aEntityId);
		fieldName = aFieldName;
		isField = aIsField;
		tryResolveField();
	}

	protected void tryResolveField() throws Exception {
		resolveField();
	}

	public void resolveField() throws Exception {
		if (entity != null) {
			if (entity instanceof ParametersEntity) {
				if (isField) {
					field = entity.getFields().get(fieldName);
				}
			} else if (entity.getQuery() != null) {
				if (isField) {
					field = entity.getFields().get(fieldName);
				} else {
					field = entity.getQuery().getParameters().get(fieldName);
				}
			}
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
