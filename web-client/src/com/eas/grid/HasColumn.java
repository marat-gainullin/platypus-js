package com.eas.grid;

import com.google.gwt.user.cellview.client.Column;

public interface HasColumn<T> {

	public Column<T, ?> getColumn();
}
