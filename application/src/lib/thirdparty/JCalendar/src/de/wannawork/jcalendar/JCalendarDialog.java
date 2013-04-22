/*
 * Created on 08.11.2003
 */
package de.wannawork.jcalendar;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Calendar;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A class that creates a Dialog with a JCalendar.
 * @author bodo
 */
public class JCalendarDialog {
	/** Parent */
	private Component _parentComponent;
	/** Title of the Dialog */
	private String _title;
	/** Message in the Dialog*/
	private String _message;
	/** JCalendar in the Dialog */
	private JCalendarComboBox _calendarBox;

	/** Which OptionType */
	private int _optionType;
	/** Wich MessageType */
	private int _messageType;

	public JCalendarDialog(Component parentComponent, String title, String message) {
		_parentComponent = parentComponent;
		_title = title;
		_message = message;

		_calendarBox = new JCalendarComboBox();
		_optionType = JOptionPane.OK_CANCEL_OPTION;
		_messageType = JOptionPane.QUESTION_MESSAGE;
	}

	public JCalendarDialog(Component parentComponent, String title, String message, JCalendarComboBox calendarBox) {
		_parentComponent = parentComponent;
		_title = title;
		_message = message;
		_calendarBox = calendarBox;

		_optionType = JOptionPane.OK_CANCEL_OPTION;
		_messageType = JOptionPane.QUESTION_MESSAGE;
	}

	public JCalendarDialog(Component parentComponent, String title, String message, JCalendarComboBox calendarBox, int optionType, int messageType) {
		_parentComponent = parentComponent;
		_title = title;
		_message = message;
		_calendarBox = calendarBox;
		_optionType = optionType;
		_messageType = messageType;
	}

	public JCalendarDialog(Component parentComponent, String title, String message, int optionType, int messageType) {
		_parentComponent = parentComponent;
		_title = title;
		_message = message;
		_optionType = optionType;
		_messageType = messageType;

		_calendarBox = new JCalendarComboBox();
	}

	/**	 * Gets the Calendar from the Dialog
         */
	public Calendar getCalendar() {
		return getCalendar(JOptionPane.OK_OPTION);
	}

	/** 
         * Returns the Calendar if a certain MessageOption was
         * returnd by the Dialog
         */
	public Calendar getCalendar(int messageOption) {
		if (showConfirmDialog() == messageOption) {
			return _calendarBox.getCalendar();
		} else {
			return null;
		}
	}

	/**
         * Creates the Dialog and returns the Result
         * @param MessageOption that was pressed
         */
	private int showConfirmDialog() {
		JTextField tf = new JTextField();

		JPanel calPanel = new JPanel();
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);
		layout.setHgap(0);
		layout.setVgap(0);
		calPanel.setLayout(layout);
		calPanel.add(_calendarBox);

		Object[] msg = { _message, calPanel };
		int result = JOptionPane.showConfirmDialog(_parentComponent, msg, _title, _optionType, _messageType);
		return result;
	}
}