package com.eas.client.form.grid.columns;

import com.google.gwt.core.client.JavaScriptObject;

public interface ModelGridColumnFacade {

	public boolean isVisible();

	public void setVisible(boolean aValue);

	public int getWidth();

	public void setWidth(int aValue);

	public String getTitle();

	public void setTitle(String aValue);

	public boolean isResizable();

	public void setResizable(boolean aValue);

	public boolean isReadonly();

	public void setReadonly(boolean aValue);
	
	public boolean isSortable();

	public void setSortable(boolean aValue);

	public JavaScriptObject getOnRender();

	public void setOnRender(JavaScriptObject aValue);

	public JavaScriptObject getOnSelect();

	public void setOnSelect(JavaScriptObject aValue);
}
