package com.eas.client.form.factories;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bearsoft.gwt.ui.widgets.ObjectFormat;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.Utils;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.combo.ComboLabelProvider;
import com.eas.client.form.grid.columns.BooleanModelGridColumn;
import com.eas.client.form.grid.columns.DateModelGridColumn;
import com.eas.client.form.grid.columns.DoubleModelGridColumn;
import com.eas.client.form.grid.columns.FormattedObjectModelGridColumn;
import com.eas.client.form.grid.columns.LookupModelGridColumn;
import com.eas.client.form.grid.columns.ModelGridColumn;
import com.eas.client.form.grid.columns.TextAreaModelGridColumn;
import com.eas.client.form.grid.columns.TextModelGridColumn;
import com.eas.client.form.grid.header.HeaderNode;
import com.eas.client.form.grid.rows.RowsetDataProvider;
import com.eas.client.form.published.widgets.model.ModelElementRef;
import com.eas.client.form.published.widgets.model.ModelGrid;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

public class GridFactory {

	//
	public static final int ONE_FIELD_ONE_QUERY_TREE_KIND = 1;
	public static final int FIELD_2_PARAMETER_TREE_KIND = 2;
	public static final int SCRIPT_PARAMETERS_TREE_KIND = 3;

	protected Element gridTag;

	protected int treeKind = ONE_FIELD_ONE_QUERY_TREE_KIND;
	protected ModelElementRef unaryLinkField;
	protected ModelElementRef param2GetChildren;
	protected ModelElementRef paramSourceField;
	protected Model model;
	protected Entity rowsSource;
	protected ModelGrid grid;
	// columns
	protected List<ComboLabelProvider> comboLabelProviders = new ArrayList<>();
	protected List<HeaderNode> headerForest = new ArrayList<>();
	protected List<ModelGridColumn<?>> columns = new ArrayList<>();

	protected Set<Entity> rowsetsOfInterestHosts = new HashSet<Entity>();

	public GridFactory(Element aTag, Model aModel) {
		super();
		gridTag = aTag;
		model = aModel;
	}

	public ModelGrid getModelGrid() {
		return grid;
	}

	public void process() throws Exception {
		Element rowsColumnsTag = Utils.getElementByTagName(gridTag, "rowsColumnsDesignInfo");
		int rowsHeaderType = Utils.getIntegerAttribute(rowsColumnsTag, "rowsHeaderType", ModelGrid.ROWS_HEADER_TYPE_USUAL);
		int fixedRows = Utils.getIntegerAttribute(rowsColumnsTag, "fixedRows", 0);
		int fixedColumns = Utils.getIntegerAttribute(rowsColumnsTag, "fixedColumns", 0);
		int rowsHeight = Utils.getIntegerAttribute(gridTag, "rowsHeight", 20);
		boolean rowLines = Utils.getBooleanAttribute(gridTag, "showHorizontalLines", true);
		boolean columnLines = Utils.getBooleanAttribute(gridTag, "showVerticalLines", true);
		boolean showOddRowsInOtherColor = Utils.getBooleanAttribute(gridTag, "showOddRowsInOtherColor", true);
		boolean editable = Utils.getBooleanAttribute(gridTag, "editable", true);
		final boolean deletable = Utils.getBooleanAttribute(gridTag, "deletable", true);
		final boolean insertable = Utils.getBooleanAttribute(gridTag, "insertable", true);
		final String generalCellFunctionName = rowsColumnsTag.getAttribute("generalRowFunction");
		ModelElementRef rowsModelElement = new ModelElementRef(Utils.getElementByTagName(rowsColumnsTag, "rowsDatasource"), model);
		rowsSource = rowsModelElement.entity;
		rowsetsOfInterestHosts.add(rowsSource);

		Element treeTag = Utils.getElementByTagName(gridTag, "treeDesignInfo");
		unaryLinkField = new ModelElementRef(Utils.getElementByTagName(treeTag, "unaryLinkField"), model);
		treeKind = Utils.getIntegerAttribute(treeTag, "treeKind", ONE_FIELD_ONE_QUERY_TREE_KIND);
		param2GetChildren = new ModelElementRef(Utils.getElementByTagName(treeTag, "param2GetChildren"), model);
		paramSourceField = new ModelElementRef(Utils.getElementByTagName(treeTag, "paramSourceField"), model);

		grid = new ModelGrid();
		grid.setRowsSource(rowsSource);
		// Selection models & service column configuration
		grid.setRowsHeaderType(rowsHeaderType);
		// Columns multi level structure
		NodeList nodesWithColumns = gridTag.getChildNodes();
		for (int i = 0; i < nodesWithColumns.getLength(); i++) {
			if ("column".equalsIgnoreCase(nodesWithColumns.item(i).getNodeName())) {
				assert nodesWithColumns.item(i) instanceof Element : "Column must be a tag";
				Element columnTag = (Element) nodesWithColumns.item(i);
				processColumn(columnTag, 0, headerForest);
			}
		}
		// Data plain and tree(optional) layers
		if (isTreeConfigured()) {
		} else {
		}
		/*
		 * editing.setEditable(editable); editing.setDeletable(deletable);
		 * editing.setInsertable(insertable);
		 */
		// row lines ?
		// column lines ?
		for (Entity entity : rowsetsOfInterestHosts) {
			grid.addUpdatingTriggerEntity(entity);
		}
		for (ComboLabelProvider labelProvider : comboLabelProviders) {
			labelProvider.setOnLabelCacheChange(grid.getCrossUpdaterAction());
		}
	}

