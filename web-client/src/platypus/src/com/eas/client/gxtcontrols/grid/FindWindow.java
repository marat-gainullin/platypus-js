package com.eas.client.gxtcontrols.grid;

import java.util.List;

import com.bearsoft.rowset.Row;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer.VBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class FindWindow extends Window {
	
	protected static GridMessages messages = GWT.create(GridMessages.class);
	
	private Label label;
	private TextField field;
	private CheckBox checkCase;
	private CheckBox checkWhole;
	private TextButton findButton;
	private TextButton closeButton;

	private Grid<Row> grid;

	private int row = 0;
	private int col = 0;

	public FindWindow(Grid<Row> aGrid) {
		grid = aGrid;
		initComponents();

		setHeadingText(messages.heading());
		setBorders(false);

		setResizable(false);
		setPixelSize(400, 145);
	}

	private void initComponents() {
		label = new Label(messages.find());
		field = new TextField();
		checkCase = new CheckBox();
		checkCase.setBoxLabel(messages.caseSensitive());

		checkWhole = new CheckBox();
		checkWhole.setBoxLabel(messages.wholeWords());

		findButton = new TextButton(messages.findNext());
		findButton.setPixelSize(90, 25);
		closeButton = new TextButton(messages.close());
		closeButton.setPixelSize(90, 25);

		findButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				findNext();
			}
		});

		closeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				close();
			}
		});

		HBoxLayoutContainer hBox = new HBoxLayoutContainer();
		hBox.setPadding(new Padding(5));
		hBox.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
		hBox.add(label, new BoxLayoutData(new Margins(5)));

		VBoxLayoutContainer vBox = new VBoxLayoutContainer();
		vBox.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);
		vBox.add(field, new BoxLayoutData(new Margins(0)));

		HBoxLayoutContainer hBox2 = new HBoxLayoutContainer();
		hBox2.setHBoxLayoutAlign(HBoxLayoutAlign.TOP);
		hBox2.add(checkCase, new BoxLayoutData(new Margins(0)));
		hBox2.add(checkWhole, new BoxLayoutData(new Margins(0, 0, 0, 10)));

		vBox.add(hBox2, new BoxLayoutData(new Margins(10)));
		BoxLayoutData data = new BoxLayoutData(new Margins(0));
		data.setFlex(1);
		hBox.add(vBox, data);

		add(hBox);
		addButton(findButton);
		addButton(closeButton);
		setButtonAlign(BoxLayoutPack.END);
	}

	private void close() {
		hide();
		removeFromParent();
	}

	private boolean findNext() {
		Store<Row> store = grid.getStore();
		boolean caseSensitive = checkCase.getValue();
		boolean wholeString = checkWhole.getValue();
		String findText = field.getText();
		if (findText.isEmpty()) {
			return false;
		}
		if (!caseSensitive) {
			findText = findText.toLowerCase();
		}
		List<Row> allVisibleItems = store.getAll();
		if (row >= allVisibleItems.size()) {
			row = 0;
			col = 0;
		} else {
			col++;
			if (row < 0) {
				row = 0;
				col = 0;
			}
			if (col >= grid.getColumnModel().getColumnCount()) {
				col = 0;
				row++;
			}
		}

		for (; row < allVisibleItems.size(); row++) {
			for (; col < grid.getColumnModel().getColumnCount(); col++) {
				if (findInnerText(grid.getView().getCell(row, col), findText, caseSensitive, wholeString)) {
					selectCell(row, col);
					return true;
				}
			}
			col = 0;
		}
		AlertMessageBox alert = new AlertMessageBox(messages.grid(), messages.endSearch());
		alert.show();
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

	// !!! --- ИЗМЕНИТЬ --- !!!
	private void selectCell(int aRow, int aCol) {
		grid.getSelectionModel().select(aRow, false);
	}

}
