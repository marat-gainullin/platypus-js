/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FindReplaceDialogView.java
 *
 * Created on 28.01.2010, 10:09:06
 */
package com.eas.client.utils.syntax;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Caret;

/**
 *
 * @author mg
 */
public class FindReplaceDialogView extends JPanel implements CaretListener, DocumentListener {

    private static final java.util.ResourceBundle messages = java.util.ResourceBundle.getBundle("com/eas/client/utils/syntax/messages");
    private JSyntaxPane m_sp = null;
    private boolean isWhole = false;
    private boolean isCase = false;
    private boolean isFwd = true;
    private boolean isBwd = false;
    private boolean isCursor = false;
    private boolean isBeg = true;
    private String targetText = "";
    private String txt2Find = "";
    private String txtReplace_with = "";
    private int currentIndex = 0;
    private boolean isInProgress = false;
    private FindActionImpl m_findAction;

    /** Creates new form FindReplaceDialogView */
    public FindReplaceDialogView() {
        initComponents();
        KeyListener kl = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (!e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_F3 || e.getKeyCode() == KeyEvent.VK_ENTER)) {
                    m_findAction.actionPerformed(null);
                }
            }
        };

        check_whole.addKeyListener(kl);
        check_case.addKeyListener(kl);
        radio_fwd.addKeyListener(kl);
        radio_bwd.addKeyListener(kl);
        radio_cusor.addKeyListener(kl);
        radio_beg.addKeyListener(kl);
        btn_find.addKeyListener(kl);
        m_findAction = new FindActionImpl(this);
        btn_find.addActionListener(m_findAction);
        btn_replace.addKeyListener(kl);
        btn_replace.addActionListener(new ReplaceActionImpl());
        edit_2find.addKeyListener(kl);
        edit_replace_with.addKeyListener(kl);

        bind();
    }

    public String getLocalizedString(String aKey) {
        try {
            return messages.getString(aKey);
        } catch (Exception ex) {
            return aKey;
        }
    }

    public void setSp(JSyntaxPane sp) {
        if (m_sp != sp) {
            isInProgress = false;
            if (m_sp != null) {
                m_sp.removeCaretListener(this);
                m_sp.getDocument().removeDocumentListener(this);
            }
            if (sp != null) {
                sp.addCaretListener(this);
                sp.getDocument().addDocumentListener(this);
            }
        }
        m_sp = sp;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            bind();
            assert m_sp != null;
            String txtSelected = m_sp.getSelectedText();
            if (txtSelected != null && !txtSelected.isEmpty()) {
                txt2Find = txtSelected;
            }
            edit_2find.setText(txt2Find);
            if (txt2Find != null) {
                edit_2find.setSelectionStart(0);
                edit_2find.setSelectionEnd(txt2Find.length());
            }
            edit_2find.requestFocus();
        }
    }

    private void bind() {
        boolean lisWhole = check_whole.isSelected();
        if (lisWhole != isWhole) {
            isInProgress = false;
        }
        isWhole = lisWhole;
        boolean lisCase = check_case.isSelected();
        if (lisCase != isCase) {
            isInProgress = false;
        }
        isCase = lisCase;
        boolean lisFwd = radio_fwd.isSelected();
        if (isFwd != lisFwd) {
            isInProgress = false;
        }
        isFwd = lisFwd;
        boolean lisBwd = radio_bwd.isSelected();
        if (isBwd != lisBwd) {
            isInProgress = false;
        }
        isBwd = lisBwd;
        isCursor = radio_cusor.isSelected();
        boolean lisBeg = radio_beg.isSelected();
        if (isBeg != lisBeg) {
            isInProgress = false;
        }
        isBeg = lisBeg;
        String lForFind = edit_2find.getText();
        if (lForFind == null) {
            lForFind = "";
        }
        if (!isCase) {
            lForFind = lForFind.toLowerCase();
        }
        if (!lForFind.equals(txt2Find)) {
            isInProgress = false;
        }
        txtReplace_with = edit_replace_with.getText();

        txt2Find = lForFind;
        if (!isCase) {
            txt2Find = txt2Find.toLowerCase();
        }

        if (m_sp != null) {
            targetText = m_sp.getText();
            if (targetText != null) {
                targetText = targetText.replace(System.getProperty("line.separator", "\n"), "\n");
                if (!isCase) {
                    targetText = targetText.toLowerCase();
                }
            }
        }
    }

    private class FindActionImpl implements ActionListener {

        private FindReplaceDialogView m_owner = null;

        public FindActionImpl(FindReplaceDialogView owner) {
            m_owner = owner;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            bind();
            if (targetText != null) {
                setupCurrentIndex();
                find();
                applyFoundIndex();
            }
        }

        private void find() {
            assert (isFwd != isBwd);
            if (!isInProgress) {
                isInProgress = true;
            }
            if (isFwd && !isBwd) {
                currentIndex = targetText.indexOf(txt2Find, currentIndex);
                int lOldIndex = -1;
                int lInsurancecounter = 0;
                while (isWhole && !isWordFound() && currentIndex > -1 &&
                        lOldIndex != currentIndex && lInsurancecounter < 2) {
                    if (lOldIndex == -1) {
                        lOldIndex = currentIndex;
                    }
                    currentIndex++;
                    currentIndex = targetText.indexOf(txt2Find, currentIndex);
                    if (currentIndex == -1) {
                        currentIndex = 0;
                        lInsurancecounter++;
                    }
                }
                if (isWhole && !isWordFound()) {
                    currentIndex = -1;
                }
                //if(currentIndex == -1)
                //    currentIndex = 0;
            } else {
                currentIndex = targetText.lastIndexOf(txt2Find, currentIndex);
                int lOldIndex = -1;
                int lInsurancecounter = 0;
                while (isWhole && !isWordFound() && currentIndex > -1 &&
                        lOldIndex != currentIndex && lInsurancecounter < 2) {
                    if (lOldIndex == -1) {
                        lOldIndex = currentIndex;
                    }
                    currentIndex--;
                    currentIndex = targetText.lastIndexOf(txt2Find, currentIndex);
                    if (currentIndex == -1) {
                        currentIndex = targetText.length() - 1;
                        lInsurancecounter++;
                    }
                }
                if (isWhole && !isWordFound()) {
                    currentIndex = -1;
                }
                if (currentIndex == -1) {
                    currentIndex = targetText.length() - 1;
                }
            }
        }

        private void applyFoundIndex() {
            if (targetText != null && currentIndex >= 0 && currentIndex < targetText.length() &&
                    currentIndex + txt2Find.length() <= targetText.length()) {
                m_sp.removeCaretListener(m_owner);
                try {
                    Caret cr = m_sp.getCaret();
                    cr.setDot(currentIndex + txt2Find.length());
                    cr.moveDot(currentIndex);
                    cr.setVisible(true);
                } finally {
                    m_sp.addCaretListener(m_owner);
                }
            }
            if (/*currentIndex < 0 ||*/currentIndex >= targetText.length()) {
                isInProgress = false;
                setupCurrentIndex();
            } else {
                if (isFwd && !isBwd) {
                    currentIndex++;
                    if (currentIndex >= targetText.length()) {
                        currentIndex = 0;
                    }
                } else {
                    currentIndex--;
                    if (currentIndex < 0) {
                        currentIndex = targetText.length() - 1;
                    }
                }
            }
        }

        private boolean isWordSeparator(int aIndex) {
            return (aIndex < 0 || aIndex >= targetText.length() || "/\\*-+=_!?.,:;'\"`~|@#$%^&*()[]{}<>\t \r\n".indexOf(targetText.charAt(aIndex)) > -1);
        }

        private boolean isWordFound() {
            return (isWordSeparator(currentIndex - 1) && isWordSeparator(currentIndex + txt2Find.length()));
        }

        private void setupCurrentIndex() {
            if (!isInProgress) {
                currentIndex = 0;
                if (isBeg) {
                    if (isFwd) {
                        currentIndex = 0;
                    } else {
                        currentIndex = m_sp.getDocument().getLength() - 1;
                    }
                }
                if (isCursor) {
                    Caret cr = m_sp.getCaret();
                    if (isFwd) {
                        currentIndex = Math.max(cr.getDot(), cr.getMark());
                    } else {
                        currentIndex = m_sp.getCaretPosition();
                        if (currentIndex > 0) {
                            currentIndex--;
                        }
                    }
                }
            }
        }
    }

    private class ReplaceActionImpl implements ActionListener {

        public ReplaceActionImpl() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            bind();
            Caret cr = m_sp.getCaret();
            if (cr != null || cr.getDot() != cr.getMark()) {
                m_sp.replaceSelection(txtReplace_with);
            }
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        if (isCursor) {
            isInProgress = false;
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        isInProgress = false;
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        isInProgress = false;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgDirection = new javax.swing.ButtonGroup();
        bgLocation = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        edit_2find = new javax.swing.JTextField();
        edit_replace_with = new javax.swing.JTextField();
        btn_find = new javax.swing.JButton();
        btn_replace = new javax.swing.JButton();
        check_whole = new javax.swing.JCheckBox();
        check_case = new javax.swing.JCheckBox();
        radio_fwd = new javax.swing.JRadioButton();
        radio_bwd = new javax.swing.JRadioButton();
        radio_cusor = new javax.swing.JRadioButton();
        radio_beg = new javax.swing.JRadioButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jLabel1.setText(messages.getString("label.text2Find")); // NOI18N

        jLabel2.setText(messages.getString("label.txtReplaceWith")); // NOI18N

        btn_find.setText(messages.getString("btn.find")); // NOI18N
        btn_find.setPreferredSize(new java.awt.Dimension(85, 23));

        btn_replace.setText(messages.getString("btn.replace")); // NOI18N
        btn_replace.setPreferredSize(new java.awt.Dimension(85, 23));

        check_whole.setText(messages.getString("check.whole")); // NOI18N

        check_case.setText(messages.getString("check.case")); // NOI18N

        bgDirection.add(radio_fwd);
        radio_fwd.setSelected(true);
        radio_fwd.setText(messages.getString("radio.fwd")); // NOI18N

        bgDirection.add(radio_bwd);
        radio_bwd.setText(messages.getString("radio.bwd")); // NOI18N

        bgLocation.add(radio_cusor);
        radio_cusor.setSelected(true);
        radio_cusor.setText(messages.getString("radio.cusor")); // NOI18N

        bgLocation.add(radio_beg);
        radio_beg.setText(messages.getString("radio.beg")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(edit_2find)
                    .addComponent(edit_replace_with, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_find, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_replace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(check_case)
                    .addComponent(check_whole))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radio_fwd)
                    .addComponent(radio_bwd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radio_beg)
                    .addComponent(radio_cusor)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(edit_2find, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_find, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edit_replace_with, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_replace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(check_whole)
                    .addComponent(radio_fwd)
                    .addComponent(radio_cusor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(check_case)
                    .addComponent(radio_bwd)
                    .addComponent(radio_beg)))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgDirection;
    private javax.swing.ButtonGroup bgLocation;
    private javax.swing.JButton btn_find;
    private javax.swing.JButton btn_replace;
    private javax.swing.JCheckBox check_case;
    private javax.swing.JCheckBox check_whole;
    private javax.swing.JTextField edit_2find;
    private javax.swing.JTextField edit_replace_with;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JRadioButton radio_beg;
    private javax.swing.JRadioButton radio_bwd;
    private javax.swing.JRadioButton radio_cusor;
    private javax.swing.JRadioButton radio_fwd;
    // End of variables declaration//GEN-END:variables
}
