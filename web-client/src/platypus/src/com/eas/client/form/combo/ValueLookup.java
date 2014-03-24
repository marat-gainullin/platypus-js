package com.eas.client.form.combo;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.locators.Locator;
import com.eas.client.converters.StringRowValueConverter;
import com.eas.client.form.published.widgets.model.ModelElementRef;

public class ValueLookup {

	protected StringRowValueConverter converter = new StringRowValueConverter();
	protected int targetColIndex;
	protected ModelElementRef lookupValueRef;
	protected ModelElementRef displayValueRef;
	protected Locator loc;

	public ValueLookup(int aTargetColIndex, ModelElementRef aLookupValueRef,
			ModelElementRef aDisplayRef) {
		super();
		targetColIndex = aTargetColIndex;
		lookupValueRef = aLookupValueRef;
		displayValueRef = aDisplayRef;
	}

	protected void init() {
		if (isValid() && lookupValueRef.isField
				&& loc == null && lookupValueRef.entity.getRowset() != null) {
			loc = lookupValueRef.entity.getRowset().createLocator();
			loc.beginConstrainting();
			try {
				loc.addConstraint(lookupValueRef.getColIndex());
			} finally {
				loc.endConstrainting();
			}
		}
	}

	protected boolean isValid() {
		return lookupValueRef.isCorrect() && lookupValueRef.field != null
				&& displayValueRef.isCorrect() && displayValueRef.field != null
				&& lookupValueRef.entity == displayValueRef.entity
				&& targetColIndex > 0;
	}

	public boolean isInited()
	{
		return isValid() && loc != null;
	}
	
	public boolean tryInit()
	{
		init();
		return isInited();
	}
	
	public ModelElementRef getLookupValueRef() {
		return lookupValueRef;
	}

	public ModelElementRef getDisplayValueRef() {
		return displayValueRef;
	}

	public Row lookupRow(Object aPkValue) throws Exception {
		init();
		if (isInited()) {
				if (aPkValue != null) {
					if (loc.find(new Object[] { aPkValue })) {
						return loc.getRow(0);
					} else {
						return null;
					}
				} else
					return null;
		} else
			return null;
	}
}
