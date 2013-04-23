package com.eas.client.gxtcontrols.grid.wrappers;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.form.IsField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;

public class PlatypusColumnConfig<M, N> extends ColumnConfig<M, N> {

	protected boolean readonly;
	protected IsField<N> editor;

	/**
	 * Creates a new column config.
	 */
	public PlatypusColumnConfig(ValueProvider<? super M, N> valueProvider) {
		super(valueProvider);
	}

	/**
	 * Creates a new column config.
	 * 
	 * @param valueProvider
	 *            the value provider
	 * @param width
	 *            the column width
	 */
	public PlatypusColumnConfig(ValueProvider<? super M, N> valueProvider, int width) {
		super(valueProvider, width);
	}

	/**
	 * Creates a new column config.
	 * 
	 * @param valueProvider
	 *            the value provider
	 * @param width
	 *            the column width
	 * @param header
	 *            the column header content
	 */
	public PlatypusColumnConfig(ValueProvider<? super M, N> valueProvider, int width, SafeHtml header) {
		super(valueProvider, width, header);
	}

	public PlatypusColumnConfig(ValueProvider<? super M, N> valueProvider, int width, SafeHtml header, boolean aReadonly) {
		this(valueProvider, width, header);
		readonly = aReadonly;
	}
	
	/**
	 * Creates a new column config.
	 * 
	 * @param valueProvider
	 *            thevalueProvider
	 * @param width
	 *            the column width
	 * @param header
	 *            the heading text
	 */
	public PlatypusColumnConfig(ValueProvider<? super M, N> valueProvider, int width, String header) {
		super(valueProvider, width, header);
	}

	public PlatypusColumnConfig(ValueProvider<? super M, N> valueProvider, int width, String header, boolean aReadonly) {
		this(valueProvider, width, header);
		readonly = aReadonly;
	}

	public IsField<N> getEditor() {
		return editor;
	}

	public void setEditor(IsField<N> aField) {
		editor = aField;
	}
	
	public boolean isReadonly() {
	    return readonly;
    }
	
	public void setReadonly(boolean aValue) {
	    readonly = aValue;
    }
}
