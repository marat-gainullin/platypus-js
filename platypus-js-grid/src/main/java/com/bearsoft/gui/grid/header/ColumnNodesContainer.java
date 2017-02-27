/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header;

/**
 *
 * @author Марат
 */
public interface ColumnNodesContainer {

    public void removeColumnNode(GridColumnsNode aCol);

    public void addColumnNode(GridColumnsNode aGroup);

    public void insertColumnNode(int atIndex, GridColumnsNode aGroup);
}
