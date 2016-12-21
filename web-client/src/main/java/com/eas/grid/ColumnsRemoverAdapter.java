/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.grid;


/**
 *
 * @author mg
 * @param <T>
 */
public class ColumnsRemoverAdapter<T> implements ColumnsRemover {

    protected GridSection<T>[] sections;

    public ColumnsRemoverAdapter(GridSection<T>... aSections) {
        super();
        sections = aSections;
    }

    @Override
    public void removeColumn(int index) {
        for (GridSection<T> section : sections) {
            ColumnsRemover old = section.getColumnsRemover();
            section.setColumnsRemover(null);
            try {
                section.removeColumn(index);
            } finally {
                section.setColumnsRemover(old);
            }
        }
    }
}