	private boolean isLazyTreeConfigured() {
		return param2GetChildren.isCorrect() && param2GetChildren.field != null && paramSourceField.isCorrect() && paramSourceField.field != null;
	}

	private boolean isTreeConfigured() throws Exception {
		return rowsSource != null && unaryLinkField.isCorrect() && unaryLinkField.field != null && (treeKind == ONE_FIELD_ONE_QUERY_TREE_KIND || treeKind == FIELD_2_PARAMETER_TREE_KIND);
	}

	private void processColumn(Element aTag, int deepness, List<HeaderNode> header) throws Exception {
		HeaderNode hNode = new HeaderNode();
		header.add(hNode);
		// plain settings
		String name = aTag.getAttribute("name");
		String title = aTag.getAttribute("title");
		ModelElementRef modelElement = null;
		Element controlTag = null;
		// style
		Element headerEasFontTag = null;
		Element styleTag = null;
		NodeList columnNodes = aTag.getChildNodes();
		int childrenCount = columnNodes != null ? columnNodes.getLength() : 0;
		for (int c = 0; c < childrenCount; c++) {
			if ("datamodelElement".equalsIgnoreCase(columnNodes.item(c).getNodeName()))
				modelElement = new ModelElementRef((Element) columnNodes.item(c), model);
			else if ("controlInfo".equalsIgnoreCase(columnNodes.item(c).getNodeName()))
				controlTag = (Element) columnNodes.item(c);
			// header style
			else if ("headerEasFont".equalsIgnoreCase(columnNodes.item(c).getNodeName()))
				headerEasFontTag = (Element) columnNodes.item(c);
			else if ("style".equalsIgnoreCase(columnNodes.item(c).getNodeName()))
				styleTag = (Element) columnNodes.item(c);
			else if ("column".equalsIgnoreCase(columnNodes.item(c).getNodeName())) {
				processColumn((Element) columnNodes.item(c), deepness + 1, hNode.getChildren());
			}
		}
		if (childrenCount == 0)
			configureColumn(name, title, modelElement, aTag, controlTag);
	}

