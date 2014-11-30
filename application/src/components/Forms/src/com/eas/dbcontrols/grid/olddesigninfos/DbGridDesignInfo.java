/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.olddesigninfos;

import com.eas.client.model.ModelElementRef;
import com.eas.client.model.ModelEntityParameterRef;
import com.eas.client.model.ModelEntityRef;
import com.eas.controls.DesignInfo;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;
import com.eas.gui.CascadedStyle;
import com.eas.store.Serial;
import com.eas.store.SerialCollection;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class DbGridDesignInfo extends DbControlDesignInfo {

    public static final String HEADER = "header";
    public static final String ROWSCOLUMNSDESIGNINFO = "rowsColumnsDesignInfo";
    public static final String TREEDESIGNINFO = "treeDesignInfo";
    public static final String ROWSHEIGHT = "rowsHeight";
    public static final String SHOWHORIZONTALLINES = "showHorizontalLines";
    public static final String SHOWVERTICALLINES = "showVerticalLines";
    public static final String SHOWODDROWSINOTHERCOLOR = "showOddRowsInOtherColor";
    public static final String ODDROWSCOLOR = "oddRowsColor";
    public static final String GRIDCOLOR = "gridColor";
    
    protected DbGridRowsColumnsDesignInfo rowsColumnsDesignInfo = new DbGridRowsColumnsDesignInfo();
    protected List<DbGridColumn> header = new ArrayList<>();
    protected DbGridTreeDesignInfo treeDesignInfo = new DbGridTreeDesignInfo();
    protected boolean insertable = true;
    protected boolean deletable = true;
    protected int rowsHeight = 20;
    protected boolean showVerticalLines = true;
    protected boolean showHorizontalLines = true;
    protected boolean showOddRowsInOtherColor = true;
    protected Color oddRowsColor = null;
    protected Color gridColor = null;

    public DbGridDesignInfo() {
        super();
    }

    @Serial
    public String getOddRowsColorValue() {
        return oddRowsColor != null ? CascadedStyle.encodeColor(oddRowsColor) : null;
    }

    @Serial
    public void setOddRowsColorValue(String aValue) {
        try {
            Color oldValue = oddRowsColor;
            if (aValue != null) {
                oddRowsColor = Color.decode(aValue);
            } else {
                oddRowsColor = null;
            }
            firePropertyChange(ODDROWSCOLOR, oldValue, oddRowsColor);
        } catch (NumberFormatException ex) {
            Logger.getLogger(DbGridDesignInfo.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    public Color getOddRowsColor() {
        return oddRowsColor;
    }

    public void setOddRowsColor(Color aValue) {
        Color oldValue = oddRowsColor;
        oddRowsColor = aValue;
        firePropertyChange(ODDROWSCOLOR, oldValue, oddRowsColor);
    }
    
    @Serial
    public String getGridColorValue() {
        return gridColor != null ? CascadedStyle.encodeColor(gridColor) : null;
    }

    @Serial
    public void setGridColorValue(String aValue) {
        try {
            Color oldValue = gridColor;
            if (aValue != null) {
                gridColor = Color.decode(aValue);
            } else {
                gridColor = null;
            }
            firePropertyChange(GRIDCOLOR, oldValue, gridColor);
        } catch (NumberFormatException ex) {
            Logger.getLogger(DbGridDesignInfo.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(Color aValue) {
        Color oldValue = gridColor;
        gridColor = aValue;
        firePropertyChange(GRIDCOLOR, oldValue, gridColor);
    }
    
    @Serial
    public boolean isInsertable() {
        return insertable;
    }

    @Serial
    public void setInsertable(boolean aValue) {
        boolean oldValue = insertable;
        insertable = aValue;
        firePropertyChange("insertable", oldValue, insertable);
    }

    @Serial
    public boolean isDeletable() {
        return deletable;
    }

    @Serial
    public void setDeletable(boolean aValue) {
        boolean oldValue = deletable;
        deletable = aValue;
        firePropertyChange("deletable", oldValue, deletable);
    }

    @Override
    public boolean isEqual(Object obj) {
        if (!super.isEqual(obj)) {
            return false;
        }
        final DbGridDesignInfo other = (DbGridDesignInfo) obj;

        if (this.insertable != other.insertable) {
            return false;
        }
        if (this.deletable != other.deletable) {
            return false;
        }
        if (this.showHorizontalLines != other.showHorizontalLines) {
            return false;
        }
        if (this.showVerticalLines != other.showVerticalLines) {
            return false;
        }
        if (this.showOddRowsInOtherColor != other.showOddRowsInOtherColor) {
            return false;
        }
        if (this.rowsHeight != other.rowsHeight) {
            return false;
        }        
        if (this.oddRowsColor != other.oddRowsColor && (this.oddRowsColor == null || !this.oddRowsColor.equals(other.oddRowsColor))) {
            return false;
        }
        if (this.gridColor != other.gridColor && (this.gridColor == null || !this.gridColor.equals(other.gridColor))) {
            return false;
        }
        if (this.rowsColumnsDesignInfo != other.rowsColumnsDesignInfo && (this.rowsColumnsDesignInfo == null || !this.rowsColumnsDesignInfo.isEqual(other.rowsColumnsDesignInfo))) {
            return false;
        }
        if (this.header != other.header && (this.header == null || !DbGridColumn.isColumnsEquals(this.header, other.header))) {
            return false;
        }
        if (this.treeDesignInfo != other.treeDesignInfo && (this.treeDesignInfo == null || !this.treeDesignInfo.isEqual(other.treeDesignInfo))) {
            return false;
        }
        return true;
    }

    @Override
    public void assign(DesignInfo aSource) {
        super.assign(aSource);
        if (aSource instanceof DbGridDesignInfo) {
            DbGridDesignInfo aInfo = (DbGridDesignInfo) aSource;
            insertable = aInfo.isInsertable();
            deletable = aInfo.isDeletable();
            rowsHeight = aInfo.getRowsHeight();
            showVerticalLines = aInfo.isShowVerticalLines();
            showHorizontalLines = aInfo.isShowHorizontalLines();
            showOddRowsInOtherColor = aInfo.isShowOddRowsInOtherColor();
            oddRowsColor = aInfo.oddRowsColor != null ? new Color(aInfo.oddRowsColor.getRed(), aInfo.oddRowsColor.getGreen(), aInfo.oddRowsColor.getBlue(), aInfo.oddRowsColor.getAlpha()) : null;
            gridColor = aInfo.gridColor != null ? new Color(aInfo.gridColor.getRed(), aInfo.gridColor.getGreen(), aInfo.gridColor.getBlue(), aInfo.gridColor.getAlpha()) : null;
            if (aInfo.getRowsColumnsDesignInfo() != null) {
                setRowsColumnsDesignInfo((DbGridRowsColumnsDesignInfo) aInfo.getRowsColumnsDesignInfo().copy());
            } else {
                setRowsColumnsDesignInfo(null);
            }
            if (aInfo.getHeader() != null) {
                List<DbGridColumn> sourceHeader = aInfo.getHeader();
                List<DbGridColumn> newHeader = new ArrayList<>();
                for (int i = 0; i < sourceHeader.size(); i++) {
                    if (sourceHeader.get(i) != null) {
                        newHeader.add(sourceHeader.get(i).copy());
                    } else {
                        newHeader.add(null);
                    }
                }
                setHeader(newHeader);
                assert header.size() == sourceHeader.size();
            } else {
                setHeader(null);
            }
            if (aInfo.getTreeDesignInfo() != null) {
                setTreeDesignInfo((DbGridTreeDesignInfo) aInfo.getTreeDesignInfo().copy());
            } else {
                setTreeDesignInfo(null);
            }
        }
    }

    @Serial
    public DbGridRowsColumnsDesignInfo getRowsColumnsDesignInfo() {
        return rowsColumnsDesignInfo;
    }

    @Serial
    public void setRowsColumnsDesignInfo(DbGridRowsColumnsDesignInfo aValue) {
        DbGridRowsColumnsDesignInfo old = rowsColumnsDesignInfo;
        rowsColumnsDesignInfo = aValue;
        firePropertyChange(ROWSCOLUMNSDESIGNINFO, old, aValue);
    }

    @SerialCollection(elementType = DbGridColumn.class, elementTagName = "column", deserializeAs = ArrayList.class)
    public List<DbGridColumn> getHeader() {
        return header;
    }

    @SerialCollection(elementType = DbGridColumn.class, elementTagName = "column", deserializeAs = ArrayList.class)
    public void setHeader(List<DbGridColumn> aValue) {
        List<DbGridColumn> old = header;
        header = aValue;
        firePropertyChange(HEADER, old, aValue);
    }

    @Serial
    public DbGridTreeDesignInfo getTreeDesignInfo() {
        return treeDesignInfo;
    }

    @Serial
    public void setTreeDesignInfo(DbGridTreeDesignInfo aValue) {
        DbGridTreeDesignInfo old = treeDesignInfo;
        treeDesignInfo = aValue;
        firePropertyChange(TREEDESIGNINFO, old, aValue);
    }

    @Serial
    public int getRowsHeight() {
        return rowsHeight;
    }

    @Serial
    public void setRowsHeight(int aValue) {
        int old = rowsHeight;
        rowsHeight = aValue;
        firePropertyChange(ROWSHEIGHT, old, aValue);
    }

    @Serial
    public boolean isShowHorizontalLines() {
        return showHorizontalLines;
    }

    @Serial
    public void setShowHorizontalLines(boolean aValue) {
        boolean old = showHorizontalLines;
        showHorizontalLines = aValue;
        firePropertyChange(SHOWHORIZONTALLINES, old, aValue);
    }

    @Serial
    public boolean isShowVerticalLines() {
        return showVerticalLines;
    }

    @Serial
    public void setShowVerticalLines(boolean aValue) {
        boolean old = showVerticalLines;
        showVerticalLines = aValue;
        firePropertyChange(SHOWVERTICALLINES, old, aValue);
    }

    @Serial
    public boolean isShowOddRowsInOtherColor() {
        return showOddRowsInOtherColor;
    }

    @Serial
    public void setShowOddRowsInOtherColor(boolean aValue) {
        boolean old = showOddRowsInOtherColor;
        showOddRowsInOtherColor = aValue;
        firePropertyChange(SHOWODDROWSINOTHERCOLOR, old, aValue);
    }

    public void replaceRowsDatasourceID(Long newRowsDatasourceID) {
        if (rowsColumnsDesignInfo != null && rowsColumnsDesignInfo.getRowsDatasource() != null) {
            Long rowsDsId = rowsColumnsDesignInfo.getRowsDatasource().getEntityId();
            if (rowsDsId != null) {
                // treeDesignInfo
                if (treeDesignInfo != null) {
                    checkDatamodelElementRef(rowsDsId, newRowsDatasourceID, treeDesignInfo.getUnaryLinkField());
                    checkDatamodelElementRef(rowsDsId, newRowsDatasourceID, treeDesignInfo.getParam2GetChildren());
                    checkDatamodelElementRef(rowsDsId, newRowsDatasourceID, treeDesignInfo.getParamSourceField());
                }
                if (header != null) {
                    for (int i = 0; i < header.size(); i++) {
                        replaceRowsDatasourceIDInCol(rowsDsId, newRowsDatasourceID, header.get(i));
                    }
                }
            }
        }
    }

    private void replaceRowsDatasourceIDInCol(Long rowsDsId, Long newRowsDatasourceID, DbGridColumn aCol) {
        if (aCol != null) {
            checkDatamodelElementRef(rowsDsId, newRowsDatasourceID, aCol.getCellsDatasource());
            checkDatamodelElementRef(rowsDsId, newRowsDatasourceID, aCol.getColumnsDatasource());
            checkDatamodelElementRef(rowsDsId, newRowsDatasourceID, aCol.getColumnsDisplayField());
            checkDatamodelElementRef(rowsDsId, newRowsDatasourceID, aCol.getDatamodelElement());
            if (aCol.getCellDesignInfo() != null) {
                checkDatamodelElementRef(rowsDsId, newRowsDatasourceID, aCol.getCellDesignInfo().getCellValueField());
                checkDatamodelElementRef(rowsDsId, newRowsDatasourceID, aCol.getCellDesignInfo().getColumnsKeyField());
                checkDatamodelElementRef(rowsDsId, newRowsDatasourceID, aCol.getCellDesignInfo().getRowsKeyField());
            }
            List<DbGridColumn> children = aCol.getChildren();
            if (children != null) {
                for (int i = 0; i < children.size(); i++) {
                    replaceRowsDatasourceIDInCol(rowsDsId, newRowsDatasourceID, children.get(i));
                }
            }
        }
    }

    private void checkDatamodelElementRef(Long rowsDsId, Long newRowsDatasourceID, ModelElementRef aDmRef) {
        if (aDmRef != null && rowsDsId != null && newRowsDatasourceID != null
                && aDmRef.getEntityId() != null && aDmRef.getEntityId().equals(rowsDsId)) {
            aDmRef.setEntityId(newRowsDatasourceID);
        }
    }

    @Override
    protected void accept(DbControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
    
    // tree design info projection
    public int getTreeKind() {
        return treeDesignInfo.getTreeKind();
    }

    public void setTreeKind(int aValue) {
        int old = treeDesignInfo.getTreeKind();
        treeDesignInfo.setTreeKind(aValue);
        firePropertyChange(DbGridTreeDesignInfo.TREEKIND, old, aValue);
    }

    public ModelElementRef getUnaryLinkField() {
        return treeDesignInfo.getUnaryLinkField();
    }

    public void setUnaryLinkField(ModelElementRef aValue) {
        ModelElementRef old = treeDesignInfo.getUnaryLinkField();
        treeDesignInfo.setUnaryLinkField(aValue);
        firePropertyChange(DbGridTreeDesignInfo.UNARYLINKFIELD, old, aValue);
    }

    public ModelElementRef getParamSourceField() {
        return treeDesignInfo.getParamSourceField();
    }

    public void setParamSourceField(ModelElementRef aValue) {
        ModelElementRef old = treeDesignInfo.getParamSourceField();
        treeDesignInfo.setParamSourceField(aValue);
        firePropertyChange(DbGridTreeDesignInfo.PARAMSOURCEFIELD, old, aValue);
    }

    public ModelEntityParameterRef getParam2GetChildren() {
        return treeDesignInfo.getParam2GetChildren();
    }

    public void setParam2GetChildren(ModelEntityParameterRef aValue) {
        ModelEntityParameterRef old = treeDesignInfo.getParam2GetChildren();
        treeDesignInfo.setParam2GetChildren(aValue);
        firePropertyChange(DbGridTreeDesignInfo.PARAM2GETCHILDREN, old, aValue);
    }

    public String getParametersSetupScript2GetChildren() {
        return treeDesignInfo.getParametersSetupScript2GetChildren();
    }

    public void setParametersSetupScript2GetChildren(String aValue) {
        String old = treeDesignInfo.getParametersSetupScript2GetChildren();
        treeDesignInfo.setParametersSetupScript2GetChildren(aValue);
        firePropertyChange(DbGridTreeDesignInfo.PARAMETERSSETUPSCRIPT2GETCHILDREN, old, aValue);
    }
    
    // rows columns design info projection
    
    public ModelEntityRef getRowsDatasource() {
        return rowsColumnsDesignInfo.getRowsDatasource();
    }

    public void setRowsDatasource(ModelEntityRef aValue) {
        ModelElementRef old = rowsColumnsDesignInfo.getRowsDatasource();
        rowsColumnsDesignInfo.setRowsDatasource(aValue);
        firePropertyChange(DbGridRowsColumnsDesignInfo.ROWSDATASOURCE, old, aValue);
    }

    public int getFixedRows() {
        return rowsColumnsDesignInfo.getFixedRows();
    }

    public void setFixedRows(int aValue) {
        int old = rowsColumnsDesignInfo.getFixedRows();
        rowsColumnsDesignInfo.setFixedRows(aValue);
        firePropertyChange(DbGridRowsColumnsDesignInfo.FIXEDROWS, old, aValue);
    }

    public int getFixedColumns() {
        return rowsColumnsDesignInfo.getFixedColumns();
    }

    public void setFixedColumns(int aValue) {
        int old = rowsColumnsDesignInfo.getFixedColumns();
        rowsColumnsDesignInfo.setFixedColumns(aValue);
        firePropertyChange(DbGridRowsColumnsDesignInfo.FIXEDCOLUMNS, old, aValue);
    }

    public int getRowsHeaderType() {
        return rowsColumnsDesignInfo.getRowsHeaderType();
    }

    public void setRowsHeaderType(int aValue) {
        int old = rowsColumnsDesignInfo.getRowsHeaderType();
        rowsColumnsDesignInfo.setRowsHeaderType(aValue);
        firePropertyChange(DbGridRowsColumnsDesignInfo.ROWSHEADERTYPE, old, aValue);
    }
}
