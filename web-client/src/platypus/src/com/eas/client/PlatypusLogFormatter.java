/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import java.util.Date;
import java.util.logging.LogRecord;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.logging.impl.FormatterImpl;

/**
 * 
 * @author mg
 */
public class PlatypusLogFormatter extends FormatterImpl {

	// format string for printing the log record
	private static final DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd.mm.YYYY HH:MM:SS");

	@Override
	public synchronized String format(LogRecord event) {
		StringBuilder message = new StringBuilder();
		Date date = new Date(event.getMillis());
		message.append(dateFormat.format(date)).append("\t");
		message.append(event.getLevel().getName()).append("\t");
		message.append(event.getMessage()).append("\t").append("\n");
		if (event.getThrown() != null) {
			message.append(getStackTraceAsString(event.getThrown(), "\n", "\t"));
		}
		return message.toString();
	}
}
