package com.eas.ui;

import com.eas.ui.events.EventsExecutor;


public interface HasEventsExecutor {
	
	public EventsExecutor getEventsExecutor();

	public void setEventsExecutor(EventsExecutor aExecutor);
}
