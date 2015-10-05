package com.eas.ui;

import com.eas.widgets.EventsExecutor;


public interface HasEventsExecutor {
	
	public EventsExecutor getEventsExecutor();

	public void setEventsExecutor(EventsExecutor aExecutor);
}