	private void configureColumn(String aName, String aTitle, final ModelElementRef aColModelElement, Element aColumnTag, Element aControlTag) throws Exception {
		ModelGridColumn<?> column = null;
		
		boolean visible = Utils.getBooleanAttribute(aColumnTag, "visible", true);
		boolean enabled = Utils.getBooleanAttribute(aColumnTag, "enabled", true);
		int width = Utils.getIntegerAttribute(aColumnTag, "width", 50);
		boolean readonly = Utils.getBooleanAttribute(aColumnTag, "readonly", false);
		boolean selectOnly = Utils.getBooleanAttribute(aColumnTag, "selectOnly", false);
		boolean fixed = Utils.getBooleanAttribute(aColumnTag, "fixed", false);

		if (aColModelElement != null && aColModelElement.isCorrect()) {
			if (rowsSource != null && rowsSource != aColModelElement.entity)
				rowsetsOfInterestHosts.add(aColModelElement.entity);
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			sb.appendHtmlConstant((aTitle != null && !aTitle.isEmpty()) ? aTitle : aName);
			String controlInfoName = aControlTag.getAttribute("classHint");
			if ("DbSchemeDesignInfo".equalsIgnoreCase(controlInfoName)) {
			} else if ("DbGridDesignInfo".equalsIgnoreCase(controlInfoName)) {
			} else if ("DbMapDesignInfo".equalsIgnoreCase(controlInfoName)) {
			} else if ("DbLabelDesignInfo".equalsIgnoreCase(controlInfoName)) {
				String formatPattern = aControlTag.getAttribute("format");
				int formatType = Utils.getIntegerAttribute(aControlTag, "valueType", ObjectFormat.MASK);
				FormattedObjectModelGridColumn _column = new FormattedObjectModelGridColumn(aName);
				column = _column;
				_column.setFormatType(formatType, formatPattern);
			} else if ("DbTextDesignInfo".equalsIgnoreCase(controlInfoName)) {
				column = new TextAreaModelGridColumn(aName);
			} else if ("DbDateDesignInfo".equalsIgnoreCase(controlInfoName)) {
				DateModelGridColumn _column = new DateModelGridColumn(aName);
				column = _column;
				String dateFormat = aControlTag.getAttribute("dateFormat");
				if (dateFormat != null)
					dateFormat = ControlsUtils.convertDateFormatString(dateFormat);
				final DateTimeFormat dtFormat = dateFormat != null ? DateTimeFormat.getFormat(dateFormat) : DateTimeFormat.getFormat(PredefinedFormat.DATE_FULL);
				_column.setFormat(dtFormat);
			} else if ("DbImageDesignInfo".equalsIgnoreCase(controlInfoName)) {
			} else if ("DbCheckDesignInfo".equalsIgnoreCase(controlInfoName)) {
				column = new BooleanModelGridColumn(aName);
			} else if ("DbSpinDesignInfo".equalsIgnoreCase(controlInfoName)) {
				DoubleModelGridColumn _column = new DoubleModelGridColumn(aName);
				column = _column;
				if (aControlTag.hasAttribute("min"))
					_column.setMin(Double.valueOf(aControlTag.getAttribute("min")));
				else
					_column.setMin(-Double.MAX_VALUE);
				if (aControlTag.hasAttribute("max"))
					_column.setMax(Double.valueOf(aControlTag.getAttribute("max")));
				else
					_column.setMax(Double.MAX_VALUE);
				if (aControlTag.hasAttribute("step"))
					_column.setStep(Double.valueOf(aControlTag.getAttribute("step")));
			} else if ("DbComboDesignInfo".equalsIgnoreCase(controlInfoName)) {
				LookupModelGridColumn _column = new LookupModelGridColumn(aName);
				column= _column;
				final ModelElementRef valueRef = new ModelElementRef(Utils.getElementByTagName(aControlTag, "valueField"), model);
				final ModelElementRef displayRef = new ModelElementRef(Utils.getElementByTagName(aControlTag, "displayField"), model);
				rowsetsOfInterestHosts.add(valueRef.entity);
				rowsetsOfInterestHosts.add(displayRef.entity);
				final boolean list = Utils.getBooleanAttribute(aControlTag, "list", true);
				final ComboLabelProvider labelProvider = new ComboLabelProvider();
				labelProvider.setTargetValueRef(aColModelElement);
				labelProvider.setLookupValueRef(valueRef);
				labelProvider.setDisplayValueRef(displayRef);
				comboLabelProviders.add(labelProvider);
				RowsetDataProvider filler = new RowsetDataProvider(null);
				if (valueRef.entity != null) {
					filler.setRowset(valueRef.entity.getRowset());
				}
				if (displayRef.entity != null) {
					Rowset displayRowset = displayRef.entity.getRowset();
					if (displayRowset != null)
						displayRowset.addRowsetListener(filler.getRowsetReflector());
				}
			}
		} else {
			// Cell calculated only by cell function
			column = new TextModelGridColumn(aName);
		}
		if(column != null){
			columns.add(column);
			column.setRowsEntity(rowsSource);
			column.setColumnModelRef(aColModelElement);
			column.setWidth(width);
			column.setReadonly(readonly);
			column.setFixed(fixed);
			column.setSelectOnly(selectOnly);
			column.setVisible(visible);
		}
	}
}
