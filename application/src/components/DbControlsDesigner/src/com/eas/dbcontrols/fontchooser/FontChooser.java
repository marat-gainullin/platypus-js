/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FontChooser.java
 *
 * Created on 18.05.2009, 15:27:21
 */

package com.eas.dbcontrols.fontchooser;

import com.eas.dbcontrols.DbControlsDesignUtils;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author mg
 */
public class FontChooser extends javax.swing.JDialog
{
    public static Font chooseFont(Component aParent, Font aOldValue, String aTitle)
    {
        Frame fr = JOptionPane.getFrameForComponent(aParent);
        FontChooser fch = new FontChooser(fr, aOldValue, aTitle);
        fch.setVisible(true);
        if(fch.isOk())
            return fch.font;
        return null;
    }

    protected Font font = new Font(Font.DIALOG, 0, 12);
    protected boolean isOkButtonPushed = false;

    /** Creates new form FontChooser */
    private FontChooser(java.awt.Frame aParent, Font aOldValue, String aTitle) {
        super(aParent, aTitle, true);
        if(aOldValue != null)
            font = aOldValue;
        initComponents();
        txtSize.setDocument(new PlainDocument()
        {

            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null)
                    return;

                str = str.trim();
                for(int index=0; index < str.length(); index++ )
                {
                    char c = str.charAt(index);
                    if ( !Character.isDigit( c ) )
                        return;
                }
                super.insertString(offs, str, a );
            }

        });
        DefaultListModel lm = new DefaultListModel();
        String[] fonts = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for(int i=0;i<fonts.length;i++)
            lm.addElement(fonts[i]);
        lstFamily.setModel(lm);
        
        updateControlsFromFont();

        ActionListener al = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                updateFontFromControls();
            }
        };
        chkBold.addActionListener(al);
        chkItalic.addActionListener(al);
        chkUnderlined.addActionListener(al);
        chkStrikedOut.addActionListener(al);
        chkUnderlined.setVisible(false);
        chkStrikedOut.setVisible(false);

        btnOk.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                isOkButtonPushed = true;
                updateFontFromControls();
                setVisible(false);
            }
        });
        btnClose.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                updateFontFromControls();
                setVisible(false);
            }
        });        
    }

    public boolean isOk()
    {
        return isOkButtonPushed;
    }

    private boolean updatingControls = false;
    private void updateControlsFromFont()
    {
        if(!updatingFont)
        {
            updatingControls = true;
            try
            {
                lstFamily.setSelectedValue(font.getFamily(), true);
                if(lstFamily.isSelectionEmpty() && lstFamily.getModel() != null && lstFamily.getModel().getSize() > 0)
                    lstFamily.setSelectedIndex(0);
                txtSize.setText(String.valueOf(font.getSize()));
                lstSize.setSelectedValue(String.valueOf(font.getSize()), true);
                chkItalic.setSelected(font.isItalic());
                chkBold.setSelected(font.isBold());
            }finally
            {
                updatingControls = false;
            }
        }
    }

    private boolean updatingFont = false;
    private void updateFontFromControls()
    {
        if(!updatingControls)
        {
            updatingFont = true;
            try
            {
                if(!lstFamily.isSelectionEmpty())
                {
                    int lStyle = 0;
                    if(chkBold.isSelected())
                        lStyle = lStyle | Font.BOLD;
                    if(chkItalic.isSelected())
                        lStyle = lStyle | Font.ITALIC;
                    int lSize = 12;
                    String lSizeString = txtSize.getText();
                    if(lSizeString != null && !lSizeString.isEmpty())
                        lSize = Integer.valueOf(txtSize.getText());
                    if(lSize > 72)
                        lSize = 72;
                    font = new Font((String)lstFamily.getSelectedValue(), lStyle, lSize);
                    lblPreview.setFont(font);
                }
            }finally
            {
                updatingFont = false;
            }
        }
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlContent = new javax.swing.JPanel();
        pnlPreview = new javax.swing.JPanel();
        lblPreview = new javax.swing.JLabel();
        pnlFont = new javax.swing.JPanel();
        scrollLstFamily = new javax.swing.JScrollPane();
        lstFamily = new javax.swing.JList();
        scrollLstSize = new javax.swing.JScrollPane();
        lstSize = new javax.swing.JList();
        txtSize = new javax.swing.JTextField();
        chkBold = new javax.swing.JCheckBox();
        chkItalic = new javax.swing.JCheckBox();
        chkUnderlined = new javax.swing.JCheckBox();
        chkStrikedOut = new javax.swing.JCheckBox();
        lblFamilyName = new javax.swing.JLabel();
        lblSize = new javax.swing.JLabel();
        lblStyle = new javax.swing.JLabel();
        pnlBottom = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnlContent.setLayout(new java.awt.BorderLayout());

        pnlPreview.setPreferredSize(new java.awt.Dimension(410, 70));

        lblPreview.setText(DbControlsDesignUtils.getLocalizedString("lblPreviewValue")); // NOI18N

        javax.swing.GroupLayout pnlPreviewLayout = new javax.swing.GroupLayout(pnlPreview);
        pnlPreview.setLayout(pnlPreviewLayout);
        pnlPreviewLayout.setHorizontalGroup(
            pnlPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPreviewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPreview)
                .addContainerGap(241, Short.MAX_VALUE))
        );
        pnlPreviewLayout.setVerticalGroup(
            pnlPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPreviewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPreview)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        pnlContent.add(pnlPreview, java.awt.BorderLayout.PAGE_END);

        pnlFont.setPreferredSize(new java.awt.Dimension(380, 255));

        lstFamily.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstFamilyValueChanged(evt);
            }
        });
        scrollLstFamily.setViewportView(lstFamily);

        lstSize.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstSize.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstSizeValueChanged(evt);
            }
        });
        scrollLstSize.setViewportView(lstSize);

        txtSize.setText("12");
        txtSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSizeActionPerformed(evt);
            }
        });

        chkBold.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkBold.setText(DbControlsDesignUtils.getLocalizedString("chkBold")); // NOI18N

        chkItalic.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        chkItalic.setText(DbControlsDesignUtils.getLocalizedString("chkItalic")); // NOI18N

        chkUnderlined.setText(DbControlsDesignUtils.getLocalizedString("chkUnderlined")); // NOI18N

        chkStrikedOut.setText(DbControlsDesignUtils.getLocalizedString("chkStrikedOut")); // NOI18N

        lblFamilyName.setText(DbControlsDesignUtils.getLocalizedString("lblFamilyName")); // NOI18N

        lblSize.setText(DbControlsDesignUtils.getLocalizedString("lblSize")); // NOI18N

        lblStyle.setText(DbControlsDesignUtils.getLocalizedString("lblStyle")); // NOI18N

        javax.swing.GroupLayout pnlFontLayout = new javax.swing.GroupLayout(pnlFont);
        pnlFont.setLayout(pnlFontLayout);
        pnlFontLayout.setHorizontalGroup(
            pnlFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFontLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFontLayout.createSequentialGroup()
                        .addComponent(scrollLstFamily, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(pnlFontLayout.createSequentialGroup()
                        .addComponent(lblFamilyName)
                        .addGap(181, 181, 181)))
                .addGroup(pnlFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFontLayout.createSequentialGroup()
                        .addGroup(pnlFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSize, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                            .addComponent(scrollLstSize, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(pnlFontLayout.createSequentialGroup()
                        .addComponent(lblSize)
                        .addGap(20, 20, 20)))
                .addGroup(pnlFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(chkStrikedOut, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkItalic)
                            .addComponent(chkUnderlined)
                            .addComponent(chkBold)))
                    .addGroup(pnlFontLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lblStyle)))
                .addGap(38, 38, 38))
        );
        pnlFontLayout.setVerticalGroup(
            pnlFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFontLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblSize)
                        .addComponent(lblStyle))
                    .addComponent(lblFamilyName))
                .addGap(5, 5, 5)
                .addGroup(pnlFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollLstFamily, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                    .addGroup(pnlFontLayout.createSequentialGroup()
                        .addGroup(pnlFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkBold))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlFontLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlFontLayout.createSequentialGroup()
                                .addComponent(chkItalic)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkUnderlined)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkStrikedOut)
                                .addGap(112, 112, 112))
                            .addComponent(scrollLstSize, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))))
                .addContainerGap())
        );

        pnlContent.add(pnlFont, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlContent, java.awt.BorderLayout.CENTER);

        pnlBottom.setPreferredSize(new java.awt.Dimension(410, 25));

        btnClose.setLabel(DbControlsDesignUtils.getLocalizedString("btnCancel")); // NOI18N

        btnOk.setLabel(DbControlsDesignUtils.getLocalizedString("btnOk")); // NOI18N

        javax.swing.GroupLayout pnlBottomLayout = new javax.swing.GroupLayout(pnlBottom);
        pnlBottom.setLayout(pnlBottomLayout);
        pnlBottomLayout.setHorizontalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBottomLayout.createSequentialGroup()
                .addContainerGap(318, Short.MAX_VALUE)
                .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose)
                .addGap(2, 2, 2))
        );
        pnlBottomLayout.setVerticalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBottomLayout.createSequentialGroup()
                .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnClose))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(pnlBottom, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lstSizeValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstSizeValueChanged
        txtSize.setText((String)lstSize.getSelectedValue());
        updateFontFromControls();
    }//GEN-LAST:event_lstSizeValueChanged

    private void lstFamilyValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstFamilyValueChanged
        updateFontFromControls();
    }//GEN-LAST:event_lstFamilyValueChanged

    private void txtSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSizeActionPerformed
        updateFontFromControls();
    }//GEN-LAST:event_txtSizeActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FontChooser dialog = new FontChooser(new javax.swing.JFrame(), null, "");
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnOk;
    private javax.swing.JCheckBox chkBold;
    private javax.swing.JCheckBox chkItalic;
    private javax.swing.JCheckBox chkStrikedOut;
    private javax.swing.JCheckBox chkUnderlined;
    private javax.swing.JLabel lblFamilyName;
    private javax.swing.JLabel lblPreview;
    private javax.swing.JLabel lblSize;
    private javax.swing.JLabel lblStyle;
    private javax.swing.JList lstFamily;
    private javax.swing.JList lstSize;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlFont;
    private javax.swing.JPanel pnlPreview;
    private javax.swing.JScrollPane scrollLstFamily;
    private javax.swing.JScrollPane scrollLstSize;
    private javax.swing.JTextField txtSize;
    // End of variables declaration//GEN-END:variables

}
