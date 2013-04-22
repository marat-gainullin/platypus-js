package com.eas.client.utils.syntax;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;


/**
 *
 * @author  Marat
 */
public class SyntaxStylesGui extends javax.swing.JDialog {

    private static final java.util.ResourceBundle messages = java.util.ResourceBundle.getBundle("com/eas/client/utils/syntax/messages");
    private static String STYLES_GUI_CAPTION = messages.getString("Customize_syntax_highlighting");
    private static String STYLES_GUI_EXAMPLE = "/* Example Script\n" + "first read the arguments" + "*/\n" + "if (! arguments)\n\tPrintOptions();\n" + "for (var i=0; i < arguments.length; i++)\n{\n\tif (debug)\n\tprint(\"argument: + \'\" + arguments[i] + \"\'\");\n//...";
    
    private List<Style> styles = null;
    private Highlighting syntax = null;
    private JSyntaxPane stylesPreview = null;
    private boolean toStyles = false;
    private boolean toComps = true;
    private boolean newHighlightSelected = false;

    /** Creates new form SyntaxStylesGui */
    public SyntaxStylesGui(java.awt.Window parent, boolean modal, Highlighting astyles) {
        super(parent, modal ? JDialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        initComponents();
        syntax = astyles;
        styles = astyles.getStylesAsVector();
        dynamicInitComponents();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if(b)
            try {
                stylesPreview.getDocument().insertString(0, STYLES_GUI_EXAMPLE, null);
                ((StyledScriptDocument)stylesPreview.getStyledDocument()).setReadOnly(true);
            } catch (BadLocationException ex) {
                Logger.getLogger(SyntaxStylesGui.class.getName()).log(Level.SEVERE, null, ex);
            }

    }

    public void shrink()
    {
        if(lblFColor.getLocation().x > 20)
            setSize(getSize().width - lblFColor.getLocation().x + 10, getSize().height);
    }
    
    public boolean getNewHighlightSelected()
    {
        return newHighlightSelected;
    }
    
    private void notifyOnClose()
    {
        WindowListener[] wsl = getWindowListeners();
        if(wsl != null)
            for(int i=0;i<wsl.length;i++)
            {
                if(wsl[i] != null)
                    wsl[i].windowClosed(new WindowEvent(this, WindowEvent.WINDOW_CLOSED));
            }
    }
    
    private void dynamicInitComponents() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontfamilyNames = ge.getAvailableFontFamilyNames();
        if (fontfamilyNames != null) {
            for (int i = 0; i < fontfamilyNames.length; i++) {
                fontCombo.addItem(fontfamilyNames[i]);
            }
        }
        for (int i = 1; i < 51; i++) {
            fontSizeCombo.addItem(String.valueOf(i));
        }
        fontSizeCombo.setSelectedIndex(11);
        setTitle(STYLES_GUI_CAPTION);
        stylesList.setModel(new javax.swing.AbstractListModel() {

            @Override
            public int getSize() {
                return styles.size();
            }

            @Override
            public Object getElementAt(int i) {
                return messages.getString(styles.get(i).getName());
            }
        });
        
        stylesPreview = new JSyntaxPane();
        JViewport port = pnl4SyntaxPane.getViewport();
        stylesPreview.setSize(pnl4SyntaxPane.getSize());
        port.add(stylesPreview);
        if(styles.size() > 0)
        {
            stylesList.setSelectedIndex(0);
            initComponentsByStyle(styles.get(stylesList.getSelectedIndex()));
        }
    }
    
    private void initComponentsByStyle(Style st)
    {
        if(!toStyles)
        {
            toComps = true;
            try
            {
                String fontFamily = StyleConstants.getFontFamily(st);
                int fontSize = StyleConstants.getFontSize(st);
                Color fc = StyleConstants.getForeground(st);
                Color bc = StyleConstants.getBackground(st);
                boolean bl = StyleConstants.isBold(st);
                boolean it = StyleConstants.isItalic(st);
                boolean un = StyleConstants.isUnderline(st);
                fontCombo.setSelectedItem(fontFamily);
                fontSizeCombo.setSelectedItem(String.valueOf(fontSize));
                btnBold.setSelected(bl);
                btnItalic.setSelected(it);
                btnUnderlined.setSelected(un);
                pnlFColor.setBackground(fc);
                //pnlBColor.setBackground(bc);
                edTabSize.setText(String.valueOf(syntax.getTabSize()));
            }
            finally
            {
                toComps = false;
            }
        }
    }

