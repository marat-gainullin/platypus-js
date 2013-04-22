/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.edits;

import com.eas.dbcontrols.grid.DbGridColumn;
import com.eas.dbcontrols.grid.DbGridDesignInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author mg
 */
public class HeaderStructure {

    protected DbGridColumn column = null;
    protected List<HeaderStructure> children = new ArrayList<>();

    private HeaderStructure(DbGridColumn aColumn) {
        super();
        column = aColumn;
    }

    public DbGridColumn getColumn() {
        return column;
    }

    public static HeaderStructure grabHeaderStructure(DbGridDesignInfo aGridInfo) {
        if (aGridInfo != null) {
            HeaderStructure structure = new HeaderStructure(null);
            List<DbGridColumn> lroots = aGridInfo.getHeader();
            if (lroots != null) {
                for (int i = 0; i < lroots.size(); i++) {
                    HeaderStructure lstruct = new HeaderStructure(lroots.get(i));
                    structure.children.add(lstruct);
                    grabColumnStructure(lstruct);
                }
                assert structure.children.size() == lroots.size();
                return structure;
            }
        }
        return null;
    }

    protected static void grabColumnStructure(HeaderStructure aStruct) {
        if (aStruct != null && aStruct.column != null) {
            List<DbGridColumn> colChildren = aStruct.column.getChildren();
            if (colChildren != null) {
                for (int i = 0; i < colChildren.size(); i++) {
                    HeaderStructure lstruct = new HeaderStructure(colChildren.get(i));
                    aStruct.children.add(lstruct);
                    grabColumnStructure(lstruct);
                }
                assert aStruct.children.size() == colChildren.size();
            }
        }
    }

    /**
     * Applies self structure to passed DbGridDesignInfo instance.
     * @param aGridInfo DbGridDesignInfo instance the structure to be applied to.
     * @see DbGridDesignInfo
     */
    public void applyStructure(DbGridDesignInfo aGridInfo) {
        if (children != null && aGridInfo != null) {
            List<DbGridColumn> lroots = aGridInfo.getHeader();
            if (lroots != null) {
                lroots.clear();
            } else {
                lroots = new ArrayList<>();
                aGridInfo.setHeader(lroots);
            }
            for (int i = 0; i < children.size(); i++) {
                lroots.add(children.get(i).column);
                children.get(i).applyStructure2Column();
            }
            assert lroots.size() == children.size();
            aGridInfo.firePropertyChange(DbGridDesignInfo.HEADER, null, lroots);
        }
    }

    protected void applyStructure2Column() {
        if (column != null && children != null) {
            List<DbGridColumn> lchildren = new ArrayList<>();
            column.setChildren(lchildren);
            for (int i = 0; i < children.size(); i++) {
                lchildren.add(children.get(i).column);
                children.get(i).column.setParent(column);
                children.get(i).applyStructure2Column();
            }
            assert lchildren.size() == children.size();
        }
    }

    protected Collection<DbGridColumn> toCollection() {
        Collection<DbGridColumn> res = _toCollection();
        if (column != null) {
            res.add(column);
        }
        return res;
    }

    protected Collection<DbGridColumn> _toCollection() {
        List<DbGridColumn> cols = new ArrayList<>();
        for (HeaderStructure hs : children) {
            if (hs.column != null) {
                cols.add(hs.column);
            }
            if (hs.children != null && !hs.children.isEmpty()) {
                cols.addAll(hs._toCollection());
            }
        }
        return cols;
    }
}
