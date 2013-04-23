/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.bearsoft.rowset.events.*;

/**
 * 
 * @author mg
 */
public class EntityDataListener extends RowsetAdapter {

	protected int events = 0;

	public EntityDataListener() {
		super();
	}

	protected void incEvents() {
		events++;
	}

	public void reset() {
		events = 0;
	}

	public int getEvents() {
		return events;
	}

	@Override
	public void rowsetFiltered(RowsetFilterEvent event) {
		incEvents();
	}

	@Override
	public void rowsetRequeried(RowsetRequeryEvent event) {
		incEvents();
	}

	@Override
	public void rowsetSaved(RowsetSaveEvent event) {
		incEvents();
	}

	@Override
	public void rowsetScrolled(RowsetScrollEvent event) {
		incEvents();
	}

	@Override
	public void rowsetSorted(RowsetSortEvent event) {
		incEvents();
	}

	@Override
	public void rowInserted(RowsetInsertEvent event) {
		incEvents();
	}

	@Override
	public void rowChanged(RowChangeEvent event) {
		incEvents();
	}

	@Override
	public void rowDeleted(RowsetDeleteEvent event) {
		incEvents();
	}
}
