/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import java.util.Date;
import java.util.logging.LogRecord;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.client.TextLogFormatter;
import com.google.gwt.logging.impl.StackTracePrintStream;

/**
 * 
 * @author mg
 */
public class PlatypusLogFormatter extends TextLogFormatter {

	private boolean showStackTraces;

	public PlatypusLogFormatter(boolean showStackTraces) {
		super(showStackTraces);
		this.showStackTraces = showStackTraces;
	}

	// format string for printing the log record
	private static final DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd.MM.yyyy HH:mm:ss");

	@Override
	public String format(LogRecord event) {
		StringBuilder message = new StringBuilder();
		Date date = new Date(event.getMillis());
		message.append(dateFormat.format(date)).append("\t");
		message.append(event.getLevel().getName()).append("\t");
		if (event.getMessage() != null)
			message.append(event.getMessage()).append("\t").append("\n");
		if (event.getThrown() != null && showStackTraces) {
			StringBuilder traceSOut = new StringBuilder();
			event.getThrown().printStackTrace(new StackTracePrintStream(traceSOut));
			message.append(traceSOut);
		}
		return message.toString();
	}
}
