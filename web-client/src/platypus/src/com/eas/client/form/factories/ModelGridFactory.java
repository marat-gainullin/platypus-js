package com.eas.client.form.factories;

import java.util.ArrayList;
import java.util.List;

import com.bearsoft.gwt.ui.widgets.ObjectFormat;
import com.bearsoft.gwt.ui.widgets.grid.header.HeaderNode;
import com.bearsoft.rowset.Utils;
import com.eas.client.form.ControlsUtils;
import com.eas.client.form.grid.columns.BooleanModelGridColumn;
import com.eas.client.form.grid.columns.DateModelColumn;
import com.eas.client.form.grid.columns.DoubleModelColumn;
import com.eas.client.form.grid.columns.FormattedObjectModelColumn;
import com.eas.client.form.grid.columns.LookupModelGridColumn;
import com.eas.client.form.grid.columns.ModelColumn;
import com.eas.client.form.grid.columns.TextAreaModelColumn;
import com.eas.client.form.grid.columns.TextModelColumn;
import com.eas.client.form.published.PublishedColor;
import com.eas.client.form.published.PublishedStyle;
import com.eas.client.form.published.widgets.model.ModelElementRef;
import com.eas.client.form.published.widgets.model.ModelGrid;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class ModelGridFactory {

	protected Element gridTag;
	//
	protected Model model;
	protected Entity rowsSource;
	protected List<HeaderNode> headerForest = new ArrayList<>();
	protected ModelGrid grid;

	public ModelGridFactory(Element aTag, Model aModel) {
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
		PublishedColor gridColor = gridTag.hasAttribute("gridColorValue") ? ControlsUtils.parseColor(gridTag.getAttribute("gridColorValue")) : null;
		PublishedColor oddRowsColor = gridTag.hasAttribute("oddRowsColorValue") ? ControlsUtils.parseColor(gridTag.getAttribute("oddRowsColorValue")) : null;
		//
		boolean editable = Utils.getBooleanAttribute(gridTag, "editable", true);
		final boolean deletable = Utils.getBooleanAttribute(gridTag, "deletable", true);
		final boolean insertable = Utils.getBooleanAttribute(gridTag, "insertable", true);
		ModelElementRef rowsModelElement = new ModelElementRef(Utils.getElementByTagName(rowsColumnsTag, "rowsDatasource"), model);
		Element treeTag = Utils.getElementByTagName(gridTag, "treeDesignInfo");
		ModelElementRef unaryLinkField = new ModelElementRef(Utils.getElementByTagName(treeTag, "unaryLinkField"), model);
		int treeKind = Utils.getIntegerAttribute(treeTag, "treeKind", ModelGrid.ONE_FIELD_ONE_QUERY_TREE_KIND);
		ModelElementRef param2GetChildren = new ModelElementRef(Utils.getElementByTagName(treeTag, "param2GetChildren"), model);
		ModelElementRef paramSourceField = new ModelElementRef(Utils.getElementByTagName(treeTag, "paramSourceField"), model);

		rowsSource = rowsModelElement.entity;

		grid = new ModelGrid();

		grid.setUnaryLinkField(unaryLinkField);
		grid.setTreeKind(treeKind);
		grid.setParam2GetChildren(param2GetChildren);
		grid.setParamSourceField(paramSourceField);

		grid.setRowsSource(rowsSource);

		grid.setEditable(editable);
		grid.setDeletable(deletable);
		grid.setInsertable(insertable);
		grid.setRowsHeight(rowsHeight);
		grid.setShowHorizontalLines(rowLines);
		grid.setShowVerticalLines(columnLines);
		if(gridColor != null)
			grid.setGridColor(gridColor);
		if(oddRowsColor != null)
			grid.setOddRowsColor(oddRowsColor);
		grid.setShowOddRowsInOtherColor(showOddRowsInOtherColor);
		// Selection models & service column configuration
		grid.setRowsHeaderType(rowsHeaderType);
		headerForest.addAll(0, grid.getHeader());
		grid.setHeaderAjusting(true);
		try {
			// Columns multi level structure
			NodeList nodesWithColumns = gridTag.getChildNodes();
			for (int i = 0; i < nodesWithColumns.getLength(); i++) {
				if ("column".equalsIgnoreCase(nodesWithColumns.item(i).getNodeName())) {
					assert nodesWithColumns.item(i) instanceof Element : "Column must be a tag";
					Element columnTag = (Element) nodesWithColumns.item(i);
					processHeaderNode(columnTag, headerForest, null);
				}
			}
			grid.setEditable(editable);
			grid.setInsertable(insertable);
			grid.setDeletable(deletable);
			grid.setHeader(headerForest);
			grid.setFrozenColumns(fixedColumns);
			grid.setFrozenRows(fixedRows);
		} finally {
			grid.setHeaderAjusting(false);
			grid.applyHeader();
		}
	}

	private void processHeaderNode(Element aTag, List<HeaderNode> aHeaderChildren, HeaderNode aHeaderParent) throws Exception {
		HeaderNode hNode = new HeaderNode();
		// plain settings
		String name = aTag.getAttribute("name");
		String title = aTag.getAttribute("title");
		if (title == null)
			title = name;
		ModelElementRef modelElement = null;
		Element controlTag = null;
		// style
		Element styleTag = null;
		NodeList columnNodes = aTag.getChildNodes();
		int childColumnsCount = 0;
		int childTagsCount = columnNodes != null ? columnNodes.getLength() : 0;
		for (int c = 0; c < childTagsCount; c++) {
			if ("datamodelElement".equalsIgnoreCase(columnNodes.item(c).getNodeName())){
				modelElement = new ModelElementRef((Element) columnNodes.item(c), model);
			}else if ("controlInfo".equalsIgnoreCase(columnNodes.item(c).getNodeName())){
				controlTag = (Element) columnNodes.item(c);
			// header style
			} else if ("headerStyle".equalsIgnoreCase(columnNodes.item(c).getNodeName())) {
				styleTag = (Element) columnNodes.item(c);
			} else if ("column".equalsIgnoreCase(columnNodes.item(c).getNodeName())) {
				childColumnsCount++;
				processHeaderNode((Element) columnNodes.item(c), hNode.getChildren(), hNode);
			}
		}
		if (childColumnsCount == 0) {
			HeaderNode _hNode = configureColumn(name, title, modelElement, aTag, controlTag);
			if (_hNode != null)
				hNode = _hNode;
		} else {
			SafeHtml safeHtml = title.startsWith("<html>") ? SafeHtmlUtils.fromTrustedString(title.substring(6)) : SafeHtmlUtils.fromString(title);
			hNode.setHeader(new SafeHtmlHeader(safeHtml));
		}
		hNode.setParent(aHeaderParent);
		if(styleTag != null){
			PublishedStyle hNodeStyle = PublishedStyle.create();
			hNode.setStyle(hNodeStyle);
			NodeList headerStyleNodes = styleTag.getChildNodes();		
			int headerStyleTagsCount = headerStyleNodes != null ? headerStyleNodes.getLength() : 0;
			for (int c = 0; c < headerStyleTagsCount; c++) {
				Node n = headerStyleNodes.item(c);
				if ("nativeFont".equalsIgnoreCase(n.getNodeName())){
					hNodeStyle.setFont(WidgetsFactory.parseFont((Element)n));
				}				
			}
			if(styleTag.hasAttribute("backgroundColor")){
				hNodeStyle.setBackground(ControlsUtils.parseColor(styleTag.getAttribute("backgroundColor")));
			}
			if(styleTag.hasAttribute("foregroundColor")){
				hNodeStyle.setForeground(ControlsUtils.parseColor(styleTag.getAttribute("foregroundColor")));
			}
		}
		aHeaderChildren.add(hNode);
	}

	private HeaderNode configureColumn(String aName, String aTitle, final ModelElementRef aColModelElement, Element aColumnTag, Element aControlTag) throws Exception {
		ModelColumn<?> column = null;

		boolean visible = Utils.getBooleanAttribute(aColumnTag, "visible", true);
		boolean enabled = Utils.getBooleanAttribute(aColumnTag, "enabled", true);
		int width = Utils.getIntegerAttribute(aColumnTag, "width", 50);
		boolean readonly = Utils.getBooleanAttribute(aColumnTag, "readonly", false);
		boolean selectOnly = Utils.getBooleanAttribute(aColumnTag, "selectOnly", false);
		boolean fixed = Utils.getBooleanAttribute(aColumnTag, "fixed", false);

		if (aColModelElement != null && aColModelElement.isCorrect()) {
			SafeHtmlBuilder sb = new SafeHtmlBuilder();
			sb.appendHtmlConstant((aTitle != null && !aTitle.isEmpty()) ? aTitle : aName);
			String controlInfoName = aControlTag.getAttribute("classHint");
			if ("DbSchemeDesignInfo".equalsIgnoreCase(controlInfoName)) {
			} else if ("DbGridDesignInfo".equalsIgnoreCase(controlInfoName)) {
			} else if ("DbMapDesignInfo".equalsIgnoreCase(controlInfoName)) {
			} else if ("DbLabelDesignInfo".equalsIgnoreCase(controlInfoName)) {
				String formatPattern = aControlTag.getAttribute("format");
				int formatType = Utils.getIntegerAttribute(aControlTag, "valueType", ObjectFormat.MASK);
				FormattedObjectModelColumn _column = new FormattedObjectModelColumn(aName);
				if(aControlTag.hasAttribute("emptyText"))
					_column.setEmptyText(aControlTag.getAttribute("emptyText"));
				column = _column;
				_column.setFormatType(formatType, formatPattern);
			} else if ("DbTextDesignInfo".equalsIgnoreCase(controlInfoName)) {
				TextAreaModelColumn _column = new TextAreaModelColumn(aName);
				column = _column;
				if(aControlTag.hasAttribute("emptyText"))
					_column.setEmptyText(aControlTag.getAttribute("emptyText"));
			} else if ("DbDateDesignInfo".equalsIgnoreCase(controlInfoName)) {
				DateModelColumn _column = new DateModelColumn(aName);
				column = _column;
				if(aControlTag.hasAttribute("emptyText"))
					_column.setEmptyText(aControlTag.getAttribute("emptyText"));
				String dateFormat = aControlTag.getAttribute("dateFormat");
				if (dateFormat != null)
					dateFormat = ControlsUtils.convertDateFormatString(dateFormat);
				final DateTimeFormat dtFormat = dateFormat != null ? DateTimeFormat.getFormat(dateFormat) : DateTimeFormat.getFormat(PredefinedFormat.DATE_FULL);
				_column.setFormat(dtFormat);
			} else if ("DbImageDesignInfo".equalsIgnoreCase(controlInfoName)) {
			} else if ("DbCheckDesignInfo".equalsIgnoreCase(controlInfoName)) {
				column = new BooleanModelGridColumn(aName);
			} else if ("DbSpinDesignInfo".equalsIgnoreCase(controlInfoName)) {
				DoubleModelColumn _column = new DoubleModelColumn(aName);
				column = _column;
				if(aControlTag.hasAttribute("emptyText"))
					_column.setEmptyText(aControlTag.getAttribute("emptyText"));
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
				column = _column;
				final ModelElementRef valueRef = new ModelElementRef(Utils.getElementByTagName(aControlTag, "valueField"), model);
				final ModelElementRef displayRef = new ModelElementRef(Utils.getElementByTagName(aControlTag, "displayField"), model);
				final boolean list = Utils.getBooleanAttribute(aControlTag, "list", true);
				if(aControlTag.hasAttribute("emptyText"))
					_column.setEmptyText(aControlTag.getAttribute("emptyText"));
				_column.setLookupValueRef(valueRef);
				_column.setDisplayValueRef(displayRef);
				_column.setList(list);
			}
		} else {
			// Cell calculated only by cell function
			column = new TextModelColumn(aName);
		}
		if (column != null) {
			column.setTitle(aTitle);
			column.setRowsEntity(rowsSource);
			column.setColumnModelRef(aColModelElement);
			column.setWidth(width);
			column.setReadonly(readonly);
			column.setFixed(fixed);
			column.setSelectOnly(selectOnly);
			column.setVisible(visible);
			column.setSortable(true);
			grid.addColumn(column);
			return column.getHeaderNode();
		} else {
			return null;
		}
	}
}
