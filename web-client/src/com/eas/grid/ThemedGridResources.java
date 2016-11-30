/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;

/**
 *
 * @author mg
 */
public class ThemedGridResources implements CellTable.Resources {

    public static final ThemedGridResources instance = new ThemedGridResources();
    protected static final CellTable.Resources defaultResources = GWT.create(CellTable.Resources.class);

    protected ThemedAbstractCellTableStyle style = new ThemedAbstractCellTableStyle();

    
    
    @Override
    public ImageResource cellTableFooterBackground() {
        return null;
    }

    @Override
    public ImageResource cellTableHeaderBackground() {
        return null;
    }

    @Override
    public ImageResource cellTableLoading() {
        return null;
    }

    @Override
    public ImageResource cellTableSelectedBackground() {
        return null;
    }

    @Override
    public ImageResource cellTableSortAscending() {
        return defaultResources.cellTableSortAscending();
    }

    @Override
    public ImageResource cellTableSortDescending() {
        return defaultResources.cellTableSortDescending();
    }

    @Override
    public CellTable.Style cellTableStyle() {
        return style;
    }

    public class ThemedAbstractCellTableStyle implements CellTable.Style {

        @Override
        public String cellTableCell() {
            return "grid-cell";
        }

        @Override
        public String cellTableEvenRow() {
            return "grid-even-row";
        }

        @Override
        public String cellTableEvenRowCell() {
            return "grid-even-row-cell";
        }

        @Override
        public String cellTableFirstColumn() {
            return "";
        }

        @Override
        public String cellTableFirstColumnFooter() {
            return "";
        }

        @Override
        public String cellTableFirstColumnHeader() {
            return "";
        }

        @Override
        public String cellTableFooter() {
            return "grid-column-footer";
        }

        @Override
        public String cellTableHeader() {
            return "grid-column-header";
        }

        @Override
        public String cellTableHoveredRow() {
            return "grid-hovered-row";
        }

        @Override
        public String cellTableHoveredRowCell() {
            return "grid-hovered-cell";
        }

        @Override
        public String cellTableKeyboardSelectedCell() {
            return "grid-focused-cell";
        }

        @Override
        public String cellTableKeyboardSelectedRow() {
            return "grid-focused-row";
        }

        @Override
        public String cellTableKeyboardSelectedRowCell() {
            return "grid-focused-row-cell";
        }

        @Override
        public String cellTableLastColumn() {
            return "";
        }

        @Override
        public String cellTableLastColumnFooter() {
            return "";
        }

        @Override
        public String cellTableLastColumnHeader() {
            return "";
        }

        @Override
        public String cellTableOddRow() {
            return "grid-odd-row";
        }

        @Override
        public String cellTableOddRowCell() {
            return "grid-odd-row-cell";
        }

        @Override
        public String cellTableSelectedRow() {
            return "grid-selected-row";
        }

        @Override
        public String cellTableSelectedRowCell() {
            return "grid-selected-cell";
        }

        @Override
        public String cellTableSortableHeader() {
            return "grid-sortable-column-header";
        }

        @Override
        public String cellTableSortedHeaderAscending() {
            return "grid-column-header-sorted-asc";
        }

        @Override
        public String cellTableSortedHeaderDescending() {
            return "grid-column-header-sorted-desc";
        }

        @Override
        public String cellTableWidget() {
            return "grid-section";
        }

        @Override
        public String cellTableLoading() {
            return "grid-loading";
        }

        @Override
        public boolean ensureInjected() {
            return true;
        }

        @Override
        public String getText() {
            return "";
        }

        @Override
        public String getName() {
            return "";
        }
    }

}
