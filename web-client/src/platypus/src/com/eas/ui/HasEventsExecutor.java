package com.eas.ui;

import com.eas.form.EventsExecutor;


public interface HasEventsExecutor {
	
	public EventsExecutor getEventsExecutor();

	public void setEventsExecutor(EventsExecutor aExecutor);
}
