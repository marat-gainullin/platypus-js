package com.eas.grid;

import java.util.List;

import com.eas.ui.Orientation;
import com.eas.widgets.containers.AnchorsPanel;
import com.eas.widgets.containers.BoxPanel;
import com.eas.window.ToolsCaption;
import com.eas.window.WindowPanel;
import com.eas.window.WindowPopupPanel;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SetSelectionModel;

public class FindWindow extends WindowPanel {

	protected static GridMessages messages = GWT.create(GridMessages.class);

	private WindowPopupPanel popup = new WindowPopupPanel(this, true, false);
	private Label label;
	private TextBox field;
	private CheckBox checkCase;
	private CheckBox checkWhole;
	private Button btnFind;
	private Button closeButton;

	private Grid<JavaScriptObject> grid;

	private int row;
	private int col;

	public FindWindow(Grid<JavaScriptObject> aGrid) {
		super();
		popup.setResizable(false);
		popup.setMinimizable(false);
		popup.setMaximizable(false);
		ToolsCaption caption = new ToolsCaption(this, messages.heading());
		setCaptionWidget(caption);
		grid = aGrid;
		initComponents();
		setSize(345, 95);
	}

	@Override
	protected Widget getMovableTarget() {
		return popup != null ? popup : this;
	}

	private void initComponents() {
		label = new Label(messages.find());
		field = new TextBox();
		
		field.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
					findNext();
				}
			}
		});
		
		checkCase = new CheckBox();
		checkCase.setText(messages.caseSensitive());

		checkWhole = new CheckBox();
		checkWhole.setText(messages.wholeWords());

		btnFind = new Button(messages.findNext());
		btnFind.setPixelSize(90, 25);
		closeButton = new Button(messages.close());
		closeButton.setPixelSize(90, 25);

		btnFind.addClickHandler(new ClickHandler() {
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

		BoxPanel findBox = new BoxPanel();
		findBox.setOrientation(Orientation.VERTICAL);
		findBox.setVgap(10);

		BoxPanel settingsBox = new BoxPanel();
		settingsBox.setHgap(10);
		settingsBox.add(checkCase);
		settingsBox.add(checkWhole);

		findBox.add(field);
		findBox.add(settingsBox);

		AnchorsPanel anchors = new AnchorsPanel();

		anchors.add(label);
		anchors.setWidgetLeftWidth(label, 5, Style.Unit.PX, 40, Style.Unit.PX);
		anchors.setWidgetTopHeight(label, 5, Style.Unit.PX, 20, Style.Unit.PX);

		anchors.add(findBox);
		anchors.setWidgetLeftWidth(findBox, 60, Style.Unit.PX, 280, Style.Unit.PX);
		anchors.setWidgetTopHeight(findBox, 5, Style.Unit.PX, 50, Style.Unit.PX);

		anchors.add(btnFind);
		anchors.setWidgetRightWidth(btnFind, 5, Style.Unit.PX, 75, Style.Unit.PX);
		anchors.setWidgetTopHeight(btnFind, 65, Style.Unit.PX, 25, Style.Unit.PX);

		anchors.add(closeButton);
		anchors.setWidgetRightWidth(closeButton, 85, Style.Unit.PX, 75, Style.Unit.PX);
		anchors.setWidgetTopHeight(closeButton, 65, Style.Unit.PX, 25, Style.Unit.PX);

		setWidget(anchors);
	}

	public boolean findNext() {
		List<JavaScriptObject> data = grid.getDataProvider().getList();
		boolean caseSensitive = checkCase.getValue();
		boolean wholeString = checkWhole.getValue();
		String findText = field.getText();
		if (findText.isEmpty()) {
			return false;
		}
		if (!caseSensitive) {
			findText = findText.toLowerCase();
		}
		if (row >= data.size()) {
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

		for (; row < data.size(); row++) {
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
		if (aNode != null) {
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
		}
		return false;
	}

	private void selectCell(int aRow, int aCol) {
		List<JavaScriptObject> data = grid.getDataProvider().getList();
		if (grid.getSelectionModel() instanceof SetSelectionModel) {
			SetSelectionModel<JavaScriptObject> ssm = (SetSelectionModel<JavaScriptObject>) grid.getSelectionModel();
			ssm.clear();
			ssm.setSelected(data.get(aRow), true);
		}
		TableCellElement cell = grid.getViewCell(aRow, aCol);
		if (cell != null) {
			cell.scrollIntoView();
			//grid.focusViewCell(aRow, aCol);// this call leads to cell editing in subsequent enter hit
		}
	}

	public void show() {
		row = 0;
		col = 0;
		popup.center();
		activate();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				field.setFocus(true);
			}
		});
	}

}
