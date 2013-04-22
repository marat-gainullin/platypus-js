/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.jsqlparser;

import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.Union;

/**
 *
 * @author mg
 */
public class ResultsFinder implements SelectVisitor, SelectItemVisitor {

    private List<SelectItem> results = new ArrayList<>();

    public static List<SelectItem> getResults(Select aSelect) {
        ResultsFinder instance = new ResultsFinder();
        aSelect.getSelectBody().accept(instance);
        return instance.results;
    }

    public void visit(AllColumns allColumns) {
        results.add(allColumns);
    }

    public void visit(AllTableColumns allTableColumns) {
        results.add(allTableColumns);
    }

    public void visit(SelectExpressionItem selectExpressionItem) {
        results.add(selectExpressionItem);
    }

    public void visit(PlainSelect plainSelect) {
        for (Object oItem : plainSelect.getSelectItems()) {
            SelectItem item = (SelectItem) oItem;
            item.accept(this);
        }
    }

    public void visit(Union union) {
        List selects = union.getPlainSelects();
        if (selects != null && !selects.isEmpty()) {
            ((PlainSelect) selects.get(0)).accept(this);
        }
    }
}
