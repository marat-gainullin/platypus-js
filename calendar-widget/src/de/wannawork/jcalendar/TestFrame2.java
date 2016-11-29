package de.wannawork.jcalendar;
/*
 * Copyright (c) 2003, Bodo Tasche (http://www.wannawork.de)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice, this 
 *       list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice, this 
 *       list of conditions and the following disclaimer in the documentation and/or other 
 *       materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This is a small Example using JCalendarComboBox, JCalendarPanel and JCalendarDialog
 * 
 * @author Bodo Tasche
 */
public class TestFrame2 extends JFrame implements ChangeListener {

	public TestFrame2() {
		setTitle("JCalendar TestFrame");

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		createGUI();
	}

	private void createGUI() {

	    JTabbedPane tab = new JTabbedPane();
	    
		JPanel content = new JPanel();

		content.setLayout(new BorderLayout());

		/**
		 * JCalendarComboBox Example
		 */
		JPanel comboPanel = new JPanel();
		comboPanel.setBorder(BorderFactory.createTitledBorder("JCalendarComboBox"));
		comboPanel.setLayout(new BorderLayout());

		calendarComboBox = new JCalendarComboBox(Calendar.getInstance());
		comboPanel.add(calendarComboBox, BorderLayout.NORTH);
		
		calendarComboBox.setHorizontalAlignment(JCalendarComboBox.CENTER);
		
		comboboxTextField = new JTextField();
		comboboxTextField.setEditable(false);

		calendarComboBox.addChangeListener(this);

		comboPanel.add(comboboxTextField, BorderLayout.CENTER);

		content.add(comboPanel, BorderLayout.NORTH);

		/**
		 * JCalendarPanel Example
		 */
		JPanel monthPanel = new JPanel();

		monthPanel.setBorder(BorderFactory.createTitledBorder("JCalendarPanel"));
		monthPanel.setLayout(new BorderLayout());

		calendarPanel = new JCalendarPanel();
		calendarPanel.addChangeListener(this);

		monthPanel.add(calendarPanel, BorderLayout.CENTER);

		calendarpanelTextField = new JTextField();
		calendarpanelTextField.setEditable(false);

		monthPanel.add(calendarpanelTextField, BorderLayout.SOUTH);

		content.add(monthPanel, BorderLayout.CENTER);

		/**
		 * Dialog-Example
		 */
		JButton dialog = new JButton("Create Dialog");

		dialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCalendarDialog();
			}

		});

		content.add(dialog, BorderLayout.SOUTH);

		tab.add("JCalendar", content);
		tab.add("Test", new JTextArea());
		
		getContentPane().add(tab);
		
		pack();
		setVisible(true);
	}

	private void showCalendarDialog() {
		JCalendarDialog caldialog = new JCalendarDialog(this, "Get Date", "Please select the Date :");
		System.out.println(caldialog.getCalendar());
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		DateFormat format = DateFormat.getDateInstance();

		if (e.getSource() == calendarComboBox) {
			comboboxTextField.setText(format.format(calendarComboBox.getCalendar().getTime()));
		} else {
			calendarpanelTextField.setText(format.format(calendarPanel.getCalendar().getTime()));
		}

	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		TestFrame2 test = new TestFrame2();
	}

	private JCalendarPanel calendarPanel;
	private JTextField calendarpanelTextField;

	private JCalendarComboBox calendarComboBox;
	private JTextField comboboxTextField;
}