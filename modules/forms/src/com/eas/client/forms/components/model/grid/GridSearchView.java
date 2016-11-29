/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GridSearchView.java
 *
 * Created on 17.12.2009, 16:05:50
 */
package com.eas.client.forms.components.model.grid;

import com.bearsoft.gui.grid.data.CellData;
import com.bearsoft.gui.grid.rendering.InsettedRenderer;
import com.bearsoft.gui.grid.rows.TabularRowsSorter;
import com.eas.client.forms.Forms;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author mg
 */
public class GridSearchView extends javax.swing.JPanel implements ListSelectionListener {

    protected ModelGrid grid;
    protected FindAction findAction = new FindAction();
    protected CloseAction closeAction = new CloseAction();
    protected SearchEntry cell2LookAt = new SearchEntry();
    protected SearchEntry lastFound = new SearchEntry();
    protected boolean caseSensitive = false;
    protected boolean wholeMatch = false;

    @Override
    public void valueChanged(ListSelectionEvent e) {
        cell2LookAt = null;
    }

    private String convertValue2StringWithRenderer(Object oValue, int col) {
        if (oValue != null) {
            TableColumnModel columnModel = grid.getColumnModel();
            String sValue = null;
            TableColumn tCol = columnModel.getColumn(col);
            if (tCol instanceof TableColumn) {
                TableColumn gCol = (TableColumn) tCol;
                TableCellRenderer renderer = gCol.getCellRenderer();
                if (renderer != null) {
                    if (renderer instanceof InsettedRenderer) {
                        renderer = ((InsettedRenderer) renderer).unwrap();
                    }
                    Component rComp = renderer.getTableCellRendererComponent(grid.getBottomRightTable(), oValue, false, false, 0, 0);
                    if (rComp instanceof JLabel) {
                        sValue = ((JLabel) rComp).getText();
                    }
                }
            }
            if (sValue == null) {
                if (oValue instanceof CellData) {
                    CellData cd = (CellData) oValue;
                    oValue = cd.display != null ? cd.display : cd.data;
                    if (oValue != null) {
                        sValue = oValue.toString();
                    } else {
                        sValue = "";
                    }
                } else {
                    sValue = oValue.toString();
                }
            }
            return sValue;
        } else {
            return null;
        }
    }

    protected class SearchEntry {

        public int row = 0;
        public int column = 0;
    }

    private void makeVisible() {
        // achive models needed
        TableColumnModel columnModel = grid.getColumnModel();

        ListSelectionModel rowsSelectionModel = grid.getRowsSelectionModel();
        ListSelectionModel columnSelectionModel = columnModel.getSelectionModel();

        grid.getRowsSelectionModel().removeListSelectionListener(this);
        try {
            // table
            rowsSelectionModel.setSelectionInterval(lastFound.row, lastFound.row);
            columnSelectionModel.setSelectionInterval(lastFound.column, lastFound.column);
            // make needed cell visible
            JTable cellTable = grid.getTableByViewCell(lastFound.row, lastFound.column);
            if (cellTable != null) {
                int cellRow = lastFound.row;
                if (cellRow >= grid.getTopLeftTable().getRowCount()) {
                    cellRow -= grid.getTopLeftTable().getRowCount();
                }
                int cellCol = lastFound.column;
                if (cellCol >= grid.getTopLeftTable().getColumnCount()) {
                    cellCol -= grid.getTopLeftTable().getColumnCount();
                }
                Rectangle cellRect = cellTable.getCellRect(cellRow, cellCol, true);
                assert cellRect != null;
                cellTable.scrollRectToVisible(cellRect);
            }
        } finally {
            grid.getRowsSelectionModel().addListSelectionListener(this);
        }
    }

    private boolean findNext(String txt2Find) {
        if (txt2Find != null) {
            // choose initial parameters
            int row = cell2LookAt.row;
            int col = cell2LookAt.column;
            TabularRowsSorter<? extends TableModel> rowsSorter = grid.getRowSorter();
            TableColumnModel columnModel = grid.getColumnModel();
            if (rowsSorter.getViewRowCount() <= 0 || columnModel.getColumnCount() <= 0) {
                return false;
            }

            while (true) {
                TableColumn tCol = columnModel.getColumn(col);
                Object oValue = rowsSorter.getModel().getValueAt(rowsSorter.convertRowIndexToModel(row), tCol.getModelIndex());
                boolean found = false;
                if (oValue != null) {
                    String sValue = convertValue2StringWithRenderer(oValue, col);
                    if (!caseSensitive) {
                        txt2Find = txt2Find.toLowerCase();
                        sValue = sValue.toLowerCase();
                    }
                    sValue = sValue.replaceAll("[^а-я-А-Я\\w\\s\\p{Punct}]+", " ");
                    txt2Find = txt2Find.replaceAll("[^а-я-А-Я\\w\\s\\p{Punct}]+", " ");
                    if (wholeMatch) {
                        found = txt2Find.equals(sValue);
                    } else {
                        found = !sValue.isEmpty() && (sValue.contains(txt2Find) || txt2Find.contains(sValue));
                    }
                }
                // mark the founded value place
                if (found) {
                    lastFound.column = col;
                    lastFound.row = row;
                }
                // find next cell to look at
                row++;
                if (row >= rowsSorter.getViewRowCount()) {
                    row = 0;
                    col++;
                    if (col >= columnModel.getColumnCount()) {
                        // model ended
                        cell2LookAt.column = 0;
                        cell2LookAt.row = 0;
                        if (!found) {
                            return false;
                        }
                    }
                }
                if (found) {
                    if (col >= columnModel.getColumnCount()) {
                        cell2LookAt.column = 0;
                    } else {
                        cell2LookAt.column = col;
                    }
                    cell2LookAt.row = row;
                    return true;
                }
            }
        }
        return false;
    }

