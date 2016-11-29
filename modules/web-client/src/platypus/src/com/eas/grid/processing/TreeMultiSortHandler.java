/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid.processing;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author mg
 * @param <T>
 */
public class TreeMultiSortHandler<T> extends ColumnSortEvent.ListHandler<T> {

    protected List<T> unsorted = new ArrayList<>();
    protected TreeDataProvider<T> dataProvider;
    protected Runnable onComplete;
    
    public TreeMultiSortHandler(TreeDataProvider<T> aDataProvider, Runnable aOnComplete) {
    	this(aDataProvider);
    	onComplete = aOnComplete;
    }
    
    public TreeMultiSortHandler(TreeDataProvider<T> aDataProvider) {
        super(aDataProvider.getList());
        dataProvider = aDataProvider;
        setList(dataProvider.getList());
    }

    @Override
    public void setList(List<T> aList) {
        super.setList(aList);
        unsorted.clear();
        unsorted.addAll(aList);
    }

    protected void unsort() {
        List<T> sorted = getList();
        sorted.clear();
        sorted.addAll(unsorted);
    }

    @Override
    public void onColumnSort(final ColumnSortEvent event) {
        if (unsorted.size() != getList().size()) {
            setList(getList());
        }
        if (event.getColumnSortList() == null || event.getColumnSortList().size() == 0) {
            unsort();
        } else {
            // Sort using the multi comparator.
            List<T> sorted = getList();
            Collections.sort(sorted, new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    if (dataProvider.getTree().getParentOf(o1) != dataProvider.getTree().getParentOf(o2)) {
                        List<T> path1 = dataProvider.buildPathTo(o1);
                        List<T> path2 = dataProvider.buildPathTo(o2);
                        if (path2.contains(o1)) {
                            // o1 is parent of o2
                            return -1;
                        }
                        if (path1.contains(o2)) {
                            // o2 is parent of o1
                            return 1;
                        }
                        for (int i = 0; i < Math.min(path1.size(), path2.size()); i++) {
                            if (path1.get(i) != path2.get(i)) {
                                o1 = path1.get(i);
                                o2 = path2.get(i);
                                break;
                            }
                        }
                    }
                    // ordinary sort
                    for (int i = 0; i < event.getColumnSortList().size(); i++) {
                        ColumnSortInfo sortInfo = event.getColumnSortList().get(i);
                        Comparator<T> c = getComparator((Column<T, ?>) sortInfo.getColumn());
                        int res = 0;
                        if (c != null) {
                            res = c.compare(o1, o2);
                        }
                        if (res != 0) {
                            return sortInfo.isAscending() ? res : -res;
                        }
                    }
                    return 0;
                }

            });
        }
        if(onComplete != null)
        	onComplete.run();
    }

}
