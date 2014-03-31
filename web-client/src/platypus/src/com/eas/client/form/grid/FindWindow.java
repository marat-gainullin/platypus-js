package com.eas.client.form.grid;

import java.util.List;

import com.bearsoft.gwt.ui.containers.HorizontalBoxPanel;
import com.bearsoft.gwt.ui.containers.VerticalBoxPanel;
import com.bearsoft.gwt.ui.containers.window.ToolsCaption;
import com.bearsoft.gwt.ui.containers.window.WindowPanel;
import com.bearsoft.gwt.ui.widgets.grid.Grid;
import com.bearsoft.rowset.Row;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;

public class FindWindow extends WindowPanel {

	protected static GridMessages messages = GWT.create(GridMessages.class);

	private PopupPanel popup = new PopupPanel(true, false);
	private Label label;
	private TextBox field;
	private CheckBox checkCase;
	private CheckBox checkWhole;
	private Button findButton;
	private Button closeButton;

	private Grid<Row> grid;

	private int row;
	private int col;

	public FindWindow(Grid<Row> aGrid) {
		super();
		ToolsCaption caption = new ToolsCaption(this, messages.heading());
		setCaptionWidget(caption);
		grid = aGrid;
		initComponents();

		setResizable(false);
		setMinimizable(false);
		setMaximizable(false);
		setSize(400, 135);
		popup.setWidget(this);
	}

	private void initComponents() {
		label = new Label(messages.find());
		field = new TextBox();
		checkCase = new CheckBox();
		checkCase.setText(messages.caseSensitive());

		checkWhole = new CheckBox();
		checkWhole.setText(messages.wholeWords());

		findButton = new Button(messages.findNext());
		findButton.setPixelSize(90, 25);
		closeButton = new Button(messages.close());
		closeButton.setPixelSize(90, 25);

		findButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				findNext();
			}
		});

		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				close();
			}
		});

		HorizontalBoxPanel hBox = new HorizontalBoxPanel();
		hBox.setHgap(5);

		VerticalBoxPanel vBox = new VerticalBoxPanel();
		vBox.setVgap(10);

		HorizontalBoxPanel hBox2 = new HorizontalBoxPanel();
		hBox2.setHgap(10);
		hBox2.add(checkCase);
		hBox2.add(checkWhole);

		vBox.add(field);
		vBox.add(hBox2);

		hBox.add(label);
		hBox.add(vBox);

		setWidget(hBox);
	}

	private boolean findNext() {
		List<Row> store = grid.getDataProvider().getList();
		boolean caseSensitive = checkCase.getValue();
		boolean wholeString = checkWhole.getValue();
		String findText = field.getText();
		if (findText.isEmpty()) {
			return false;
		}
		if (!caseSensitive) {
			findText = findText.toLowerCase();
		}
		if (row >= store.size()) {
			row = 0;
			col = 0;
		} else {
			col++;
			if (row < 0) {
				row = 0;
				col = 0;
			}
			if (col >= grid.getDataColumnCount()) {
				col = 0;
				row++;
			}
		}

		for (; row < store.size(); row++) {
			for (; col < grid.getDataColumnCount(); col++) {
				if (findInnerText(grid.getViewCell(row, col), findText, caseSensitive, wholeString)) {
					selectCell(row, col);
					return true;
				}
			}
			col = 0;
		}
		// AlertMessageBox alert = new AlertMessageBox(messages.heading(),
		// messages.endSearch());
		// alert.show();
		Window.alert(messages.endSearch());
		return false;
	}

	private boolean findInnerText(Node aNode, String aFindText, boolean aCaseSensitive, boolean aWholeString) {
		int cnt = aNode.getChildCount();
		if (cnt == 0) {
			if (aNode instanceof Element) {
				Element element = (Element) aNode;

				String cellText = element.getInnerText();
				if (!aCaseSensitive) {
					cellText = cellText.toLowerCase();
				}

				if (aWholeString) {
					return cellText.equals(aFindText);
				} else {
					return cellText.contains(aFindText);
				}
			}
		} else {
			for (int i = 0; i < cnt; i++) {
				if (findInnerText(aNode.getChild(i), aFindText, aCaseSensitive, aWholeString)) {
					return true;
				}
			}

		}
		return false;
	}

	private void selectCell(int aRow, int aCol) {
		// TODO; mark individual found cells (aCol parameter)
		grid.getSelectionModel().setSelected(grid.getObject(aRow), true);
	}

	public void show() {
		popup.show();
	}

}
