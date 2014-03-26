package com.eas.client.form.published;

import com.eas.client.form.EventsExecutor;

public interface HasEventsExecutor {
	public EventsExecutor getEventsExecutor();

	public void setEventsExecutor(EventsExecutor aExecutor);
}
