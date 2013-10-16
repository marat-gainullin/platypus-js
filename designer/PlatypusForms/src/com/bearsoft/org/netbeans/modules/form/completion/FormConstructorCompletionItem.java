/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.bearsoft.org.netbeans.modules.form.PlatypusFormSupport;
import com.eas.designer.application.module.completion.AppElementConstructorCompletionItem;
import java.util.List;
import javax.swing.ImageIcon;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;

/**
 *
 * @author vv
 */
public class FormConstructorCompletionItem extends AppElementConstructorCompletionItem {
    
    private static final ImageIcon CONSTRUCTOR_ICON = new ImageIcon(ImageUtilities.loadImage(PlatypusFormSupport.iconURL)); //NOI18N
    private static final int SORT_PRIORITY = 20;
    
    public FormConstructorCompletionItem(String name, String rightText, List<String> params, FileObject aFileObject, int aStartOffset, int aEndOffset) {
        super(name, rightText, params, aFileObject, aStartOffset, aEndOffset);
    }
    
    @Override
    public ImageIcon getIcon() {
        return CONSTRUCTOR_ICON;
    }
    
    @Override
    public int getSortPriority() {
        return SORT_PRIORITY;
    }
}
