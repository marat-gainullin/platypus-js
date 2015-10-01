package com.eas.form.published;

import com.eas.form.EventsExecutor;

public interface HasEventsExecutor {
	
	public EventsExecutor getEventsExecutor();

	public void setEventsExecutor(EventsExecutor aExecutor);
}
