package com.eas.client.form.grid.columns;

import com.eas.client.form.published.HasJsName;
import com.google.gwt.core.client.JavaScriptObject;

public interface ModelGridColumnFacade extends HasJsName {

	public boolean isVisible();

	public void setVisible(boolean aValue);

	public double getWidth();

	public void setWidth(double aValue);

	public String getTitle();

	public void setTitle(String aValue);

	public boolean isResizable();

	public void setResizable(boolean aValue);

	public boolean isMoveable();

	public void setMoveable(boolean aValue);

	public boolean isReadonly();

	public void setReadonly(boolean aValue);

	public boolean isSortable();

	public void setSortable(boolean aValue);

	public JavaScriptObject getOnRender();

	public void setOnRender(JavaScriptObject aValue);

	public JavaScriptObject getOnSelect();

	public void setOnSelect(JavaScriptObject aValue);
}
