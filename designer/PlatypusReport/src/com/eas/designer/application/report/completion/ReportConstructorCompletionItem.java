/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report.completion;

import com.eas.designer.application.module.completion.AppElementConstructorCompletionItem;
import com.eas.designer.application.report.PlatypusReportDataObject;
import java.util.List;
import javax.swing.ImageIcon;
import org.openide.filesystems.FileObject;

/**
 *
 * @author vv
 */
public class ReportConstructorCompletionItem extends AppElementConstructorCompletionItem {
    
    private static final ImageIcon CONSTRUCTOR_ICON = new ImageIcon(PlatypusReportDataObject.class.getResource("report.png"));//NOI18N
    private static final int SORT_PRIORITY = 30;
    
    public ReportConstructorCompletionItem(String name, String rightText, List<String> params, FileObject aFileObject, int aStartOffset, int aEndOffset) {
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