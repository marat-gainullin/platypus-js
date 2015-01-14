/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.ordering;

import com.bearsoft.rowset.Row;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Predicate;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class Subset extends LinkedHashSet<Row> {

    protected JSObject published;

    public Subset() {
        super();
    }

    public JSObject getPublished() {
        return published;
    }

    public void setPublished(JSObject aValue) {
        published = aValue;
    }

    @Override
    public void clear() {
        super.clear();
        published = null;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (super.retainAll(c)) {
            published = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean add(Row e) {
        if (super.add(e)) {
            published = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addAll(Collection<? extends Row> c) {
        if (super.addAll(c)) {
            published = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        if (super.remove(o)) {
            published = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (super.removeAll(c)) {
            published = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeIf(Predicate<? super Row> filter) {
        if (super.removeIf(filter)) {
            published = null;
            return true;
        } else {
            return false;
        }
    }
}