    private void initStyleByComponents()
    {
        if(!toComps)
        {
            toStyles = true;
            try {
                int selectedIndex = stylesList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Style st = styles.get(selectedIndex);
                    assert (st != null);
                    StyleConstants.setFontFamily(st, (String) fontCombo.getSelectedItem());
                    StyleConstants.setFontSize(st, Integer.valueOf((String) fontSizeCombo.getSelectedItem()));
                    StyleConstants.setForeground(st, pnlFColor.getBackground());
                    //StyleConstants.setBackground(st, pnlBColor.getBackground());
                    StyleConstants.setBold(st, btnBold.isSelected());
                    StyleConstants.setItalic(st, btnItalic.isSelected());
                    StyleConstants.setUnderline(st, btnUnderlined.isSelected());
                }
                int ltabsize = 4;
                try
                {
                    ltabsize = Integer.valueOf(edTabSize.getText());
                }catch(Exception ex)
                {
                    ltabsize = 4;
                }
                if(ltabsize < 1)
                    ltabsize = 1;
                if(ltabsize > 16)
                    ltabsize = 16;
                edTabSize.setText(String.valueOf(ltabsize));
                syntax.setTabSize(ltabsize);
                try {
                    ltabsize = stylesPreview.modelToView(ltabsize).x - stylesPreview.modelToView(0).x;
                } catch (BadLocationException ex) {
                    ltabsize = 40;
                }
                syntax.setTabPixelSize(ltabsize);
            } finally {
                toStyles = false;
            }
            stylesPreview.highlightMLComments(true);
        }
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnApplyDefaults = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        stylesList = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        fontCombo = new javax.swing.JComboBox();
        fontSizeCombo = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnBold = new javax.swing.JToggleButton();
        btnItalic = new javax.swing.JToggleButton();
        btnUnderlined = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        pnl4SyntaxPane = new javax.swing.JScrollPane();
        lblFColor = new javax.swing.JLabel();
        pnlFColor = new javax.swing.JPanel();
        edTabSize = new javax.swing.JTextField();
        lblTabSize = new javax.swing.JLabel();
        pnlFColor1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/eas/client/utils/syntax/messages"); // NOI18N
        btnOk.setText(bundle.getString("___OK___")); // NOI18N
        btnOk.setToolTipText(messages.getString("Save_changes_and_close")); // NOI18N
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        btnCancel.setText(bundle.getString("Cancel")); // NOI18N
        btnCancel.setToolTipText(messages.getString("Close_and_discard_changes")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnApplyDefaults.setText(messages.getString("Apply_defaults")); // NOI18N
        btnApplyDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyDefaultsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(btnApplyDefaults)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 178, Short.MAX_VALUE)
                .addComponent(btnOk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk)
                    .addComponent(btnApplyDefaults))
                .addContainerGap(5, Short.MAX_VALUE))
        );

        jSplitPane1.setDividerLocation(120);
        jSplitPane1.setContinuousLayout(true);

        stylesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        stylesList.setToolTipText(messages.getString("Syntax_elements_list")); // NOI18N
        stylesList.setName("syntaxElementsList"); // NOI18N
        stylesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                stylesListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(stylesList);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        fontCombo.setToolTipText(messages.getString("Font_family_name")); // NOI18N
        fontCombo.setName("fontFamilyCombo"); // NOI18N
        fontCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontComboActionPerformed(evt);
            }
        });
        jToolBar1.add(fontCombo);

        fontSizeCombo.setToolTipText(messages.getString("Font_size")); // NOI18N
        fontSizeCombo.setName("fontSizeCombo"); // NOI18N
        fontSizeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontSizeComboActionPerformed(evt);
            }
        });
        jToolBar1.add(fontSizeCombo);
        jToolBar1.add(jSeparator1);

        btnBold.setFont(new java.awt.Font("Times New Roman", 1, 14));
        btnBold.setText(bundle.getString("__B__")); // NOI18N
        btnBold.setToolTipText("null");
        btnBold.setFocusable(false);
        btnBold.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBold.setName("btnBold"); // NOI18N
        btnBold.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBoldActionPerformed(evt);
            }
        });
        jToolBar1.add(btnBold);

        btnItalic.setFont(new java.awt.Font("Times New Roman", 2, 14));
        btnItalic.setText(bundle.getString("__I__")); // NOI18N
        btnItalic.setToolTipText(messages.getString("Italic")); // NOI18N
        btnItalic.setFocusable(false);
        btnItalic.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnItalic.setName("btnItalic"); // NOI18N
        btnItalic.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnItalic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnItalicActionPerformed(evt);
            }
        });
        jToolBar1.add(btnItalic);

        btnUnderlined.setFont(new java.awt.Font("Times New Roman", 0, 14));
        btnUnderlined.setText(bundle.getString("__U__")); // NOI18N
        btnUnderlined.setToolTipText(messages.getString("Underlined")); // NOI18N
        btnUnderlined.setFocusable(false);
        btnUnderlined.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUnderlined.setName("btnUnderlined"); // NOI18N
        btnUnderlined.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnUnderlined.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnderlinedActionPerformed(evt);
            }
        });
        jToolBar1.add(btnUnderlined);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 44, Short.MAX_VALUE)
        );

        pnl4SyntaxPane.setToolTipText(messages.getString("Program_example")); // NOI18N

        lblFColor.setText(bundle.getString("Foreground_color")); // NOI18N

        pnlFColor.setBackground(javax.swing.UIManager.getDefaults().getColor("textText"));
        pnlFColor.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlFColor.setToolTipText(messages.getString("Click_to_select_text_foreground_color:")); // NOI18N
        pnlFColor.setName("pnlFColor"); // NOI18N
        pnlFColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlFColorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlFColorLayout = new javax.swing.GroupLayout(pnlFColor);
        pnlFColor.setLayout(pnlFColorLayout);
        pnlFColorLayout.setHorizontalGroup(
            pnlFColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 65, Short.MAX_VALUE)
        );
        pnlFColorLayout.setVerticalGroup(
            pnlFColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 22, Short.MAX_VALUE)
        );

        edTabSize.setColumns(9);
        edTabSize.setText("4");
        edTabSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edTabSizeActionPerformed(evt);
            }
        });

        lblTabSize.setText(messages.getString("Tab_size")); // NOI18N

        pnlFColor1.setBackground(javax.swing.UIManager.getDefaults().getColor("textText"));
        pnlFColor1.setToolTipText(messages.getString("Click_to_select_text_foreground_color:")); // NOI18N
        pnlFColor1.setName("pnlFColor"); // NOI18N
        pnlFColor1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlFColor1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlFColor1Layout = new javax.swing.GroupLayout(pnlFColor1);
        pnlFColor1.setLayout(pnlFColor1Layout);
        pnlFColor1Layout.setHorizontalGroup(
            pnlFColor1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlFColor1Layout.setVerticalGroup(
            pnlFColor1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFColor, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTabSize))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(edTabSize, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                    .addComponent(pnlFColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(104, 104, 104))
            .addComponent(pnl4SyntaxPane, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFColor)
                            .addComponent(pnlFColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(edTabSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTabSize))))
                .addGap(12, 12, 12)
                .addComponent(pnl4SyntaxPane, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnUnderlinedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnderlinedActionPerformed
        initStyleByComponents();
}//GEN-LAST:event_btnUnderlinedActionPerformed

    private void pnlFColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlFColorMouseClicked
        Color newColor = JColorChooser.showDialog(rootPane, STYLES_GUI_CAPTION, pnlFColor.getBackground());
        if(newColor != null)
        {
            pnlFColor.setBackground(newColor);
            pnlFColor.setForeground(newColor);
            initStyleByComponents();
        }
    }//GEN-LAST:event_pnlFColorMouseClicked

    private void stylesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_stylesListValueChanged
        int selectedIndex = stylesList.getSelectedIndex();
        if(selectedIndex != -1)
            initComponentsByStyle(styles.get(selectedIndex));
    }//GEN-LAST:event_stylesListValueChanged

    private void btnBoldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBoldActionPerformed
        initStyleByComponents();
    }//GEN-LAST:event_btnBoldActionPerformed

    private void btnItalicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnItalicActionPerformed
        initStyleByComponents();
    }//GEN-LAST:event_btnItalicActionPerformed

    private void fontSizeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontSizeComboActionPerformed
        initStyleByComponents();
    }//GEN-LAST:event_fontSizeComboActionPerformed

    private void fontComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontComboActionPerformed
        initStyleByComponents();
    }//GEN-LAST:event_fontComboActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        setVisible(false);
        initStyleByComponents();
        syntax.writeStyles();
        newHighlightSelected = true;
        notifyOnClose();
        dispose();
    }//GEN-LAST:event_btnOkActionPerformed

    @SuppressWarnings("static_access")
    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        setVisible(false);
        syntax.updateStyles();
        notifyOnClose();
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnApplyDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyDefaultsActionPerformed
        syntax.deleteSettingsFile();
        syntax.updateStyles();
        newHighlightSelected = true;
        notifyOnClose();
        dispose();
    }//GEN-LAST:event_btnApplyDefaultsActionPerformed

private void edTabSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edTabSizeActionPerformed
  initStyleByComponents();
}//GEN-LAST:event_edTabSizeActionPerformed

private void pnlFColor1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlFColor1MouseClicked
// TODO add your handling code here:
}//GEN-LAST:event_pnlFColor1MouseClicked
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApplyDefaults;
    private javax.swing.JToggleButton btnBold;
    private javax.swing.JButton btnCancel;
    private javax.swing.JToggleButton btnItalic;
    private javax.swing.JButton btnOk;
    private javax.swing.JToggleButton btnUnderlined;
    private javax.swing.JTextField edTabSize;
    private javax.swing.JComboBox fontCombo;
    private javax.swing.JComboBox fontSizeCombo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblFColor;
    private javax.swing.JLabel lblTabSize;
    private javax.swing.JScrollPane pnl4SyntaxPane;
    private javax.swing.JPanel pnlFColor;
    private javax.swing.JPanel pnlFColor1;
    private javax.swing.JList stylesList;
    // End of variables declaration//GEN-END:variables
    
}
