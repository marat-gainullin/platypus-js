package com.eas.form.grid.cells.rowmarker;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface RowMarkerResources extends ClientBundle {

	public static final RowMarkerResources INSTANCE = GWT.create(RowMarkerResources.class);
	
	ImageResource currentRow();

	ImageResource editedRow();

	ImageResource newRow();

	@Source("RowMarker.css")
	RowMarkerStyle style();

}