    private void checkActions() {
        findAction.checkEnabled();
        closeAction.checkEnabled();
    }

    protected class SurrogateFindAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            findAction.actionPerformed(e);
        }
    }

    protected class FindAction extends AbstractAction {

        public FindAction() {
            super();
            putValue(Action.NAME, Forms.getLocalizedString("btnFind"));
            setEnabled(false);
        }

        public void checkEnabled() {
            setEnabled(isEnabled());
        }

        @Override
        public boolean isEnabled() {
            return txtText2Find.getText() != null
                    && !txtText2Find.getText().isEmpty();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isEnabled()) {
                if (cell2LookAt == null) {
                    cell2LookAt = new SearchEntry();
                    cell2LookAt.column = grid.getColumnModel().getSelectionModel().getLeadSelectionIndex();
                    cell2LookAt.row = grid.getRowsSelectionModel().getLeadSelectionIndex();
                    if (cell2LookAt.column == -1 || cell2LookAt.row == -1) {
                        cell2LookAt.column = 0;
                        cell2LookAt.row = 0;
                    } else {
                        // find next cell to look at to avoid search to stop at position just selected
                        cell2LookAt.row++;
                        if (cell2LookAt.row >= grid.getRowSorter().getViewRowCount()) {
                            cell2LookAt.row = 0;
                            cell2LookAt.column++;
                            if (cell2LookAt.column >= grid.getColumnModel().getColumnCount()) {
                                // model has ended
                                cell2LookAt.column = 0;
                                cell2LookAt.row = 0;
                            }
                        }
                    }
                }
                if (findNext(txtText2Find.getText())) {
                    makeVisible();
                } else {
                    JOptionPane.showMessageDialog(GridSearchView.this, Forms.getLocalizedString("notFound"), Forms.getLocalizedString("Search"), JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    protected class CloseAction extends AbstractAction {

        public CloseAction() {
            super();
            putValue(Action.NAME, Forms.getLocalizedString("btnClose"));
        }

        public void checkEnabled() {
            setEnabled(isEnabled());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Window w = SwingUtilities.getWindowAncestor(GridSearchView.this);
            if (w != null) {
                w.setVisible(false);
                w.dispose();
            }
        }
    }

    protected class SearchViewDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            checkActions();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkActions();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            checkActions();
        }
    }

    /**
     * Creates new form GridSearchView
     */
    public GridSearchView(ModelGrid aGrid) {
        initComponents();
        grid = aGrid;
        //grid.getRowsSelectionModel().addListSelectionListener(this);
        btnClose.setAction(closeAction);
        btnFind.setAction(findAction);
        txtText2Find.setAction(new SurrogateFindAction());
        txtText2Find.getDocument().addDocumentListener(new SearchViewDocumentListener());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnFind = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        lblFind = new javax.swing.JLabel();
        txtText2Find = new javax.swing.JTextField();
        chkCase = new javax.swing.JCheckBox();
        chkWhole = new javax.swing.JCheckBox();

        setMinimumSize(new java.awt.Dimension(330, 100));

        btnFind.setText(Forms.getLocalizedString("btnFind")); // NOI18N
        btnFind.setMargin(new java.awt.Insets(2, 2, 2, 2));

        btnClose.setText(Forms.getLocalizedString("btnClose")); // NOI18N
        btnClose.setMargin(new java.awt.Insets(2, 2, 2, 2));

        lblFind.setText(Forms.getLocalizedString("lblFind")); // NOI18N

        chkCase.setText(Forms.getLocalizedString("chkCase")); // NOI18N
        chkCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCaseActionPerformed(evt);
            }
        });

        chkWhole.setText(Forms.getLocalizedString("chkWhole")); // NOI18N
        chkWhole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkWholeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFind)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtText2Find, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkCase)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnFind, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkWhole))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFind)
                    .addComponent(txtText2Find, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCase)
                    .addComponent(chkWhole))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClose)
                    .addComponent(btnFind, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(9, 9, 9))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCaseActionPerformed
        caseSensitive = chkCase.isSelected();
    }//GEN-LAST:event_chkCaseActionPerformed

    private void chkWholeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkWholeActionPerformed
        wholeMatch = chkWhole.isSelected();
    }//GEN-LAST:event_chkWholeActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnFind;
    private javax.swing.JCheckBox chkCase;
    private javax.swing.JCheckBox chkWhole;
    private javax.swing.JLabel lblFind;
    private javax.swing.JTextField txtText2Find;
    // End of variables declaration//GEN-END:variables
}
