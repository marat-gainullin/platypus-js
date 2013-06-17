/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata.gui;

import java.util.Date;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;

/**
 *
 * @author vy
 */
public class TextAreaHandler extends Handler {

    private JTextArea textArea;

    public TextAreaHandler(JTextArea aTextArea) {
        textArea = aTextArea;
    }

    public TextAreaHandler(JTextArea aTextArea, Formatter aFormatter) {
        this(aTextArea);
        setFormatter(aFormatter);
    }

    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record) || textArea == null) {
            return;
        }
        String msg;

        try {
            Formatter formatter = getFormatter();
            if (formatter != null) {
                msg = formatter.format(record);
            } else {
                //default
                String message = record.getMessage();
                Throwable thrown = record.getThrown();
                msg = String.format("%s: %s -> %s %s\n", record.getLevel(), new Date(record.getMillis()), (message == null ? "" : message), (thrown == null ? "" : thrown.toString()));
            }
        } catch (Exception ex) {
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }
        textArea.append(msg);
        textArea.setCaretPosition(textArea.getText().length());
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
