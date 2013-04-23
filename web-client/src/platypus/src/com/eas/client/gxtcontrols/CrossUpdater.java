package com.eas.client.gxtcontrols;

import java.util.HashSet;
import java.util.Set;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetAdapter;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSaveEvent;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.eas.client.beans.PropertyChangeEvent;
import com.eas.client.beans.PropertyChangeListener;
import com.eas.client.model.Entity;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class CrossUpdater extends RowsetAdapter implements PropertyChangeListener {

	protected Set<Entity> toListenTo = new HashSet();
	protected Runnable onChange;

	public CrossUpdater(Runnable aOnChange) {
		super();
		onChange = aOnChange;
	}

	public void add(Entity aEntity) {
		if (aEntity != null && !toListenTo.contains(aEntity)) {
			toListenTo.add(aEntity);
			if (aEntity.getRowset() == null) {
				aEntity.getChangeSupport().addPropertyChangeListener(this);
			} else {
				aEntity.getRowset().addRowsetListener(this);
				rowsetRequeried(null);
			}
		}
	}

	public void remove(Entity aEntity) {
		if (aEntity != null) {
			toListenTo.remove(aEntity);
			aEntity.getChangeSupport().removePropertyChangeListener(this);
			if (aEntity.getRowset() != null) {
				aEntity.getRowset().removeRowsetListener(this);
			}
		}
	}

	public void die() {
		for (Entity e : toListenTo.toArray(new Entity[] {})) {
			remove(e);
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if ("rowset".equals(evt.getPropertyName()) && evt.getOldValue() == null && evt.getNewValue() != null) {
			assert evt.getNewValue() instanceof Rowset;
			assert evt.getSource() instanceof Entity;
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					((Entity) evt.getSource()).getChangeSupport().removePropertyChangeListener(CrossUpdater.this);
				}
			});
			((Rowset) evt.getNewValue()).addRowsetListener(this);
			rowsetRequeried(null);
		}
	}

	@Override
	public void rowsetFiltered(RowsetFilterEvent event) {
		onChange.run();
	}

	@Override
	public void rowsetRequeried(RowsetRequeryEvent event) {
		onChange.run();
	}

	@Override
	public void rowsetRolledback(RowsetRollbackEvent event) {
		onChange.run();
	}

	@Override
	public void rowsetSaved(RowsetSaveEvent event) {
	}

	@Override
	public void rowsetScrolled(RowsetScrollEvent event) {
	}

	@Override
	public void rowInserted(RowsetInsertEvent event) {
		onChange.run();
	}

	@Override
	public void rowChanged(RowChangeEvent event) {
		onChange.run();
	}

	@Override
	public void rowDeleted(RowsetDeleteEvent event) {
		onChange.run();
	}
}
