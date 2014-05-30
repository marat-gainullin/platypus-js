package com.eas.client.form.combo;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.locators.Locator;
import com.eas.client.form.published.widgets.model.ModelElementRef;

public class ValueLookup {

	protected ModelElementRef lookupValueRef;
	protected Locator loc;

	public ValueLookup(ModelElementRef aLookupValueRef) {
		super();
		lookupValueRef = aLookupValueRef;
	}

	protected void init() {
		if (isValid() && lookupValueRef.isField && loc == null && lookupValueRef.entity.getRowset() != null) {
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
		return lookupValueRef != null && lookupValueRef.isCorrect() && lookupValueRef.field != null;
	}

	public boolean isInited() {
		return isValid() && loc != null;
	}

	public void die(){
		if(isInited()){
			lookupValueRef.entity.getRowset().removeLocator(loc);
			loc = null;
		}
	}
	
	public boolean tryInit() {
		init();
		return isInited();
	}

	public ModelElementRef getLookupValueRef() {
		return lookupValueRef;
	}

	public Row lookupRow(Object aLookupValue) throws Exception {
		init();
		if (isInited()) {
			if (aLookupValue != null) {
				if (loc.find(new Object[] { aLookupValue })) {
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
