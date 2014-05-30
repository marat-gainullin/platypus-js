package com.eas.client.form.published;

import com.bearsoft.rowset.metadata.Field;

public interface HasBinding {

	public Field getBinding() throws Exception;

	public void setBinding(Field aField) throws Exception;
}
